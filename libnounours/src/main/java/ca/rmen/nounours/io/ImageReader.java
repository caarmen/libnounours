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

import ca.rmen.nounours.data.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads a CSV file of image data. The required columns are: <code>
   - Id: String.  The id of the image.
   - Filename: String.  The filename containing the image data.
  </code> The optional columns are:<code>
   - OnRelease: String.  The id of another image which should be displayed when the user releases the mouse/finger from the current image.
 * </code>Id (String, id of the image),
 * Filename (String, location of the image file).
 *
 * @author Carmen Alvarez
 *
 */
public class ImageReader extends NounoursReader {
    private final Map<String, Image> images = new HashMap<String, Image>();

    private static final String COL_ID = "Id";
    private static final String COL_FILENAME = "Filename";
    private static final String COL_ONRELEASE = "OnRelease";

    /**
     * Immediately reads the CSV data and stores the images in a cache.
     *
     * @param is
     *            the image CSV content
     * @throws IOException if the file could not be read.
     */
    public ImageReader(final InputStream is) throws IOException {
        super(is);
        load();
    }

    /**
     * Read a line in the CSV file, create an Image object, and add it to the
     * cache.
     * @param reader contains the line to be read.
     */
    @Override
    protected void readLine(final CSVReader reader) {
        final String id = reader.getValue(COL_ID);
        final String filename = reader.getValue(COL_FILENAME);
        final String onReleaseImageId = reader.getValue(COL_ONRELEASE);
        final Image image = new Image(id, filename);
        if (onReleaseImageId != null) {
            image.setOnReleaseImageId(onReleaseImageId);
        }
        images.put(id, image);
    }

    /**
     * @return a Map of image id to Image, for images read from the CSV file.
     */
    public Map<String, Image> getImages() {
        return Collections.unmodifiableMap(images);
    }
}
