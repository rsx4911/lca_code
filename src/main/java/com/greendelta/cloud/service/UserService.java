package com.greendelta.cloud.service;

import org.apache.shiro.subject.Subject;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.cloud.model.User;

public class UserService {

	private Provider<Subject> subjectProvider;
	private Dao<User> dao;

	@Inject
	public UserService(Provider<Subject> subjectProvider, Dao<User> dao) {
		this.subjectProvider = subjectProvider;
		this.dao = dao;
	}

	public User getForName(String name) {
		return dao.getFirstForAttribute("name", name);
	}

	public User getCurrentUser() {
		Subject subject = subjectProvider.get();
		if (!subject.isAuthenticated())
			return null;
		String name = subject.getPrincipal().toString();
		return getForName(name);
	}

}