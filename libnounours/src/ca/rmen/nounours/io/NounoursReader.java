/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
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
