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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ca.rmen.nounours.Nounours;

/**
 * Show the nounours application in an applet.
 * 
 * @author Carmen Alvarez
 * 
 */
public class NounoursApplet extends JApplet {

    SwingNounours nounours = null;
    private static final int APPLET_HEIGHT = 480;
    private static final int APPLET_WIDTH = 320;

    public NounoursApplet() {
        // Do nothing
    }

    public void init() {
        super.init();
        try {

            // Read the data
            String dataFilesLocation = getParameter("dataFilesLocation");
            InputStream propertiesFile = new URL(dataFilesLocation + "nounours.common.properties").openStream();
            InputStream themePropertiesFile = new URL(dataFilesLocation + "nounours.common.properties")
                    .openStream();
            InputStream featureFile = new URL(dataFilesLocation + "feature.csv").openStream();
            InputStream imageFile = new URL(dataFilesLocation + "image-applet.csv").openStream();
            InputStream imageSetFile = new URL(dataFilesLocation + "imageset.csv").openStream();
            InputStream imageFeatureFile = new URL(dataFilesLocation + "imagefeatureassoc.csv").openStream();
            InputStream adjacentImageFile = new URL(dataFilesLocation + "adjacentimage.csv").openStream();
            InputStream animationFile = new URL(dataFilesLocation + "animation.csv").openStream();
            InputStream flingAnimationFile = new URL(dataFilesLocation + "flinganimation.csv").openStream();
            InputStream soundFile = new URL(dataFilesLocation + "sound.csv").openStream();

            // Set up the menu bar
            JMenu animation = new JMenu("Animations");
            JMenu option = new JMenu("Options");
            JMenu help = new JMenu("Help");
            JMenuBar menuBar = new JMenuBar();
            menuBar.add(animation);
            menuBar.add(help);
            JPanel menuBarPanel = new JPanel();
            menuBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            menuBarPanel.add(menuBar);

            // Set up the image panel
            CachedImageComponent nounoursPanel = new CachedImageComponent();
            nounours = new SwingNounours(nounoursPanel, animation, option, help, propertiesFile,
                    imageSetFile, "0");
            SwingNounoursMouseAdapter mouseAdapter = new SwingNounoursMouseAdapter(nounours);
            SwingNounoursComponentAdapter componentAdapter = new SwingNounoursComponentAdapter(nounours);
            nounoursPanel.addMouseListener(mouseAdapter);
            nounoursPanel.addMouseMotionListener(mouseAdapter);
            nounoursPanel.addComponentListener(componentAdapter);
            getContentPane().add(menuBarPanel, BorderLayout.NORTH);
            getContentPane().add(nounoursPanel, BorderLayout.CENTER);

            System.out.println("Init done");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

    }

    /**
     * Run the applet inside a frame.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // Create an applet
        NounoursApplet applet = new NounoursApplet() {

            private static final long serialVersionUID = 1L;
            private Properties props = null;

            public String getParameter(String key) {
                if (props == null) {
                    props = new Properties();
                    try {
                        props.load(new FileInputStream("nounours.applet.properties"));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                return props.getProperty(key);
            }
        };
        // Initialize the applet
        applet.init();
        applet.start();
        // Size the applet
        applet.setPreferredSize(new Dimension(APPLET_WIDTH, APPLET_HEIGHT));
        JScrollPane scrollpane = new JScrollPane(applet);

        // Add the applet to the frame.
        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scrollpane, BorderLayout.CENTER);
        frame.setVisible(true);

        // Automatically size the frame.
        frame.pack();

    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}
