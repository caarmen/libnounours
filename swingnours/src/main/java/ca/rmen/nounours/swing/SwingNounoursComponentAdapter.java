/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.swing;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Date;

import javax.swing.JFrame;

import ca.rmen.nounours.Nounours;
import ca.rmen.nounours.Util;

/**
 *
 * @author Carmen Alvarez
 *
 */
public class SwingNounoursComponentAdapter extends ComponentAdapter {
    private Point lastFrameLocation = null;
    private Date lastFrameMoveTime = null;
    private Nounours nounours = null;

    public SwingNounoursComponentAdapter(Nounours nounours) {
        this.nounours = nounours;
    }

    /**
     * The whole window was moved.
     *
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentMoved(ComponentEvent evt) {
        if (evt.getSource() instanceof JFrame) {
            JFrame jframe = (JFrame) evt.getSource();
            Point newLocation = jframe.getLocationOnScreen();
            Date now = new Date();
            if (lastFrameMoveTime != null) {
                long timeDiff = now.getTime() - lastFrameMoveTime.getTime();
                if (timeDiff == 0)
                    timeDiff = 1;
                long distance = Util
                        .getDistance(newLocation.x, newLocation.y, lastFrameLocation.x, lastFrameLocation.y);
                if (distance / timeDiff > nounours.getMinShakeSpeed())
                    nounours.onShake();
            }
            lastFrameMoveTime = now;
            lastFrameLocation = newLocation;
        }

    }

}
