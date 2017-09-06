package com.monk.loader;

import com.monk.main.Main;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to manipulate the classloader
 *
 * @author damarten on 13.07.2017
 * @see java.net.URLClassLoader
 */
public class ClassLoaderHelper {

	private static final String JAR_SUFFIX = ".JAR";

	private static final FileFilter dirsFilter = new FileFilter() {

		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}

	};

	static final private FileFilter jarsFilter = new FileFilter() {

		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getName().toUpperCase().endsWith(JAR_SUFFIX);
		}

	};

	/**
	 * Constructor should not be used
	 */
	private ClassLoaderHelper() {
		throw new IllegalStateException("ClassLoaderHelper is a utility class!");
	}

	/**
	 * Builds a new classloader from URLClassLoader
	 * and adds all Jars to it
	 *
	 * @param directories    List to include
	 * @param includeSubDirs If true, subdirs will be included
	 * @return The ClassLoader "containing" the jars
	 */
	public static ClassLoader buildClassLoader(List<File> directories, boolean includeSubDirs) {

		return buildClassLoader(directories, includeSubDirs, Main.class.getClassLoader());

	}

	/**
	 * Builds a new classloader from URLClassLoader
	 * and adds all Jars to it
	 *
	 * @param directories    List to include
	 * @param includeSubDirs If true, subdirs will be included
	 * @param loader         The ClassLoader to add to
	 * @return The ClassLoader "containing" the jars
	 */
	private static ClassLoader buildClassLoader(List<File> directories, boolean includeSubDirs, ClassLoader loader) {

		List<URL> jars = new ArrayList<URL>();
		// Find all Jars in each directory
		for (File dir : directories) {
			addJarsToList(jars, dir, includeSubDirs);
		}
		return new URLClassLoader(jars.toArray(new URL[jars.size()]), loader);

	}

	/**
	 * Adds dirs to the list of jars,
	 * optional with subdirectories
	 *
	 * @param jars           The List of jars to add
	 * @param dir            The directory to search in
	 * @param includeSubDirs If true, subdirs will be included
	 */
	private static void addJarsToList(List<URL> jars, File dir, boolean includeSubDirs) {

		if (dir.listFiles(jarsFilter) != null
				&& dir.listFiles(dirsFilter) != null) {
			try {
				for (File jar : dir.listFiles(jarsFilter)) {
					jars.add(jar.toURI().toURL());
				}

				if (includeSubDirs) {
					for (File subdir : dir.listFiles(dirsFilter)) {
						addJarsToList(jars, subdir, true);
					}
				}
			} catch (Exception e) {
				Logger.error("Error in ClassLoaderHelper");
			}
		} else {
			throw new IllegalStateException("Something went wrong.");
		}

	}

}

