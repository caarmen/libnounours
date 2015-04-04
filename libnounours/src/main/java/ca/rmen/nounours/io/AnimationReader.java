/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ca.rmen.nounours.data.Animation;

/**
 * Reads in the CSV file listing the possible animations. The required columns
 * are: <code>
 - Id: String, an id for the animation
 - Label: String, the text which appears in the interface for the animation
 - Interval: integer, the number of  milliseconds between each image
 - Repeat: integer, the number of times to display the animation
 - Sequence: semicolon-separated list of image ids
 </code> The optional columns are: <code>
 - Visible: boolean (default=true), if the animation will appear in the animations menu
 - Vibrate: boolean (default=false), if the device should vibrate when this animation is displayed
 - Sound: String, the id of a sound to play when the animation is displayed.
 * </code>
 *
 * @author Carmen Alvarez
 *
 */
public class AnimationReader extends NounoursReader {
    private final Map<String, Animation> animations = new HashMap<String, Animation>();

    private static final String COL_ID = "Id";
    private static final String COL_LABEL = "Label";
    private static final String COL_INTERVAL = "Interval";
    private static final String COL_SEQUENCE = "Sequence";
    private static final String COL_REPEAT = "Repeat";
    private static final String COL_VISIBLE = "Visible";
    private static final String COL_VIBRATE = "Vibrate";
    private static final String COL_SOUND = "Sound";

    /**
     * Immediately reads the CSV file and builds a cache of Animation objects.
     *
     * @param is
     * @throws IOException
     */
    public AnimationReader(final InputStream is) throws IOException {
        super(is);
        load();
    }

    /**
     * Reads a line in the CSV file and adds it to a cache of animations.
     */
    @Override
    protected void readLine(final CSVReader reader) {
        final String id = reader.getValue(COL_ID);
        final String label = reader.getValue(COL_LABEL);
        final int interval = Integer.parseInt(reader.getValue(COL_INTERVAL));
        final int repeat = Integer.parseInt(reader.getValue(COL_REPEAT));
        final boolean visible = Boolean.parseBoolean(reader.getValue(COL_VISIBLE));
        final boolean vibrate = Boolean.parseBoolean(reader.getValue(COL_VIBRATE));
        final String soundId = reader.getValue(COL_SOUND);
        final String sequence = reader.getValue(COL_SEQUENCE);
        final String[] images = sequence.split(";");
        final Animation animation = new Animation(id, label, interval, repeat, visible, vibrate, soundId);
        float duration = 1.0f;
        for (final String image : images) {
            if (image.startsWith("d=")) {
                duration = Float.parseFloat(image.substring(2));
            } else
                animation.addImage(image, duration);
        }
        animations.put(id, animation);

    }

    /**
     * @return a Map of animation id to Animation
     */
    public Map<String, Animation> getAnimations() {
        return Collections.unmodifiableMap(animations);
    }
}
