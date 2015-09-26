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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ca.rmen.nounours.Util;
import ca.rmen.nounours.io.AdjacentImageReader;
import ca.rmen.nounours.io.AnimationReader;
import ca.rmen.nounours.io.FeatureReader;
import ca.rmen.nounours.io.FlingAnimationReader;
import ca.rmen.nounours.io.ImageFeatureReader;
import ca.rmen.nounours.io.ImageReader;
import ca.rmen.nounours.io.SoundReader;
import ca.rmen.nounours.io.ThemeUpdateListener;

public class Theme {

    public static final String PROP_SHAKE_ANIMATION = "animation.shake";
    public static final String PROP_RESUME_ANIMATION = "animation.resume";
    public static final String PROP_IDLE_ANIMATION = "animation.idle";
    public static final String PROP_END_IDLE_ANIMATION = "animation.idle.end";
    public static final String PROP_HELP_IMAGE = "help.image";
    public static final String PROP_DEFAULT_IMAGE = "default.image";
    public static final String PROP_HEIGHT = "resolution.height";
    public static final String PROP_WIDTH = "resolution.width";

    private Map<String, Image> images = new HashMap<String, Image>();
    private Map<String, Animation> animations = new HashMap<String, Animation>();
    private Map<String, Sound> sounds = new HashMap<String, Sound>();
    private List<FlingAnimation> flingAnimations = new ArrayList<FlingAnimation>();
    private Animation shakeAnimation = null;
    private Animation resumeAnimation = null;
    private Animation idleAnimation = null;
    private Animation endIdleAnimation = null;
    private Properties properties = null;
    private Image helpImage = null;
    private Image defaultImage = null;
    private final String id;
    private final String name;
    private URL location;
    private int height;
    private int width;

    private boolean isLoaded = false;

    public Theme(String id, String name, URL location) {
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

    public String getName() {
        return name;
    }

    public URL getLocation() {
        return location;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public String toString() {
        return id + "," + name + "," + location;
    }

    public boolean update(String downloadedFilesDir, ThemeUpdateListener listener) throws IllegalStateException,
            IOException, URISyntaxException {
        boolean updateOK = true;
        File themeDir = new File(downloadedFilesDir + File.separator + id);
        String[] simpleFiles = new String[] { "nounours.properties", "feature.csv", "imagefeatureassoc.csv",
                "adjacentimage.csv", "animation.csv", "flinganimation.csv", "orientationimage.csv" };
        int numberOfFiles = simpleFiles.length + images.size() + sounds.size() + 2;
        int fileNumber = 1;
        for (String fileName : simpleFiles) {
            boolean fileUpdatedOk = updateFile(themeDir, fileName);
            listener.updatedFile(fileName, fileNumber++, numberOfFiles, fileUpdatedOk);
            updateOK = updateOK && fileUpdatedOk;

        }
        InputStream imageFile = getFileInputStream(themeDir, "image.csv", true);
        listener.updatedFile("image.csv", fileNumber++, numberOfFiles, imageFile != null);
        ImageReader imageReader = new ImageReader(imageFile);
        Map<String, Image> newImages = imageReader.getImages();
        for (Image image : newImages.values()) {
            boolean fileUpdatedOk = updateFile(themeDir, image.getFilename());
            listener.updatedFile(image.getFilename(), fileNumber++, numberOfFiles, fileUpdatedOk);
            updateOK = updateOK && fileUpdatedOk;
        }
        InputStream soundFile = getFileInputStream(themeDir, "sound.csv", true);
        listener.updatedFile("sound.csv", fileNumber++, numberOfFiles, soundFile != null);
        SoundReader soundReader = new SoundReader(soundFile);
        Map<String, Sound> newSounds = soundReader.getSounds();
        for (Sound sound : newSounds.values()) {
            boolean fileUpdatedOk = updateFile(themeDir, sound.getFilename());
            listener.updatedFile(sound.getFilename(), fileNumber++, numberOfFiles, fileUpdatedOk);
            updateOK = updateOK && fileUpdatedOk;
        }
        return updateOK;
    }

    public void init(String downloadedFilesDir, boolean forceDownload) throws URISyntaxException, IOException {
        File themeDir = new File(downloadedFilesDir + File.separator + id);
        if (!themeDir.exists())
            themeDir.mkdirs();
        InputStream propertiesFile = getFileInputStream(themeDir, "nounours.properties", forceDownload);
        InputStream imagesFile = getFileInputStream(themeDir, "image.csv", forceDownload);
        InputStream featureFile = getFileInputStream(themeDir, "feature.csv", forceDownload);
        InputStream imageFeatureFile = getFileInputStream(themeDir, "imagefeatureassoc.csv", forceDownload);
        InputStream adjacentImageFile = getFileInputStream(themeDir, "adjacentimage.csv", forceDownload);
        InputStream animationFile = getFileInputStream(themeDir, "animation.csv", forceDownload);
        InputStream flingAnimationFile = getFileInputStream(themeDir, "flinganimation.csv", forceDownload);
        InputStream soundFile = getFileInputStream(themeDir, "sound.csv", forceDownload);
        init(propertiesFile, imagesFile, featureFile, imageFeatureFile, adjacentImageFile, animationFile,
                flingAnimationFile, soundFile);
        isLoaded = true;

    }

    public void init(InputStream propertiesFile, InputStream imageFile, InputStream featureFile,
            InputStream imageFeatureFile, InputStream adjacentImageFile, InputStream animationFile,
            InputStream flingAnimationFile, InputStream soundFile) throws IOException {
        // Read theme properties
        properties = new Properties();
        properties.load(propertiesFile);
        String shakeAnimationId = properties.getProperty(PROP_SHAKE_ANIMATION);
        String resumeAnimationId = properties.getProperty(PROP_RESUME_ANIMATION);
        String idleAnimationId = properties.getProperty(PROP_IDLE_ANIMATION);
        String endIdleAnimationId = properties.getProperty(PROP_END_IDLE_ANIMATION);
        String helpImageId = properties.getProperty(PROP_HELP_IMAGE);
        String defaultImageId = properties.getProperty(PROP_DEFAULT_IMAGE);
        height = (int) Util.getLongProperty(properties, PROP_HEIGHT, 455);
        width = (int) Util.getLongProperty(properties, PROP_WIDTH, 320);

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
        AnimationReader animationReader = new AnimationReader(animationFile);
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
     * Get the properties.
     * 
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
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

    private boolean updateFile(File themeDir, String fileName) throws IllegalStateException,
            URISyntaxException {
        File file = new File(themeDir, fileName);
        if (!Util.downloadFile(new URI(location + "/" + fileName), file)) {
            System.out.println("Could not download file " + fileName + " when updating " + this);
            return false;
        }
        return true;

    }

    private InputStream getFileInputStream(File themeDir, String fileName, boolean forceDownload)
            throws IllegalStateException, IOException, URISyntaxException {

        if (location.getProtocol().toLowerCase().startsWith("jar")) {
            String fullPath = location.toString() + fileName;
            String filePath = fullPath.substring(fullPath.indexOf("!") + 1);
            return getClass().getResourceAsStream(filePath);
        }
        File file = new File(themeDir, fileName);
        if (!file.exists() || forceDownload) {
            Util.downloadFile(new URI(location + "/" + fileName), file);
        }
        if (!file.exists())
            return null;
        return new FileInputStream(file);
    }

}
