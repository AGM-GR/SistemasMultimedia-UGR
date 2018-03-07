package sm.agm.graficos;

import java.awt.Point;
import java.awt.Rectangle;

/**
 *Forma Rectángulo.
 * @author Alejandro Guerrero Martínez
 */
public class Rectangulo extends Forma {
 
    Point posicion = new Point (0,0);
    
    /**
     *Constructor de la clase
     * Crea un rectángulo vacío sin definir.
     */
    public Rectangulo() {
        forma = new Rectangle();
    }
    
    @Override
    public Point getOffset(Point puntoSeleccionado) {
        return new Point ((int)((Rectangle)forma).getX()- puntoSeleccionado.x, (int)((Rectangle)forma).getY()- puntoSeleccionado.y);
    }

    @Override
    public void setLocation(Point posicion) {
        this.posicion = posicion;
        ((Rectangle)forma).setLocation(posicion);
    }

    @Override
    public void setLocation(Point posicion, Point offset) {
        posicion.translate(offset.x, offset.y);
        setLocation (posicion);
    }
    
    @Override
    public void setBounds(Point p) {
        ((Rectangle)forma).setFrameFromDiagonal(posicion.x, posicion.y, p.x, p.y);
    }

    @Override
    public void setBounds(Point p1, Point p2) {
        posicion = p1;
        ((Rectangle)forma).setFrameFromDiagonal(posicion, p2);
    }
}
