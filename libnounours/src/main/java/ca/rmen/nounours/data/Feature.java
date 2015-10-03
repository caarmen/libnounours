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

/**
 * This class represents a feature of the main object in an image. For example:
 * The left paw of Nounours.
 *
 * @author Carmen Alvarez
 *
 */
public class Feature {

    private String id = null;
    private String name = null;

    /**
     * @param id
     *            an identifier for the feature
     * @param name
     *            a display name for the feature (currently unused)
     */
    public Feature(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + "," + name;
    }
}
