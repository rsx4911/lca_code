package com.greendelta.collaboration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.Restriction;

@Service
public class RestrictionService {

	private final Dao<Restriction> dao;

	@Autowired
	public RestrictionService(Dao<Restriction> dao) {
		this.dao = dao;
	}

	public List<Restriction> getAll() {
		return dao.getAll();
	}

	public Restriction insert(Restriction restriction) {
		return dao.insert(restriction);
	}

	public Restriction update(Restriction restriction) {
		return dao.update(restriction);
	}

	public void delete(Restriction restriction) {
		dao.delete(restriction);
	}

	public Restriction getForName(String name) {
		return dao.getFirstForAttribute("name", name);
	}

}
