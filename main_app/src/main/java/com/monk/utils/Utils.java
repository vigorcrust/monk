package com.monk.utils;

/**
 * Utility class
 *
 * @author ahatzold on 20.07.2017
 */
public class Utils {

	/**
	 * Constructor should not be used
	 */
	private Utils() {
		throw new IllegalStateException("Utils is a utility class!");
	}

	/**
	 * Checks if a String is empty
	 *
	 * @param str The String to check
	 * @return true, if the String is empty, otherwise false
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * Gets the first word of a string.
	 * Used to find statements starting with a prohibited word.
	 *
	 * @param text The String to search in
	 * @return The first word of the given text
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

}
