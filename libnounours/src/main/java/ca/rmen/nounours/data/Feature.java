/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
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
