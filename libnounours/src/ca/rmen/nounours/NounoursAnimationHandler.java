/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours;

import ca.rmen.nounours.data.Animation;

/**
 * Implementing classes implement logic for displaying animations to the device.
 *
 * @author Carmen Alvarez
 *
 */
public interface NounoursAnimationHandler {

    /**
     * The implementing class may implement this to add the menu item for the
     * animation, as it is read from the CSV file. If this must be handled
     * later, the method {#link {@link #getAnimations()} may be used instead.
     *
     * @param animation
     */
    public void addAnimation(Animation animation);

    /**
     * Implementing classes should implement the logic to stop the currently
     * running animation.
     */
    public void stopAnimation();

    /**
     * @return true if we are currently displaying an animation.
     */
    public boolean isAnimationRunning();

    /**
     * Implementing classes should implement the logic to show the animation on
     * the device.
     *
     * @param animation
     */
    public void doAnimation(Animation animation, boolean isDynamicAnimation);
}
