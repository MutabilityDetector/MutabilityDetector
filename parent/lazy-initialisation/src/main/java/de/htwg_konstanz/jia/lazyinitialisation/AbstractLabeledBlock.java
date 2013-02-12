package de.htwg_konstanz.jia.lazyinitialisation;

import java.util.Iterator;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 12.02.2013
 */
abstract class AbstractLabeledBlock implements LabeledBlock {

    /* (Kein Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<LabeledBlock> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (Kein Javadoc)
     * @see de.htwg_konstanz.jia.lazyinitialisation.LabeledBlock#getPositionNumber()
     */
    @Override
    public int getPositionNumber() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (Kein Javadoc)
     * @see de.htwg_konstanz.jia.lazyinitialisation.LabeledBlock#getLabelNode()
     */
    @Override
    public LabelNode getLabelNode() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (Kein Javadoc)
     * @see de.htwg_konstanz.jia.lazyinitialisation.LabeledBlock#getAffectedVariable()
     */
    @Override
    public FieldNode getAffectedVariable() {
        // TODO Auto-generated method stub
        return null;
    }

}
