package ca.rmen.nounours.swing;

import java.awt.image.BufferedImage;

public interface ICachedImageComponent {
    public void setImage(BufferedImage image);

    public int getHeight();

    public int getWidth();

    public int getImageHeight();

    public int getImageWidth();

    public void repaint();
}
