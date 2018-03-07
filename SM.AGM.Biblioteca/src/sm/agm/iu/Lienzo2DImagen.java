package sm.agm.iu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import sm.agm.graficos.*;

/**
 *Clase Lienzo2DImagen es un típo de JPanel que nos permitirá visualizar imágenes y dibujar formas sobre el.
 * @author Alejandro Guerrero Martínez
 */
public class Lienzo2DImagen extends JPanel {
    
    //Forma
    private Forma forma = null;
    private HerramientasDibujo HerramientaActual = HerramientasDibujo.PUNTO;
    private boolean editar = false;
    private Point offsetSeleccion = null;
    
    //Atributos para una nueva forma
    Color colorContorno = Color.black;
    Color colorRelleno = Color.black;
    Color colorDegradado = Color.white;
    int grosor = 1;
    float transparencia = 0.5f;
    boolean relleno = false;
    boolean degradado = false;
    boolean degradadoHorizontal = true;
    boolean trazoDiscontinuo = false;
    boolean transparente = false;
    boolean alisar = false;
    
    //Atributos para texto
    String texto = "Lorem ipsum";
    String fuente = "Arial";
    int tamanoFuente = 12;
    boolean subrayado = false;
    boolean negrita = false;
    boolean cursiva = false;
    
    //Imagen
    private BufferedImage imagen;
    
    //Recorte del area
    Rectangle clipArea = null;
    float patronDiscontinuidad[] = {3.0f, 3.0f};
    Stroke trazoClip = new BasicStroke(2.0f,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_MITER, 1.0f,
            patronDiscontinuidad, 0.0f);
    
    //Variables cursor
    private java.awt.Cursor cursor = java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.CROSSHAIR_CURSOR);
    
    //Constructor de la clase

    /**
     *Constructor de la clase.
     * Inicializa el componente.
     */
    public Lienzo2DImagen() {
        initComponents();
        clipArea = new Rectangle(0,0,200,100);
        imagen = new BufferedImage(200, 100, BufferedImage.TYPE_3BYTE_BGR);
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        
        //Dibuja el clip area y lo aplica
        g2d.setStroke(trazoClip);
        g2d.draw(clipArea);
        g2d.clip(clipArea);
        
        //Dibuja la imagen
        g2d.drawImage(imagen,0,0,this);
        
        //Dibuja la ultima forma si está creada
        if (forma != null)
            forma.draw(g2d);
    }

    private void CreateShape (Point p) {
       
        GuardarUltimaFiguraEnImagen();
        
        switch (HerramientaActual) {
        case PUNTO:
            forma = new Punto ();
            break;
        case LINEA:
            forma = new Linea ();
            break;
        case RECTANGULO:
            forma = new Rectangulo();
            break;
        case ELIPSE:
            forma = new Elipse();
            break;
        case CURVA:
            forma = new Curva();
            break;
        case TEXTO:
            forma = new Texto();
            break;
        }
        
        forma.setLocation (p);
        forma.setTrazo(trazoDiscontinuo);
        forma.setColorTrazo(colorContorno);
        forma.setColorRelleno(colorRelleno);
        forma.setColorDegradado(colorDegradado);
        forma.setDegradado(degradado);
        forma.setDireccionDegradado(degradadoHorizontal);
        forma.setGrosor(grosor);
        forma.setRelleno(relleno);
        forma.setTransparencia(transparente);
        forma.setCantidadTransparencia(transparencia);
        forma.setAlisar(alisar);
        
        if (forma instanceof Texto) {
            ((Texto)forma).setTexto(texto);
            ((Texto)forma).setFuente(fuente);
            ((Texto)forma).setTamano(tamanoFuente);
            ((Texto)forma).setEstilo(negrita, cursiva, subrayado);
        }
    }
    
    /**
     *Establece la herramienta de dibujo actualmente seleccionada.
     * @param herramienta Herramienta de Dibujo.
     */
    public void setHerramienta (HerramientasDibujo herramienta) {
        if (herramienta == HerramientasDibujo.SELECCION) {
            editar = true;
        } else {
            editar = false;
            this.HerramientaActual = herramienta;
        }
        if (forma != null)
            forma.showBoundingBox(editar);
        repaint();
    }
    
    /**
     *Obtiene la herramiente de dibuja actualmente seleccioanda en este lienzo.
     * @return
     */
    public HerramientasDibujo getHerramienta () {
        return this.HerramientaActual;
    }
    
    /**
     *Devuelve el Ancho establecido de este lienzo.
     * @return Int ancho.
     */
    public int getAncho () {
        return clipArea.width;
    }
    
    /**
     *Devuelve el Alto establecido de este lienzo.
     * @return Int Alto.
     */
    public int getAlto () {
        return clipArea.height;
    }
    
    /**
     *Establece el tamaño del Lienzo.
     * @param ancho Ancho del lienzo.
     * @param alto Alto del lienzo.
     */
    public void setTamanno(int ancho, int alto) {
        clipArea.setFrameFromDiagonal(0, 0, ancho, alto);
        setPreferredSize(new Dimension(ancho,alto));
        
        repaint();
    }
    
    /**
     *Establece una imagen en el Lienzo.
     * El lienzo toma el tamaño de la imágen automáticamente.
     * @param imagen BufferedImage a establecer en el lienzo.
     */
    public void setImagen(BufferedImage imagen) {
        this.imagen = imagen;
        if(imagen!=null)
            setTamanno(imagen.getWidth(),imagen.getHeight());
    }
    
    private void GuardarUltimaFiguraEnImagen() {
        if (forma != null) {
            //Desactivamos el BoundingBox
            forma.showBoundingBox(false);
            
            //Dibuja la figura sobre la imagen y establece la imagen
            BufferedImage fullImage = new BufferedImage(imagen.getWidth(), imagen.getHeight(), imagen.getType());
            this.paint(fullImage.createGraphics());
            setImagen(fullImage);
            
            //Quita la forma para que no se cree una forma superpuesta editable
            forma = null;
        }
    }
    
    /**
     *Devuelve la imagen generada en este lienzo,
     * Guardando la última forma creada sin posibilidad de modificarla posteriormente.
     * @return BufferedImage Imagen generada en el lienzo.
     */
    public BufferedImage getImagen() {
        
        GuardarUltimaFiguraEnImagen();
        return imagen;
    }
    
    /**
     *Devuelve una cópia de la imagen generada en este lienzo,
     * La imagen generada contiene la última forma creada pero se permitirá seguir modificando en este lienzo.
     * @return BufferedImage Imagen generada en el lienzo.
     */
    public BufferedImage getImagenCopia() {
        
        if (forma != null) {
            //Desactivamos el BoundingBox
            forma.showBoundingBox(false);
            
            //Dibuja la figura sobre la imagen y la devuelve
            BufferedImage fullImage = new BufferedImage(imagen.getWidth(), imagen.getHeight(), imagen.getType());
            this.paint(fullImage.createGraphics());
            
            //Devolvemos el boundingBox
            forma.showBoundingBox(editar);
            
            return fullImage;
        }
        
        return imagen;
    }
    
    /**
     *Establece el tipo de trazo para las nuevas formas o para la forma actual si está en modo editar.
     * @param discontinuo True si el trazo es discontinuo, False si es continuo.
     */
    public void setTrazo (boolean discontinuo) {
        if (editar && forma != null) {
            forma.setTrazo(discontinuo);
            repaint();
        } else {
            this.trazoDiscontinuo = discontinuo;
        }
    }
    
    /**
     *Establece el color del trazo para las nuevas formas o para la forma actual si está en modo editar.
     * @param color Color del trazo.
     */
    public void setColorTrazo (Color color) {
        if (editar && forma != null) {
            forma.setColorTrazo(color);
            repaint();
        } else {
            this.colorContorno = color;
        }
    }
    
    /**
     *Establece el color del relleno o el primer color del degradado para las nuevas formas o para la forma actual si está en modo editar.
     * @param color Color relleno o primer color degradado.
     */
    public void setColorRelleno (Color color) {
        if (editar && forma != null) {
            forma.setColorRelleno(color);
            repaint();
        } else {
            this.colorRelleno = color;
        }
    }
    
    /**
     *Establece el segundo color del degradado para las nuevas formas o para la forma actual si está en modo editar.
     * @param color Segundo Color degradado.
     */
    public void setColorDegradado (Color color) {
        if (editar && forma != null) {
            forma.setColorDegradado(color);
            repaint();
        } else {
            this.colorDegradado = color;
        }
    }
    
    /**
     *Establece el grosor del trazo para las nuevas formas o para la forma actual si está en modo editar.
     * @param grosor Grosor del trazo.
     */
    public void setGrosor (int grosor) {
        if (editar && forma != null) {
            forma.setGrosor(grosor);
            repaint();
        } else {
            this.grosor = grosor;
        }
    }
    
    /**
     *Establece si se rellena las nuevas formas o la forma actual si está en modo editar.
     * Las opciones relleno y degradado no deben de estar activas a la vez.
     * @param enable True si activa el relleno, False sin relleno.
     */
    public void setRelleno (boolean enable) {
        if (editar && forma != null) {
            forma.setRelleno(enable);
            repaint();
        } else {
            this.relleno = enable;
        }
    }
    
    /**
     *Establece si se rellena con degradado las nuevas formas o la forma actual si está en modo editar.
     * Las opciones relleno y degradado no deben de estar activas a la vez.
     * @param enable True si activa el relleno degradado, False sin relleno degradado.
     */
    public void setDegradado (boolean enable) {
        if (editar && forma != null) {
            forma.setDegradado(enable);
            repaint();
        } else {
            this.degradado = enable;
        }
    }
    
    /**
     *Establece la dirección del degradado para las nuevas formas o para la forma actual si está en modo editar.
     * @param horizontal True degradado horizontal, False degradado vertical.
     */
    public void setDireccionDegradado (boolean horizontal) {
        if (editar && forma != null) {
            forma.setDireccionDegradado(horizontal);
            repaint();
        } else {
            this.degradadoHorizontal = horizontal;
        }
    }
    
    /**
     *Establece se se activa la transparencia para las nuevas formas o para la forma actual si está en modo editar.
     * @param enable True transparencia activada, False transparencia desactivada.
     */
    public void setTransparencia (boolean enable) {
        if (editar && forma != null) {
            forma.setTransparencia(enable);
            repaint();
        } else {
            this.transparente = enable;
        }
    }
    
    /**
     *Establece el nivel de transparencia para las nuevas formas o para la forma actual si está en modo editar.
     * Solo acepta valores de 0.0 al 1.0, 
     * siendo 0.0 totalmente transparente y 1.0 totalmente opaco.
     * @param transparencia Nivel de transparencia.
     */
    public void setCantidadTransparencia (float transparencia) {
        if (editar && forma != null) {
            forma.setCantidadTransparencia(transparencia);
            repaint();
        } else {
            this.transparencia = transparencia;
        }
    }
    
    /**
     *Establece si se alisa la forma para las nuevas formas o para la forma actual si está en modo editar.
     * @param enable
     */
    public void setAlisar (boolean enable) {
        if (editar && forma != null) {
            forma.setAlisar(enable);
            repaint();
        } else {
            this.alisar = enable;
        }
    }
    
    /**
     *Establece el texto para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @param texto Texto a mostrar.
     */
    public void setTexto (String texto) {
        if (editar && forma != null && forma instanceof Texto) {
            ((Texto)forma).setTexto(texto);
            repaint ();
        } else {
            this.texto = texto;
        }
    }
    
    /**
     *Establece la fuente del texto para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @param fuente Fuente del sistema.
     */
    public void setFuente (String fuente) {
        if (editar && forma != null && forma instanceof Texto) {
            ((Texto)forma).setFuente(fuente);
            repaint ();
        } else {
            this.fuente = fuente;
        }
    }
    
    /**
     *Establece el tamaño de la letra para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @param tamano Tamaño de la letra.
     */
    public void setTamanoLetra (int tamano) {
        if (editar && forma != null && forma instanceof Texto) {
            ((Texto)forma).setTamano(tamano);
            repaint ();
        } else {
            this.tamanoFuente = tamano;
        }
    }
    
    /**
     *Establece el formato Negrita para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @param negrita True negrita activado, False negrita desactivado.
     */
    public void setNegrita (boolean negrita) {
        if (editar && forma != null && forma instanceof Texto) {
            ((Texto)forma).setEstilo(negrita, ((Texto)forma).getCursiva(), ((Texto)forma).getSubrayado());
            repaint ();
        } else {
            this.negrita = negrita;
        }
    }
    
    /**
     *Establece el formato Cursiva para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @param cursiva True cursiva activado, False cursiva desactivado.
     */
    public void setCursiva (boolean cursiva) {
        if (editar && forma != null && forma instanceof Texto) {
            ((Texto)forma).setEstilo(((Texto)forma).getNegrita(), cursiva, ((Texto)forma).getSubrayado());
            repaint ();
        } else {
            this.cursiva = cursiva;
        }
    }
    
    /**
     *Establece el formato Subrayado para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @param subrayado True subrayado, False sin subrayado.
     */
    public void setSubrayado (boolean subrayado) {
        if (editar && forma != null && forma instanceof Texto) {
            ((Texto)forma).setEstilo(((Texto)forma).getNegrita(), ((Texto)forma).getCursiva(), subrayado);
            repaint ();
        } else {
            this.subrayado = subrayado;
        }
    }
    
    /**
     *Obtiene el tipo del trazo de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * @return True trazo discontinuo, False trazo continuo.
     */
    public boolean getTrazo () {
        if (editar && forma != null)
            return forma.getTrazo();
        return trazoDiscontinuo;
    }
    
    /**
     *Obtiene el Color del trazo de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * @return Color del trazo.
     */
    public Color getColorTrazo () {
        if (editar && forma != null)
            return forma.getColorTrazo();
        return this.colorContorno;
    }
    
    /**
     *Obtiene el Color del relleno o el primer color del degradado de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * @return Color relleno o primer color degradado.
     */
    public Color getColorRelleno () {
        if (editar && forma != null)
            return forma.getColorRelleno();
        return this.colorRelleno;
    }
    
    /**
     *Obtiene el segundo solor del degradado de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * @return Segundo Color del degradado.
     */
    public Color getColorDegradado () {
        if (editar && forma != null)
            return forma.getColorDegradado();
        return this.colorDegradado;
    }
    
    /**
     *Obtiene el grosor del trazo de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * @return Int grosor.
     */
    public int getGrosor () {
        if (editar && forma != null)
            return forma.getGrosor();
        return this.grosor;
    }
    
    /**
     *Obtiene si está activo el relleno de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * @return True con relleno, False sin relleno.
     */
    public boolean getRelleno () {
        if (editar && forma != null)
            return forma.getRelleno();
        return this.relleno;
    }
    
    /**
     *Obtiene si está activo el relleno degradado de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * @return True con relleno degradado, False sin relleno degradado.
     */
    public boolean getDegradado () {
        if (editar && forma != null)
            return forma.getDegradado();
        return this.degradado;
    }
    
    /**
     *Obtiene la dirección del degradado de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * @return True degradado horizontal, False degradado vertical.
     */
    public boolean getDireccionDegradado () {
        if (editar && forma != null)
            return forma.getDireccionDegradado();
        return this.degradadoHorizontal;
    }
    
    /**
     *Obtiene si está activa la transparencia de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * @return True con transparencia, False sin transparencia.
     */
    public boolean getTransparencia () {
        if (editar && forma != null)
            return forma.getTransparencia();
        return this.transparente;
    }
    
    /**
     *Obtiene el nivel de transparencia de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * Devuelve un valor del 0.0 al 1.0, 
     * siendo 0.0 totalmente transparente y 1.0 totalmente opaco.
     * @return Float nivel de transparencia.
     */
    public float getCantidadTransparencia () {
        if (editar && forma != null)
            return forma.getCantidadTransparencia();
        return this.transparencia;
    }
    
    /**
     *Obtiene si está activo el alisado de las nuevas formas en el lienzo o de la forma actual si está en modo editar.
     * @return True alisado activo, False alisado desactivado.
     */
    public boolean getAlisar () {
        if (editar && forma != null)
            return forma.getAlisar();
        return this.alisar;
    }
    
    /**
     *Obtiene el texto que muestran las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @return String texto.
     */
    public String getTexto () {
        if (editar && forma != null && forma instanceof Texto)
            return ((Texto)forma).getTexto();
        return texto;
    }
    
    /**
     *Obtiene la fuente del texto para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @return String nombre de la Fuente.
     */
    public String getFuente () {
        if (editar && forma != null && forma instanceof Texto)
            return ((Texto)forma).getFuente();
        return fuente;
    }
    
    /**
     *Obtiene el tamaño de la letra del texto para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @return Int tamaño letra.
     */
    public int getTamanoLetra () {
        if (editar && forma != null && forma instanceof Texto)
            return ((Texto)forma).getTamano();
        return tamanoFuente;
    }
    
    /**
     *Obtiene si esta activo el formato Negrita del texto para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @return True formato negrita, False sin negrita.
     */
    public boolean getNegrita () {
        if (editar && forma != null && forma instanceof Texto)
            return ((Texto)forma).getNegrita();
        return negrita;
    }
    
    /**
     *Obtiene si esta activo el formato Cursiva del texto para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @return True formato cursiva, False sin cursiva.
     */
    public boolean getCursiva () {
        if (editar && forma != null && forma instanceof Texto)
            return ((Texto)forma).getCursiva();
        return cursiva;
    }
    
    /**
     *Obtiene si esta activo el formato Subrayado del texto para las nuevas formas de tipo texto o para la forma de tipo texto actual si está en modo editar.
     * @return True formato subrayado, False sin subrayado.
     */
    public boolean getSubrayado () {
        if (editar && forma != null && forma instanceof Texto)
            return ((Texto)forma).getSubrayado();
        return subrayado;
    }
    
    /**
     *Obtiene si el lienzo está en modo editar.
     * @return True editando, False no editando.
     */
    public boolean getEnEdicion () {
        return this.editar;
    }
    
    /**
     *Obtiene si este lienzo se está editando un texto.
     * @return True editando texto, False no editando texto.
     */
    public boolean editandoTexto () {
        
        return (editar && forma != null && forma instanceof Texto);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(204, 204, 204));
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setPreferredSize(new java.awt.Dimension(300, 300));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 411, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 325, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        
        if (editar && forma != null) {
            if (forma.boundsContains(evt.getPoint())) {
                this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.MOVE_CURSOR));
                offsetSeleccion = forma.getOffset(evt.getPoint());
            }
        }
        else if (!editar)
            //Si la forma es una curva sin definir un punto de control, lo define
            if (HerramientaActual == HerramientasDibujo.CURVA && (forma instanceof Curva) && ((Curva) forma).puntoControlNoDefinido())
                ((Curva) forma).setPuntoControl(evt.getPoint());
            else
                //Si no crea una nueva figura según la herramienta seleccionada
                CreateShape (evt.getPoint());
        
        repaint();
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged

        if (editar) {
            if (offsetSeleccion != null)
                forma.setLocation (evt.getPoint(), offsetSeleccion);
        }
        else if (!editar) {
            //Si la forma es una curva con un punto definido se redefine el punto de control
            if (HerramientaActual == HerramientasDibujo.CURVA && (forma instanceof Curva) && !((Curva) forma).puntoControlNoDefinido())
                ((Curva) forma).setPuntoControl(evt.getPoint());
            else
                //Si no cambia el tamaño de la figura respecto al punto en el que se ha hecho click
                forma.setBounds(evt.getPoint()); 
        }
        repaint();
    }//GEN-LAST:event_formMouseDragged

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased

        this.formMouseDragged(evt);
        if (editar) {
            this.setCursor(cursor);
            offsetSeleccion = null;
        }
    }//GEN-LAST:event_formMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
