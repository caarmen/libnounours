/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.data;

/**
 * Contains information about a sound. For now, only the filename is stored.
 * 
 * @author Carmen Alvarez
 * 
 */
public class Sound {

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
