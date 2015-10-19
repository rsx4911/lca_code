package com.greendelta.cloud.service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.cloud.model.User;

public class UserService {

	private final static Logger log = LoggerFactory.getLogger(UserService.class);
	private final static Random random = new SecureRandom();
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

	public User createNewUser(String name, String password) {
		User user = new User();
		user.setName(name);
		try {
			byte[] salt = new byte[8];
			random.nextBytes(salt);
			String hash = new Sha256Hash(password.getBytes("UTF-8"), salt, 50).toHex();
			user.setHash(hash);
			user.setSalt(Hex.encodeToString(salt));
		} catch (UnsupportedEncodingException e) {
			log.error("Unexpected encoding exception", e);
		}		
		return dao.insert(user);
	}

	public void delete(long id) {
		dao.delete(id);
	}

}