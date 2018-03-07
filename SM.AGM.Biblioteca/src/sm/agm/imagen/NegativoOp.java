package sm.agm.imagen;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *Filtro Negativo para imágenes.
 * Invierte el color de todos los píxeles de una imagen.
 * @author Alejandro Guerrero Martínez
 */
public class NegativoOp extends sm.image.BufferedImageOpAdapter {

    /**
     *Constructor de la clase.
     */
    public NegativoOp () { }
    
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
                int negativoR = 255 - color.getRed();
                int negativoG = 255 - color.getGreen();
                int negativoB = 255 - color.getBlue();
                
                Color negativo = new Color(negativoR, negativoG, negativoB);
                src.setRGB(x, y, negativo.getRGB());
            }
        }
        
        return dest;
    }
}