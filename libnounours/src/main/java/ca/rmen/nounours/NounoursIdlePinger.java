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
package ca.rmen.nounours;

/**
 * This thread pings nounours every second.
 *
 * @author Carmen Alvarez
 *
 */
class NounoursIdlePinger implements Runnable {

    private Nounours nounours = null;
    private long pingInterval = 5000;
    private boolean doPing = true;

    /**
     * @param nounours The Nounours to ping periodically.
     */
    public NounoursIdlePinger(final Nounours nounours) {
        this.nounours = nounours;
        pingInterval = Long.parseLong(nounours.getProperty(Nounours.PROP_IDLE_PING_INTERVAL));
    }

    public void setDoPing(final boolean doPing) {
        this.doPing = doPing;
    }

    /**
     * Ping nounours periodically.
     */
    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            if (doPing) {
                nounours.ping();
            }
            try

            {
                Thread.sleep(pingInterval);
            } catch (final InterruptedException e) {
                // Do nothing
            }
        }
    }
}
