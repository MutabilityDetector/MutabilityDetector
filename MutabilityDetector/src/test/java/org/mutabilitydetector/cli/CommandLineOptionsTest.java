/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.mutabilitydetector.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Test;
import org.mutabilitydetector.cli.CommandLineOptions.ReportMode;

public class CommandLineOptionsTest {

    private BatchAnalysisOptions options;
    private File classListFile;

    private final PrintStream errorStream = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            // suppress output in tests
        }
    });

    @Test
    public void canParseClasspathOption() throws Exception {
        String[] args = makeArgs("-classpath", "fakeClasspath");
        assertEquals("fakeClasspath", createOptions(args).classpath());
    }

    @Test
    public void toAnalyseRegexCanBeSpecified() throws Exception {
        String[] args = makeArgs("-match", "*.somepackage.*");
        options = createOptions(args);
        assertEquals("*.somepackage.*", options.match());
    }

    @Test
    public void verboseOptionCanBeSetWithShortOpt() throws Exception {
        String[] args = makeArgs("-v");
        options = createOptions(args);
        assertEquals(true, options.verbose());
    }

    @Test
    public void verboseOptionCanBeSetWithLongOpt() throws Exception {
        String[] args = makeArgs("-verbose");
        options = createOptions(args);
        assertEquals(true, options.verbose());
    }

    @Test
    public void reportModeCanBeSetToAll() throws Exception {
        String[] args = makeArgs("-report", "all");
        options = createOptions(args);
        assertEquals(ReportMode.ALL, options.reportMode());
    }

    @Test
    public void immutableAndMutableReportModesAvailable() throws Exception {
        assertModeAvailable("immutable", ReportMode.IMMUTABLE);
        assertModeAvailable("mutable", ReportMode.MUTABLE);
    }

    @Test
    public void testReportClassesOptionStoresFile() throws Exception {
        classListFile = new File("someFileName.txt");
        assertTrue(classListFile.createNewFile());

        String[] args = makeArgs("-classlist", "someFileName.txt");

        options = createOptions(args);
        assertEquals(classListFile, options.classListFile());
    }

    @Test(expected = CommandLineOptionsException.class)
    public void throwsExceptionIfClassListFileIsInvalid() throws Exception {
        options = createOptions("-classlist", "");
    }

    @Test
    public void isUsingClassList() throws Exception {
        options = createOptions("-cp", ".");
        assertFalse("Should not be using class list.", options.isUsingClassList());

        classListFile = new File("someFileName.txt");
        assertTrue(classListFile.createNewFile());
        options = createOptions("-classlist", "someFileName.txt");
        assertTrue("Should be using class list.", options.isUsingClassList());
    }

    @Test
    public void shouldReportErrors() throws Exception {
        options = createOptions("-cp", ".");
        assertFalse("By default, errors should not be shown.", options.reportErrors());

        options = createOptions("-cp", ".", "-e");
        assertTrue("With the '-e' flag, errors should be shown.", options.reportErrors());
    }
    
    @Test
    public void canSpecifyFailFastOption() throws Exception {
        options = createOptions("-cp", ".");
        assertFalse("By default, analysis should not fail fast.", options.failFast());

        options = createOptions("-cp", ".", "-failFast");
        assertTrue(options.failFast());
    }

    @After
    public void tearDown() {
        removeTestFile();
    }

    private BatchAnalysisOptions createOptions(String... args) {
        return new CommandLineOptions(errorStream, args);
    }

    private String[] makeArgs(String... args) {
        return args;
    }

    private void assertModeAvailable(String reportArg, ReportMode reportMode) {
        String[] args = makeArgs("-r", reportArg);
        options = createOptions(args);
        assertEquals(reportMode, options.reportMode());
    }

    private void removeTestFile() {
        if (classListFile != null) assertTrue(classListFile.delete());
    }

}
