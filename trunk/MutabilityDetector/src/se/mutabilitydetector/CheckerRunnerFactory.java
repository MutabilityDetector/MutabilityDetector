package se.mutabilitydetector;

import com.google.classpath.ClassPath;

public class CheckerRunnerFactory implements ICheckerRunnerFactory {

	private final ClassPath classpath;

	public CheckerRunnerFactory(ClassPath classpath) {
		this.classpath = classpath;
	}
	
	@Override
	public CheckerRunner createRunner() {
		return new CheckerRunner(classpath);
	}

}
