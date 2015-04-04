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

/**
 * Class containing common functionality for reading the different CSV files for
 * the Nounours application
 *
 * @author Carmen Alvarez
 *
 */
abstract public class NounoursReader {
    private CSVReader reader = null;

    /**
     *
     * @param is
     * @throws IOException
     */
    public NounoursReader(final InputStream is) throws IOException {
        reader = new CSVReader(is);
    }

    /**
     * Reads each line in the CSV file.
     *
     * @throws IOException
     */
    public void load() throws IOException {
        while (reader.next()) {
            readLine(reader);
        }
        reader.close();
    }

    /**
     * Called when the CSVReader has read one line in the CSV file. Extending
     * classes can access the values read from the line.
     *
     * @param reader
     */
    protected abstract void readLine(CSVReader csvReader);
}
