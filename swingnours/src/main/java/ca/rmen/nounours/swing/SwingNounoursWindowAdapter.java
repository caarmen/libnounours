/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
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
