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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ca.rmen.nounours.Nounours;

/**
 *
 * @author Carmen Alvarez
 *
 */
public class SwingNounoursWindowAdapter extends WindowAdapter {

    private Nounours nounours = null;
    private CachedImageComponent component = null;

    public SwingNounoursWindowAdapter(Nounours nounours, CachedImageComponent component) {
        this.nounours = nounours;
        this.component = component;
    }

    /**
     * Repaint the screen when the window becomes active.
     */
    @Override
    public void windowActivated(WindowEvent arg0) {
        Trace.debug(this, "windowActivated");
        component.repaint();
    }

    /**
     * Repaint the screen when the window gains focus
     */
    @Override
    public void windowGainedFocus(WindowEvent evt) {
        Trace.debug(this, "windowGainedFocus");
        component.repaint();
    }

    /**
     * Call onResume() when the window becomes deiconified.
     */
    @Override
    public void windowDeiconified(WindowEvent arg0) {
        nounours.onResume();
    }

}
