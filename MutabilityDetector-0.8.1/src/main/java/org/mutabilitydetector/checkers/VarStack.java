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

package org.mutabilitydetector.checkers;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VarStack {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private List<Integer> indices = new ArrayList<Integer>();
    private List<VarStackSnapshot> snapshots = new ArrayList<VarStackSnapshot>();
    private Iterator<VarStackSnapshot> snapshotsIterator = snapshots.iterator();

    public void visitVarInsn(int var) {
        logger.debug("Parameter var: {}.", var);
        indices.add(var);
    }

    public void takeSnapshotOfVarsAtPutfield() {
        snapshots.add(new VarStackSnapshot(indices));
        snapshotsIterator = snapshots.iterator();
        logger.debug("snapshots: {}.", getSnapshotsAsString());
    }

    private String getSnapshotsAsString() {
        final StringBuilder sb = new StringBuilder();
        final String separator = ",";
        String sep = "";
        for (final VarStackSnapshot snapshot : snapshots) {
            sb.append(snapshot.indices).append(sep);
            sep = separator;
        }
        return sb.toString();
    }

    public VarStackSnapshot next() {
        final VarStackSnapshot result = snapshotsIterator.next();
        logger.debug("result.indices: {}.", result.indices);
        return result;
    }

    public static class VarStackSnapshot {

        private final List<Integer> indices;

        public VarStackSnapshot(List<Integer> indices) {
            this.indices = unmodifiableList(new ArrayList<Integer>(indices));
        }

        public boolean thisObjectWasAddedToStack() {
            // the "this" reference is at position 0 of the local variable table
            return indices.contains(0);
        }

        public int indexOfOwningObject() {
            int stackSpaceToLookBack = 1;
            return indices.get((indices.size() - 1) - stackSpaceToLookBack);
        }

    }

}
