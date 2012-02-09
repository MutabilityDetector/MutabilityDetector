/*
 * FindBugs4JUnit. Copyright (c) 2011 youDevise, Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
*/

package org.mutabilitydetector.findbugs;

import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.IsImmutable;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ba.ClassContext;

public class MutabilityDetector implements Detector {

	private static final int PRIORITY_TO_REPORT = Priorities.NORMAL_PRIORITY;
	
	private final BugReporter bugReporter;

    private final Detector pluginToRegisterBugsWith;

    private final IAnalysisSession analysisSession;

	public MutabilityDetector(Detector pluginToRegisterBugsWith, BugReporter bugReporter, IAnalysisSession analysissession) {
		this.pluginToRegisterBugsWith = pluginToRegisterBugsWith;
        this.bugReporter = bugReporter;
        this.analysisSession = analysissession;
	}

	public void visitClassContext(ClassContext classContext) {
		AnalysisResult result = analysisSession.resultFor(classContext.getClassDescriptor().getDottedClassName());
		
		if (result.isImmutable != IsImmutable.IMMUTABLE) {
		    BugInstance bugInstance = new BugInstance(pluginToRegisterBugsWith, "JCIP_IS_NOT_IMMUTABLE", PRIORITY_TO_REPORT)
		                                    .addClass(classContext.getClassDescriptor());
            bugReporter.reportBug(bugInstance);
            
		}
	}
	
	public void report() { }
	
}