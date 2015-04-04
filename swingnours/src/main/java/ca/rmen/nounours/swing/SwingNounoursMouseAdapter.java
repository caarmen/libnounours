/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.swing;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import ca.rmen.nounours.Nounours;

/**
 *
 * @author Carmen Alvarez
 *
 */
public class SwingNounoursMouseAdapter extends MouseAdapter {

    private Nounours nounours = null;
    private Date lastMouseMoveTime = null;
    private Point lastMouseLocation = null;

    public SwingNounoursMouseAdapter(Nounours nounours) {
        this.nounours = nounours;
    }

    /**
     * Notify Nounours that the user clicked the mouse.
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        nounours.onPress(evt.getX(), evt.getY());
    }

    /**
     * Notify Nounours that the user released the mouse.
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        nounours.onRelease();

    }

    /**
     * Notify Nounours that the user dragged the mouse. Also, if the drag motion
     * fits the criteria for a fling, calls onFling().
     */
    @Override
    public void mouseDragged(MouseEvent evt) {
        nounours.onMove(evt.getX(), evt.getY());

        // Calculate the velocity to call onFling().
        Date now = new Date();
        if (lastMouseLocation != null) {
            long timeDiff = (now.getTime() - lastMouseMoveTime.getTime());
            if (timeDiff == 0)
                timeDiff = 1;
            float velX = 1000 * (evt.getX() - lastMouseLocation.x) / timeDiff;
            float velY = 1000 * (evt.getY() - lastMouseLocation.y) / timeDiff;
            Trace.debug(this, "velocity : " + velX + "," + velY);
            nounours.onFling(evt.getX(), evt.getY(), velX, velY);
        }
        lastMouseMoveTime = now;

        lastMouseLocation = new Point(evt.getX(), evt.getY());

    }

}
