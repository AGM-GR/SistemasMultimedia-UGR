package sm.agm.imagen;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *Filtro Pixelizar para imágenes.
 * Pixeliza una imágen en píxeles de un tamaño determinado.
 * @author Alejandro Guerrero Martínez
 */
public class PixelizarOp extends sm.image.BufferedImageOpAdapter {

    int pixelSize;
    
    /**
     *Constructor de la clase.
     * Establece por defecto un tamaño de pixelizacion de 10 píxeles.
     */
    public PixelizarOp () { 
        pixelSize = 10;
    }
    
    /**
     *Constructor de la clase.
     * @param pixelSize Tamaño del pixel a generar.
     */
    public PixelizarOp (int pixelSize) {
        this.pixelSize = pixelSize;
    }
    
    public BufferedImage filter(BufferedImage src, BufferedImage dest){
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }
        
        for (int x = 0; x < src.getWidth(); x+=pixelSize) {
            for (int y = 0; y < src.getHeight(); y+=pixelSize) {
                
                int pixelR = 0;
                int pixelG = 0;
                int pixelB = 0;
                int numeroSumas = 0;
                
                for (int x2 = x; x2 < x+pixelSize && x2 < src.getWidth(); x2++) {
                    for (int y2 = y; y2 < y+pixelSize && y2 < src.getHeight(); y2++) {
                        Color color = new Color(src.getRGB(x2, y2));
                        pixelR += color.getRed();
                        pixelG += color.getGreen();
                        pixelB += color.getBlue();
                        numeroSumas++;
                    }
                }
                
                pixelR = pixelR / numeroSumas;
                pixelG = pixelG / numeroSumas;
                pixelB = pixelB / numeroSumas;
                
                Color media = new Color(pixelR, pixelG, pixelB);
                
                for (int x2 = x; x2 < x+pixelSize && x2 < src.getWidth(); x2++)
                    for (int y2 = y; y2 < y+pixelSize && y2 < src.getHeight(); y2++)
                        src.setRGB(x2, y2, media.getRGB());
            }
        }
        
        return dest;
    }
}
