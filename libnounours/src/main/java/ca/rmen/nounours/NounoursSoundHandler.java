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
 * Implementing classes implement logic for playing sounds on the device.
 *
 * @author Carmen Alvarez
 *
 */
public interface NounoursSoundHandler {

    /**
     * Mute or unmute sound on the device.
     *
     * @param enableSound if true, sounds will be played.  Sounds will be muted otherwise.
     */
    public void setEnableSound(boolean enableSound);

    /**
     * Play a sound on the device. The sound should be played in a separate
     * thread.
     *
     * @param soundId the id of the sound to play.
     */
    public void playSound(String soundId);

    /**
     * Stop playing a sound on the device, if one is playing.
     */
    public void stopSound();

}
