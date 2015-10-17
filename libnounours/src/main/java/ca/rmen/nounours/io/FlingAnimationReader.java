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
package ca.rmen.nounours.io;

import ca.rmen.nounours.data.FlingAnimation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reads a CSV file with data indicating which animations to display during
 * fling events. The required columns are: <code>
 - Id: String, an id for this fling-animation association.
 - X: integer. The left of the bounds
 - Y: integer. The top of the bounds
 - Width: integer. The width of the bounds
 - Height: integer. The height of the bounds
 - MinVelX: integer. The minimum velocity in the x direction required to launch this animation.
 - MinVelY: integer. The minimum velocity in the y direction required to launch this animation
 - AnimationId: String.  The id of the animation to launch.
 - VariableSpeed: boolean. Whether the speed of the fling influences the speed of the animation.
 </code>
 *
 * @author Carmen Alvarez
 *
 */
public class FlingAnimationReader extends NounoursReader {
    private final List<FlingAnimation> flingAnimations = new ArrayList<FlingAnimation>();

    private static final String COL_ID = "Id";
    private static final String COL_X = "X";
    private static final String COL_Y = "Y";
    private static final String COL_WIDTH = "Width";
    private static final String COL_HEIGHT = "Height";
    private static final String COL_MIN_VEL_X = "MinVelX";
    private static final String COL_MIN_VEL_Y = "MinVelY";
    private static final String COL_ANIMATION_ID = "AnimationId";
    private static final String COL_VARIABLE_SPEED = "VariableSpeed";

    /**
     * Immediately begins reading the CSV file and builds a cache of
     * FlingAnimation objects.
     *
     * @param is the stream of the fling animation file to read.
     * @throws IOException if the file could not be read.
     */
    public FlingAnimationReader(final InputStream is) throws IOException {
        super(is);
        load();
    }

    /**
     * Reads a line in the CSV file, creates a new FlingAnimation objet, and
     * adds it to the cache.
     * @param reader contains the line to be read.
     */
    @Override
    protected void readLine(final CSVReader reader) {
        final String id = reader.getValue(COL_ID);
        final int x = Integer.parseInt(reader.getValue(COL_X));
        final int y = Integer.parseInt(reader.getValue(COL_Y));
        final int width = Integer.parseInt(reader.getValue(COL_WIDTH));
        final int height = Integer.parseInt(reader.getValue(COL_HEIGHT));
        final float minVelX = Float.parseFloat(reader.getValue(COL_MIN_VEL_X));
        final float minVelY = Float.parseFloat(reader.getValue(COL_MIN_VEL_Y));
        final String animationId = reader.getValue(COL_ANIMATION_ID);
        final boolean variableSpeed = Boolean.parseBoolean(reader.getValue(COL_VARIABLE_SPEED));
        final FlingAnimation flingAnimation = new FlingAnimation(id, x, y, width, height, minVelX, minVelY,
                animationId, variableSpeed);
        flingAnimations.add(flingAnimation);
    }

    /**
     * @return the list of FlingAnimation objects read from the CSV file.
     */
    public List<FlingAnimation> getFlingAnimations() {
        return Collections.unmodifiableList(flingAnimations);
    }
}
