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

/**
 * This class contains information about a given feature in the context of a
 * given image. For now, only the position of the feature in the image is
 * stored.
 *
 * @author Carmen Alvarez
 *
 */
public class ImageFeature {
    private final String featureId;
    private final String imageId;
    private final int x;
    private final int y;

    /**
     * @param imageId
     *            an image in which the given feature is found
     * @param featureId
     *            a feature which appears in the given image
     * @param x
     *            the x-position of the feature in the image
     * @param y
     *            the y-position of the feature in the image
     */
    public ImageFeature(final String imageId, final String featureId, final int x, final int y) {
        this.featureId = featureId;
        this.imageId = imageId;
        this.x = x;
        this.y = y;
    }

    public String getFeatureId() {
        return featureId;
    }

    public String getImageId() {
        return imageId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return imageId + "," + featureId + "," + x + "," + y;
    }
}
