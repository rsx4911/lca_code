package com.greendelta.collaboration.service.user;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;
import com.warrenstrange.googleauth.GoogleAuthenticator;

@Service
public class UserService implements UserDetailsService {

	private final Dao<User> dao;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(Dao<User> dao, PasswordEncoder passwordEncoder) {
		this.dao = dao;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = getForUsername(username);
		if (user == null)
			throw new UsernameNotFoundException("Couldn't find user " + username);
		return user;
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
		return dao.getForAttribute("settings.admin", true);
	}

	public List<User> getUserManagers() {
		var managers = new HashSet<User>();
		managers.addAll(dao.getForAttribute("settings.admin", true));
		managers.addAll(dao.getForAttribute("settings.userManager", true));
		return new ArrayList<>(managers);
	}

	public boolean isLastAdmin(User user) {
		if (!user.isAdmin())
			return false;
		var admins = getAdmins();
		return admins.size() == 1;
	}

	public User getCurrentUser() {
		if (isAnonymous())
			return new User();
		var auth = SecurityContextHolder.getContext().getAuthentication();
		return getForUsername(auth.getName());
	}

	public long getCount() {
		return dao.getCount();
	}

	public int getNoOfRepositories(User user, String repositoryPath) {
		if (user.username == null || user.username.isEmpty())
			return 0;
		if (repositoryPath == null || repositoryPath.isEmpty())
			return 0;
		var userGroup = new File(repositoryPath, user.username);
		if (!userGroup.exists())
			return 0;
		return userGroup.listFiles().length;
	}

	public SearchResult<User> getVisible(int page, int pageSize, String filter) {
		var user = getCurrentUser();
		if (user == null)
			return SearchResults.from(new ArrayList<>());
		var parameters = new HashMap<String, Object>();
		if (!user.isUserManager())
			parameters.put("user", user);
		if (!Strings.nullOrEmpty(filter))
			parameters.put("name", "%" + filter.toLowerCase() + "%");
		var query = createQuery(user, filter);
		var data = dao.getAll(query, parameters).stream()
				.distinct()
				.sorted(this::sortUser)
				.toList();
		return SearchResults.paged(page, pageSize, data);
	}

	private int sortUser(User u1, User u2) {
		var b1 = u1.isDeactivated();
		var b2 = u2.isDeactivated();
		if (b1 != b2)
			return Boolean.compare(b1, b2);
		return u1.name.toLowerCase().compareTo(u2.name.toLowerCase());
	}
	
	private String createQuery(User user, String filter) {
		var jpql = new StringBuilder();
		if (user.isUserManager()) {
			jpql.append("SELECT u FROM User u");
			if (!Strings.nullOrEmpty(filter)) {
				jpql.append(" WHERE LOWER(u.name) LIKE :name");
			}
		} else {
			jpql.append("SELECT u FROM Team t JOIN t.users u WHERE :user MEMBER OF t.users AND u != :user");
			if (!Strings.nullOrEmpty(filter)) {
				jpql.append(" AND LOWER(u.name) LIKE :name");
			}
		}
		return jpql.toString();
	}

	public void setPassword(User user, String password) {
		user.password = passwordEncoder.encode(password);
	}

	public String enableTwoFactorAuthentication(User user, String servername) {
		user.twoFactorSecret = createTwoFactorKey();
		user = update(user);
		return getTwoFactorUrl(user, servername);
	}

	private static String createTwoFactorKey() {
		var authenticator = new GoogleAuthenticator();
		var key = authenticator.createCredentials();
		return key.getKey();
	}

	public String getTwoFactorUrl(User user, String servername) {
		var key = user.twoFactorSecret;
		return getOtpAuthTotpURL(servername, user.username, key);
	}

	private String getOtpAuthTotpURL(String issuer, String username, String key) {
		var uri = new URIBuilder();
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

	public void delete(User user) {
		dao.delete(user);
	}

	public User insert(User user) {
		return dao.insert(user);
	}

	public User update(User user) {
		return dao.update(user);
	}

	public boolean isAnonymous() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		return auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken;
	}

}