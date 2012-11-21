package de.htwg_konstanz.jia.testsubjects.escape;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class StartingBenignThread {

    private static final class HelloPrinter implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println("Hello!");
                tryToSleep();
            }
        }

        private void tryToSleep() {
            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public StartingBenignThread() {
        final Thread thread = new Thread(new HelloPrinter());
        thread.start();
    }

}
