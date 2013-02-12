package de.htwg_konstanz.jia.lazyinitialisation;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 12.02.2013
 */
final class NullBlock extends AbstractLabeledBlock {

    private static final class InstanceHolder {
        private static final NullBlock INSTANCE = new NullBlock();
    }

    public static NullBlock getInstance() {
        return InstanceHolder.INSTANCE;
    }

}
