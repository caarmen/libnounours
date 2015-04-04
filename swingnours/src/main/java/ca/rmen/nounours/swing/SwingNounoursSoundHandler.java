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

import java.io.File;
import java.io.FileInputStream;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

import ca.rmen.nounours.Nounours;
import ca.rmen.nounours.NounoursSoundHandler;
import ca.rmen.nounours.data.Sound;

/**
 *
 * @author Carmen Alvarez
 *
 */
public class SwingNounoursSoundHandler implements NounoursSoundHandler, MetaEventListener {

    private Nounours nounours = null;
    private Sequencer sequencer = null;

    SourceDataLine auline = null;

    public SwingNounoursSoundHandler(Nounours nounours) {
        this.nounours = nounours;
        try {
            sequencer = MidiSystem.getSequencer(false);
        } catch (MidiUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sequencer.addMetaEventListener(this);

    }

    /**
     * Play the given sound
     *
     * @see ca.rmen.nounours.Nounours#playSound(java.lang.String)
     */
    public void playSound(String soundId) {
        Trace.debug(this,"playSound " + soundId);
        Sound sound = nounours.getSound(soundId);
        if (sound.getFilename().toLowerCase().endsWith("mid")) {
            playMidi(sound);
        } else if (sound.getFilename().toLowerCase().endsWith("wav")) {
            playWav(sound);
        }
    }

    /**
     * Play a midi sound.
     *
     * @param sound
     */
    private void playMidi(Sound sound) {
        try {
            // Create a midi sequence from the sound file.
            Sequence sequence = MidiSystem.getSequence(new FileInputStream(sound.getFilename()));
            Trace.debug(this,"Midi has " + sequence.getTracks().length + " tracks");

            // Try to open the sequencer
            sequencer.open();
            if (!sequencer.isOpen()) {
                Trace.debug(this,"Problem opening sequencer");
                return;
            }
            // Try to play the file.
            sequencer.setSequence(sequence);
            sequencer.start();
            Trace.debug(this,"playing " + sound.getId());

        } catch (Exception e) {
            Trace.debug(this,e);
            e.printStackTrace();
        }
    }

    /**
     * Play a wav file in a separate thread.
     *
     * @param sound
     */
    private void playWav(final Sound sound) {
        Runnable wavRunnable = new Runnable() {
            public void run() {
                try {

                    // Read the wav file into an audio stream.
                    File file = new File(sound.getFilename());
                    AudioInputStream soundStream = AudioSystem.getAudioInputStream(file);
                    // Don't really know what the following lines do -- copied
                    // them from somewhere :)
                    javax.sound.sampled.AudioFormat format = soundStream.getFormat();
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                    auline = (SourceDataLine) AudioSystem.getLine(info);
                    auline.open(format);
                    if (auline.isControlSupported(FloatControl.Type.PAN)) {
                        FloatControl pan = (FloatControl) auline.getControl(FloatControl.Type.PAN);
                        pan.setValue(-1.0f);
                    }

                    // Start playing the audio.
                    auline.start();
                    // Play the audio in blocks.
                    int nBytesRead = 0;
                    byte[] abData = new byte[524288];
                    while (nBytesRead != -1) {
                        nBytesRead = soundStream.read(abData, 0, abData.length);
                        if (nBytesRead >= 0)
                            auline.write(abData, 0, nBytesRead);
                    }
                    // Cleanup after the wav is done playing.
                    auline.drain();
                    auline.close();
                    auline = null;
                } catch (Exception e) {
                    Trace.debug(this,e);
                    e.printStackTrace();
                }
            }
        };
        new Thread(wavRunnable).start();
    }

    /**
     * Stop playback of sound, if sound is playing
     */
    public void stopSound() {
        Trace.debug(this,"stop sound");
        // Close the MidiDevice & free resources
        if (sequencer.isRunning())
            sequencer.stop();
        if (sequencer.isOpen())
            sequencer.close();
        // Stop any wav playing.
        if (auline != null) {
            auline.stop();
            auline.close();
        }

    }

    /**
     * Called when the end of the midi file is reached. Free up midi resources.
     *
     * @see javax.sound.midi.MetaEventListener#meta(javax.sound.midi.MetaMessage)
     */
    @Override
    public void meta(MetaMessage meta) {
        Trace.debug(this,"MetaMessage: type = " + meta.getType() + ", status = " + meta.getStatus() + "," + meta.getStatus());
        if (meta.getType() == 47) {
            Trace.debug(this,"Finished playing sound");
            if (sequencer.isRunning())
                stopSound();
        }
    }
    /**
     * Mute or unmute the sound. Note that this will only have effect for the
     * current sequence which is playing!
     *
     * @see ca.rmen.nounours.Nounours#setEnableSoundImpl(boolean)
     */
    public void setEnableSound(boolean enableSound) {
        Sequence sequence = sequencer.getSequence();
        if(sequence == null)
            return;
        Track[] tracks = sequence.getTracks();
        for (int i = 0; i < tracks.length; i++) {
            sequencer.setTrackMute(i, !enableSound);
        }
    }

}
