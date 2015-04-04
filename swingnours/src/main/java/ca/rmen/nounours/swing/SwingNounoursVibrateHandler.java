/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.swing;

import ca.rmen.nounours.NounoursVibrateHandler;

/**
 *
 * @author Carmen Alvarez
 *
 */
public class SwingNounoursVibrateHandler implements NounoursVibrateHandler {

    /**
     * This implementation just prints "vibrate" to the console.
     *
     * @see ca.rmen.nounours.Nounours#doVibrate(long)
     */
    @Override
    public void doVibrate(final long duration) {
        Trace.debug(this,"Will vibrate for " + duration + " milliseconds");
        Runnable doVibrate = new Runnable() {
            public void run() {
                for (int i = 0; i < duration / 500; i++) {
                    Trace.debug(this,"Vibrating");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        // Do nothing
                    }
                }

            }
        };
        new Thread(doVibrate).start();

    }

    /**
     * This implementation just prints "vibrate" to the console.
     *
     * @see ca.rmen.nounours.Nounours#doVibrate(long, long)
     */
    @Override
    public void doVibrate(long duration, long interval) {
        doVibrate(duration);

    }


}
