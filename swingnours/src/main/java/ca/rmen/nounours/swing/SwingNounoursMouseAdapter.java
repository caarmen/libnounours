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
