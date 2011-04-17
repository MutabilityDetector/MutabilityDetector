/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.checkers.info;

import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.checkers.ISessionCheckerRunner;

public class AnalysisDatabase {

	public static final class InfoKey<C> {
		private final Class<C> clazz;

		private InfoKey(Class<C> clazz) {
			this.clazz = clazz;
		}
		
		Class<C> classForInfo() { return clazz; }
	}
	
	public static final InfoKey<PrivateMethodInvocationInformation> PRIVATE_METHOD_INVOCATION 
						= new InfoKey<PrivateMethodInvocationInformation>(PrivateMethodInvocationInformation.class);

	public static final InfoKey<TypeStructureInformation> TYPE_STRUCTURE
						= new InfoKey<TypeStructureInformation>(TypeStructureInformation.class);
	
	@SuppressWarnings("rawtypes") private Map infoMap = new HashMap();

	private final ISessionCheckerRunner sessionCheckerRunner;
	
	private AnalysisDatabase(ISessionCheckerRunner sessionCheckerRunner) {
		this.sessionCheckerRunner = sessionCheckerRunner;
	}
	
	public static AnalysisDatabase newAnalysisDatabase(ISessionCheckerRunner sessionCheckerRunner) {
		return new AnalysisDatabase(sessionCheckerRunner);
	}
	
	@SuppressWarnings("unchecked") public <I extends AnalysisInformation> I requestInformation(InfoKey<I> infoCategory) {
		if(infoMap.containsKey(infoCategory)) {
			return (I) infoMap.get(infoCategory);
		} else {
			return createInfoForCategory(infoCategory);
		}
	}

	private <I> InfoKeyException newException(InfoKey<I> infoCategory) {
		return new InfoKeyException("Programming error in instantiating information class for "
				+ infoCategory.classForInfo().getName());
	}

	@SuppressWarnings("unchecked") 
	private <I extends AnalysisInformation> I createInfoForCategory(InfoKey<I> infoCategory) {
		I info = null;
		if(infoCategory == PRIVATE_METHOD_INVOCATION) {
			info = (I) new PrivateMethodInvocationInformation(sessionCheckerRunner);
		} else if(infoCategory == TYPE_STRUCTURE) {
			info = (I) new TypeStructureInformation(sessionCheckerRunner);
		}
		
		if(info == null) {
			throw newException(infoCategory);
		} else {
			infoMap.put(infoCategory, info);
			return info;
		}
	}
}
