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

import ca.rmen.nounours.data.Image;

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
     * @throws IOException
     */
    public ImageReader(final InputStream is) throws IOException {
        super(is);
        load();
    }

    /**
     * Read a line in the CSV file, create an Image object, and add it to the
     * cache.
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
