package org.mutabilitydetector.checkers.util;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.mutabilitydetector.checkers.MutabilityAnalysisException;
import org.mutabilitydetector.checkers.info.TypeInformationRetriever;
import org.mutabilitydetector.locations.Dotted;

import static java.lang.String.format;
import static org.mutabilitydetector.checkers.AccessModifierQuery.type;
import static org.mutabilitydetector.locations.Dotted.fromSlashedString;

public class TypeStructureInformationAnalyser extends TypeInformationRetriever {

    private final Dotted className;
    private Boolean isAbstract;
    private boolean isInterface;

    private TypeStructureInformationAnalyser(Dotted className) {
        this.className = className;
    }

    public static TypeStructureInformationAnalyser newAnalyser(Dotted className) {
        return new TypeStructureInformationAnalyser(className);
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public boolean isInterface() {
        return isInterface;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        checkIsVisitingCorrectClass();

        storeIsAbstract(access);
        storeIsInterface(access);
    }

    private void checkIsVisitingCorrectClass() {
        Dotted expectToVisit = fromSlashedString(ownerClass);
        if (!expectToVisit.equals(className)) {
            String message = format("Programming error: Expected to visit [%s], but am visiting [%s].",
                    className,
                    expectToVisit);
            throw new MutabilityAnalysisException(message);
        }
    }

    private void storeIsAbstract(int access) {
        isAbstract = type(access).isAbstract();
    }

    private void storeIsInterface(int access) {
        isInterface = type(access).isInterface();
    }

}
