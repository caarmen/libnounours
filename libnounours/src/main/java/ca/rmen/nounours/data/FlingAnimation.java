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
 * Represents the criteria during a fling event to launch a given animation. If
 * the fling is within certain bounds, and has at least a certain minimum
 * velocity, a certain animation is displayed.
 *
 * @author Carmen Alvarez
 *
 */
public class FlingAnimation {

    private String id = null;

    private int x = -1;
    private int y = -1;
    private int width = -1;
    private int height = -1;
    private float minVelX = -1f;
    private float minVelY = -1f;
    private String animationId = null;
    private boolean variableSpeed = false;

    /**
     * If the fling motion is within the given bounds, with at least the given
     * velocity, the given animation may be launched.
     *
     * @param id
     *            the id of this fling-animation association
     * @param x
     *            the x location of upper left corner of the bounds
     * @param y
     *            the y location of the upper left corner of the bounds
     * @param width
     *            the width of the bounds
     * @param height
     *            the height of the bounds
     * @param minVelX
     *            the minimum velocity in the x direction required to launch the
     *            given animation
     * @param minVelY
     *            the minimum velocity in the y direction required to launch the
     *            given animation
     * @param animationId
     *            the animation to launch
     * @param variableSpeed
     *            whether the speed of the fling should influence the speed of
     *            the animation.
     */
    public FlingAnimation(String id, int x, int y, int width, int height, float minVelX, float minVelY,
            String animationId, boolean variableSpeed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.minVelX = minVelX;
        this.minVelY = minVelY;
        this.animationId = animationId;
        this.variableSpeed = variableSpeed;
    }

    public String getId() {
        return id;
    }

    /**
     *
     * @return the left of the bound in which the fling must occur to launch the
     *         associated animation.
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @return the top of the bound in which the fling must occur to launch the
     *         associated animation.
     */
    public int getY() {
        return y;
    }

    /**
     * @return the width of the bound in which the fling must occur to launch
     *         the associated animation.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height of the bound in which the fling must occur to launch
     *         the associated animation.
     */
    public int getHeight() {
        return height;
    }

    /**
     * The minimum velocity on the X-axis required to launch the animation.
     *
     * @return
     */
    public float getMinVelX() {
        return minVelX;
    }

    /**
     *
     * @return The minimum velocity on the Y-axis required to launch the
     *         animation.
     */
    public float getMinVelY() {
        return minVelY;
    }

    /**
     *
     * @return The id of the animation to display.
     */
    public String getAnimationId() {
        return animationId;
    }

    /**
     * @return whether the speed of the fling should influence the speed of the
     *         animation.
     */
    public boolean isVariableSpeed() {
        return variableSpeed;
    }

}
