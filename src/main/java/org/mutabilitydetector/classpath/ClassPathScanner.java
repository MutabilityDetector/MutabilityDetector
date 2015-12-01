package org.mutabilitydetector.classpath;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;

public class ClassPathScanner {

    private static ImmutableSet<ClassPath.ClassInfo> scan(ClassLoader classLoader) throws Exception {
        ImmutableSet<ClassPath.ClassInfo> classesOnThisClasspath;

        try {
            ClassPath classPath = ClassPath.from(classLoader);
            classesOnThisClasspath = classPath.getAllClasses();
        } catch (Exception e) {
            throw e;
        }

        return classesOnThisClasspath;
    }

    public static ArrayList<String> findImmutableClasses(ClassLoader classLoader) throws Exception {
        ArrayList<String> foundClasses = new ArrayList<String>();
        ImmutableSet<ClassPath.ClassInfo> classesOnThisClasspath = scan(classLoader);

        for (ClassPath.ClassInfo classInfo : classesOnThisClasspath) {
            Class<?> foundClass = classInfo.load();
            if (foundClass.isAnnotationPresent(Immutable.class))
                foundClasses.add(foundClass.toGenericString());
        }

        return foundClasses;
    }
}