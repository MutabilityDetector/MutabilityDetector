package de.htwg_konstanz.jia.lazyinitialisation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 18.02.2013
 */
@Immutable
final class Range {

    /**
     * Builder for creating instances of
     * {@link de.htwg_konstanz.jia.lazyinitialisation.Range}.
     */
    @NotThreadSafe
    public static final class RangeBuilder {
        private final SortedSet<Integer> items;

        public RangeBuilder() {
            items = new TreeSet<Integer>();
        }

        public RangeBuilder add(final int item) {
            items.add(Integer.valueOf(item));
            return this;
        }

        public Range build() {
            return Range.newInstance(items);
        }
    } // class RangeBuilder


    /** Value of this range's lower boundary. */
    public final int lowerBoundary;

    /** Value of this range's upper boundary. */
    public final int upperBoundary;

    /** All items of this range. */
    public final List<Integer> allItems;

    private Range(final int theLowerBoundary, final int theUpperBoundary, final List<Integer> allItems) {
        lowerBoundary = theLowerBoundary;
        upperBoundary = theUpperBoundary;
        this.allItems = Collections.unmodifiableList(allItems);
    }

    public static Range newInstance(final SortedSet<Integer> allItems) {
        final List<Integer> allItemsList = new ArrayList<Integer>(allItems.size());
        for (final Integer item : new TreeSet<Integer>(allItems)) {
            allItemsList.add(item);
        }
        final Integer lowerBoundary = allItemsList.isEmpty() ? -1 : allItemsList.get(0);
        final Integer upperBoundary = allItemsList.isEmpty() ? -1 : allItemsList.get(allItemsList.size() - 1);
        return new Range(lowerBoundary, upperBoundary, allItemsList);
    }

    public boolean covers(final int index) {
        return allItems.contains(Integer.valueOf(index));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + allItems.hashCode();
        result = prime * result + lowerBoundary;
        result = prime * result + upperBoundary;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Range)) {
            return false;
        }
        final Range other = (Range) obj;
        if (!allItems.equals(other.allItems)) {
            return false;
        }
        if (lowerBoundary != other.lowerBoundary) {
            return false;
        }
        if (upperBoundary != other.upperBoundary) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Range[");
        builder.append("lowerBoundary=").append(lowerBoundary).append(", upperBoundary=").append(upperBoundary);
        builder.append(", allItems=").append(allItems).append("]");
        return builder.toString();
    }

}
