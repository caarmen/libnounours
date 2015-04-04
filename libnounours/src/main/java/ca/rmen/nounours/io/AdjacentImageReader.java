/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ca.rmen.nounours.data.Image;

/**
 * Reads in a file of adjacent image associations. The required columns are:
 * <code>
   - ImageId: String, an id for a given image
   - FeatureId: String, the id of a feature which, when moved, may transition the
              display into another image
   - AdjacentImageId: String, the id of another image to which we may transition,
              if the given feature moves.
 * </code>
 *
 * @author Carmen Alvarez
 *
 */
public class AdjacentImageReader extends NounoursReader {

    private Map<String, Image> imageMap = new HashMap<String, Image>();
    private static final String COL_IMAGE_ID = "ImageId"; //$NON-NLS-1$
    private static final String COL_FEATURE_ID = "FeatureId"; //$NON-NLS-1$
    private static final String COL_ADJACENT_IMAGE_ID = "AdjacentImageId"; //$NON-NLS-1$

    /**
     * @param imageMap
     *            A Map of image id to image. All images referred to in the CSV
     *            file should be present in this Map. The images in this Map are
     *            updated by this class.
     * @param is
     *            the CSV file stream.
     * @throws IOException
     */
    public AdjacentImageReader(Map<String, Image> imageMap, InputStream is) throws IOException {
        super(is);
        this.imageMap = imageMap;
        load();

    }

    /**
     * Updates given images with transitions into adjacent images.
     *
     * @see ca.rmen.nounours.io.NounoursReader#readLine(ca.rmen.nounours.io.CSVReader)
     */
    @Override
    protected void readLine(CSVReader reader) {

        String imageId = reader.getValue(COL_IMAGE_ID);
        String featureId = reader.getValue(COL_FEATURE_ID);
        String adjacentImageId = reader.getValue(COL_ADJACENT_IMAGE_ID);

        Image image = imageMap.get(imageId);
        Image adjacentImage = imageMap.get(adjacentImageId);
        image.addAdjacentImage(featureId, adjacentImage);
    }

}
