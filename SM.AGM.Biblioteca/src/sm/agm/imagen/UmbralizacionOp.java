package sm.agm.imagen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import sm.image.BufferedImageOpAdapter;

/**
 *Filtro Umbralización para imágenes.
 * Establece la imágen bicromática blanco y negro dependiendo del color médio del pixel comparado con el umbral.
 * @author Alejandro Guerrero Martínez
 */
public class UmbralizacionOp extends BufferedImageOpAdapter {
    
    private int umbral;
    
    /**
     *Constructor de la clase.
     * @param umbral Umbral para definir el color del pixel.
     */
    public UmbralizacionOp(int umbral) {
        this.umbral = umbral;
    }
    
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
                int colorMedio = (color.getRed() + color.getGreen() + color.getBlue())/3;
                
                if (colorMedio >= umbral)
                    dest.setRGB(x, y, Color.WHITE.getRGB());
                else
                    dest.setRGB(x, y, Color.BLACK.getRGB());
            }
        }
        
        return dest;
    }
}
