package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

/**
 * This class provides test cases for API learning tests of (some) contents of
 * the Java package {@code java.util.concurrent.atomic}.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 18.12.2012
 */
public final class AtomicTest {

    private interface Countable {
        int get();
        int incrementAndGet();
    }

    private static final class AtomicCountable implements Countable {
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public int incrementAndGet() {
            return counter.incrementAndGet();
        }

        @Override
        public int get() {
            return counter.get();
        }

    }

    private static final class DumbCountable implements Countable {
        private int counter = 0;

        @Override
        public int incrementAndGet() {
            final int result = ++counter;
            return result;
        }

        @Override
        public int get() {
            return counter;
        }

    }

    private static final class LockedCountable implements Countable {
        private final Lock lock = new ReentrantLock();
        private int counter = 0;
        
        @Override
        public int incrementAndGet() {
            lock.lock();
            try {
                return ++counter;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int get() {
            return counter;
        }
        
    }
    
    private static final class Counter implements Runnable {

        private final Countable countable;

        public Counter(final Countable countable) {
            this.countable = countable;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                countable.incrementAndGet();
            }
        }

    }

    @Test
    public void atomicMultithreadedIncrementing() throws InterruptedException {
        final byte numberOfThreads = 100;
        for (int x = 0; x < numberOfThreads; x++) {
            final ExecutorService threadPool = Executors.newFixedThreadPool(10);
            final Countable countable = new AtomicCountable();
            final Counter counter = new Counter(countable);
            for (int i = 0; i < numberOfThreads; i++) {
                threadPool.execute(counter);
            }
            threadPool.shutdown();
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
            assertThat(countable.get(), is(1000));
        }
    }

    @Test
    public void atomicLongForDoubleValue() {
        final double doubleValue = 23.42D;
        final AtomicLong atomicLong = new AtomicLong(Double.doubleToRawLongBits(doubleValue));
        assertThat(Double.longBitsToDouble(atomicLong.get()), is(doubleValue));
    }

}
