package com.greendelta.collaboration.service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;

import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class UserService {

	private final static Logger log = LogManager.getLogger(UserService.class);
	private final static Random random = new SecureRandom();
	private final Provider<Subject> subjectProvider;
	private final Dao<User> dao;
	private final String servername;
	private final Provider<EntityManager> entityManagerProvider;

	@Inject
	public UserService(Provider<Subject> subjectProvider, Dao<User> dao, @Named("twofactor.servername") String server,
			Provider<EntityManager> entityManagerProvider) {
		this.subjectProvider = subjectProvider;
		this.dao = dao;
		this.servername = server;
		this.entityManagerProvider = entityManagerProvider;
	}

	public User getForUsername(String username) {
		return dao.getFirstForAttribute("username", username, true);
	}

	public User getForEmail(String email) {
		return dao.getFirstForAttribute("email", email, true);
	}

	public boolean exists(String username) {
		return getForUsername(username) != null;
	}

	public List<User> getAdmins() {
		return dao.getForAttribute("admin", true);
	}

	public boolean isLastAdmin(User user) {
		if (!user.admin)
			return false;
		List<User> admins = getAdmins();
		return admins.size() == 1;
	}

	public User getCurrentUser() {
		Subject subject = subjectProvider.get();
		if (!subject.isAuthenticated())
			return new User();
		String name = subject.getPrincipal().toString();
		return getForUsername(name);
	}

	public long getCount() {
		return dao.getCount();
	}

	public SearchResult<User> getAll(int page, String filter) {
		Map<String, Object> parameters = new HashMap<>();
		if (!Strings.isNullOrEmpty(filter))
			parameters.put("name", "%" + filter.toLowerCase() + "%");
		String query = createQuery(filter, true);
		long subTotal = dao.getCount(query, parameters);
		int start = page == 0 ? 0 : 1 + (page - 1) * 10;
		int limit = page == 0 ? 0 : 10;
		query = createQuery(filter, false);
		List<User> data = dao.getAll(query, parameters, start, limit);
		return SearchResults.from(data, page, 10, subTotal);
	}

	private String createQuery(String filter, boolean forCount) {
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

	public String enableTwoFactorAuthentication(User user) {
		GoogleAuthenticator authenticator = new GoogleAuthenticator();
		GoogleAuthenticatorKey key = authenticator.createCredentials();
		user.twoFactorSecret = key.getKey();
		user = update(user);
		return getTwoFactorUrl(user);
	}

	public String getTwoFactorUrl(User user) {
		String key = user.twoFactorSecret;
		return getOtpAuthTotpURL(servername, user.username, key);
	}

	private String getOtpAuthTotpURL(String issuer, String username, String key) {
		URIBuilder uri = new URIBuilder();
		uri.setScheme("otpauth");
		uri.setHost("totp");
		uri.setPath("/" + formatLabel(issuer, username));
		uri.setParameter("secret", key);
		if (issuer == null)
			return uri.toString();
		uri.setParameter("issuer", issuer);
		return uri.toString();
	}

	private String formatLabel(String issuer, String username) {
		if (username == null || username.trim().length() == 0)
			throw new IllegalArgumentException("Account name must not be empty.");
		if (issuer == null)
			return username;
		if (issuer.contains(":"))
			throw new IllegalArgumentException("Issuer cannot contain the \':\' character.");
		return issuer + ":" + username;
	}

	void delete(User user) {
		dao.delete(user);
	}

	public User insert(User user) {
		return dao.insert(user);
	}

	public User update(User user) {
		return dao.update(user);
	}

	public void clearCache() {
		entityManagerProvider.get().getEntityManagerFactory().getCache().evict(User.class);
	}

	public boolean logout() {
		Subject subject = subjectProvider.get();
		if (!subject.isAuthenticated())
			return false;
		String principal = subject.getPrincipal().toString();
		log.info("User {} attempts to logout", principal);
		subject.logout();
		log.info("User {} successfully logged out", principal);
		return true;
	}

}