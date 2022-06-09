package com.greendelta.collaboration.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.RestrictionSet;
import com.greendelta.collaboration.service.Dao;

@Service
public class RestrictionService {

	private final Dao<RestrictionSet> dao;

	@Autowired
	public RestrictionService(Dao<RestrictionSet> dao) {
		this.dao = dao;
	}

	public List<RestrictionSet> getAll() {
		return dao.getAll();
	}

	public RestrictionSet insert(RestrictionSet set) {
		return dao.insert(set);
	}

	public RestrictionSet update(RestrictionSet set) {
		return dao.update(set);
	}

	public void delete(RestrictionSet set) {
		dao.delete(set);
	}

	public RestrictionSet getForName(String name) {
		return dao.getFirstForAttribute("name", name);
	}

}
