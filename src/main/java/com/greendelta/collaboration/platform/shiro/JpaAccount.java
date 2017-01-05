package com.greendelta.collaboration.platform.shiro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.shiro.authc.Account;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

import com.greendelta.collaboration.model.User;

public class JpaAccount implements Account, SaltedAuthenticationInfo {

	private static final long serialVersionUID = 3450571619512879895L;
	private SimpleAuthenticationInfo authenticationInfo;
	private Collection<String> roles;

	public JpaAccount(User user, Realm realm) {
		Sha256Hash hash = Sha256Hash.fromHexString(user.hash);
		ByteSource salt = new SimpleByteSource(Hex.decode(user.salt));
		authenticationInfo = new SimpleAuthenticationInfo(user.username, hash, salt, realm.getName());
		roles = new ArrayList<>();
		if (user.admin)
			roles.add("admin");
	}

	@Override
	public PrincipalCollection getPrincipals() {
		return authenticationInfo.getPrincipals();
	}

	@Override
	public Object getCredentials() {
		return authenticationInfo.getCredentials();
	}

	@Override
	public ByteSource getCredentialsSalt() {
		return authenticationInfo.getCredentialsSalt();
	}

	@Override
	public Collection<String> getRoles() {
		return roles;
	}

	@Override
	public Collection<String> getStringPermissions() {
		return Collections.emptyList();
	}

	@Override
	public Collection<Permission> getObjectPermissions() {
		return Collections.emptyList();
	}

}
