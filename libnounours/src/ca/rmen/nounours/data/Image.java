/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a picture of nounours. In a given picture, a set of
 * features are present. In a given picture, if one feature is moved, the
 * display may transition to another ("adjacent") picture.
 * 
 * @author Carmen Alvarez
 * 
 */
public class Image {
    private final String id;
    private String filename;
    private final Map<String, ImageFeature> featureToPosition = new HashMap<String, ImageFeature>();
    private final Set<Feature> features = new HashSet<Feature>();
    private final Map<String, Set<Image>> adjacentImages = new HashMap<String, Set<Image>>();
    private String onReleaseImageId = null;

    /**
     * @param id
     *            an identifier for the image
     * @param filename
     *            the location of the image file on disk.
     */
    public Image(final String id, final String filename) {
        this.id = id;
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Declare the position of a given feature in this image.
     * 
     * @param feature
     *            the feature
     * @param x
     *            the x-position of the feature in the context of this image.
     * @param y
     *            the y-position of the feature in the context of this image.
     */
    public void addFeature(final Feature feature, final int x, final int y) {
        features.add(feature);
        final ImageFeature imageFeature = new ImageFeature(id, feature.getId(), x, y);
        featureToPosition.put(feature.getId(), imageFeature);
    }

    /**
     * 
     * @return the list of features in this image.
     */
    public Set<Feature> getFeatures() {
        return Collections.unmodifiableSet(features);
    }

    /**
     * 
     * @param featureId
     * @return the information about the given feature in the context of this
     *         image.
     */
    public ImageFeature getImageFeature(final String featureId) {
        return featureToPosition.get(featureId);
    }

    /**
     * Add a transition from this image to another image, if the given feature
     * is moved.
     * 
     * @param featureId
     *            the feature allowing us to transition to the other image
     * @param image
     *            the other image to which we may transition, if the given
     *            feature is moved.
     */
    public void addAdjacentImage(final String featureId, final Image image) {
        // Get or create the list of adjacent images to which we may transition
        // if the given feature moves.
        Set<Image> images = adjacentImages.get(featureId);
        if (images == null) {
            images = new HashSet<Image>();
            adjacentImages.put(featureId, images);
        }
        // Add the transition if we don't have it already.
        if (!images.contains(image)) {
            images.add(image);
        }

        // Add the transition in the other direction (from the given image to
        // this one).
        final Set<Image> reverseAdjacentImages = image.getAdjacentImages(featureId);
        if (!reverseAdjacentImages.contains(this)) {
            image.addAdjacentImage(featureId, this);
        }
    }

    /**
     * Get the list of possible images to which we may transition, if the given
     * feature moves.
     * 
     * @param featureId
     * @return
     */
    public Set<Image> getAdjacentImages(final String featureId) {
        Set<Image> result = adjacentImages.get(featureId);
        if (result == null) {
            result = new HashSet<Image>();
        }
        return result;
    }

    public List<Image> getAllAdjacentImages() {
        List<Image> result = new ArrayList<Image>();
        for (Set<Image> images : adjacentImages.values()) {
            result.addAll(images);
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * @param onReleaseImageId
     *            The image which should be displayed when the user releases the
     *            finger/mouse from the current image.
     */
    public void setOnReleaseImageId(final String onReleaseImageId) {
        this.onReleaseImageId = onReleaseImageId;
    }

    /**
     * @return The image which should be displayed when the user releases the
     *         finger/mouse from the current image.
     */
    public String getOnReleaseImageId() {
        return onReleaseImageId;
    }

    @Override
    public String toString() {
        return id + "," + filename;
    }
}
