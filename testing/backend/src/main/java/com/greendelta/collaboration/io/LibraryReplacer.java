package com.greendelta.collaboration.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.PersonIdent;
import org.openlca.core.library.Library;
import org.openlca.core.model.ModelType;
import org.openlca.git.RepositoryInfo;
import org.openlca.git.iterator.ChangeIterator;
import org.openlca.git.model.Diff;
import org.openlca.git.model.Reference;
import org.openlca.git.repo.OlcaRepository;
import org.openlca.git.util.BinaryResolver;
import org.openlca.git.writer.CommitWriter;
import org.openlca.jsonld.LibraryLink;
import org.openlca.util.Strings;

public class LibraryReplacer extends CommitWriter {

	private final OlcaRepository repo;
	private String message;
	private String toReplace;
	private Library replaceWith;

	private LibraryReplacer(OlcaRepository repo) {
		super(repo, BinaryResolver.NULL);
		this.repo = repo;
	}

	public static LibraryReplacer in(OlcaRepository repo) {
		return new LibraryReplacer(repo);
	}

	public LibraryReplacer as(PersonIdent committer) {
		super.as(committer);
		return this;
	}

	public LibraryReplacer withMessage(String message) {
		this.message = message;
		return this;
	}

	public LibraryReplacer replace(String toReplace, Library replaceWith) {
		this.toReplace = toReplace;
		this.replaceWith = replaceWith;
		return this;
	}

	public void run() throws IOException {
		if (Strings.nullOrEmpty(toReplace) || replaceWith == null)
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

	private void collectReferences(String headCommitId, Consumer<Reference> consumer) {
		try (var reader = replaceWith.openJsonZip()) {
			for (var type : ModelType.values()) {
				for (var refId : reader.getRefIds(type)) {
					var ref = repo.references.get(type, refId, headCommitId);
					if (ref == null)
						continue;
					consumer.accept(ref);
				}
			}
		}
	}

	@Override
	protected List<LibraryLink> getLibraries() {
		return repo.getLibraries().stream()
				.map(lib -> lib.equals(toReplace)
						? new LibraryLink(replaceWith.name(), null)
						: new LibraryLink(lib, null))
				.collect(Collectors.toList());
	}

	@Override
	protected byte[] getData(Diff change) throws IOException {
		// not needed, only deletions and library changes are applied
		return null;
	}

}
