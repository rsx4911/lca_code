package com.greendelta.collaboration.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Password {

	private static final List<Character> DIGITS;
	private static final List<Character> LOWERCASE;
	private static final List<Character> UPPERCASE;
	private static final List<Character> SPECIAL;

	static {
		DIGITS = new ArrayList<>();
		for (var i = 48; i <= 57; i++)
			DIGITS.add((char) i);
		LOWERCASE = new ArrayList<>();
		for (var i = 97; i <= 122; i++)
			LOWERCASE.add((char) i);
		UPPERCASE = new ArrayList<>();
		for (var i = 65; i <= 90; i++)
			UPPERCASE.add((char) i);
		SPECIAL = Arrays.asList(new Character[] { '!', '@' });
	}

	public static String generate() {
		String password = null;
		do
			password = _generate();
		while (!isValid(password) || !checkOccurrences(password, SPECIAL, 1));
		return password;
	}

	private static String _generate() {
		var chars = new ArrayList<Character>();
		chars.addAll(DIGITS);
		chars.addAll(LOWERCASE);
		chars.addAll(UPPERCASE);
		chars.addAll(SPECIAL);
		var password = "";
		for (var i = 0; i < 16; i++) {
			var next = (int) (Math.random() * chars.size());
			if (next == chars.size()) {
				next--;
			}
			password += chars.get(next);
		}
		return password;
	}

	public static boolean isValid(String password) {
		if (!checkOccurrences(password, DIGITS, 1))
			return false;
		if (!checkOccurrences(password, LOWERCASE, 2))
			return false;
		if (!checkOccurrences(password, UPPERCASE, 2))
			return false;
		return true;
	}

	private static boolean checkOccurrences(String password, List<Character> list, int minimum) {
		var occurrences = 0;
		for (var c : list) {
			if (password.indexOf(c) != -1) {
				occurrences++;
			}
		}
		return occurrences >= minimum;
	}

	public static Integer getToken(String password) {
		return getToken(password, null);
	}

	public static Integer getToken(String password, Integer token) {
		if (token != null && token != 0)
			return token;
		var i = password.lastIndexOf("&token=");
		if (i == -1 || password.length() != i + 13)
			return null;
		return Integer.parseInt(password.substring(password.lastIndexOf("&token=") + 7));
	}

	public static String getPasswordWithoutToken(String password) {
		if (!password.contains("&token="))
			return password;
		return password.substring(0, password.lastIndexOf("&token="));
	}

}
