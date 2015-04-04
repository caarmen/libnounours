/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
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
     * @param enableSound
     */
    public void setEnableSound(boolean enableSound);

    /**
     * Play a sound on the device. The sound should be played in a separate
     * thread.
     *
     * @param soundId
     */
    public void playSound(String soundId);

    /**
     * Stop playing a sound on the device, if one is playing.
     */
    public void stopSound();

}
