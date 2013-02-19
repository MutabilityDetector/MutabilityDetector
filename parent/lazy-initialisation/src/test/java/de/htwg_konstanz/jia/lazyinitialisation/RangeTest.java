package de.htwg_konstanz.jia.lazyinitialisation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Test;

import de.htwg_konstanz.jia.lazyinitialisation.Range.RangeBuilder;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 19.02.2013
 */
public final class RangeTest {

    private Range range = null;

    @After
    public void nullifyRange() {
        range = null;
    }
    
    @Test
    public void coherentRange() {
        initialiseRangeWith(0, 1, 2, 3, 4);
        assertRangeDoesNotCover(-1);
        assertRangeCovers(0);
        assertRangeCovers(1);
        assertRangeCovers(2);
        assertRangeCovers(3);
        assertRangeCovers(4);
        assertRangeDoesNotCover(5);
    }

    private void initialiseRangeWith(final int first, final int ... further) {
        final SortedSet<Integer> items = new TreeSet<Integer>();
        items.add(Integer.valueOf(first));
        for (final int f : further) {
            items.add(Integer.valueOf(f));
        }
        range = Range.newInstance(items);
    }

    private void assertRangeDoesNotCover(final int notCovered) {
        assertFalse(range.covers(notCovered));
    }
    
    private void assertRangeCovers(final int covered) {
        assertTrue(range.covers(covered));
    }

    @Test
    public void incoherentRage() {
        initialiseRangeWith(0, 2, 4, 6, 8);
        assertRangeDoesNotCover(-1);
        assertRangeCovers(0);
        assertRangeDoesNotCover(1);
        assertRangeCovers(2);
        assertRangeDoesNotCover(3);
        assertRangeCovers(4);
        assertRangeDoesNotCover(5);
        assertRangeCovers(6);
        assertRangeDoesNotCover(7);
        assertRangeCovers(8);
        assertRangeDoesNotCover(9);
    }

    @Test
    public void coherentRangeWithRangeBuilder() {
        final RangeBuilder b = new RangeBuilder();
        b.add(0).add(1).add(2).add(3).add(4);
        range = b.build();
        assertRangeDoesNotCover(-1);
        assertRangeCovers(0);
        assertRangeCovers(1);
        assertRangeCovers(2);
        assertRangeCovers(3);
        assertRangeCovers(4);
        assertRangeDoesNotCover(5);
    }

    @Test
    public void incoherentRangeWithRangeBuilder() {
        final RangeBuilder b = new RangeBuilder();
        b.add(0).add(2).add(4).add(6).add(8);
        range = b.build();
        assertRangeDoesNotCover(-1);
        assertRangeCovers(0);
        assertRangeDoesNotCover(1);
        assertRangeCovers(2);
        assertRangeDoesNotCover(3);
        assertRangeCovers(4);
        assertRangeDoesNotCover(5);
        assertRangeCovers(6);
        assertRangeDoesNotCover(7);
        assertRangeCovers(8);
        assertRangeDoesNotCover(9);
    }

}
