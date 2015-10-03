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
package ca.rmen.nounours.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a sequence of Images. The images are to be displayed at
 * a given interval, and the sequence may be repeated a given number of times.
 *
 * @author Carmen Alvarez
 *
 */
public class Animation implements Serializable {
    private final List<AnimationImage> images = new ArrayList<AnimationImage>();
    private String id = null;
    private String label = null;
    private int interval = -1;
    private int repeat = 0;
    private boolean visible = false;
    private boolean vibrate = false;
    private String soundId = null;

    /**
     * @param id
     *            an identifier for the animation
     * @param label
     *            the label which will be displayed in the UI for this
     *            animation.
     * @param interval
     *            the time in milliseconds each image should be displayed.
     * @param repeat
     *            the number of times to display the sequence (1 minimum).
     * @param visible
     *            if true, this animation will appear in the animations menu
     * @param vibrate
     *            if true, the device will vibrate during the animation
     * @param soundId
     *            the id of the sound to play during the animation, if any.
     */
    public Animation(final String id, final String label, final int interval, final int repeat, final boolean visible,
            final boolean vibrate, final String soundId) {
        this.id = id;
        this.label = label;
        this.interval = interval;
        this.repeat = repeat;
        this.visible = visible;
        this.vibrate = vibrate;
        this.soundId = soundId;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getRepeat() {
        return repeat;
    }

    public String getLabel() {
        return label;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(final int interval) {
        this.interval = interval;
    }

    public void addImage(final Image image, float duration) {
        AnimationImage animationImage = new AnimationImage(image, duration);
        images.add(animationImage);
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public String getSoundId() {
        return soundId;
    }

    /**
     * @return in order the sequence of images to display.
     */
    public List<AnimationImage> getImages() {
        return Collections.unmodifiableList(images);
    }

    @Override
    public String toString() {
        return id + ": " + interval + "ms " + repeat + " times: " + images;
    }

    /**
     * @return the total duration of the animation, in milliseconds
     */
    public long getDuration() {
        int duration = 0;
        for (AnimationImage image : images) {
            duration += image.getDuration()*interval;
        }
        return duration * repeat;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        final Animation dup = new Animation(id, label, interval, repeat, visible, vibrate, soundId);
        for (final AnimationImage image : images) {
            dup.addImage(image.getImage(), image.getDuration());
        }
        return dup;
    }
}
