package se.mutabilitydetector.cli;

import static com.google.classpath.RegExpResourceFilter.ANY;
import static com.google.classpath.RegExpResourceFilter.ENDS_WITH_CLASS;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import se.mutabilitydetector.AnalysisSession;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;

public class RunMutabilityDetector {

	public static void main(String[] args) {
		CommandLineOptions options = createOptionsFromArgs(args);
		ClassPath cp = new ClassPathFactory().createFromPath(options.classpath());

		RegExpResourceFilter regExpResourceFilter = new RegExpResourceFilter(ANY, ENDS_WITH_CLASS);
		String[] findResources = cp.findResources("", regExpResourceFilter);

		setCustomClassLoader(options);

		AnalysisSession session = new AnalysisSession(cp);

		List<String> filtered = getNamesOfClassesToAnalyse(options, findResources);

		session.runAnalysis(filtered);

		StringBuilder output = new SessionResultsFormatter(options).format(session);
		System.out.println(output);

	}

	private static CommandLineOptions createOptionsFromArgs(String[] args) {
		CommandLineOptions options;
		try {
			options = new CommandLineOptions(args);
			return options;
		} catch (Throwable e) {
			System.out.println("Exiting...");
			System.exit(1);
		}
		return null; // impossible statement
	}

	private static void setCustomClassLoader(CommandLineOptions options) {
		String[] classPathUrls = options.classpath().split(":");

		List<URL> urlList = new ArrayList<URL>();

		for (String classPathUrl : classPathUrls) {
			try {
				URL toAdd = new File(classPathUrl).toURI().toURL();
				urlList.add(toAdd);
			} catch (MalformedURLException e) {
				System.err.printf("Classpath option %s is invalid.", classPathUrl);
			}
		}
		ClassLoader classLoader = new URLClassLoader(urlList.toArray(new URL[] {}));
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	private static List<String> getNamesOfClassesToAnalyse(CommandLineOptions options, String[] findResources) {
		List<String> filtered = new ArrayList<String>();
		List<String> classNames = new ArrayList<String>();
		classNames.addAll(Arrays.asList(findResources));
		String matcher = options.match().replace(".", "\\.").replace("*", ".*"); 
		for (String className : classNames) {

			String dottedClassName = className.replace(".class", "").replace("/", ".");
			if (Pattern.matches(matcher, dottedClassName)) {
				filtered.add(className);
			}
		}
		return classNames;
	}

}
