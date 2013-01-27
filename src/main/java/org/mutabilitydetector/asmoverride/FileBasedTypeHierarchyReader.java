/*
 *    Copyright (c) 2008-2013 Graham Allan
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
package org.mutabilitydetector.asmoverride;

import static org.mutabilitydetector.locations.Dotted.dotted;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.TypeHierarchyReader;

import com.google.common.io.InputSupplier;

public class FileBasedTypeHierarchyReader extends TypeHierarchyReader {

    private final Map<Dotted, InputSupplier<InputStream>> classFiles;

    public FileBasedTypeHierarchyReader(Map<Dotted, InputSupplier<InputStream>> classFiles) {
        this.classFiles = classFiles;
    }
    
    @Override
    protected ClassReader reader(Type t) throws IOException {
        InputStream is = classFiles.get(dotted(t.getClassName())).getInput();
        return new ClassReader(is);
    }
    
}
