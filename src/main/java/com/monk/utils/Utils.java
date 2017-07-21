package com.monk.utils;

/**
 * Created by ahatzold on 20.07.2017 in project monk_project.
 */
public class Utils {

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	//taken from stackoverflow
	public static String getFirstWord(String text) {
		// Check if there is more than one word.
		if (text.indexOf(' ') > -1) {
			// Extract first word.
			return text.substring(0, text.indexOf(' '));
		} else {
			// Text is the first word itself.
			return text;
		}
	}


	//Prohibited Words are INSERT, UPDATE, DELETE
	public static boolean containsProhibited(String query) {

		String[] prohibitedWords = {"INSERT", "UPDATE", "DELETE"};
		String firstWord = getFirstWord(query);
		for (String prohibitedWord : prohibitedWords) {
			if (prohibitedWord.equals(firstWord)) {
				return true;
			}
		}
		return false;

	}

}
