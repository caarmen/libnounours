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
package ca.rmen.nounours;

import ca.rmen.nounours.data.*;
import ca.rmen.nounours.io.StreamLoader;
import ca.rmen.nounours.io.ThemeReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

/**
 * This class contains the logic for initializing the nounours (reading the CSV
 * data files), handling mouse/touch actions (click/press, move, release),
 * handling animations. Subclasses must implement the UI-specificities (i.e.:
 * registering as mouselisteners for swing, or handling touch events on
 * android).
 * 
 * @author Carmen Alvarez
 * 
 */
public abstract class Nounours {
    static final String PROP_IDLE_PING_INTERVAL = "idle.ping.interval";

    private static final String PROP_DROP_VIBRATE_DURATION = "drop.vibrate.duration";
    private static final String PROP_VIBRATE_INTERVAL = "vibrate.interval";
    private static final String PROP_IDLE_TIME = "idle.time";
    private static final String PROP_FLING_FACTOR = "fling.factor";
    private static final String PROP_FLING_PRECISION = "fling.precision";
    private static final String PROP_MIN_SHAKE_SPEED = "shake.factor";

    public static final String DEFAULT_THEME_ID = "0";

    private Random random = null;
    private boolean isShaking = false;
    private Image curImage = null;
    private Theme curTheme = null;
    private boolean loaded = false;
    private boolean isLoading = true;
    private Feature curFeature = null;
    private Animation curAnimation = null;
    private float flingFactor = 1f;
    private int flingPrecision = 25;
    private long dropVibrateDuration = 100;
    private long vibrateInterval = 100;
    private long idleTimeout = 60000;
    private long pingInterval = 5000;
    private long lastActionTimestamp = -1;

    private Theme defaultTheme = null;
    private Map<String, Theme> themes = null;

    private boolean enableSound = true;
    private boolean enableVibrate = true;
    private NounoursIdlePinger pinger = null;

    private NounoursSoundHandler soundHandler = null;
    private NounoursAnimationHandler animationHandler = null;
    private NounoursVibrateHandler vibrateHandler = null;
    private final NounoursRecorder nounoursRecorder = new NounoursRecorder();
    private StreamLoader streamLoader;

    private Properties nounoursProperties;

    // Begin abstract methods

    protected abstract int getDeviceWidth();

    protected abstract int getDeviceHeight();
    
    /**
     * The subclass must display the current image to the device.
     * @param image the image to display.
     */
    protected abstract void displayImage(Image image);

    /**
     * Subclasses should implement a way to run a task in the background.
     * 
     * @param task the task to run in the background.
     */
    protected abstract void runTask(Runnable task);

    @SuppressWarnings("WeakerAccess")
    public boolean isLoading() {
        return isLoading;
    }

    // End abstract methods.

    /**
     * Display an animation. This method stops any currently running animation.
     * This method invokes the doVibrate and playSound methods implemented by
     * the subclass, before displaying the image animation.
     * @param animation the animation to play
     * @param isDynamicAnimation if true, this animation was generated at runtime, and is not part of the preset list of animations.
     */
    @SuppressWarnings("WeakerAccess")
    public void doAnimation(Animation animation, boolean isDynamicAnimation) {
        if (isLoading)
            return;
        // If an animation is running, stop it..
        curAnimation = getCurrentAnimation();
        if (animationHandler.isAnimationRunning()) {
            stopAnimation();
        }
        curAnimation = animation;
        // Vibrate if necessary
        if (animation.isVibrate() && enableVibrate)
            vibrateHandler.doVibrate(animation.getDuration(), getVibrateInterval());
        // Play a sound if necessary
        if (animation.getSoundId() != null)
            soundHandler.playSound(animation.getSoundId());
        // Reset the idle counter
        if (!isDynamicAnimation)
            resetIdle();
        // Launch the image animation.
        if (nounoursRecorder.isRecording()) nounoursRecorder.addImages(animation);
        animationHandler.doAnimation(animation, isDynamicAnimation);
    }

    public void doAnimation(Animation animation) {
        doAnimation(animation, false);
    }

    /**
     * Stop the current animation, if one is running.
     */
    @SuppressWarnings("WeakerAccess")
    public void stopAnimation() {
        animationHandler.stopAnimation();
        soundHandler.stopSound();
        curAnimation = null;
    }

    /**
     * @return true if an animation is currently being displayed.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isAnimationRunning() {
        return animationHandler.isAnimationRunning();
    }

    /**
     * @return the current animation, if one is running. Otherwise return null.
     */
    private Animation getCurrentAnimation() {
        if (!animationHandler.isAnimationRunning())
            // Reset the current animation to null, if it is not running
            // anymore.
            curAnimation = null;
        return curAnimation;
    }

    Animation createRandomAnimation() {
        if (!loaded)
            return null;
        int interval = 100 + random.nextInt(400);
        int repeat = 1;// 1 + random.nextInt(3);
        int numberFrames = 2 + random.nextInt(8);

        Animation result = new Animation("random" + System.currentTimeMillis(), "random", interval, repeat, false,
                false, null);
        Image curAnimationImage = curImage;
        for (int i = 0; i < numberFrames; i++) {
            if (curAnimationImage == null)
                continue;
            float duration = 0.5f + random.nextFloat() * 2.0f;
            result.addImage(curAnimationImage, duration);
            curAnimationImage = getRandomImage(curAnimationImage);
        }
        return result;
    }

    @SuppressWarnings("UnusedDeclaration")
    public NounoursRecorder getNounoursRecorder() {
        return nounoursRecorder;
    }


    private Image getRandomImage(Image fromImage) {
        List<Image> allAdjacentImages = fromImage.getAllAdjacentImages();
        if (allAdjacentImages.size() == 0)
            return null;
        int toImageNumber = random.nextInt(allAdjacentImages.size());
        return allAdjacentImages.get(toImageNumber);
    }

    /**
     * Reads the CSV files containing the image, feature, and animation data.
     * Starts the idle counter which will launch {{@link #onIdle()} after
     * PROP_IDLE_TIME milliseconds of inactivity. Displays the default image.
     *
     * @param streamLoader tells us how to open files.
     * @param pAnimationHandler responsible for displaying animations
     * @param pSoundHandler responsible for playing sounds
     * @param pVibrateHandler responsible for vibrating the device
     * @param nounoursPropertiesFile properties file specific to the given theme.
     * @param propertiesFile
     *            the nounours.properties file containing application-wide
     *            properties.
     * @param imageFile contains the list of images in the given theme.
     * @param themeFile contains the list of themes.
     * @param featureFile contains the list of features for the given theme.
     * @param imageFeatureFile identifies the position of each feature in each image.
     * @param adjacentImageFile identifies which features can move from one image to another.
     * @param animationFile defines the image sequences which make the animations.
     * @param flingAnimationFile defines which fling gestures trigger which animations.
     * @param soundFile associates sounds to animations
     * @param themeId the id of the initial theme to use.
     *            The default image is the first image displayed. The display
     *            should also be reset to the default image at the end of
     *            animations.
     * @throws IOException if any of the given files could not be read.
     */
    public void init(StreamLoader streamLoader, NounoursAnimationHandler pAnimationHandler, NounoursSoundHandler pSoundHandler,
            NounoursVibrateHandler pVibrateHandler, InputStream nounoursPropertiesFile, InputStream propertiesFile,
            InputStream imageFile, InputStream themeFile, InputStream featureFile, InputStream imageFeatureFile,
            InputStream adjacentImageFile, InputStream animationFile, InputStream flingAnimationFile,
            InputStream soundFile, String themeId) throws IOException {
        debug("init");

        this.streamLoader = streamLoader;
        random = new Random(System.currentTimeMillis());
        initHandlersAndThemes(pAnimationHandler, pSoundHandler, pVibrateHandler, nounoursPropertiesFile, themeFile);
        defaultTheme = new Theme(DEFAULT_THEME_ID, "Default", null);
        defaultTheme.init(propertiesFile, imageFile, featureFile, imageFeatureFile, adjacentImageFile, animationFile,
                flingAnimationFile, soundFile);

        if (themeId.equals(Nounours.DEFAULT_THEME_ID))
            curTheme = defaultTheme;
        else
            curTheme = themes.get(themeId);
        useTheme(themeId);
        resetIdle();
        debug("postInit");

        // Start the keep-alive
        pinger = new NounoursIdlePinger(this);
        new Thread(pinger).start();
    }

    @SuppressWarnings("UnusedDeclaration")
    public Theme getDefaultTheme() {
        return defaultTheme;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Theme getCurrentTheme() {
        return curTheme;
    }

    /**
     * 
     */
    private void initHandlersAndThemes(NounoursAnimationHandler pAnimationHandler, NounoursSoundHandler pSoundHandler,
            NounoursVibrateHandler pVibrateHandler, InputStream nounoursPropertiesFile, InputStream themeFile)
            throws IOException {
        this.animationHandler = pAnimationHandler;
        this.soundHandler = pSoundHandler;
        this.vibrateHandler = pVibrateHandler;
        // read application properties
        nounoursProperties = new Properties();
        nounoursProperties.load(nounoursPropertiesFile);
        flingFactor = Float.parseFloat(getProperty(PROP_FLING_FACTOR));
        dropVibrateDuration = Util.getLongProperty(nounoursProperties, PROP_DROP_VIBRATE_DURATION, dropVibrateDuration);
        vibrateInterval = Util.getLongProperty(nounoursProperties, PROP_VIBRATE_INTERVAL, vibrateInterval);
        idleTimeout = Util.getLongProperty(nounoursProperties, PROP_IDLE_TIME, idleTimeout);
        pingInterval = Util.getLongProperty(nounoursProperties, PROP_IDLE_PING_INTERVAL, pingInterval);
        flingPrecision = (int) Util.getLongProperty(nounoursProperties, PROP_FLING_PRECISION, flingPrecision);

        // try first to get remote themes.
        if (themes == null || themes.isEmpty()) {
            ThemeReader themeReader = new ThemeReader(themeFile);
            themes = themeReader.getThemes();

        }

    }

    protected abstract boolean cacheImages();

    /**
     * Use the given set of images
     * 
     * @param id the id of the theme to use.
     * @return true if the theme was successfully loaded.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean useTheme(String id) {
        debug("Use theme " + id);
        isLoading = true;
        try {
            // Do nothing if this is the current theme.
            if (curTheme != null && id.equals(curTheme.getId()) && loaded) {
                debug("Already using theme " + id);
                return true;
            }
            // Stop any currently running animation.
            stopAnimation();

            if (id.equals(DEFAULT_THEME_ID))
                curTheme = defaultTheme;
            else
                curTheme = themes.get(id);
            if (curTheme == null) {
                debug("Trying to use missing theme " + id);
                curTheme = defaultTheme;
                id = Nounours.DEFAULT_THEME_ID;
            }
            if (!curTheme.isLoaded() && !id.equals(DEFAULT_THEME_ID)) {

                try {
                    debug("init theme " + curTheme);
                    curTheme.init(streamLoader);
                } catch (Exception e) {
                    debug("Could not load theme " + curTheme + ": " + e);
                    debug(e);
                    curTheme = defaultTheme;
                    return false;
                }
            }
            // Identify the "special" animations
            for (Animation animation : curTheme.getAnimations().values()) {
                animationHandler.addAnimation(animation);
            }

            int size = curTheme.getImages().size();

            // Loading the default theme.
            if (id.equals(DEFAULT_THEME_ID)) {
                debug("loading the default theme");
                updatePreloadProgress(curTheme.getImages().size(), size);
            }
            // Loading a non-default theme.
            else {
                debug("Loading theme");
                URI themeLocation = curTheme.getLocation();
                try {
                    int i = 0;
                    debug("Loading images");
                    for (Image image : curTheme.getImages().values()) {
                        i++;
                        // Update the image data to point to the filenames of
                        // this set
                        if (themeLocation.getScheme().startsWith("jar")) {
                            if (!image.getFilename().startsWith("jar"))
                                image.setFilename(themeLocation + image.getFilename());
                        } else if (themeLocation.getScheme().equals("file")) {
                            if(!image.getFilename().startsWith("file"))
                                image.setFilename(themeLocation + File.separator + image.getFilename());
                        }
                        updatePreloadProgress(i, size);
                    }
                } catch (Exception e) {
                    debug("Could not use image set " + curTheme + ":  " + e);
                    debug(e);
                    return false;
                }
            }

            // Reload images.
            boolean cachedImages = cacheImages();
            if (!cachedImages)
                return false;
            Runnable resetImage = new Runnable() {
                public void run() {
                    reset();
                    // Redraw the current image in the new theme.
                    displayImage(curImage);

                }
            };
            runTask(resetImage);
            loaded = true;
            return true;
        } finally {
            isLoading = false;
        }
    }

    @SuppressWarnings({"WeakerAccess", "UnusedParameters", "EmptyMethod"})
    protected void updatePreloadProgress(int progress, int max) {
        // Do nothing
    }

    /*******************************************************************
     * SOUND
     ******************************************************************/
    // Begin sound-related methods
    /**
     * Mute or unmute the sound.
     * 
     * @param enableSound if true, sounds will be played, otherwise sounds will be muted.
     */
    public void setEnableSound(boolean enableSound) {
        this.enableSound = enableSound;
        soundHandler.setEnableSound(enableSound);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setEnableVibrate(boolean enableVibrate) {
        this.enableVibrate = enableVibrate;
    }

    /**
     * @return true if sound is not muted.
     */
    public boolean isSoundEnabled() {
        return enableSound;
    }

    public Sound getSound(String soundId) {
        return curTheme.getSounds().get(soundId);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void stopSound() {
        soundHandler.stopSound();
    }

    // End sound-related methods.

    /*******************************************************************
     * ANIMATION
     ******************************************************************/

    // Begin animation-related methods
    /**
     * @return a Map of animation id to Animation.
     */
    public Map<String, Animation> getAnimations() {
        return curTheme.getAnimations();
    }

    /**
     * Displays one of the possible animations.
     */
    public void doRandomAnimation() {
        debug("Random animation");
        int numAnimations = getAnimations().size();
        boolean createAnimation = random.nextBoolean();
        Animation randomAnimation = null;
        if (createAnimation)
            randomAnimation = createRandomAnimation();
        else {
            if (numAnimations == 0)
                return;
            int randomAnimationIdx = (int) (Math.random() * numAnimations);
            Iterator<String> it = getAnimations().keySet().iterator();
            String randomAnimationId = null;
            for (int i = 0; i <= randomAnimationIdx && it.hasNext(); i++) {
                randomAnimationId = it.next();
            }
            randomAnimation = getAnimations().get(randomAnimationId);
        }
        doAnimation(randomAnimation, createAnimation);
    }

    // End animation-related methods

    /*******************************************************************
     * MOTION
     ******************************************************************/

    // Begin motion-related methods
    /**
     * The subclass should call this during a mouse click or touch event.
     * 
     * @param x the x-position of the pointer
     * @param y the y-position of the pointer
     */
    public void onPress(int x, int y) {
        if (curTheme == null)
            return;
        int[] translatedPoints = Util.translate(x, y, getDeviceWidth(), getDeviceHeight(), curTheme.getWidth(),
                curTheme.getHeight());
        debug("onPress " + x + "," + y + "=>" + translatedPoints[0] + "," + translatedPoints[1]);
        if (curImage == null)
            return;
        boolean wasIdle = animationHandler.isAnimationRunning() && curAnimation != null
                && curAnimation == curTheme.getIdleAnimation();
        stopAnimation();
        soundHandler.stopSound();
        resetIdle();
        if (wasIdle && curTheme.getEndIdleAnimation() != null)
            doAnimation(curTheme.getEndIdleAnimation());
        else {
            // Find the closest feature to where the user clicked.
            curFeature = Util.getClosestFeature(curImage, translatedPoints[0], translatedPoints[1]);
            if (curFeature != null) {
                // Find all images we can transition to when this feature moves.
                Set<Image> adjacentImages = curImage.getAdjacentImages(curFeature.getId());
                // No transitions from this feature. Reset the image.
                if (adjacentImages.size() == 0)
                    curImage = curTheme.getDefaultImage();
            }
        }
    }

    /**
     * The subclass should call this during a mouse release event.
     */
    public void onRelease() {
        resetIdle();
        curFeature = null;
        debug("onRelease");
        if (curImage != null) {
            String nextImageId = curImage.getOnReleaseImageId();
            if (nextImageId != null) {
                Image nextImage = curTheme.getImages().get(nextImageId);
                setImage(nextImage);
                if (enableVibrate)
                    vibrateHandler.doVibrate(dropVibrateDuration);
                if (nounoursRecorder.isRecording()) {
                    nounoursRecorder.addImage(curImage);
                }
            }
        }
    }

    /**
     * The subclass should call this when the mouse/finger moves.
     * 
     * @param x the x-position of the pointer at the end of the move
     * @param y the y-position of the pointer at the end of the move
     */
    public void onMove(int x, int y) {
        resetIdle();
        boolean doRefresh = true;
        if (curTheme == null)
            return;
        int[] translatedPoints = Util.translate(x, y, getDeviceWidth(), getDeviceHeight(), curTheme.getWidth(),
                curTheme.getHeight());

        stopAnimation();

        // Assume we have already selected a feature during onPress
        if (curFeature != null) {
            // Find the image, among the images to which we may transition, in
            // which the feature is closest to the given
            // location.
            Image image = Util.getAdjacentImage(curImage, curFeature.getId(), translatedPoints[0], translatedPoints[1]);
            if (image != null) {
                // If the closest image is the current image, do nothing
                if (curImage != null && curImage.getId().equals(image.getId())) {
                    doRefresh = false;
                }
                curImage = image;

            }
            // If there are no possible images to transition to (not even the
            // current image), reset the image
            else {
                curImage = curTheme.getDefaultImage();

            }
        }
        // If the image has changed, refresh the screen.
        if (doRefresh) {
            displayImage(curImage);
            if (nounoursRecorder.isRecording()) {
                nounoursRecorder.addImage(curImage);
            }
        }
    }

    /**
     * The user shook the device or window.
     */
    public void onShake() {
        // Don't shake if already shaking
        if (isShaking) {
            debug("Already shaking!");
            return;
        }
        if (curTheme.getShakeAnimation() != null) {
            // Set the shaking flag
            isShaking = true;
            // Start the shake animation (separate thread)
            doAnimation(curTheme.getShakeAnimation());
            // At the end of the shake animation, reset the shaking flag.
            Runnable resetShake = new Runnable() {

                @Override
                public void run() {

                    try {
                        Thread.sleep(curTheme.getShakeAnimation().getDuration());
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
                    isShaking = false;
                }

            };
            new Thread(resetShake).start();
        }
    }

    /**
     * @return true if Nounours is currently shaking.
     */
    @SuppressWarnings("UnusedDeclaration")
    public boolean isShaking() {
        return isShaking;
    }

    /**
     * @return the minimum shake speed required to launch the shake animation.
     */
    public float getMinShakeSpeed() {
        return Util.getFloatProperty(nounoursProperties, PROP_MIN_SHAKE_SPEED, Float.MAX_VALUE);
    }

    /**
     * The user did a fling action (dragged than released).
     * @param x the x-position at the end of the fling action
     * @param y the y-position at the end of the fling action
     * @param velX the velocity on the x-axis at the end of the fling action
     * @param velY the velocity on the y-axis at the end of the fling action
     */
    public void onFling(int x, int y, float velX, float velY) {
        if (curTheme == null)
            return;
        int[] translatedPoints = Util.translate(x, y, getDeviceWidth(), getDeviceHeight(), curTheme.getWidth(),
                curTheme.getHeight());
        // See if we have an animation we can launch.
        for (FlingAnimation flingAnimation : curTheme.getFlingAnimations()) {
            // Must be fast enough on the x-axis.
            if (!Util.isFaster(velX, flingAnimation.getMinVelX()))
                continue;
            // Must be fast enough on the y-axis.
            if (!Util.isFaster(velY, flingAnimation.getMinVelY()))
                continue;
            // The fling must have been within the bounds.
            if (!Util.pointIsInSquare(translatedPoints[0], translatedPoints[1], flingAnimation.getX(), flingAnimation
                    .getY(), flingAnimation.getWidth(), flingAnimation.getHeight()))
                continue;
            // Get the animation to display.
            Animation animation = curTheme.getAnimations().get(flingAnimation.getAnimationId());
            if (flingAnimation.isVariableSpeed()) {
                try {
                    // The speed of the animation will depend on the velocity of
                    // the
                    // fling.
                    Animation animationCopy = (Animation) animation.clone();
                    float vel = (float) Math.sqrt(velX * velX + velY * velY);
                    int interval = (int) (flingFactor * animation.getInterval() / vel);
                    interval = interval - (interval % flingPrecision);
                    animationCopy.setInterval(interval);
                    animationCopy.setId(animation.getId() + "-" + interval);
                    doAnimation(animationCopy);
                    return;

                } catch (CloneNotSupportedException e) {
                    debug(e.getMessage());
                }
            } else {
                doAnimation(animation);
                return;
            }

        }
    }

    // End motion-related methods

    /*******************************************************************
     * IDLE
     ******************************************************************/

    // Begin idle activity related methods
    /**
     * @param doPing if true, the pinger will periodically check the application for idleness.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void doPing(boolean doPing) {
        pinger.setDoPing(doPing);
    }

    /**
     * This method is called when the application has been idle for the time
     * indicated by the property PROP_IDLE_TIME.
     */
    void onIdle() {
        debug("Idle!");
        resetIdle();
        if (curTheme != null && curTheme.getIdleAnimation() != null) {
            if (!animationHandler.isAnimationRunning())
                doAnimation(curTheme.getIdleAnimation());
        }
    }

    /**
     * @return true if the application has had no activity for at least the time
     *         indicated by the property PROP_IDLE_TIME.
     */
    private boolean isIdleForSleepAnimation() {
        if (lastActionTimestamp > 0)
            return (System.currentTimeMillis() - lastActionTimestamp > idleTimeout);
        return false;
    }

    private boolean isIdleForRandomAnimation() {
        if (lastActionTimestamp > 0)
            return (System.currentTimeMillis() - lastActionTimestamp > pingInterval);
        return false;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    /**
     * Checks if the application has had activity during the last PROP_IDLE_TIME
     * milliseconds. If not, the onIdle() method is invoked. This should be
     * called from a separate thread.
     */
    public void ping() {
        if (isLoading())
            return;
        if (isIdleForSleepAnimation()) {
            runTask(new Runnable() {
                public void run() {

                    onIdle();
                }
            });
        } else {
            if (isIdleForRandomAnimation() && !isAnimationRunning()) {
                runTask(new Runnable() {
                    public void run() {
                        Animation randomAnimation = createRandomAnimation();
                        if (randomAnimation != null)
                            doAnimation(randomAnimation, true);
                    }
                });
            }
        }
    }

    /**
     * Reset the display to the default image
     */
    public void reset() {
        resetIdle();
        setImage(curTheme.getDefaultImage());
    }

    /**
     * This is called whenever some activity has occurred.
     */
    private void resetIdle() {
        lastActionTimestamp = System.currentTimeMillis();
    }

    // End idle-activity related methods

    /*******************************************************************
     * IMAGES
     ******************************************************************/

    // Begin image-related methods

    /**
     * @return all the themes.
     */
    @SuppressWarnings("UnusedDeclaration")
    public Map<String, Theme> getThemes() {
        return themes;
    }

    /**
     * @return the default image of the current theme.
     */
    @SuppressWarnings("UnusedDeclaration")
    public Image getDefaultImage() {
        return curTheme.getDefaultImage();
    }

    /**
     * Display the given image.
     * 
     * @param image the image to display.
     */
    public void setImage(Image image) {
        boolean doRefresh = (curImage != image);
        curImage = image;
        if (doRefresh)
            displayImage(curImage);

    }

    // End image-related methods

    /*******************************************************************
     * OTHER
     ******************************************************************/

    /**
     * The user selected "Help" from the menu.
     */
    public void onHelp() {
        if (curTheme.getHelpImage() != null) {
            stopAnimation();
            displayImage(curTheme.getHelpImage());
        }
    }

    /**
     * The subclass should call this when the screen becomes visible after
     * having been hidden.
     */
    public void onResume() {
        resetIdle();
        if (curTheme.getResumeAnimation() != null)
            doAnimation(curTheme.getResumeAnimation());

    }

    /**
     * @return the time in milliseconds of each pulse, when the device vibrates
     *         in pulses.
     */
    long getVibrateInterval() {
        return vibrateInterval;
    }

    /**
     * @param propertyName
     *            the name of the property to retrieve
     * @return the value of a property, either specified in the
     *         nounours.properties file, or the default value.
     */
    public String getProperty(String propertyName) {
        return nounoursProperties.getProperty(propertyName);
    }

    protected void debug(Object o) {
        System.out.println(getClass() + ": " + o);
        if (o instanceof Throwable)
            ((Throwable) o).printStackTrace();
    }

}
