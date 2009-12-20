package se.mutabilitydetector.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.mutabilitydetector.cli.CommandLineOptions.ReportMode;


public class CommandLineOptionsTest {

	private CommandLineOptions options;

	@Test
	public void testOptionsCanParseClasspathOption() throws Exception {
		String[] args = makeArgs("-classpath", "fakeClasspath");
		CommandLineOptions options = new CommandLineOptions(args);
		assertEquals("fakeClasspath", options.classpath());
	}
	
	@Test
	public void testDefaultWhenNoClasspathIsSpecifedIsDot() throws Exception {
		String[] args = makeArgs();
		options = new CommandLineOptions(args);
		assertEquals(".", options.classpath());
	}
	
	@Test
	public void testToAnalyseRegexCanBeSpecified() throws Exception {
		String[] args = makeArgs("-match", "*.somepackage.*");
		options = new CommandLineOptions(args);
		assertEquals("*.somepackage.*", options.match());
	}
	
	@Test
	public void testVerboseOptionCanBeSetWithShortOpt() throws Exception {
		String[] args = makeArgs("-v");
		options = new CommandLineOptions(args);
		assertEquals(true, options.verbose());
	}
	
	@Test
	public void testVerboseOptionCanBeSetWithLongOpt() throws Exception {
		String[] args = makeArgs("-verbose");
		options = new CommandLineOptions(args);
		assertEquals(true, options.verbose());
	}
	
	@Test
	public void testReportModeCanBeSetToAll() throws Exception {
		String[] args = makeArgs("-report", "all");
		options = new CommandLineOptions(args);
		assertEquals(ReportMode.ALL, options.reportMode());
	}
	
	@Test
	public void testReportModes() throws Exception {
		assertModeAvailable("immutable", ReportMode.IMMUTABLE);
		assertModeAvailable("mutable", ReportMode.MUTABLE);
	}
	
	private void assertModeAvailable(String reportArg, ReportMode reportMode) {
		String[] args = makeArgs("-r", reportArg);
		options = new CommandLineOptions(args);
		assertEquals(reportMode, options.reportMode());
	}

	@Test(expected=RuntimeException.class)
	public void testHelpWillTerminateWithRuntimeException() throws Exception {
		options = new CommandLineOptions(makeArgs("-help"));
	}

	private String[] makeArgs(String... args) {
		return args;
	}
	
}
