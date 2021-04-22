package com.greendelta.collaboration.service.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.internal.storage.file.FileRepository;

import com.greendelta.collaboration.service.repository.References.CommitReference;

public class Datasets {

	private static final Logger log = LogManager.getLogger(Datasets.class);
	private final FileRepository repo;

	Datasets(FileRepository repo) throws IOException {
		this.repo = repo;
	}

	public String get(CommitReference ref) {
		if (ref == null)
			return null;
		try {
			return new String(repo.getObjectDatabase().newReader().open(ref.objectId).getBytes(), "utf-8");
		} catch (IOException e) {
			log.error("Error loading " + ref.type.name() + " " + ref.refId + " from commit " + ref.commitId);
			return null;
		}
	}

	public Binary getBinary(CommitReference ref, String fileName) {
		if (ref == null || fileName == null || fileName.isEmpty())
			return null;
		// TODO read binary data from git
		return null;
	}

	public List<Binary> getBinaries(CommitReference ref) {
		if (ref == null)
			return new ArrayList<>();
		// TODO read binary data from git
		return new ArrayList<>();
	}

	public class Binary {

		public final String filename;
		public final byte[] data;

		private Binary(String filename, byte[] data) {
			this.filename = filename;
			this.data = data;
		}

	}

}
