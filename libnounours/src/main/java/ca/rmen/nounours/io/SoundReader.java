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

import ca.rmen.nounours.data.Sound;

/**
 * Reads a CSV file of sound data. Required columns are:<code>
   - Id: String. An id of the sound
   - Filename: String. The location of the sound file.
</code>
 *
 * @author Carmen Alvarez
 *
 */
public class SoundReader extends NounoursReader {
    private final Map<String, Sound> sounds = new HashMap<String, Sound>();

    private static final String COL_ID = "Id";
    private static final String COL_FILENAME = "Filename";

    /**
     * Immediately begins reading the CSV content and adding Sound objects to
     * the cache.
     *
     * @param is
     *            the sound CSV content
     * @throws IOException
     */
    public SoundReader(final InputStream is) throws IOException {
        super(is);
        load();
    }

    /**
     * Reads a line in the CSV file, creates a new Sound object, and adds it to
     * the cache.
     */
    @Override
    protected void readLine(final CSVReader reader) {
        final String id = reader.getValue(COL_ID);
        final String filename = reader.getValue(COL_FILENAME);
        final Sound sound = new Sound(id, filename);
        sounds.put(id, sound);
    }

    /**
     * @return a Map of sound id to Sound, for sounds read from the CSV file.
     */
    public Map<String, Sound> getSounds() {
        return Collections.unmodifiableMap(sounds);
    }
}
