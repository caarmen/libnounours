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
     * animation, as it is read from the CSV file.
     * later, the method {#link {@link ca.rmen.nounours.data.Theme#getAnimations()} may be used instead.
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
