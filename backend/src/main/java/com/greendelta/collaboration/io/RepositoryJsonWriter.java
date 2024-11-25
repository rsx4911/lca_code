package com.greendelta.collaboration.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.git.RepositoryInfo;
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.jsonld.LibraryLink;
import org.openlca.jsonld.ModelPath;
import org.openlca.jsonld.SchemaVersion;
import org.openlca.jsonld.ZipStore;
import org.openlca.jsonld.input.CategoryImport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class RepositoryJsonWriter implements Closeable {

	private final static Logger log = LogManager.getLogger(RepositoryJsonWriter.class);
	private final ZipStore zipStore;
	private final OlcaRepository repo;

	public static void write(File gitDir, String commitId, File cachedJsonFile, List<LibraryLink> libraries) {
		try (var repo = new OlcaRepository(gitDir)) {
			var writer = new RepositoryJsonWriter(repo, libraries, repo.getInfo().schemaVersion(), cachedJsonFile);
			var categories = new JsonArray();
			repo.references.find().includeCategories().commit(commitId).iterate(entry -> {
				if (entry.isCategory) {
					categories.add(entry.path);
				} else if (entry.isDataset) {
					writer.put(entry);
				}
			});
			writer.writeCategoriesJson(categories);
			writer.close();
		} catch (IOException e) {
			log.error("Error writing json-ld archive", e);
		}
	}

	RepositoryJsonWriter(OlcaRepository repo, List<LibraryLink> libraries, SchemaVersion schemaVersion,
			File file) throws IOException {
		this.zipStore = ZipStore.open(file);
		this.repo = repo;
		RepositoryInfo.create()
				.withLibraries(libraries)
				.withSchemaVersion(schemaVersion)
				.writeTo(zipStore);
	}

	String put(Reference ref) {
		var data = repo.datasets.get(ref);
		if (data == null)
			return null;
		zipStore.put(ModelPath.jsonOf(ref.type, ref.refId), data.getBytes(StandardCharsets.UTF_8));
		repo.references.getBinaries(ref).forEach(binary -> {
			zipStore.putBin(ref.type, ref.refId, binary, repo.datasets.getBinary(ref, binary));
		});
		return data;
	}

	@Override
	public void close() throws IOException {
		zipStore.close();
	}

	void writeCategoriesJson(JsonArray categories) {
		if (categories.isEmpty())
			return;
		var json = new Gson().toJson(categories);
		var data = json.getBytes(StandardCharsets.UTF_8);
		zipStore.put(CategoryImport.FILE_NAME, data);
	}

}
