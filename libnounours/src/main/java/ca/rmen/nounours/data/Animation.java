/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.data;

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
public class Animation {
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

    public void addImage(final String imageId, float duration) {
        AnimationImage image = new AnimationImage(imageId, duration);
        images.add(image);
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
            dup.addImage(image.getImageId(), image.getDuration());
        }
        return dup;
    }
}
