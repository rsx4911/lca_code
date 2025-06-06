package com.greendelta.collaboration.service.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.openlca.util.Strings;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.controller.util.Response;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.Dao;
import com.greendelta.collaboration.util.Dates;
import com.greendelta.collaboration.util.SearchResults;
import com.greendelta.search.wrapper.SearchResult;
import com.warrenstrange.googleauth.GoogleAuthenticator;

@Service
public class UserService implements UserDetailsService, OAuth2UserService<OidcUserRequest, OidcUser> {

	private final Dao<User> dao;
	private final PasswordEncoder passwordEncoder;

	public UserService(Dao<User> dao, PasswordEncoder passwordEncoder) {
		this.dao = dao;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		var user = getForUsernameOrEmail(usernameOrEmail);
		if (user == null)
			throw new UsernameNotFoundException("Couldn't find user " + usernameOrEmail);
		return user;
	}

	public User getForUsername(String username) {
		return dao.getFirstForAttribute("username", username, true);
	}

	public User getForEmail(String email) {
		return dao.getFirstForAttribute("email", email, true);
	}

	public User getForUsernameOrEmail(String usernameOrEmail) {
		var user = getForUsername(usernameOrEmail);
		if (user != null)
			return user;
		return getForEmail(usernameOrEmail);
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
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var user = getUser(auth);
		if (user == null)
			throw Response.unauthorized("Could not find a matching user");
		if (user.isDeactivated())
			throw Response.unauthorized("User is deactivated or approval is pending");
		return user;
	}

	public User getUser(Authentication auth) {
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken)
			return new User();
		if (auth instanceof UsernamePasswordAuthenticationToken)
			return getForUsername(auth.getName());
		if (auth instanceof OAuth2AuthenticationToken token) {
			var principal = token.getPrincipal();
			String email = principal.getAttribute("email");
			if (Strings.nullOrEmpty(email))
				return null;
			return getForEmail(email);
		}
		return null;
	}

	public User createUser(OAuth2User principal) {
		String email = principal.getAttribute("email");
		String preferredUsername = principal.getAttribute("preferred_username");
		var user = new User();
		user.email = email;
		user.name = principal.getAttribute("name");
		var emailUser = email.substring(0, email.indexOf("@"));
		if (Strings.nullOrEmpty(user.name)) {
			user.name = emailUser;
		}
		var username = toUsername(preferredUsername);
		if (Strings.nullOrEmpty(username) || exists(username)) {
			username = toUsername(user.name);
		}
		if (Strings.nullOrEmpty(username) || exists(username)) {
			username = toUsername(emailUser);
		}
		var emailProvider = email.substring(email.indexOf("@") + 1, email.lastIndexOf("."));
		var count = 0;
		while (exists(username)) {
			var suffix = count != 0 ? Integer.toString(count) : "";
			username = toUsername(emailUser, emailProvider, suffix);
			count++;
		}
		user.username = username;
		user.settings.setDefaults();
		return user;
	}

	private String toUsername(String... values) {
		var username = "";
		for (var value : values) {
			if (Strings.nullOrEmpty(value))
				continue;
			if (!username.isEmpty()) {
				username += "_";
			}
			for (var c : value.toCharArray()) {
				if (Character.isLetterOrDigit(c) || c == '_') {
					username += c;
				} else {
					username += "_";
				}
			}
		}
		return username.toLowerCase();
	}

	public long getCount() {
		return dao.getCount();
	}

	public SearchResult<User> getAll(int page, int pageSize, String filter) {
		var user = getCurrentUser();
		if (user == null || !user.isUserManager())
			return SearchResults.from(new ArrayList<>());
		var jpql = "SELECT u FROM User u";
		var parameters = new HashMap<String, Object>();
		if (!Strings.nullOrEmpty(filter)) {
			jpql += " WHERE LOWER(u.name) LIKE :name";
			parameters.put("name", "%" + filter.toLowerCase() + "%");
		}
		var data = dao.getAll(jpql, parameters).stream()
				.distinct()
				.sorted(this::sortUser)
				.toList();
		return SearchResults.paged(page, pageSize, data);
	}

	private int sortUser(User u1, User u2) {
		var d1 = u1.settings.activeUntil != null
				? Dates.toCalendar(u1.settings.activeUntil, false).getTimeInMillis()
				: Long.MAX_VALUE;
		var d2 = u2.settings.activeUntil != null
				? Dates.toCalendar(u2.settings.activeUntil, false).getTimeInMillis()
				: Long.MAX_VALUE;
		if (d1 > d2)
			return -1;
		if (d1 < d2)
			return 1;
		return u1.name.toLowerCase().compareTo(u2.name.toLowerCase());
	}

	public void setPassword(User user, String password) {
		user.password = passwordEncoder.encode(password);
	}

	public String enableTwoFactorAuthentication(User user, String servername) {
		user.twoFactorSecret = createTwoFactorKey();
		user = update(user);
		return getTwoFactorUrl(user, servername);
	}

	private String createTwoFactorKey() {
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

	@Override
	public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
		var email = request.getIdToken().getEmail();
		if (Strings.nullOrEmpty(email))
			return new DefaultOidcUser(Collections.emptyList(), request.getIdToken());
		var user = getForEmail(email);
		if (user == null)
			return new DefaultOidcUser(Collections.emptyList(), request.getIdToken());
		user.idToken = request.getIdToken();
		return user;
	}

}