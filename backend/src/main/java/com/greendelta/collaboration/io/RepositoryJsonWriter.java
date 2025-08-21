package com.greendelta.collaboration.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.git.RepositoryInfo;
import org.openlca.git.model.Commit;
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.git.util.ModelRefSet;
import org.openlca.jsonld.ModelPath;
import org.openlca.jsonld.ZipStore;
import org.openlca.jsonld.input.CategoryImport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.greendelta.collaboration.service.LibraryService.LibraryLoader;

public class RepositoryJsonWriter implements Closeable {

	private final static Logger log = LogManager.getLogger(RepositoryJsonWriter.class);
	private final ZipStore zipStore;
	private final OlcaRepository repo;
	private final Commit commit;
	private final JsonArray categories = new JsonArray();
	private final Set<String> linkedLibraries = new HashSet<>();
	private final Map<String, ModelRefSet> libraryDatasets = new HashMap<>();
	private final LibraryLoader libraryLoader;

	public static void write(File gitDir, Commit commit, File cachedJsonFile, LibraryLoader libraryLoader) {
		try (var repo = new OlcaRepository(gitDir)) {
			var writer = new RepositoryJsonWriter(repo, libraryLoader, commit, cachedJsonFile);
			repo.references.find().includeCategories().includeLibraries().commit(commit.id).iterate(writer::put);
			writer.close();
		} catch (IOException e) {
			log.error("Error writing json-ld archive", e);
		}
	}

	RepositoryJsonWriter(OlcaRepository repo, LibraryLoader libraryLoader, Commit commit, File file)
			throws IOException {
		this.zipStore = ZipStore.open(file);
		this.repo = repo;
		this.commit = commit;
		this.libraryLoader = libraryLoader;
		for (var lib : repo.getLibraries(commit)) {
			this.libraryDatasets.put(lib, libraryLoader.getRefs(lib));
		}
	}

	boolean collectIfLibrary(ModelType type, String refId) {
		var found = false;
		for (var lib : libraryDatasets.keySet()) {
			if (libraryDatasets.get(lib).contains(type, refId)) {
				linkedLibraries.add(lib);
				found = true;
			}
		}
		return found;
	}

	String put(Reference ref) {
		if (ref.isLibrary) {
			linkedLibraries.add(ref.name);
			return null;
		}
		if (ref.isCategory) {
			categories.add(ref.path);
			return null;
		}
		if (!ref.isDataset)
			return null;
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
		var linkedLibraries = this.linkedLibraries.stream()
				.map(libraryLoader::getLink)
				.collect(Collectors.toList());
		RepositoryInfo.create()
				.withLibraries(linkedLibraries)
				.withSchemaVersion(repo.getInfo(commit.id).schemaVersion())
				.writeTo(zipStore);
		if (!categories.isEmpty()) {
			var json = new Gson().toJson(categories);
			var data = json.getBytes(StandardCharsets.UTF_8);
			zipStore.put(CategoryImport.FILE_NAME, data);
		}
		zipStore.close();
	}

}
