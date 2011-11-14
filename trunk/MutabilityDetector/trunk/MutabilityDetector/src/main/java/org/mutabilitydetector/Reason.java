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

package org.mutabilitydetector;


/**
 * 
 * Provides an abstraction for the reason a class can be mutable. It is expected that checkers will provide a single
 * public instance of this class to act as a 'key' for the mutability reason. Other checkers can then use the reasons
 * already associated with a class to help their decision.
 * 
 * @author Graham Allan / Grundlefleck at gmail dot com
 * 
 */
public interface Reason {

    String description();

    String code();

    IsImmutable createsResult();
    
    public boolean isOneOf(Reason... reasons);

}
