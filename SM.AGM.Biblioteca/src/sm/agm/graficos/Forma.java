package sm.agm.graficos;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;

/**
 *Forma abstracta que podrá ser dibujada en un Graphics2D.
 * @author Alejandro Guerrero Martínez
 */
public abstract class Forma {
    
    //Propiedades de la forma
    Color colorContorno = Color.black;
    Color colorRelleno = Color.black;
    Color colorDegradado = Color.white;
    int grosor = 1;
    float transparencia = 0.5f;
    boolean relleno = false;
    boolean degradado = false;
    boolean degradadoHorizontal = true;
    boolean transparente = false;
    boolean alisar = false;
    boolean trazoDiscontinuo = false;
    boolean boundingBox = false;
    Stroke trazo;
    float patronDiscontinuidad[] = {10.0f, 10.0f};
    RenderingHints render = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparencia);
    Paint rellenoDegradado; 
   
    //Forma a dibujar
    Shape forma;
    
     //Límites de la forma en modo editar
    Rectangle limitesForma = null;
    float patronDiscontinuidadLimites[] = {2.0f, 2.0f};
    Stroke trazoLimites = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 1.0f,
            patronDiscontinuidadLimites, 0.0f);
    
    /**
     *Método que obtiene el offset entre un punto dado y el punto de referencia de la forma.
     * @param puntoSeleccionado Punto desde el cual se calcula el offset.
     * @return Point un punto que guarda el offset en el eje X e Y.
     */
    public abstract Point getOffset(Point puntoSeleccionado);

    /**
     *Establece la posición de la forma,
     * moviendo el punto de referencia de la forma al punto indicado.
     * @param posicion Punto al que se transladará la forma.
     */
    public abstract void setLocation(Point posicion);

    /**
     *Establece la posición de la forma,
     * moviendo el punto de referencia de la forma al punto indicado más es offset.
     * @param posicion Punto al que se transladará la forma.
     * @param offset Offset que será sumado a la posición del punto indicado.
     */
    public abstract void setLocation(Point posicion, Point offset);

    /**
     *Establece el segundo punto de referencia de la forma.
     * @param p Segundo punto de referencia.
     */
    public abstract void setBounds(Point p);

    /**
     *Establece el primer y segundo punto de referencia de la forma.
     * @param p1 Primer punto de referencia.
     * @param p2 Segundo punto de referencia.
     */
    public abstract void setBounds(Point p1, Point p2);
    
    /**
     *Método para obtener el BoundingBox de la forma, tiene en cuenta el grosor del trazo.
     * @return Rectangle BoundingBox de la forma.
     */
    public Rectangle getBounds() {
        Rectangle bounds = forma.getBounds();
        bounds.grow(grosor/2, grosor/2);
        return bounds;
    }
    
    /**
     *Método que calcula si un punto dado está dentro del BoundingBox de la forma.
     * @param p Punto a comprobar.
     * @return true si está contenido en la BoundingBox. false si no está contenido en la BoundingBox.
     */
    public boolean boundsContains (Point p) {
        return getBounds().contains(p);
    }
    
    /**
     *Método draw para dibujar la forma con los parametros establecidos en un Graphics2D,
     * Dibuja también el BoundigBox si se ha activado su visualización.
     * @param g2d Graphics2D sobre el que se dibujará la forma.
     */
    public void draw (Graphics2D g2d) {
        
        if (boundingBox) {
            g2d.setPaint(Color.black);
            g2d.setStroke(trazoLimites);
            limitesForma = getBounds();
            limitesForma.grow(2, 2);
            g2d.draw(limitesForma);
        }
        
        if (trazoDiscontinuo) {
            patronDiscontinuidad[0] = 10.0f+grosor;
            patronDiscontinuidad[1] = 10.0f+grosor;
            trazo = new BasicStroke(grosor,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 1.0f,
            patronDiscontinuidad, 0.0f);
        } else
            trazo = new BasicStroke(grosor);    
        g2d.setStroke(trazo);

        if (transparente)
            g2d.setComposite(comp);

        if (alisar)
            g2d.setRenderingHints(render);

        //Dibuja el relleno si tiene
        if (relleno || degradado) {
            if (relleno)
                g2d.setPaint(colorRelleno);
            if (degradado) {
                Point pd1, pd2;
                if (degradadoHorizontal) {
                    pd1 = new Point(getBounds().x, getBounds().y);
                    pd2 = new Point(getBounds().x + getBounds().width, getBounds().y);
                } else {
                    pd1 = new Point(getBounds().x, getBounds().y);
                    pd2 = new Point(getBounds().x, getBounds().y  + getBounds().height);
                }
                rellenoDegradado = new GradientPaint(pd1, colorRelleno, pd2, colorDegradado);
                g2d.setPaint(rellenoDegradado);
            }
            g2d.fill(forma);
        }
        
        //Dibuja el contorno
        g2d.setPaint(colorContorno);
        g2d.draw(forma);
    }
    
    /**
     *Activa o Desactiva el dibujado de la BoundingBox en el método draw.
     * @param show True para activarlo, false para desactivarlo.
     */
    public void showBoundingBox(Boolean show) {
        boundingBox = show;
    }
    
    /**
     *Establece el color del trazo de la forma.
     * @param color Color del trazo.
     */
    public void setColorTrazo (Color color) {
        this.colorContorno = color;
    }
    
    /**
     *Establece el color del relleno de la forma, 
     * o si se usa un relleno degradado es el primer color del degradado.
     * @param color Color del relleno o primer color del degradado.
     */
    public void setColorRelleno (Color color) {
        this.colorRelleno = color;
    }
    
    /**
     *Establece el segundo color del degradado.
     * @param color Segundo color del degradado.
     */
    public void setColorDegradado (Color color) {
        this.colorDegradado = color;
    }
    
    /**
     *Establede la dirección del relleno degradado en la forma.
     * @param horizontal True para degradado horizontal, False para degradado vertical.
     */
    public void setDireccionDegradado (boolean horizontal) {
        this.degradadoHorizontal = horizontal;
    }
    
    /**
     *Establece el tipo de trazo a la hora de dibujar la forma.
     * @param discontinuo True trazo discontinuo, False trazo continuo.
     */
    public void setTrazo (boolean discontinuo) {
        this.trazoDiscontinuo = discontinuo;
    }
    
    /**
     *Establece el grosor del trazo de la forma.
     * @param grosor Grosor del trazo.
     */
    public void setGrosor (int grosor) {
        this.grosor = grosor;
    }
    
    /**
     *Establece si se rellena la forma al dibuajarla, 
     * el relleno y el degradado no pueden estar activos a la vez.
     * @param enable True relleno activo, False sin relleno.
     */
    public void setRelleno (boolean enable) {
        this.relleno = enable;
    }
    
    /**
     *Establece si se rellena con Degradado la forma al dibujarla, 
     * el relleno y el degradado no pueden estar activos a la vez.
     * @param enable True relleno con degradado activo, False sin relleno degradado.
     */
    public void setDegradado (boolean enable) {
        this.degradado = enable;
    }
    
    /**
     *Activa y desactiva la transparencia en la forma.
     * @param enable True activa la transparencia, False quita la transparencia.
     */
    public void setTransparencia (boolean enable) {
        this.transparente = enable;
    }
    
    /**
     *Establece el nivel de transparencia de la forma.
     * Solo acepta valores de 0.0 al 1.0, 
     * siendo 0.0 totalmente transparente y 1.0 totalmente opaco.
     * @param transparencia Nivel de transparencia.
     */
    public void setCantidadTransparencia (float transparencia) {
        this.transparencia = transparencia;
        comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparencia);
    }
    
    /**
     *Activa y desactiva el alisado de la forma. 
     * @param enable True alisado activado, false alisado desactivado.
     */
    public void setAlisar (boolean enable) {
        this.alisar = enable;
    }
    
    /**
     *Obtiene el color del trazo de esta forma.
     * @return Color del trazo.
     */
    public Color getColorTrazo () {
        return colorContorno;
    }
    
    /**
     *Obtiene el color del relleno/primer color del degradado, de esta forma.
     * @return Color relleno/primer color degradado.
     */
    public Color getColorRelleno () {
        return colorRelleno;
    }
    
    /**
     *Obtiene el segundo color del degradado de esta forma.
     * @return Color segundo color degradado.
     */
    public Color getColorDegradado () {
        return colorDegradado;
    }
    
    /**
     *Obtiene la dirección del degradado de esta forma.
     * @return True si es horizontal, False si es vertical.
     */
    public boolean getDireccionDegradado () {
        return degradadoHorizontal;
    }
    
    /**
     *Obtiene el tipo de trazo de esta forma.
     * @return True si es discontinuo, false si es continuo.
     */
    public boolean getTrazo () {
        return trazoDiscontinuo;
    }
    
    /**
     *Obtiene el grosor del trazo de esta forma.
     * @return Int grosor del trazo.
     */
    public int getGrosor () {
        return grosor;
    }
    
    /**
     *Obtiene si la forma tienen el relleno de color activo.
     * @return True relleno activo, False sin relleno.
     */
    public boolean getRelleno () {
        return relleno;
    }
    
    /**
     *Obtiene si la forma tiene el relleno de Degradado activo.
     * @return True si el relleno degradado activo, False sin relleno degradado.
     */
    public boolean getDegradado () {
        return degradado;
    }
    
    /**
     *Obtiene si la forma tiene la transparencia activa.
     * @return True transparencia activa, False sin transparencia.
     */
    public boolean getTransparencia () {
        return transparente;
    }
    
    /**
     *Obtiene el nivel de transparencia de la forma.
     * Devuelve un valor del 0.0 al 1.0, 
     * siendo 0.0 totalmente transparente y 1.0 totalmente opaco.
     * @return Float nivel de transparencia.
     */
    public float getCantidadTransparencia () {
        return transparencia;
    }
    
    /**
     *Obtiene si le forma tiene el alisado activo.
     * @return True alisado activo, False sin alisar.
     */
    public boolean getAlisar () {
        return alisar;
    }
}