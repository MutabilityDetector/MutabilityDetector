/**
 * 
 */package org.mutabilitydetector.checkers.settermethod;

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



/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
public final class NullAssignmentInsn implements AssignmentInsn {

    private static final class InstanceHolder {
        private static final NullAssignmentInsn INSTANCE = new NullAssignmentInsn();
    }

    private NullAssignmentInsn() {
        super();
    }

    public static AssignmentInsn getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public int getIndexWithinMethod() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String getNameOfAssignedVariable() {
        return "";
    }

    @Override
    public ControlFlowBlock getSurroundingControlFlowBlock() {
        return null;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" []");
        return b.toString();
    }

}
