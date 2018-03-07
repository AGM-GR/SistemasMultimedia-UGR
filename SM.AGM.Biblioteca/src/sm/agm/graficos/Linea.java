package sm.agm.graficos;

import java.awt.Point;
import java.awt.geom.Line2D;

/**
 *Forma Linea.
 * @author Alejandro Guerrero Martínez
 */
public class Linea extends Forma{
    
    /**
     *Constructor de la clase
     * Crea una línea vacía sin definir.
     */
    public Linea () {
        forma = new Line2D.Float();
    }
    
    @Override
    public Point getOffset(Point puntoSeleccionado) {
        return new Point ((int)((Line2D)forma).getX1() - puntoSeleccionado.x, (int)((Line2D)forma).getY1()- puntoSeleccionado.y);
    }

    @Override
    public void setLocation(Point posicion) {
        Point lineaP2Dist = new Point((int)(((Line2D)forma).getX2() - ((Line2D)forma).getX1()), 
                (int)(((Line2D)forma).getY2() - ((Line2D)forma).getY1()));

        ((Line2D)forma).setLine(posicion, new Point (posicion.x+lineaP2Dist.x, posicion.y+lineaP2Dist.y));
    }

    @Override
    public void setLocation(Point posicion, Point offset) {
        posicion.translate(offset.x, offset.y);
            
        Point lineaP2Dist = new Point((int)(((Line2D)forma).getX2() - ((Line2D)forma).getX1()), 
                (int)(((Line2D)forma).getY2() - ((Line2D)forma).getY1()));

        ((Line2D)forma).setLine(posicion, new Point (posicion.x+lineaP2Dist.x, posicion.y+lineaP2Dist.y));
    }
    
    @Override
    public void setBounds(Point p) {
        ((Line2D)forma).setLine(((Line2D)forma).getP1(), p);
    }

    @Override
    public void setBounds(Point p1, Point p2) {
        ((Line2D)forma).setLine(p1, p2);
    }
}
