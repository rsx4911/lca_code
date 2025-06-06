package com.greendelta.collaboration.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.ReleaseInfo;

@Service
public class ReleaseService {

	private Dao<ReleaseInfo> dao;

	public ReleaseService(Dao<ReleaseInfo> dao) {
		this.dao = dao;
	}

	public List<ReleaseInfo> getAll() {
		return dao.getAll();
	}

	public boolean isReleased(String repositoryPath, String commitId) {
		return get(repositoryPath, commitId) != null;
	}

	public List<ReleaseInfo> getFor(String repositoryPath) {
		return dao.getForAttribute("repositoryPath", repositoryPath);
	}

	public ReleaseInfo get(String repositoryPath, String commitId) {
		return dao.getFirstForAttributes(Map.of(
				"repositoryPath", repositoryPath,
				"commitId", commitId));
	}

	public ReleaseInfo insert(ReleaseInfo release) {
		return dao.insert(release);
	}

	public ReleaseInfo update(ReleaseInfo release) {
		return dao.update(release);
	}

	public void move(String oldPath, String newPath) {
		var releases = getFor(oldPath);
		releases.forEach(release -> {
			release.repositoryPath = newPath;
			update(release);
		});
	}

	public void delete(String repositoryPath) {
		getFor(repositoryPath).forEach(this::delete);
	}

	public void delete(ReleaseInfo release) {
		dao.delete(release);
	}

}
