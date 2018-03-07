package sm.agm.graficos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.util.Map;

/**
 *Forma Texto.
 * @author Alejandro Guerrero Martínez
 */
public class Texto extends Forma{
    
    String fuente;
    int estilo;
    int tamano;
    boolean subrayado;
    Font estiloFuente;
    String texto;
    Point posicion;
    FontMetrics textMetrics;
    Rectangle bounds;
    
    /**
     *Constructor de la clase
     * Crea un texto por defecto "Lorem ipsum".
     */
    public Texto () {
        
        texto = "Lorem ipsum";
        fuente = "Arial";
        estilo = Font.PLAIN;
        tamano = 12;
        subrayado = false;
        posicion = new Point (0,0);
        bounds = new Rectangle(0,0,0,0);
    }
    
    @Override
    public Point getOffset(Point puntoSeleccionado) {
        return new Point (posicion.x - puntoSeleccionado.x, posicion.y- puntoSeleccionado.y);
    }

    @Override
    public void setLocation(Point posicion) {
        this.posicion = posicion;
    }

    @Override
    public void setLocation(Point posicion, Point offset) {
        posicion.translate(offset.x, offset.y);
            
        setLocation(posicion);
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
        return bounds;
    }
    
    @Override
    public void draw (Graphics2D g2d) {
        
        g2d.setPaint(colorContorno);
        
        trazo = new BasicStroke(grosor);    
        g2d.setStroke(trazo);

        if (transparente)
            g2d.setComposite(comp);

        if (alisar)
            g2d.setRenderingHints(render);
        
        estiloFuente = new Font(fuente, estilo, tamano);
        if (subrayado) {
            Map atributosTexto = estiloFuente.getAttributes();
            atributosTexto.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            g2d.setFont(estiloFuente.deriveFont(atributosTexto));
        } else 
            g2d.setFont(estiloFuente);
        
        textMetrics = g2d.getFontMetrics();
        bounds = new Rectangle(posicion.x, posicion.y - textMetrics.getAscent(), textMetrics.stringWidth(texto), textMetrics.getAscent());
        
        g2d.drawString(texto, posicion.x, posicion.y);
        
        if (boundingBox) {
            g2d.setPaint(Color.black);
            g2d.setStroke(trazoLimites);
            limitesForma = getBounds();
            limitesForma.grow(2, 2);
            g2d.draw(limitesForma);
        }
    }
    
    /**
     *Establece el texto a mostrar.
     * @param texto String con el texto a mostrar.
     */
    public void setTexto (String texto) {
        this.texto = texto;
    }
    
    /**
     *Establece la fuente del texto.
     * @param fuente String fuente del sistema.
     */
    public void setFuente (String fuente) {
        this.fuente = fuente;
    }
    
    /**
     *Establece el tamaño de la letra.
     * @param tamano Tamaño letra.
     */
    public void setTamano (int tamano) {
        this.tamano = tamano;
    }
    
    /**
     *Establece el formato del texto.
     * @param negrita Texto en negrita.
     * @param cursiva Texto en cursiva.
     * @param subrayado Texto subrayado.
     */
    public void setEstilo (boolean negrita, boolean cursiva, boolean subrayado) {
        
        estilo = Font.PLAIN;
        if (negrita)
            estilo = estilo | Font.BOLD;
        if (cursiva)
            estilo = estilo | Font.ITALIC;
        
        this.subrayado = subrayado;
    }
    
    /**
     *Obtiene el texto que muestra.
     * @return String texto.
     */
    public String getTexto () {
        return texto;
    }
    
    /**
     *Obtiene la fuente del texto.
     * @return String fuente.
     */
    public String getFuente () {
        return fuente;
    }
    
    /**
     *Obtiene el tamaño de la letra.
     * @return Int tamaño letra.
     */
    public int getTamano () {
        return tamano;
    }
    
    /**
     *Obtiene si el texto está formateado en Negrita.
     * @return True si es negrita, False si no es negrita.
     */
    public boolean getNegrita () {
        return estiloFuente.isBold();
    }
    
    /**
     *Obtiene si el texto está formateado en Cursiva.
     * @return True si es cursiva, False si no es cursiva.
     */
    public boolean getCursiva () {
        return estiloFuente.isItalic();
    }
    
    /**
     *Obtiene si el texto está formateado como Subrayado.
     * @return True si está subrayado, False si no está subrayado.
     */
    public boolean getSubrayado () {
        return subrayado;
    }
}