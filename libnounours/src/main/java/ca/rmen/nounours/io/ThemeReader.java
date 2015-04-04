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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ca.rmen.nounours.data.Theme;

/**
 * Reads a CSV file of image data. The required columns are: <code>
 *
 * @author Carmen Alvarez
 *
 */
public class ThemeReader extends NounoursReader {
    private final Map<String, Theme> themes = new HashMap<String, Theme>();

    private static final String COL_ID = "Id";
    private static final String COL_NAME = "Name";
    private static final String COL_URL = "URL";

    /**
     * Immediately reads the CSV data and stores the images in a cache.
     *
     * @param is
     *            the image CSV content
     * @throws IOException
     */
    public ThemeReader(final InputStream is) throws IOException {
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
        final String name = reader.getValue(COL_NAME);
        URL url;
        try {
            url = new URL(reader.getValue(COL_URL));
            final Theme theme = new Theme(id, name, url);
            themes.put(id, theme);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public Map<String, Theme> getThemes() {
        return Collections.unmodifiableMap(themes);
    }
}
