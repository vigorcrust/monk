package com.monk.utils;

import com.monk.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by damarten on 13.07.2017.
 */
public class ClassLoaderHelper {

	static final private FileFilter dirsFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};
	static Logger logger = LoggerFactory.getLogger(ClassLoaderHelper.class);
	private static String JAR_SUFFIX = ".JAR";
	static final private FileFilter jarsFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getName().toUpperCase().endsWith(JAR_SUFFIX);
		}
	};

	public ClassLoaderHelper() {
	}

	public static ClassLoader buildClassLoader(List<File> directories, boolean includeSubDirectiories) {
		return buildClassLoader(directories, includeSubDirectiories, Main.class.getClass().getClassLoader());
	}

	public static ClassLoader buildClassLoader(List<File> directories, boolean includeSubDirectiories, ClassLoader loader) {
		List<URL> jars = new ArrayList<URL>();
		// Find all Jars in each directory
		for (File dir : directories) {
			addJarsToList(jars, dir, includeSubDirectiories);
		}
		return new URLClassLoader(jars.toArray(new URL[jars.size()]), loader);
	}

	private static void addJarsToList(List<URL> jars, File dir, boolean includeSubDirectiories) {
		try {
			for (File jar : dir.listFiles(jarsFilter)) {
				jars.add(jar.toURI().toURL());
			}

			if (includeSubDirectiories) {
				for (File subdir : dir.listFiles(dirsFilter)) {
					addJarsToList(jars, subdir, true);
				}
			}
		} catch (Exception e) {
			logger.error("Error in ClassLoaderHelper: " + e);
		}
	}

	public static void isJarExisting(String jarName) {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		if (classLoader instanceof URLClassLoader) {
			URLClassLoader classLoader2 = (URLClassLoader) classLoader;
			URL[] urls = classLoader2.getURLs();
			for (URL url : urls) {
				File file = new File(url.getFile());
				if (file.getPath().endsWith(jarName)) {
					return;
				}
			}
			logger.trace(jarName + " not exists");
		}
	}
}

