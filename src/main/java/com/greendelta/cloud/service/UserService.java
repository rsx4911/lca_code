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

public class UserService {

	private final static Logger log = LoggerFactory
			.getLogger(UserService.class);
	private final static Random random = new SecureRandom();
	private final Provider<Subject> subjectProvider;
	private final Dao<User> dao;
	private final MembershipService memberService;

	@Inject
	public UserService(Provider<Subject> subjectProvider, Dao<User> dao, MembershipService memberService) {
		this.subjectProvider = subjectProvider;
		this.dao = dao;
		this.memberService = memberService;
	}

	public User getForUsername(String username) {
		return dao.getFirstForAttribute("username", username);
	}

	public boolean exists(String username) {
		return getForUsername(username) != null;
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
			parameters.put("name", "%" + filter.toLowerCase() + "%");
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

	public boolean delete(long id) {
		User user = dao.get(id);
		if (user == null)
			return false;
		memberService.removeMemberships(user);
		dao.delete(user);
		return true;
	}

	@RequiresRoles("admin")
	public User insert(User user) {
		return dao.insert(user);
	}

	public User update(User user) {
		return dao.update(user);
	}

}