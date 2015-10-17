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

import ca.rmen.nounours.data.Sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
     * @throws IOException if the file could not be read.
     */
    public SoundReader(final InputStream is) throws IOException {
        super(is);
        load();
    }

    /**
     * Reads a line in the CSV file, creates a new Sound object, and adds it to
     * the cache.
     *
     * @param reader contains the line to be read.
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
