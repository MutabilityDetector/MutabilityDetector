package org.mutabilitydetector.misc;

import javax.annotation.concurrent.Immutable;

import java.lang.management.ManagementFactory;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

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

@Immutable
public class TimingUtil {
    
    private Clock jvmStartTimeClock;
    private Clock currentTimeClock;
    
    public TimingUtil() {
        long jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        jvmStartTimeClock = Clock.fixed(Instant.ofEpochMilli(jvmStartTime), ZoneId.systemDefault());
        currentTimeClock = Clock.systemDefaultZone();
    }

    public long getCurrentTimeMillis() {
        return currentTimeClock.millis();
    }
    
    public long getVMStartTimeMillis() {
        return jvmStartTimeClock.millis();
    }

}
