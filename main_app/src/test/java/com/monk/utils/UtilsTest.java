package com.monk.utils;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by ahatzold on 28.07.2017 in project monk_project.
 */
public class UtilsTest {
	@BeforeMethod
	public void setUp() throws Exception {
	}

	@AfterMethod
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsEmpty() throws Exception {

		boolean isEmpty = Utils.isEmpty("");
		assertEquals(isEmpty, true);

		boolean isEmptyWithNull = Utils.isEmpty(null);
		assertEquals(isEmptyWithNull, true);

		boolean isEmptyWithString = Utils.isEmpty("doesntmatter");
		assertEquals(isEmptyWithString, false);
	}

	@Test
	public void testGetFirstWord() throws Exception {

		String result = Utils.getFirstWord("First word in the text");
		assertEquals(result, "First");

		String singleWord = Utils.getFirstWord("Single");
		assertEquals(singleWord, "Single");

	}

	@Test(expectedExceptions = NullPointerException.class)
	public void testGetFirstWordWithNull() throws Exception {

		//throws NullPointerException
		Utils.getFirstWord(null);

	}

	/*@Test
	public void testContainsProhibited() throws Exception {

		boolean prohibitedWordUppercase = Utils.containsProhibited("UPDATE Name FROM Names");
		assertEquals(prohibitedWordUppercase, true);

		boolean prohibitedWordLowercase = Utils.containsProhibited("update name from names");
		assertEquals(prohibitedWordLowercase, true);

		boolean noProhibitedWord = Utils.containsProhibited("SELECT name FROM Names");
		assertEquals(noProhibitedWord, false);

		boolean prohibitedWordAsTableNameUppercase = Utils.containsProhibited("SELECT version FROM update");
		assertEquals(prohibitedWordAsTableNameUppercase, false);

		boolean prohibitedWordAsTableNameLowercase = Utils.containsProhibited("select version from update");
		assertEquals(prohibitedWordAsTableNameLowercase, false);

	}

	@Test(expectedExceptions = NullPointerException.class)
	public void testContainsProhibitedWithNull() throws Exception {

		Utils.containsProhibited(null);

	}*/

}