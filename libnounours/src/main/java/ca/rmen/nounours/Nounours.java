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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import ca.rmen.nounours.data.Animation;
import ca.rmen.nounours.data.Feature;
import ca.rmen.nounours.data.FlingAnimation;
import ca.rmen.nounours.data.Image;
import ca.rmen.nounours.data.Sound;
import ca.rmen.nounours.data.Theme;
import ca.rmen.nounours.io.ThemeReader;

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

    public static final String PROP_DROP_VIBRATE_DURATION = "drop.vibrate.duration";
    public static final String PROP_VIBRATE_INTERVAL = "vibrate.interval";
    public static final String PROP_IDLE_TIME = "idle.time";
    public static final String PROP_IDLE_PING_INTERVAL = "idle.ping.interval";
    public static final String PROP_DOWNLOADED_IMAGES_DIR = "downloaded.images.dir";
    public static final String PROP_FLING_FACTOR = "fling.factor";
    public static final String PROP_FLING_PRECISION = "fling.precision";
    public static final String PROP_MIN_SHAKE_SPEED = "shake.factor";
    public static final String PROP_THEME_LIST = "theme.list";

    public static final String DEFAULT_THEME_ID = "0";

    private Random random = null;
    boolean isShaking = false;
    Image curImage = null;
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
    private String downloadedImagesDir = null;

    private Theme defaultTheme = null;
    private Map<String, Theme> themes = null;

    private boolean enableSound = true;
    private boolean enableVibrate = true;
    private boolean enableRandomAnimations = true;
    private NounoursIdlePinger pinger = null;

    private NounoursSoundHandler soundHandler = null;
    private NounoursAnimationHandler animationHandler = null;
    private NounoursVibrateHandler vibrateHandler = null;

    private Properties nounoursProperties;

    // Begin abstract methods

    protected abstract int getDeviceWidth();

    protected abstract int getDeviceHeight();
    
    /**
     * The subclass must display the current image to the device.
     */
    protected abstract void displayImage(Image image);

    /**
     * Subclasses should implement a way to run a task in the background.
     * 
     * @param task
     */
    protected abstract void runTask(Runnable task);

    protected abstract boolean isThemeUpToDate(Theme theme);

    protected abstract void setIsThemeUpToDate(Theme theme);

    public boolean isLoading() {
        return isLoading;
    }

    // End abstract methods.

    /**
     * Display an animation. This method stops any currently running animation.
     * This method invokes the doVibrate and playSound methods implemented by
     * the subclass, before displaying the image animation.
     * 
     * @param animationId
     */
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
        animationHandler.doAnimation(animation, isDynamicAnimation);
    }

    public void doAnimation(Animation animation) {
        doAnimation(animation, false);
    }

    /**
     * Stop the current animation, if one is running.
     */
    public void stopAnimation() {
        animationHandler.stopAnimation();
        soundHandler.stopSound();
        curAnimation = null;
    }

    /**
     * @return true if an animation is currently being displayed.
     */
    public boolean isAnimationRunning() {
        return animationHandler.isAnimationRunning();
    }

    /**
     * Return the current animation, if one is running. Otherwise return null.
     * 
     * @return
     */
    private Animation getCurrentAnimation() {
        if (!animationHandler.isAnimationRunning())
            // Reset the current animation to null, if it is not running
            // anymore.
            curAnimation = null;
        return curAnimation;
    }

    public Animation createRandomAnimation() {
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
            result.addImage(curAnimationImage.getId(), duration);
            curAnimationImage = getRandomImage(curAnimationImage);
        }
        return result;
    }

    private Image getRandomImage(Image fromImage) {
        Map<String, Image> images = getImages();
        List<Image> allAdjacentImages = fromImage.getAllAdjacentImages();
        if (allAdjacentImages.size() == 0)
            return null;
        int toImageNumber = random.nextInt(allAdjacentImages.size());
        Image toImage = allAdjacentImages.get(toImageNumber);
        return toImage;
    }

    /**
     * Reads the CSV files containing the image, feature, and animation data.
     * Starts the idle counter which will launch {{@link #onIdle()} after
     * PROP_IDLE_TIME milliseconds of inactivity. Displays the default image.
     * 
     * @param propertiesFile
     *            the nounours.properties file containing application-wide
     *            properties.
     * @param imageFile
     * @param featureFile
     * @param imageFeatureFile
     * @param adjacentImageFile
     * @param animationFile
     * @param flingAnimationFile
     * @param soundFile
     * @param defaultImageId
     *            The default image is the first image displayed. The display
     *            should also be reset to the default image at the end of
     *            animations.
     * @throws IOException
     */
    public void init(NounoursAnimationHandler pAnimationHandler, NounoursSoundHandler pSoundHandler,
            NounoursVibrateHandler pVibrateHandler, InputStream nounoursPropertiesFile, InputStream propertiesFile,
            InputStream imageFile, InputStream themeFile, InputStream featureFile, InputStream imageFeatureFile,
            InputStream adjacentImageFile, InputStream animationFile, InputStream flingAnimationFile,
            InputStream soundFile, String themeId) throws IOException {
        debug("init");
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

    public Theme getDefaultTheme() {
        return defaultTheme;
    }

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
        File appDir = getAppDir();
        if (appDir != null)
            downloadedImagesDir = appDir.getAbsolutePath();
        flingFactor = Float.parseFloat(getProperty(PROP_FLING_FACTOR));
        dropVibrateDuration = Util.getLongProperty(nounoursProperties, PROP_DROP_VIBRATE_DURATION, dropVibrateDuration);
        vibrateInterval = Util.getLongProperty(nounoursProperties, PROP_VIBRATE_INTERVAL, vibrateInterval);
        idleTimeout = Util.getLongProperty(nounoursProperties, PROP_IDLE_TIME, idleTimeout);
        pingInterval = Util.getLongProperty(nounoursProperties, PROP_IDLE_PING_INTERVAL, pingInterval);
        flingPrecision = (int) Util.getLongProperty(nounoursProperties, PROP_FLING_PRECISION, flingPrecision);

        // try first to get remote themes.

        if (appDir != null && !appDir.exists())
            appDir.mkdirs();
        if (appDir != null && appDir.exists()) {
            String localThemeFileName = downloadedImagesDir + File.separator + "themes.csv";
            File localThemesFile = new File(localThemeFileName);
            if (localThemesFile.exists()) {
                try {
                    ThemeReader themeReader = new ThemeReader(new FileInputStream(localThemesFile));
                    themes = themeReader.getThemes();
                } catch (Exception e) {
                    debug("Error loading themes from sdcard: " + e.getMessage());
                    debug(e);
                }

            }
        }
        if (themes == null || themes.isEmpty()) {
            ThemeReader themeReader = new ThemeReader(themeFile);
            themes = themeReader.getThemes();

        }

    }

    protected abstract boolean cacheImages();

    /**
     * Use the given set of images
     * 
     * @param id
     */
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
            boolean forceDownload = !isThemeUpToDate(curTheme);
            if (!curTheme.isLoaded() && !id.equals(DEFAULT_THEME_ID)) {

                try {
                    debug("init theme " + curTheme);
                    curTheme.init(downloadedImagesDir, forceDownload);
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

            int size = curTheme.getImages().size() + curTheme.getSounds().size();

            // Loading the default theme.
            if (id.equals(DEFAULT_THEME_ID)) {
                debug("loading the default theme");
                updatePreloadProgress(curTheme.getImages().size(), size);
            }
            // Loading a non-default theme.
            else {
                debug("Loading theme");
                // Access or create the local directory for this theme.
                String localDirName = downloadedImagesDir + File.separator + curTheme.getId();
                File localDir = new File(localDirName);
                if (!localDir.exists())
                    localDir.mkdirs();
                URL themeLocation = curTheme.getLocation();
                try {
                    int i = 0;
                    boolean needsDownload = false;
                    boolean downloadFailed = false;
                    debug("Loading images");
                    for (Image image : curTheme.getImages().values()) {
                        i++;
                        // Update the image data to point to the filenames of
                        // this
                        // set
                        if (themeLocation.getProtocol().startsWith("jar")) {
                            if (!image.getFilename().startsWith("jar"))
                                image.setFilename(themeLocation + image.getFilename());
                        } else {
                            String imageFileName = new File(image.getFilename()).getName();
                            /*String remoteFileName = themeLocation + "/" + (useHd() ? "hd/" : "") + imageFileName;
                            URI remoteImageLocation = new URI(remoteFileName);*/
                            URI remoteImageLocation = new URI(themeLocation + "/" + imageFileName);                            
                            File localImageLocation = new File(localDir, imageFileName);
                            // Download the image if we don't have it.
                            if (!localImageLocation.exists()) {
                                if (!Util.downloadFile(remoteImageLocation, localImageLocation)) {
                                    debug("Error downloading image " + image);
                                    return false;
                                }

                                needsDownload = true;
                            }
                            image.setFilename(localImageLocation.getAbsolutePath());
                        }
                        if (needsDownload)
                            updateDownloadProgress(i, size);
                        else
                            updatePreloadProgress(i, size);
                    }
                    debug("Loading " + curTheme.getSounds().size() + " sounds");
                    for (Sound sound : curTheme.getSounds().values()) {
                        i++;
                        String soundFileName = new File(sound.getFilename()).getName();
                        URI remoteSoundLocation = new URI(themeLocation + "/" + soundFileName);
                        File localSoundLocation = new File(localDir, soundFileName);
                        // Download the image if we don't have it.
                        if (!localSoundLocation.exists()) {
                            if (!Util.downloadFile(remoteSoundLocation, localSoundLocation))
                                return false;

                            needsDownload = true;
                        }
                        debug("Loaded " + sound.getFilename());
                        if (needsDownload)
                            updateDownloadProgress(i, size);
                        else
                            updatePreloadProgress(i, size);
                    }
                    if (forceDownload && !downloadFailed)
                        setIsThemeUpToDate(curTheme);
                } catch (Exception e) {
                    debug("Could not use image set " + curTheme + ":  " + e);
                    debug(e);
                    themeLoadError(e.toString());
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

    /**
     * Show the user the download progress of images.F
     * 
     * @param progress
     * @param max
     */
    protected void updateDownloadProgress(int progress, int max) {
        // Do nothing
    }

    protected void updatePreloadProgress(int progress, int max) {
        // Do nothing
    }

    protected void themeLoadError(String message)

    {
        // Do nothing
    }

    /*******************************************************************
     * SOUND
     ******************************************************************/
    // Begin sound-related methods
    /**
     * Mute or unmute the sound.
     * 
     * @param enableSound
     */
    public void setEnableSound(boolean enableSound) {
        this.enableSound = enableSound;
        soundHandler.setEnableSound(enableSound);
    }

    public void setEnableVibrate(boolean enableVibrate) {
        this.enableVibrate = enableVibrate;
    }

    public void setEnableRandomAnimations(boolean enableRandomAnimations) {
        this.enableRandomAnimations = enableRandomAnimations;
    }

    /**
     * @return true if sound is not muted.
     */
    public boolean isSoundEnabled() {
        return enableSound;
    }

    public boolean isVibrateEnabled() {
        return enableVibrate;
    }

    public boolean isRandomAnimationsEnabled() {
        return enableRandomAnimations;
    }

    public Sound getSound(String soundId) {
        return curTheme.getSounds().get(soundId);
    }

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
        if (createAnimation && isRandomAnimationsEnabled())
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
     * @param x
     * @param y
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
            }
        }
    }

    /**
     * The subclass should call this when the mouse/finger moves.
     * 
     * @param x
     * @param y
     */
    public void onMove(int x, int y) {
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
        if (doRefresh)
            displayImage(curImage);
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
     * Enabling pinging the application for idleness.
     * 
     * @param doPing
     */
    public void doPing(boolean doPing) {
        pinger.setDoPing(doPing);
    }

    /**
     * This method is called when the application has been idle for the time
     * indicated by the property PROP_IDLE_TIME.
     */
    public void onIdle() {
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

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public long getIdleTimeout() {
        return idleTimeout;
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
            if (isIdleForRandomAnimation() && !isAnimationRunning() && isRandomAnimationsEnabled()) {
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
     * @return a Map of image id to Image
     */
    public Map<String, Image> getImages() {
        return curTheme.getImages();
    }

    public Map<String, Theme> getThemes() {
        return themes;
    }

    /**
     * Get the current image which is displayed.
     * 
     * @return
     */
    protected Image getCurrentImage() {
        return curImage;
    }

    /**
     * Get the default image
     * 
     * @return
     */
    public Image getDefaultImage() {
        return curTheme.getDefaultImage();
    }

    /**
     * Display the given image.
     * 
     * @param image
     */
    public void setImage(Image image) {
        boolean doRefresh = (curImage != image);
        curImage = image;
        if (doRefresh)
            displayImage(curImage);

    }

    /**
     * @return true if HD images should be used.
     */
    /*
    protected boolean useHd() {
    	return false;
    }*/

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
    public long getVibrateInterval() {
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

    public File getAppDir() {
        String dir = nounoursProperties.getProperty(PROP_DOWNLOADED_IMAGES_DIR);
        return new File(dir);
    }
}
