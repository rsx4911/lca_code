package com.greendelta.collaboration.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.PersonIdent;
import org.openlca.core.library.LibraryDir;
import org.openlca.core.library.LibraryPackage;
import org.openlca.core.model.ModelType;
import org.openlca.git.RepositoryInfo;
import org.openlca.git.iterator.ChangeIterator;
import org.openlca.git.model.Diff;
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.git.util.BinaryResolver;
import org.openlca.git.writer.CommitWriter;
import org.openlca.jsonld.LibraryLink;
import org.openlca.util.Dirs;
import org.openlca.util.Strings;

public class LibraryReplacer extends CommitWriter {

	private final OlcaRepository repo;
	private Function<String, File> libraryFileResolver;
	private String message;
	private String toReplace;
	private String replaceWith;

	private LibraryReplacer(OlcaRepository repo) {
		super(repo, BinaryResolver.NULL);
		this.repo = repo;
	}

	public static LibraryReplacer in(OlcaRepository repo) {
		return new LibraryReplacer(repo);
	}

	public LibraryReplacer resolveLibraryFileWith(Function<String, File> libraryFileResolver) {
		this.libraryFileResolver = libraryFileResolver;
		return this;
	}

	public LibraryReplacer as(PersonIdent committer) {
		super.as(committer);
		return this;
	}

	public LibraryReplacer withMessage(String message) {
		this.message = message;
		return this;
	}

	public LibraryReplacer replace(String toReplace, String replaceWith) {
		this.toReplace = toReplace;
		this.replaceWith = replaceWith;
		return this;
	}

	public void run() throws IOException {
		if (libraryFileResolver == null)
			throw new IllegalArgumentException("No library file resolver set");
		if (Strings.nullOrEmpty(toReplace) || Strings.nullOrEmpty(replaceWith))
			throw new IllegalArgumentException("No replacements set");
		if (!repo.getLibraries(repo.commits.head()).contains(toReplace))
			throw new IllegalArgumentException("Repository does not contain library " + toReplace);
		if (Strings.nullOrEmpty(message)) {
			message = "Updated library: " + toReplace + " -> " + replaceWith;
		}
		var headCommitId = repo.getHeadCommit().getName();
		var diffs = new ArrayList<Diff>();
		diffs.add(Diff.deleted(new Reference(RepositoryInfo.FILE_NAME + "/" + toReplace)));
		diffs.add(Diff.added(new Reference(RepositoryInfo.FILE_NAME + "/" + replaceWith)));
		collectReferences(headCommitId, ref -> diffs.add(Diff.deleted(ref)));
		var iterator = ChangeIterator.discardEmptiedCategories(repo, binaryResolver, diffs);
		write(message, iterator, repo.getHeadCommit().getId());
	}

	private void collectReferences(String headCommitId, Consumer<Reference> consumer)
			throws IOException {
		var tmp = Files.createTempDirectory("cs-library-dir").toFile();
		var libFile = libraryFileResolver.apply(replaceWith);
		var libDir = LibraryDir.of(tmp);
		LibraryPackage.unzip(libFile, libDir);
		var lib = libDir.getLibrary(replaceWith).get();
		try (var reader = lib.openJsonZip()) {
			for (var type : ModelType.values()) {
				for (var refId : reader.getRefIds(type)) {
					var ref = repo.references.get(type, refId, headCommitId);
					if (ref == null)
						continue;
					consumer.accept(ref);
				}
			}
		} finally {
			Dirs.delete(tmp);
		}
	}

	@Override
	protected List<LibraryLink> getLibraries() {
		return repo.getLibraries().stream()
				.map(lib -> lib.equals(toReplace)
						? new LibraryLink(replaceWith, null)
						: new LibraryLink(toReplace, null))
				.collect(Collectors.toList());
	}

	@Override
	protected byte[] getData(Diff change) throws IOException {
		// not needed, only deletions and library changes are applied
		return null;
	}

}
