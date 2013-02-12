/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 12.02.2013
 */
interface LabeledBlock extends Iterable<LabeledBlock> {

    public int getPositionNumber();

    public LabelNode getLabelNode();

    public FieldNode getAffectedVariable();

}
