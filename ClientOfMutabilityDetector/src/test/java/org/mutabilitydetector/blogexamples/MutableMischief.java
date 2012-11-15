package org.mutabilitydetector.blogexamples;

public class MutableMischief extends Immutable {

    @Override
    public String getLabel() {
        return "Return value keeps changing: " + (System.currentTimeMillis() % 10);
    }

    public static void main(String args[]) throws Exception {
        MutableMischief mutObj = new MutableMischief();
        ImmutableClient immutClient = new ImmutableClient(mutObj);
        System.out.println(immutClient.printImmutableLabel());
        Thread.sleep(1000);
        System.out.println(immutClient.printImmutableLabel());
    }
}

class Immutable {
    private String label = "Original Label";

    public String getLabel() {
        return label;
    }
}

class ImmutableClient {

    Immutable immutObj;

    ImmutableClient(Immutable immut) {
        this.immutObj = immut;
    }

    public String printImmutableLabel() {
        return immutObj.getLabel();
    }

}