package com.greendelta.collaboration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.Library;

@Service
public class LibraryService {

	private final Dao<Library> dao;

	@Autowired
	public LibraryService(Dao<Library> dao) {
		this.dao = dao;
	}

	public List<Library> getAll() {
		return dao.getAll();
	}

	public Library insert(Library library) {
		return dao.insert(library);
	}

	public Library update(Library library) {
		return dao.update(library);
	}

	public void delete(Library library) {
		dao.delete(library);
	}

	public Library getForName(String library) {
		return dao.getFirstForAttribute("name", library);
	}

}
