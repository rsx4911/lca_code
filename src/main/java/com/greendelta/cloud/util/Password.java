package com.greendelta.cloud.util;

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
		for (int i = 48; i <= 57; i++)
			DIGITS.add((char) i);
		LOWERCASE = new ArrayList<>();
		for (int i = 97; i <= 122; i++)
			LOWERCASE.add((char) i);
		UPPERCASE = new ArrayList<>();
		for (int i = 65; i <= 90; i++)
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
		List<Character> chars = new ArrayList<>();
		chars.addAll(DIGITS);
		chars.addAll(LOWERCASE);
		chars.addAll(UPPERCASE);
		chars.addAll(SPECIAL);
		String password = "";
		for (int i = 0; i < 16; i++) {
			int next = (int) (Math.random() * chars.size());
			if (next == chars.size())
				next--;
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
		int occurrences = 0;
		for (Character c : list)
			if (password.indexOf(c) != -1)
				occurrences++;
		return occurrences >= minimum;
	}

}
