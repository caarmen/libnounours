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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.rmen.nounours.data.Feature;

/**
 * Reads a CSV file of features. The required column names are: <code>
 - Id: String, id of a feature
 - Description: String, description of the feature.
 </code>
 *
 * @author Carmen Alvarez
 *
 */
public class FeatureReader extends NounoursReader {
    private final List<Feature> features = new ArrayList<Feature>();

    private static final String COL_ID = "Id";
    private static final String COL_DESCRIPTION = "Description";

    /**
     * Immediately begins reading the CSV file and building a cache of Feature
     * objects.
     *
     * @param is
     * @throws IOException
     */
    public FeatureReader(final InputStream is) throws IOException {
        super(is);
        load();
    }

    /**
     * Reads a line, creates a new Feature, and adds it to a cache.
     */
    @Override
    protected void readLine(final CSVReader reader) {
        final String id = reader.getValue(COL_ID);
        final String description = reader.getValue(COL_DESCRIPTION);
        final Feature feature = new Feature(id, description);
        features.add(feature);
    }

    /**
     * @return a List of Feature read in the CSV file.
     */
    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }
}
