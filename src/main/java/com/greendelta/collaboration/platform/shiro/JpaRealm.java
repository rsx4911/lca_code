package com.greendelta.collaboration.platform.shiro;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.service.user.UserService;

public class JpaRealm extends AuthorizingRealm {

	private static final Logger log = LogManager.getLogger(JpaRealm.class);

	private UserService userService;

	@Inject
	public JpaRealm(UserService userService) {
		this.userService = userService;
		setName("jpa-realm");
		HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
		credentialsMatcher.setHashIterations(50);
		setCredentialsMatcher(credentialsMatcher);
		setCachingEnabled(false);
		setAuthenticationCachingEnabled(false);
		setAuthorizationCachingEnabled(false);
		log.info("Successfully constructed jpa-realm");
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof UsernamePasswordToken;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authToken;
		return getAccount(token.getUsername());
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return getAccount((String) getAvailablePrincipal(principals));
	}

	private JpaAccount getAccount(String username) {
		User user = userService.getForUsername(username);
		if (user == null)
			return null;
		return new JpaAccount(user, this);
	}

}
