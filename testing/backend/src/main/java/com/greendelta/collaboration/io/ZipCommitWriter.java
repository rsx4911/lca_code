package com.greendelta.collaboration.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipException;

import org.eclipse.jgit.lib.PersonIdent;
import org.openlca.core.model.ModelType;
import org.openlca.git.model.Diff;
import org.openlca.git.model.Reference;
import org.openlca.git.util.BinaryResolver;
import org.openlca.git.util.GitUtil;
import org.openlca.git.util.MetaDataParser;
import org.openlca.git.writer.CommitWriter;
import org.openlca.git.writer.UsedFeatures;
import org.openlca.jsonld.ModelPath;
import org.openlca.jsonld.PackageInfo;
import org.openlca.jsonld.ZipStore;
import org.openlca.jsonld.input.CategoryImport;
import org.openlca.util.Strings;

import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Repository;

public class ZipCommitWriter extends CommitWriter {

	private final ZipStore zip;

	private ZipCommitWriter(ZipStore zip, Repository repo) {
		super(repo, new ZipBinaryResolver(zip));
		this.zip = zip;
	}

	public static boolean write(InputStream stream, Repository git, User user, String commitMessage)
			throws ZipException, IOException {
		Path tmpFile = null;
		ZipStore zip = null;
		try {
			tmpFile = Files.createTempFile("cs-json2repository-", ".zip");
			Files.copy(stream, tmpFile, StandardCopyOption.REPLACE_EXISTING);
			zip = ZipStore.open(tmpFile.toFile());
			var writer = new ZipCommitWriter(zip, git);
			writer.usedFeatures = UsedFeatures.of(PackageInfo.readFrom(zip));
			writer.as(new PersonIdent(user.username, user.email));
			writer.write(commitMessage);
			return true;
		} finally {
			if (zip != null) {
				zip.close();
			}
			if (tmpFile != null) {
				Files.delete(tmpFile);
			}
		}
	}

	private void write(String message) throws IOException {
		var previousCommit = repo.getHeadCommit();
		if (previousCommit == null) {
			write(message, getChanges());
		} else {
			write(message, getChanges(), previousCommit.getId());
		}
	}

	private List<Diff> getChanges() {
		return Arrays.asList(ModelType.values()).stream()
				.sorted((t1, t2) -> Strings.compare(t1.name(), t2.name()))
				.map(this::getChanges)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private List<Diff> getChanges(ModelType type) {
		var changes = zip.getRefIds(type).stream()
				.map(refId -> getPath(type, refId))
				.map(Reference::new)
				.map(Diff::added)
				.collect(Collectors.toList());
		var categories = zip.getJson(CategoryImport.FILE_NAME);
		if (categories == null || !categories.isJsonArray())
			return changes;
		var allCategories = changes.stream()
				.map(c -> c.type.name() + "/" + c.getCategoryPath())
				.collect(Collectors.toSet());
		for (var category : categories.getAsJsonArray()) {
			var path = category.getAsString();
			if (!allCategories.contains(path)) {
				allCategories.add(path);
				changes.add(Diff.added(new Reference(path)));
			}
		}
		return changes;
	}

	private String getPath(ModelType type, String refId) {
		var category = getCategory(type, refId);
		return GitUtil.toDatasetPath(type, category, refId);
	}

	private String getCategory(ModelType type, String refId) {
		var path = ModelPath.jsonOf(type, refId);
		var stream = zip.getStream(path);
		var data = MetaDataParser.parse(stream, "category");
		if (data.get("category") != null)
			return data.get("category").toString();
		return null;
	}

	@Override
	protected byte[] getData(Diff change) throws IOException {
		var path = ModelPath.jsonOf(change.type, change.refId);
		return zip.getBytes(path);
	}

	private static class ZipBinaryResolver implements BinaryResolver {

		private final ZipStore zip;

		private ZipBinaryResolver(ZipStore zip) {
			this.zip = zip;
		}

		@Override
		public List<String> list(Diff change, String relativePath) {
			var root = ModelPath.binFolderOf(change.type, change.refId);
			var path = getPath(change, relativePath);
			return zip.getFiles(path).stream()
					.map(p -> p.substring(root.length() + 2))
					.toList();
		}

		@Override
		public boolean isDirectory(Diff change, String relativePath) {
			var path = getPath(change, relativePath);
			var files = zip.getBinFiles(change.type, change.refId);
			return !files.contains("/" + path);
		}

		@Override
		public byte[] resolve(Diff change, String relativePath) throws IOException {
			var path = getPath(change, relativePath);
			return zip.getBytes(path);
		}

		private String getPath(Diff change, String relativePath) {
			var root = ModelPath.binFolderOf(change.type, change.refId);
			if (Strings.nullOrEmpty(root))
				return root;
			return root + "/" + relativePath;
		}

	}

}
