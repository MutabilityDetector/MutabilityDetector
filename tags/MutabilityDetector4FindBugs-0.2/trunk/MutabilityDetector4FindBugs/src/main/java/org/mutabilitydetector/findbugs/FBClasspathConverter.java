package org.mutabilitydetector.findbugs;

import java.io.File;
import java.util.List;

import org.mutabilitydetector.repackaged.com.google.classpath.ClassPath;
import org.mutabilitydetector.repackaged.com.google.classpath.ClassPathFactory;

public class FBClasspathConverter {

    
    public ClassPath createClassPathForCodeBases(List<String> codeBasePaths) {
        StringBuilder allClassPathsInString = new StringBuilder();
        for (String classPathUrl : codeBasePaths) {
            allClassPathsInString.append(classPathUrl + File.pathSeparator);
        }
        
        return new ClassPathFactory().createFromPath(allClassPathsInString.toString());
    }

}
