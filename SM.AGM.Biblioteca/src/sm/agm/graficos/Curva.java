package sm.agm.graficos;

import java.awt.Point;
import java.awt.geom.QuadCurve2D;

/**
 *Forma Curva con un punto de control.
 * @author Alejandro Guerrero Martínez
 */
public class Curva extends Forma{
    
    Point puntoControl = null;
    
    /**
     *Constructor de la clase
     * Crea una curva vacía sin definir.
     */
    public Curva () {
        forma = new QuadCurve2D.Float();
    }
    
    @Override
    public Point getOffset(Point puntoSeleccionado) {
        return new Point ((int)((QuadCurve2D)forma).getX1() - puntoSeleccionado.x, (int)((QuadCurve2D)forma).getY1()- puntoSeleccionado.y);
    }

    @Override
    public void setLocation(Point posicion) {
        Point lineaP2Dist = new Point((int)(((QuadCurve2D)forma).getX2() - ((QuadCurve2D)forma).getX1()), 
            (int)(((QuadCurve2D)forma).getY2() - ((QuadCurve2D)forma).getY1()));
        
        if (puntoControl != null) {
            Point lineaPCDist = new Point (puntoControl.x - (int)((QuadCurve2D)forma).getX1(), 
                puntoControl.y - (int)((QuadCurve2D)forma).getY1());
            
            puntoControl = new Point(posicion.x + lineaPCDist.x, posicion.y + lineaPCDist.y);
        }
        
        setBounds(posicion, new Point (posicion.x+lineaP2Dist.x, posicion.y+lineaP2Dist.y));
    }

    @Override
    public void setLocation(Point posicion, Point offset) {
        posicion.translate(offset.x, offset.y);
        setLocation(posicion);
    }
    
    @Override
    public void setBounds(Point p) {
        setBounds(new Point ((int)((QuadCurve2D)forma).getP1().getX(), (int)((QuadCurve2D)forma).getP1().getY()), p);
    }

    @Override
    public void setBounds(Point p1, Point p2) {
        if (puntoControl != null)
            ((QuadCurve2D)forma).setCurve(p1, puntoControl, p2);
        else
            ((QuadCurve2D)forma).setCurve(p1, new Point ((p1.x+p2.x)/2, (p1.y+p2.y)/2), p2);
    }
    
    /**
     *Obtiene si se ha definido el punto de control para la curva.
     * @return True si no se ha definido, False si está definido.
     */
    public boolean puntoControlNoDefinido () {
        
        return puntoControl == null;
    }
    
    /**
     *Establece el punto de control para la curva.
     * @param puntoControl Punto de control.
     */
    public void setPuntoControl (Point puntoControl) {
    
        this.puntoControl = puntoControl;
        setBounds(new Point ((int)((QuadCurve2D)forma).getP1().getX(), (int)((QuadCurve2D)forma).getP1().getY()), 
                new Point ((int)((QuadCurve2D)forma).getP2().getX(), (int)((QuadCurve2D)forma).getP2().getY()));
    }
}
