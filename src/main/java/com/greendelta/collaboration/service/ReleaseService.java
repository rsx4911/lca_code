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

	public boolean hasReleases(String groupOrRepository) {
		var releases = dao.query(
				"SELECT release FROM ReleaseInfo release WHERE release.repositoryPath LIKE :path",
				Map.of("path", groupOrRepository + "%"));
		return !releases.isEmpty();
	}

	public boolean isReleased(String repositoryPath, String commitId) {
		var release = dao.getFirstForAttributes(Map.of(
				"repositoryPath", repositoryPath,
				"commitId", commitId));
		return release != null;
	}

	public List<ReleaseInfo> getFor(String repositoryPath) {
		return dao.getForAttribute("repositoryPath", repositoryPath);
	}


	public ReleaseInfo getLatest(String repositoryPath) {
		var releases = dao.getForAttribute("repositoryPath", repositoryPath);
		if (releases.isEmpty())
			return null;
		return releases.get(releases.size() - 1);
	}
	public ReleaseInfo insert(ReleaseInfo release) {
		return dao.insert(release);
	}

	public ReleaseInfo update(ReleaseInfo release) {
		return dao.update(release);
	}

	public void delete(ReleaseInfo release) {
		dao.delete(release);
	}

}
