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

/**
 * Stores the image id and relative frame length, of an image within an
 * animation.
 *
 * @author Carmen Alvarez
 *
 */
public class AnimationImage implements Serializable {

    private Image image = null;
    private float duration = -1;

    public AnimationImage(Image image, float duration) {
        this.image = image;
        this.duration = duration;
    }

    /**
     * Get the image
     *
     * @return the image
     */
    public Image getImage() {
        return image;
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
        return image + "(" + duration + ")";
    }

}
