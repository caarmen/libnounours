/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.swing;

/**
 * Utility class for logging.
 *
 * @author Carmen Alvarez
 *
 */
public class Trace {

    public static void debug(Object context, Object message) {
        System.out.println(context.getClass() + ": " + message);
        if (message instanceof Throwable)
            ((Throwable) message).printStackTrace();
    }
}
