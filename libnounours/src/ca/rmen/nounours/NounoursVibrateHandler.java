/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours;

/**
 * Implementing classes should implement logic for vibrating the device.
 *
 * @author Carmen Alvarez
 *
 */
public interface NounoursVibrateHandler {
    /**
     * The subclass must implement logic to vibrate the device for the given
     * length of time. The vibration should be done in a separate thread.
     *
     * @param duration
     *            the time in milliseconds the device should vibrate
     */
    public abstract void doVibrate(long duration);

    /**
     * The subclass must implement logic to vibrate the device in pulses for the
     * given length of time. The vibration should be done in a separate thread.
     *
     * @param duration
     *            the time in milliseconds the device should vibrate
     * @param interval
     *            length of vibration pulses
     */
    public abstract void doVibrate(long duration, long interval);

}
