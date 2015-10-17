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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import ca.rmen.nounours.Nounours;
import ca.rmen.nounours.data.Image;
import ca.rmen.nounours.data.Theme;
import ca.rmen.nounours.io.DefaultStreamLoader;

/**
 * Displays the nounours application in a JFrame.
 * 
 * @author Carmen Alvarez
 * 
 */
public class SwingNounours extends Nounours implements ActionListener {

    private static final int HEIGHT = 480;
    private static final int WIDTH = 320;
    private ICachedImageComponent component = null;
    private Map<String, BufferedImage> cache = new HashMap<String, BufferedImage>();
    static final String MENU_RANDOM = "Random";
    private static final String MENU_HELP_FEATURES = "HelpFeatures";
    private static final String MENU_TOGGLE_SOUND = "ToggleSound";

    private SwingNounoursAnimationHandler animationHandler = null;
    private SwingNounoursSoundHandler soundHandler = null;
    private SwingNounoursVibrateHandler vibrateHandler = null;

    /**
     * @param component
     *            Swing component which displays the images.
     * @param animationMenu
     *            Menu containing the list of animations.
     * @param optionMenu
     * @param helpMenu
     * @param globalPropertiesFile
     */
    public SwingNounours(ICachedImageComponent component, JMenu animationMenu, JMenu optionMenu, JMenu helpMenu,
            InputStream globalPropertiesFile, InputStream imageSetFile,
            String themeId) throws IOException, MidiUnavailableException {
        this.animationHandler = new SwingNounoursAnimationHandler(this, animationMenu);
        this.soundHandler = new SwingNounoursSoundHandler(this);
        this.component = component;
        vibrateHandler = new SwingNounoursVibrateHandler();
        // Set up the menus
        if (animationMenu != null && optionMenu != null && helpMenu != null) {
            JMenuItem menuItem = new MyMenuItem(MENU_RANDOM, MENU_RANDOM);
            menuItem.addActionListener(this);
            animationMenu.add(menuItem);
            JMenuItem helpFeaturesMenu = new MyMenuItem(MENU_HELP_FEATURES, "Show Features");
            helpMenu.add(helpFeaturesMenu);
            helpFeaturesMenu.addActionListener(this);
            JMenuItem toggleSoundMenu = new MyMenuItem(MENU_TOGGLE_SOUND, "Disable sound");
            optionMenu.add(toggleSoundMenu);
            toggleSoundMenu.addActionListener(this);
        }

        // Initialize
        init(new DefaultStreamLoader(), animationHandler, soundHandler, vibrateHandler, globalPropertiesFile,
                imageSetFile,  themeId);
    }

    /**
     * Load the image bitmaps into memory.
     */
    @Override
    protected boolean cacheResources() {
        // Cache images for efficient drawing
        Map<String, Image> images = getCurrentTheme().getImages();
        for (Image image : images.values())
            cacheImage(image);
        return true;
    }

    /**
     * Use the classic java Thread class to run the runnable.
     * 
     * @see ca.rmen.nounours.Nounours#runTask(java.lang.Runnable)
     */
    protected void runTask(Runnable task) {
        new Thread(task).start();
    }

    /**
     * Read the image from file and store it in memory.
     * 
     * @param image
     */
    private void cacheImage(Image image) {
        BufferedImage bufImage;
        InputStream imageInputStream = null;
        try {
            imageInputStream = readFile(image.getFilename());
            if(imageInputStream == null)
            {
                debug("Could not read file " + image);
                return;
            }
            bufImage = ImageIO.read(imageInputStream);

            if (bufImage == null)
                return;
            cache.put(image.getId(), bufImage);
        } catch (IOException e) {
            debug(image.getId() + "," + image.getFilename());
            e.printStackTrace();
        }
    }

    protected InputStream readFile(String filename) throws IOException {
        InputStream imageInputStream = null;

        if (filename.startsWith("http://")) {
            URL imageUrl = new URL(filename);
            imageInputStream = imageUrl.openStream();

        } else {
            File file = new File(filename);
            if (!file.exists() || !file.isFile()) {
                debug(filename + " is not a valid file");
                return null;
            }
            imageInputStream = new FileInputStream(file);
        }
        return imageInputStream;
    }

    /**
     * Show the image on the cached image component.
     * 
     * @see ca.rmen.nounours.Nounours#displayImage(ca.rmen.nounours.data.Image)
     */
    @Override
    protected void displayImage(Image image) {
        if (image == null) {
            System.out.println("No image to display!");
            return;
        }
        BufferedImage bufImage = cache.get(image.getId());
        component.setImage(bufImage);
        component.repaint();

    }

    /**
     * A menu item was chosen.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() instanceof MyMenuItem) {
            MyMenuItem menuItem = (MyMenuItem) evt.getSource();
            debug(menuItem);
            if (menuItem.getId().equals(MENU_HELP_FEATURES)) {
                onHelp();

            }
            // Random animation
            else if (menuItem.getId().equals(SwingNounours.MENU_RANDOM))
                doRandomAnimation();
            // Activate/disactivate sound.
            else if (menuItem.getId().equals(MENU_TOGGLE_SOUND)) {
                setEnableSound(!isSoundEnabled());
                if (isSoundEnabled())
                    menuItem.setText("Disable sound");
                else
                    menuItem.setText("Enable sound");
            }
        }
    }

    public static void main(String[] args) throws IOException, MidiUnavailableException {

        CachedImageComponent panel = new CachedImageComponent();
        JMenu animation = new JMenu("Animations");
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(animation);
        JMenu optionsMenu = new JMenu("Options");
        menuBar.add(optionsMenu);
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        InputStream propertiesFile = new FileInputStream("nounours.common.properties");
        InputStream imageSetFile = new FileInputStream("imageset.csv");

        SwingNounours nounours = new SwingNounours(panel, animation, optionsMenu, helpMenu, propertiesFile,
                imageSetFile, "0");

        JFrame frame = new JFrame("Test");
        frame.setJMenuBar(menuBar);
        frame.setSize(WIDTH, HEIGHT);
        // panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        JScrollPane scrollpane = new JScrollPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scrollpane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        SwingNounoursMouseAdapter mouseAdapter = new SwingNounoursMouseAdapter(nounours);
        SwingNounoursWindowAdapter windowAdapter = new SwingNounoursWindowAdapter(nounours, panel);
        SwingNounoursComponentAdapter componentAdapter = new SwingNounoursComponentAdapter(nounours);
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
        frame.addWindowListener(windowAdapter);
        frame.addWindowFocusListener(windowAdapter);
        frame.addComponentListener(componentAdapter);

    }

    @Override
    protected int getDeviceHeight() {

        return component.getHeight();
    }

    @Override
    protected int getDeviceWidth() {

        return component.getWidth();
    }

    protected int getImageHeight() {
        return component.getImageHeight();
    }

    protected int getImageWidth() {

        return component.getImageWidth();
    }
}

/**
 * Extend the menu item class so we can have an Id to identify menu items.
 * 
 * @author Carmen Alvarez
 * 
 */
class MyMenuItem extends JMenuItem {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String id = null;

    public MyMenuItem(String id, String label) {
        super(label);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
