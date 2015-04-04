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
