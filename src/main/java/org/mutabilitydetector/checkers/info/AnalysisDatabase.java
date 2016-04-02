package org.mutabilitydetector.checkers.info;

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


import java.util.HashMap;
import java.util.Map;

public final class AnalysisDatabase {

    public static final class InfoKey<C> {
        private final Class<C> clazz;

        private InfoKey(Class<C> clazz) {
            this.clazz = clazz;
        }

        Class<C> classForInfo() {
            return clazz;
        }
    }

    public static final InfoKey<PrivateMethodInvocationInformation> PRIVATE_METHOD_INVOCATION = new InfoKey<PrivateMethodInvocationInformation>(PrivateMethodInvocationInformation.class);

    public static final InfoKey<TypeStructureInformation> TYPE_STRUCTURE = new InfoKey<TypeStructureInformation>(TypeStructureInformation.class);

    public static final InfoKey<LineNumberInformation> LINE_NUMBERS = new InfoKey<LineNumberInformation>(LineNumberInformation.class);

    @SuppressWarnings("rawtypes")
    private final Map infoMap = new HashMap();

    private final InformationRetrievalRunner sessionCheckerRunner;

    private AnalysisDatabase(InformationRetrievalRunner sessionCheckerRunner) {
        this.sessionCheckerRunner = sessionCheckerRunner;
    }

    public static AnalysisDatabase newAnalysisDatabase(InformationRetrievalRunner sessionCheckerRunner) {
        return new AnalysisDatabase(sessionCheckerRunner);
    }

    @SuppressWarnings("unchecked")
    public <I extends AnalysisInformation> I requestInformation(InfoKey<I> infoCategory) {
        return (infoMap.containsKey(infoCategory))
                ? (I) infoMap.get(infoCategory)
                : createInfoForCategory(infoCategory);
    }

    private <I> InfoKeyException newException(InfoKey<I> infoCategory) {
        return new InfoKeyException("Programming error in instantiating information class for " + infoCategory.classForInfo().getName());
    }

    @SuppressWarnings("unchecked")
    private <I extends AnalysisInformation> I createInfoForCategory(InfoKey<I> infoCategory) {
        I info = null;
        if (infoCategory == PRIVATE_METHOD_INVOCATION) {
            info = (I) new PrivateMethodInvocationInformation(sessionCheckerRunner);
        } else if (infoCategory == TYPE_STRUCTURE) {
            info = (I) new TypeStructureInformation(sessionCheckerRunner);
        } else if (infoCategory == LINE_NUMBERS) {
            info = (I) new LineNumberInformation(sessionCheckerRunner);
        }

        if (info == null) {
            throw newException(infoCategory);
        }

        infoMap.put(infoCategory, info);
        return info;
    }
}
