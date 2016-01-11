package com.greendelta.cloud.service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.greendelta.cloud.model.User;

import static org.openlca.cloud.util.Strings.concat;

public class UserService {

	private final static Logger log = LoggerFactory
			.getLogger(UserService.class);
	private final static Random random = new SecureRandom();
	private Provider<Subject> subjectProvider;
	private Dao<User> dao;

	@Inject
	public UserService(Provider<Subject> subjectProvider, Dao<User> dao) {
		this.subjectProvider = subjectProvider;
		this.dao = dao;
	}

	public User getForUsername(String name) {
		return dao.getFirstForAttribute("username", name);
	}

	public User getCurrentUser() {
		Subject subject = subjectProvider.get();
		if (!subject.isAuthenticated())
			return null;
		String name = subject.getPrincipal().toString();
		return getForUsername(name);
	}

	@RequiresRoles("admin")
	public long getCount() {
		return dao.getCount();
	}

	@RequiresRoles("admin")
	public PagedResult<User> getAll(int page, String filter) {
		Map<String, Object> parameters = new HashMap<>();
		if (!Strings.isNullOrEmpty(filter))
			parameters.put("name", concat("%", filter.toLowerCase(), "%"));
		long total = dao.getCount();
		String query = createQuery(page, filter, true);
		long subTotal = dao.getCount(query, parameters);
		int start = 1 + (page - 1) * 10;
		query = createQuery(page, filter, false);
		List<User> data = dao.getAll(query, parameters, start, 10);
		return new PagedResult<>(page, filter, total, subTotal, data);
	}

	private String createQuery(int page, String filter, boolean forCount) {
		StringBuilder jpql = new StringBuilder();
		if (forCount)
			jpql.append("SELECT count(u) FROM User u");
		else
			jpql.append("SELECT u FROM User u");
		if (!Strings.isNullOrEmpty(filter))
			jpql.append(" WHERE LOWER(u.name) LIKE :name");
		return jpql.toString();
	}

	public void setPassword(User user, String password) {
		try {
			byte[] salt = new byte[8];
			random.nextBytes(salt);
			byte[] pass = password.getBytes("UTF-8");
			String hash = new Sha256Hash(pass, salt, 50).toHex();
			user.hash = hash;
			user.salt = Hex.encodeToString(salt);
		} catch (UnsupportedEncodingException e) {
			log.error("Unexpected encoding exception", e);
		}
	}

	public void delete(long id) {
		dao.delete(id);
	}

	@RequiresRoles("admin")
	public User insert(User user) {
		return dao.insert(user);
	}

	public User update(User user) {
		return dao.update(user);
	}

}