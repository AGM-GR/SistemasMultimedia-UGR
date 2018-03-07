package sm.agm.graficos;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

/**
 *Forma Elipse.
 * @author Alejandro Guerrero Martínez
 */
public class Elipse extends Forma {
    
    Point posicion = new Point (0,0);
    
    /**
     *Constructor de la clase
     * Crea una elipse vacía sin definir.
     */
    public Elipse() {
        forma = new Ellipse2D.Float();
    }
    
    @Override
    public Point getOffset(Point puntoSeleccionado) {
        return new Point ((int)((Ellipse2D)forma).getX()- puntoSeleccionado.x, (int)((Ellipse2D)forma).getY()- puntoSeleccionado.y);
    }

    @Override
    public void setLocation(Point posicion) {
        this.posicion = posicion;
        ((Ellipse2D)forma).setFrame(posicion, new Dimension((int)((Ellipse2D)forma).getWidth(), (int) ((Ellipse2D)forma).getHeight()));
    }

    @Override
    public void setLocation(Point posicion, Point offset) {
        posicion.translate(offset.x, offset.y);
        setLocation (posicion);
    }
    
    @Override
    public void setBounds(Point p) {
        ((Ellipse2D)forma).setFrameFromDiagonal(posicion.x, posicion.y, p.x, p.y);
    }

    @Override
    public void setBounds(Point p1, Point p2) {
        posicion = p1;
        ((Ellipse2D)forma).setFrameFromDiagonal(p1, p2);
    }
}
