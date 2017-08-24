package com.monk.utils;

/**
 * Created by ahatzold on 20.07.2017 in project monk_project.
 */
public class Utils {

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * Gets the first word of a string.
	 * Used to find Statements starting with a prohibited word.
	 *
	 * @param text
	 * @return
	 */
	public static String getFirstWord(String text) {
		if (Utils.isEmpty(text)) {
			throw new NullPointerException("Method needs to have a not null String.");
		} else {
			// Check if there is more than one word.
			if (text.indexOf(' ') > -1) {
				// Extract first word.
				return text.substring(0, text.indexOf(' '));
			} else {
				// Text is the first word itself.
				return text;
			}
		}
	}

	/**
	 * Checks, if a query contains a prohibited word
	 * such as INSERT, UPDATE, DELETE
	 *
	 * @param query
	 * @return
	 */
	public static boolean containsProhibited(String query) {

		String[] prohibitedWords = {"INSERT", "UPDATE", "DELETE"};
		String firstWord = getFirstWord(query).toLowerCase();
		for (String prohibitedWord : prohibitedWords) {
			if (prohibitedWord.toLowerCase().equals(firstWord)) {
				return true;
			}
		}
		return false;

	}

}
