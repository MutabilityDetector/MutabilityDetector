package org.mutabilitydetector.benchmarks.circular;

import org.mutabilitydetector.benchmarks.circular.MimicAwtCircularDependencies.Component.NativeInLightFixer;

@SuppressWarnings("unused")
public class MimicAwtCircularDependencies {
    private final Container container;

    public MimicAwtCircularDependencies(Container container) {
        this.container = container;
    }

    public static class Container {
        Container parent;
        NativeInLightFixer nativeInLightFixer;

        public Container(Container parent, NativeInLightFixer nativeInLightFixer) {
            this.parent = parent;
            this.nativeInLightFixer = nativeInLightFixer;
        }
    }

    public static class Component {
        NativeInLightFixer nativeInLightFixer = new NativeInLightFixer();

        class NativeInLightFixer {
            Container container = new Container(null, null);
        }
    }
}