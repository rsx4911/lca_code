package com.greendelta.collaboration.util.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.UUID;
import java.util.function.Function;
import java.util.zip.ZipException;

import org.openlca.cloud.model.data.Commit;
import org.openlca.core.model.ModelType;
import org.openlca.jsonld.ModelPath;
import org.openlca.util.BinUtils;
import org.openlca.util.Dirs;

import com.greendelta.collaboration.model.User;

public class Json2Repository {

	public static void convert(File repoDir, User user, String commitMessage) throws ZipException, IOException {
		String commitId = createCommitHistoryFile(repoDir, user.username, commitMessage);
		createVersionFile(repoDir);
		int size = convertStructure(repoDir, commitId, false);
		size += convertStructure(new File(repoDir, "bin"), commitId, true);
		createSizeFile(repoDir, size);
		new File(repoDir, "meta.info").delete();
	}

	private static String createCommitHistoryFile(File dir, String username, String message) throws IOException {
		File file = new File(dir, "history.txt");
		Commit commit = new Commit();
		commit.id = UUID.randomUUID().toString();
		commit.timestamp = Calendar.getInstance().getTimeInMillis();
		commit.user = username;
		commit.message = message;
		write(file, commit.toString().getBytes());
		return commit.id;
	}

	private static void createVersionFile(File dir) throws IOException {
		File file = new File(dir, ".version");
		write(file, "1".getBytes());
	}

	private static int convertStructure(File dir, String commitId, boolean isBinary) throws IOException {
		if (!dir.exists())
			return 0;
		int size = 0;
		for (ModelType type : ModelType.values()) {
			File typeDir = renameTypeDir(dir, type);
			if (typeDir == null)
				continue;
			for (File file : typeDir.listFiles()) {
				if (isBinary) {
					for (File child : file.listFiles()) {
						child = move(child, new File(getCommitDir(file, commitId), child.getName()));
						size += compress(child, f -> f.getName() + ".gz");
					}
					Dirs.delete(file.toPath());
				} else {
					file = move(file, new File(getModelDir(file), file.getName()));
					size += compress(file, f -> commitId + ".json.gz");
				}
			}
		}
		return size;
	}

	private static File renameTypeDir(File dir, ModelType type) throws IOException {
		File typeDir = new File(dir, ModelPath.get(type));
		if (!typeDir.exists())
			return null;
		return move(typeDir, new File(dir, type.toString().toLowerCase()));
	}

	private static File move(File from, File to) throws IOException {
		if (from.isDirectory()) {
			Dirs.move(from.toPath(), to.toPath());
			return to;
		}
		if (!from.renameTo(to))
			throw new IOException("Could not move file from " + from.getAbsolutePath() + " to " + to.getAbsolutePath());
		return to;
	}

	private static File getCommitDir(File file, String commitId) throws IOException {
		File commitDir = new File(getModelDir(file), commitId);
		if (!commitDir.exists()) {
			commitDir.mkdir();
		}
		return commitDir;
	}

	private static File getModelDir(File file) throws IOException {
		File intermediateDir = new File(file.getParentFile(), file.getName().substring(0, 2));
		if (!intermediateDir.exists()) {
			intermediateDir.mkdir();
		}
		String modelId = file.getName();
		if (modelId.endsWith(".json")) {
			modelId = modelId.substring(0, file.getName().lastIndexOf(".json"));
		}
		File modelDir = new File(intermediateDir, modelId);
		if (!modelDir.exists()) {
			modelDir.mkdir();
		}
		return modelDir;
	}

	private static int compress(File file, Function<File, String> getFileName) throws IOException {
		if (!file.isDirectory()) {
			byte[] data = Files.readAllBytes(file.toPath());
			file.delete();
			data = BinUtils.gzip(data);
			file = new File(file.getParentFile(), getFileName.apply(file));
			write(file, data);
			return data.length;
		}
		int size = 0;
		for (File child : file.listFiles()) {
			size += compress(child, getFileName);
		}
		return size;
	}

	private static void write(File file, byte[] data) throws IOException {
		Files.write(file.toPath(), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	private static void createSizeFile(File dir, int size) throws IOException {
		File file = new File(dir, ".size");
		write(file, Integer.toString(size).getBytes());
	}

}
