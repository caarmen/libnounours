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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ca.rmen.nounours.Nounours;
import ca.rmen.nounours.NounoursAnimationHandler;
import ca.rmen.nounours.data.Animation;
import ca.rmen.nounours.data.AnimationImage;

/**
 *
 * @author Carmen Alvarez
 *
 */
public class SwingNounoursAnimationHandler implements NounoursAnimationHandler, ActionListener {

    boolean isDoingAnimation = false;
    private JMenu animationMenu = null;
    private Thread animationThread = null;
    Nounours nounours = null;

    public SwingNounoursAnimationHandler(Nounours nounours, JMenu animationMenu) {
        this.nounours = nounours;
        this.animationMenu = animationMenu;
    }

    /**
     * Build the animation menu as we read from the animation.csv file.
     *
     * @see ca.rmen.nounours.Nounours#addAnimation(ca.rmen.nounours.data.Animation)
     */
    @Override
    public void addAnimation(Animation animation) {
        if (animation.isVisible()) {
            JMenuItem menuItem = new MyMenuItem(animation.getId(), animation.getLabel());
            menuItem.addActionListener(this);
            if(animationMenu != null)
                animationMenu.add(menuItem);
        }
    }

    /**
     * Display an animation.
     *
     * @see ca.rmen.nounours.Nounours#doAnimation(ca.rmen.nounours.data.Animation)
     */
    public void doAnimation(final Animation animation, final boolean isDynamicAnimation) {
        Runnable doAnimation = new Runnable() {

            @Override
            public void run() {
                // Note that we are doing an animation
                synchronized (this) {
                    isDoingAnimation = true;
                }

                Trace.debug(this, "Do animation " + animation.getId());
                boolean interrupted = false;
                // Iterate through each of the images and display them.
                for (int i = 0; i < animation.getRepeat() && !interrupted; i++) {
                    for (AnimationImage image : animation.getImages()) {
                        nounours.setImage(nounours.getImages().get(image.getImageId()));
                        try {
                            Thread.sleep((long) (animation.getInterval() * image.getDuration()));
                        } catch (InterruptedException e) {
                            Trace.debug(this, animation.getLabel() + " interrupted");
                            interrupted = true;
                            break;
                        }
                    }
                }
                if(!isDynamicAnimation)
                    nounours.reset();
                // No longer doing an animation.
                synchronized (this) {
                    isDoingAnimation = false;
                }
            }

        };
        animationThread = new Thread(doAnimation);
        animationThread.start();
    }

    /**
     * Interrupt the animation thread.
     */
    public void stopAnimation() {
        Trace.debug(this, "stop animation");
        if (animationThread != null)
            animationThread.interrupt();
    }

    /*
     * (non-Javadoc)
     *
     * @see ca.rmen.nounours.Nounours#isAnimationRunning()
     */
    public boolean isAnimationRunning() {
        synchronized (this) {
            if (isDoingAnimation) {
                Trace.debug(this, "Already doing animation");
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() instanceof MyMenuItem) {
            MyMenuItem menuItem = (MyMenuItem) evt.getSource();
            Trace.debug(this, menuItem);
            // Show a specific animation.
            Animation animation = nounours.getAnimations().get(menuItem.getId());
            nounours.doAnimation(animation);
        }
    }

}
