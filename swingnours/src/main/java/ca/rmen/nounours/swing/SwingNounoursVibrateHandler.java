/*
 * Copyright (c) 2009 Carmen Alvarez.
 *
 * This file is part of Nounours.
 *
 * Nounours is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Nounours is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Nounours.  If not, see <http://www.gnu.org/licenses/>.
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
