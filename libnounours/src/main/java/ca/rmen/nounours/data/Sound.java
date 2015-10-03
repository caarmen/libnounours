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
package ca.rmen.nounours.data;

import java.io.Serializable;

/**
 * Contains information about a sound. For now, only the filename is stored.
 * 
 * @author Carmen Alvarez
 * 
 */
public class Sound implements Serializable {

    private final String id;
    private String filename;

    /**
     * @param id
     *            An id for this sound
     * @param filename
     *            The filename containing the sound data.
     */
    public Sound(final String id, final String filename) {
        this.id = id;
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Sound id = " + id + ", file = " + filename;
    }
}
