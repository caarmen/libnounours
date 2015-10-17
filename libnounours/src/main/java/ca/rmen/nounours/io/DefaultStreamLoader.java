/*
 * Copyright (c) 2015 Carmen Alvarez.
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

import java.io.*;
import java.net.URI;

public class DefaultStreamLoader implements StreamLoader {

    public DefaultStreamLoader() {
    }

    @Override
    public InputStream open(URI uri) throws IOException {
        // A file contained in a jar
        if (uri.getScheme().toLowerCase().startsWith("jar")) {
            String fullPath = uri.toString();
            String filePath = fullPath.substring(fullPath.indexOf("!") + 1);
            return getClass().getResourceAsStream(filePath);
        }
        // A file on our local filesystem
        else if (uri.getScheme().toLowerCase().equals("file")) {
            return new FileInputStream(new File(uri.getPath()));
        }
        else {
            return null;
        }
    }
}
