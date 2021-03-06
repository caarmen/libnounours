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

import ca.rmen.nounours.Util;
import ca.rmen.nounours.io.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Theme {

    private static final String PROP_SHAKE_ANIMATION = "animation.shake";
    private static final String PROP_RESUME_ANIMATION = "animation.resume";
    private static final String PROP_IDLE_ANIMATION = "animation.idle";
    private static final String PROP_END_IDLE_ANIMATION = "animation.idle.end";
    private static final String PROP_HELP_IMAGE = "help.image";
    private static final String PROP_DEFAULT_IMAGE = "default.image";
    private static final String PROP_HEIGHT = "resolution.height";
    private static final String PROP_WIDTH = "resolution.width";

    private Map<String, Image> images = new HashMap<String, Image>();
    private Map<String, Animation> animations = new HashMap<String, Animation>();
    private Map<String, Sound> sounds = new HashMap<String, Sound>();
    private List<FlingAnimation> flingAnimations = new ArrayList<FlingAnimation>();
    private Animation shakeAnimation = null;
    private Animation resumeAnimation = null;
    private Animation idleAnimation = null;
    private Animation endIdleAnimation = null;
    private Image helpImage = null;
    private Image defaultImage = null;
    private final String id;
    private final String name;
    private final URI location;
    private int height;
    private int width;

    private boolean isLoaded = false;
    private Properties themeProperties;

    public Theme(String id, String name, URI location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getId() {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getName() {
        return name;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public String toString() {
        return id + "," + name + "," + location;
    }

    public void init(StreamLoader streamLoader) throws URISyntaxException, IOException {
        InputStream propertiesFile = streamLoader.open(new URI(location.toString() + File.separator + "nounours.properties"));
        InputStream imagesFile = streamLoader.open(new URI(location.toString() + File.separator + "image.csv"));
        InputStream featureFile = streamLoader.open(new URI(location.toString() + File.separator + "feature.csv"));
        InputStream imageFeatureFile = streamLoader.open(new URI(location.toString() + File.separator + "imagefeatureassoc.csv"));
        InputStream adjacentImageFile = streamLoader.open(new URI(location.toString() + File.separator + "adjacentimage.csv"));
        InputStream animationFile = streamLoader.open(new URI(location.toString() + File.separator + "animation.csv"));
        InputStream flingAnimationFile = streamLoader.open(new URI(location.toString() + File.separator + "flinganimation.csv"));
        InputStream soundFile = streamLoader.open(new URI(location.toString() + File.separator + "sound.csv"));
        init(propertiesFile, imagesFile, featureFile, imageFeatureFile, adjacentImageFile, animationFile,
                flingAnimationFile, soundFile);
        isLoaded = true;

    }

    private void init(InputStream propertiesFile, InputStream imageFile, InputStream featureFile,
            InputStream imageFeatureFile, InputStream adjacentImageFile, InputStream animationFile,
            InputStream flingAnimationFile, InputStream soundFile) throws IOException {
        // Read theme properties
        themeProperties = new Properties();
        themeProperties.load(propertiesFile);
        String shakeAnimationId = themeProperties.getProperty(PROP_SHAKE_ANIMATION);
        String resumeAnimationId = themeProperties.getProperty(PROP_RESUME_ANIMATION);
        String idleAnimationId = themeProperties.getProperty(PROP_IDLE_ANIMATION);
        String endIdleAnimationId = themeProperties.getProperty(PROP_END_IDLE_ANIMATION);
        String helpImageId = themeProperties.getProperty(PROP_HELP_IMAGE);
        String defaultImageId = themeProperties.getProperty(PROP_DEFAULT_IMAGE);
        height = (int) Util.getLongProperty(themeProperties, PROP_HEIGHT, 455);
        width = (int) Util.getLongProperty(themeProperties, PROP_WIDTH, 320);

        // Load the list of features
        FeatureReader featureReader = new FeatureReader(featureFile);

        // Load the list of images
        ImageReader imageReader = new ImageReader(imageFile);
        images = imageReader.getImages();

        // Load the list of sounds.
        SoundReader soundReader = new SoundReader(soundFile);
        sounds = soundReader.getSounds();

        // Load the list of image - feature associations
        new ImageFeatureReader(images, featureReader.getFeatures(), imageFeatureFile);

        // Load the list of image transitions
        new AdjacentImageReader(images, adjacentImageFile);

        // Load the list of animations
        AnimationReader animationReader = new AnimationReader(images, animationFile);
        animations = animationReader.getAnimations();

        // Identify the "special" animations
        for (Animation animation : animations.values()) {
            if (shakeAnimationId != null && animation.getId().equals(shakeAnimationId))
                shakeAnimation = animation;
            if (resumeAnimationId != null && animation.getId().equals(resumeAnimationId))
                resumeAnimation = animation;
            if (idleAnimationId != null && animation.getId().equals(idleAnimationId))
                idleAnimation = animation;
            if (endIdleAnimationId != null && animation.getId().equals(endIdleAnimationId))
                endIdleAnimation = animation;
        }

        // Load the list of fling animations.
        FlingAnimationReader flingAnimationReader = new FlingAnimationReader(flingAnimationFile);
        flingAnimations = flingAnimationReader.getFlingAnimations();

        for (Image image : images.values()) {
            if (image.getId().equals(defaultImageId)) {
                defaultImage = image;
            } else if (image.getId().equals(helpImageId))
                helpImage = image;
        }

    }

    /**
     * @param propertyName
     *            the name of the property to retrieve
     * @return the value of a property, either specified in the
     *         nounours.properties file, or the default value.
     */
    public String getProperty(String propertyName) {
        return themeProperties.getProperty(propertyName);
    }

    /**
     * Get the images.
     * 
     * @return the images
     */
    public Map<String, Image> getImages() {
        return images;
    }

    /**
     * Get the animations.
     * 
     * @return the animations
     */
    public Map<String, Animation> getAnimations() {
        return animations;
    }

    /**
     * Get the sounds.
     * 
     * @return the sounds
     */
    public Map<String, Sound> getSounds() {
        return sounds;
    }

    /**
     * Get the flingAnimations.
     * 
     * @return the flingAnimations
     */
    public List<FlingAnimation> getFlingAnimations() {
        return flingAnimations;
    }

    /**
     * Get the shakeAnimation.
     * 
     * @return the shakeAnimation
     */
    public Animation getShakeAnimation() {
        return shakeAnimation;
    }

    /**
     * Get the resumeAnimation.
     * 
     * @return the resumeAnimation
     */
    public Animation getResumeAnimation() {
        return resumeAnimation;
    }

    /**
     * Get the idleAnimation.
     * 
     * @return the idleAnimation
     */
    public Animation getIdleAnimation() {
        return idleAnimation;
    }

    /**
     * Get the endIdleAnimation.
     * 
     * @return the endIdleAnimation
     */
    public Animation getEndIdleAnimation() {
        return endIdleAnimation;
    }

    /**
     * Get the helpImage.
     * 
     * @return the helpImage
     */
    public Image getHelpImage() {
        return helpImage;
    }

    /**
     * Get the defaultImage.
     * 
     * @return the defaultImage
     */
    public Image getDefaultImage() {
        return defaultImage;
    }

}
