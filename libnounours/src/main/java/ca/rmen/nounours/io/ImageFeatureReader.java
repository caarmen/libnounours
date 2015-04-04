/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.rmen.nounours.data.Feature;
import ca.rmen.nounours.data.Image;

/**
 * Reads a CSV file of image-feature associations. The required columns are:
 * <code>
   - ImageId: String.  The id of the image
   - FeatureId: String.  The id of the feature.
   - X: integer.  The x location of this feature within this image.
   - Y: integer.  The y location of this feature within this image.
 * </code>
 * 
 * @author Carmen Alvarez
 * 
 */
public class ImageFeatureReader extends NounoursReader {

    private static final String COL_IMAGE_ID = "ImageId";
    private static final String COL_FEATUREID = "FeatureId";
    private static final String COL_X = "X";
    private static final String COL_Y = "Y";

    private Map<String, Image> imageMap = new HashMap<String, Image>();
    private Map<String, Feature> featureMap = new HashMap<String, Feature>();

    /**
     * Immediately begins reading the CSV file.
     * 
     * @param imageMap
     *            a Map of image id to Image. All images referenced by the CSV
     *            file must be in this Map.
     * @param features
     *            a List of all Features. All features referenced by the CSV
     *            file must be in this list.
     * @param is
     *            the CSV stream
     * @throws IOException
     */
    public ImageFeatureReader(Map<String, Image> imageMap, List<Feature> features, InputStream is) throws IOException {
        super(is);
        this.imageMap = imageMap;
        for (Feature feature : features)
            featureMap.put(feature.getId(), feature);
        load();
    }

    /**
     * Updates the given images by adding the associations of the related
     * features.
     * 
     * @see ca.rmen.nounours.io.NounoursReader#readLine(ca.rmen.nounours.io.CSVReader)
     */
    @Override
    protected void readLine(CSVReader reader) {
        String imageId = reader.getValue(COL_IMAGE_ID);
        String featureId = reader.getValue(COL_FEATUREID);
        String xString = reader.getValue(COL_X);
        String yString = reader.getValue(COL_Y);
        try {
            int x = Integer.parseInt(xString);
            int y = Integer.parseInt(yString);
            Image image = imageMap.get(imageId);
            Feature feature = featureMap.get(featureId);
            image.addFeature(feature, x, y);
        } catch (RuntimeException e) {
            System.out.println(getClass().getName() + ": Error reading line " + reader.getLineNumber() + ": " + imageId
                    + "," + featureId + "," + xString + "," + yString);
            throw e;
        }
    }

}
