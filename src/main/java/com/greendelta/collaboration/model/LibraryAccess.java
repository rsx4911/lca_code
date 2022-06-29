package com.greendelta.collaboration.model;

public enum LibraryAccess {

	PUBLIC,
	
	USER,
	
	MEMBER;
	
	public static boolean isTeamAccess(String access) {
		for (var a : LibraryAccess.values())
			if (a.name().equals(access))
				return true;
		return false;
	}
		
}
