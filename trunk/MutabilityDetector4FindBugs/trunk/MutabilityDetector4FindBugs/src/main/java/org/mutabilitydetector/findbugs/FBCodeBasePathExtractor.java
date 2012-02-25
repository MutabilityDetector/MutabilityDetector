package org.mutabilitydetector.findbugs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.umd.cs.findbugs.classfile.IClassPath;
import edu.umd.cs.findbugs.classfile.ICodeBase;

public class FBCodeBasePathExtractor {

    public List<String> listOfCodeBasePaths(IClassPath findBugsClassPath) throws InterruptedException {
        List<String> codeBasePaths = new ArrayList<String>();
        pathsFromCodeBase(codeBasePaths, findBugsClassPath.appCodeBaseIterator());
        pathsFromCodeBase(codeBasePaths, findBugsClassPath.auxCodeBaseIterator());
        return codeBasePaths;
    }
    

    private static void pathsFromCodeBase(List<String> codeBasePaths, Iterator<? extends ICodeBase> codeBaseIterator) {
        while (codeBaseIterator.hasNext()) {
            ICodeBase codeBase = codeBaseIterator.next();
            String pathName = codeBase.getPathName();
            
            if (pathName != null) {
                codeBasePaths.add(pathName);
            }
        }
    }
}
