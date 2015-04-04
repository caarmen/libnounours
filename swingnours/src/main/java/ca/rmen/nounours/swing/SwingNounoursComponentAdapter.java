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
