package com.example.Loan.Service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class BGProcessService {

    public BufferedImage convertToPng(byte[] imageBytes) throws IOException {
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageBytes));
        BufferedImage pngImage = new BufferedImage(
            original.getWidth(),
            original.getHeight(),
            BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = pngImage.createGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        
        return pngImage;
    }

    public BufferedImage replaceBackground(byte[] imageBytes, 
                                        Color newBgColor, 
                                        Color colorToReplace,
                                        int tolerance) throws IOException {
        
        // Convert to PNG first
        BufferedImage original = convertToPng(imageBytes);
        BufferedImage result = new BufferedImage(
            original.getWidth(), 
            original.getHeight(), 
            BufferedImage.TYPE_INT_ARGB
        );

        int targetRGB = colorToReplace.getRGB();
        int newRGB = newBgColor.getRGB();

        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int pixel = original.getRGB(x, y);
                if (isBackground(pixel, targetRGB, tolerance)) {
                    result.setRGB(x, y, newRGB);
                } else {
                    result.setRGB(x, y, pixel);
                }
            }
        }
        return result;
    }

    public String convertToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public String convertToBase64(byte[] imageBytes) throws IOException {
        return convertToBase64(convertToPng(imageBytes));
    }

    private boolean isBackground(int pixel, int targetRGB, int tolerance) {
        if ((pixel >> 24) == 0x00) return true; // Transparent
        
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = pixel & 0xff;
        
        int tr = (targetRGB >> 16) & 0xff;
        int tg = (targetRGB >> 8) & 0xff;
        int tb = targetRGB & 0xff;

        double distance = Math.sqrt(
            Math.pow(r - tr, 2) + 
            Math.pow(g - tg, 2) + 
            Math.pow(b - tb, 2)
        );
        
        return distance <= tolerance;
    }
}