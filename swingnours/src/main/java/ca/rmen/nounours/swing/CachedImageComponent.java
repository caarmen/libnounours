/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours.swing;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Swing component that displays an image.
 * 
 * @author Carmen Alvarez
 * 
 */
public class CachedImageComponent extends JPanel implements ICachedImageComponent {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    BufferedImage cachedImage = null;

    /**
     * Set the image to draw;
     * 
     * @param image
     */
    public void setImage(BufferedImage image) {
        cachedImage = image;
    }

/**
     * Draws the image previously set via {@link #setImage(BufferedImage)
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        if (cachedImage != null) {
            float widthRatio = (float) getWidth() / cachedImage.getWidth();
            float heightRatio = (float) getHeight() / cachedImage.getHeight();
            float ratioToUse = widthRatio > heightRatio ? heightRatio : widthRatio;
            int offsetX = 0;
            int offsetY = 0;
            int imageWidth = (int) (cachedImage.getWidth() * ratioToUse);
            int imageHeight = (int) (cachedImage.getHeight() * ratioToUse);
            if (heightRatio > widthRatio)
                offsetY = (getHeight() - imageHeight) / 2;
            else
                offsetX = (getWidth() - imageWidth) / 2;

//            System.out.println(getWidth() + "," + getHeight() + ":" + cachedImage.getWidth() + ","
//                    + cachedImage.getHeight() + "=>" + offsetX+ "," + offsetY + "," + imageWidth + "," + imageHeight);
            g.drawImage(cachedImage, offsetX, offsetY, imageWidth, imageHeight, null);
        }
    }

    public int getImageWidth() {
        return cachedImage == null ? 0 : cachedImage.getWidth();
    }

    public int getImageHeight() {
        return cachedImage == null ? 0 : cachedImage.getHeight();
    }

}