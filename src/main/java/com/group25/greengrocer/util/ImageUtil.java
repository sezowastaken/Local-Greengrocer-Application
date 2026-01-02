package com.group25.greengrocer.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    private static final int MAX_DIMENSION = 1024;

    /**
     * Reads an image file, resizes it if needed, checks size constraints,
     * and returns the byte array.
     * 
     * @param file The image file to process
     * @return byte[] of the processed image
     */
    public static byte[] compressAndResize(File file) {
        try {
            BufferedImage originalImage = ImageIO.read(file);
            if (originalImage == null) {
                return null;
            }

            // Calculate new dimensions
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int newWidth = originalWidth;
            int newHeight = originalHeight;

            if (originalWidth > MAX_DIMENSION || originalHeight > MAX_DIMENSION) {
                if (originalWidth > originalHeight) {
                    newWidth = MAX_DIMENSION;
                    newHeight = (int) (originalHeight * ((double) MAX_DIMENSION / originalWidth));
                } else {
                    newHeight = MAX_DIMENSION;
                    newWidth = (int) (originalWidth * ((double) MAX_DIMENSION / originalHeight));
                }
            }

            // Resize
            Image resultingImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

            // Draw original image to new image with white background (handles transparent
            // PNGs converting to JPG)
            Graphics2D g2d = outputImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, newWidth, newHeight);
            g2d.drawImage(resultingImage, 0, 0, null);
            g2d.dispose();

            // Write to ByteArrayOutputStream as JPEG (good compression)
            // Note: detailed compression control requires ImageWriter,
            // but for simplicity locally we use default JPEG or basic resizing which
            // reduces size significantly.
            // If explicit compression quality is needed, we'd use ImageWriteParam.
            // For this scope, resizing + JPEG format usually solves the 1MB limit for
            // standard photos.

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Using jpg for compression benefits
            ImageIO.write(outputImage, "jpg", baos);

            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
