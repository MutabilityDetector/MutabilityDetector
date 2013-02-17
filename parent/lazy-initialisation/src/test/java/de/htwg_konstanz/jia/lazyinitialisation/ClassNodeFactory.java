package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/**
 * 
 *
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 17.02.2013
 */
@Immutable
final class ClassNodeFactory {

    private static final class InstanceHolder {
        private static final ClassNodeFactory INSTANCE = new ClassNodeFactory();
    }

    private ClassNodeFactory() {
        super();
    }

    /**
     * @return the sole instance of this class.
     */
    public static ClassNodeFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public ConvenienceClassNode convenienceClassNodeFor(final Class<?> klasse) {
        final ClassNode classNodeToWrap = classNodeFor(notNull(klasse));
        return ConvenienceClassNode.newInstance(classNodeToWrap);
    }

    public ClassNode classNodeFor(final Class<?> klasse) {
        final ClassName dotted = Dotted.fromClass(notNull(klasse));
        final ClassReader cr = tryToCreateClassReaderFor(dotted.asString());
        final ClassNode result = new ClassNode();
        cr.accept(result, 0);
        return result;
    }

    private static ClassReader tryToCreateClassReaderFor(final String dottedClassName) {
        try {
            return new ClassReader(dottedClassName);
        } catch (final IOException e) {
            final String msg = String.format("Unable to create ClassReader for '%s'.", dottedClassName);
            throw new IllegalStateException(msg, e);
        }
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        return builder.toString();
    }

}
