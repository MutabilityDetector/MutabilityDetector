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
