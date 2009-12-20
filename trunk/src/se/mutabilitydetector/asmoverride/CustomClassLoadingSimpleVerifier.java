package se.mutabilitydetector.asmoverride;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.SimpleVerifier;

public class CustomClassLoadingSimpleVerifier extends SimpleVerifier {

	@Override
	protected Class<?> getClass(Type t) {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			if (t.getSort() == Type.ARRAY) {
				return Class.forName(t.getDescriptor().replace('/', '.'), false, loader);
			}
			return Class.forName(t.getClassName(), false, loader);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.toString());
		}
	}
}
