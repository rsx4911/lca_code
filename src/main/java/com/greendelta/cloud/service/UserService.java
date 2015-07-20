package com.greendelta.cloud.service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Collections;

import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.cloud.model.User;

public class UserService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private Provider<Subject> subjectProvider;
	private Dao<User> dao;
	private SecureRandom random;

	@Inject
	public UserService(Provider<Subject> subjectProvider, Dao<User> dao, SecureRandom random) {
		this.subjectProvider = subjectProvider;
		this.dao = dao;
		this.random = random;
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

	private long getLastId() {
		User value = dao.getFirst("SELECT user FROM User user ORDER BY user.id DESC", Collections.emptyMap());
		if (value == null)
			return 0;
		return value.getId();
	}

	public void createNewUser(String name, String password) {
		User user = new User();
		user.setId(getLastId() + 1);
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
		dao.insert(user);
	}

	public void delete(long id) {
		dao.delete(id);
	}
}