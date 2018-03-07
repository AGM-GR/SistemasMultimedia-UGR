package sm.agm.graficos;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

/**
 *Forma Punto.
 * @author Alejandro Guerrero Martínez
 */
public class Punto extends Forma{
    
    /**
     *Constructor de la clase
     * Crea un punto vacío sin definir.
     */
    public Punto () {
        forma = new Line2D.Float();
    }
    
    @Override
    public Point getOffset(Point puntoSeleccionado) {
        return new Point ((int)((Line2D)forma).getX1() - puntoSeleccionado.x, (int)((Line2D)forma).getY1()- puntoSeleccionado.y);
    }

    @Override
    public void setLocation(Point posicion) {
        ((Line2D)forma).setLine(posicion, posicion);
    }

    @Override
    public void setLocation(Point posicion, Point offset) {
        posicion.translate(offset.x, offset.y);
        ((Line2D)forma).setLine(posicion, posicion);
    }
    
    @Override
    public void setBounds(Point p) {
        setLocation(p);
    }

    @Override
    public void setBounds(Point p1, Point p2) {
        setLocation(p1);
    }
    
    @Override
    public Rectangle getBounds() {
        Rectangle bounds = forma.getBounds();
        
        if (grosor < 8)
            bounds.grow(4, 4);
        else
            bounds.grow(grosor/2, grosor/2);
        
        return bounds;
    }
    
}
