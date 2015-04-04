/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours;

/**
 * This thread pings nounours every second.
 *
 * @author Carmen Alvarez
 *
 */
public class NounoursIdlePinger implements Runnable {

    private Nounours nounours = null;
    private long pingInterval = 5000;
    private boolean doPing = true;

    /**
     * @param nounours
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
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
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
