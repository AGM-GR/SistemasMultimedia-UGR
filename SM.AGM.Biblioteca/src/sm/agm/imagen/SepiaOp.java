package sm.agm.imagen;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *Filtro Sepia para imágenes.
 * @author Alejandro Guerrero Martínez
 */
public class SepiaOp extends sm.image.BufferedImageOpAdapter {

    /**
     *Constructor de la clase.
     */
    public SepiaOp () { }
    
    public BufferedImage filter(BufferedImage src, BufferedImage dest){
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }
        
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                Color color = new Color(src.getRGB(x, y));
                int sepiaR = (int) Math.min(255 , 0.393*color.getRed() + 0.769*color.getGreen() + 0.189*color.getBlue());
                int sepiaG = (int) Math.min(255, 0.349*color.getRed() + 0.686*color.getGreen() + 0.168*color.getBlue());
                int sepiaB = (int) Math.min(255, 0.272*color.getRed() + 0.534*color.getGreen() + 0.131*color.getBlue());
                
                Color sepia = new Color(sepiaR, sepiaG, sepiaB);
                src.setRGB(x, y, sepia.getRGB());
            }
        }
        
        return dest;
    }
}
