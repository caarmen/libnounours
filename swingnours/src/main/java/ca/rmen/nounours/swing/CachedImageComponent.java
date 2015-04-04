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