package hnu.chat.client.ui;

import hnu.chat.common.Constants;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

public class ImageHandler {
    public static BufferedImage compressImage(BufferedImage originalImage) {
        int maxWidth = Constants.COMPRESS_IMAGE_WIDTH;
        int maxHeight = Constants.COMPRESS_IMAGE_HEIGHT;
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        if (width <= maxWidth && height <= maxHeight) {
            return originalImage;
        }
        
        double scale = Math.min((double)maxWidth/width, (double)maxHeight/height);
        width = (int)(width * scale);
        height = (int)(height * scale);
        
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        java.awt.Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        
        return resizedImage;
    }

    public static String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public static BufferedImage base64ToImage(String base64) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }
} 