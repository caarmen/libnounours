/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.data;

/**
 * Stores the image id and relative frame length, of an image within an
 * animation.
 *
 * @author Carmen Alvarez
 *
 */
public class AnimationImage {

    private String imageId = null;
    private float duration = -1;

    public AnimationImage(String imageId, float duration) {
        this.imageId = imageId;
        this.duration = duration;
    }

    /**
     * Get the imageId.
     *
     * @return the imageId
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * Get the duration.
     *
     * @return the duration
     */
    public float getDuration() {
        return duration;
    }

    public String toString() {
        return imageId + "(" + duration + ")";
    }

}
