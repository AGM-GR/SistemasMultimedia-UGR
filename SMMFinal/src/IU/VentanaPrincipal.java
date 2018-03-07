package IU;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;
import java.awt.image.WritableRaster;
import java.io.File;
import static java.lang.Math.*;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.agm.imagen.NegativoOp;
import sm.agm.imagen.PixelizarOp;
import sm.agm.imagen.SepiaOp;
import sm.agm.imagen.UmbralizacionOp;
import sm.agm.iu.HerramientasDibujo;
import sm.image.*;
import uk.co.caprica.vlcj.filter.VideoFileFilter;

public class VentanaPrincipal extends javax.swing.JFrame {

    private JFileChooser fileChooser = new JFileChooser();
    private Color colors[] = {Color.BLACK, Color.RED, Color.BLUE, Color.WHITE, Color.YELLOW, Color.GREEN};
    private boolean userChange = true;
    private BufferedImage tmpIMG = null;
    
    private int anchoLienzo = 300;
    private int altoLienzo = 300;
    
    public VentanaPrincipal() {
        initComponents();
        
        //Crea el grupo de botones de la barra de herramientas para que solo haya uno seleccionado a la vez
        BotonesHerramientas.add(punto);
        BotonesHerramientas.add(linea);
        BotonesHerramientas.add(rectangulo);
        BotonesHerramientas.add(elipse);
        BotonesHerramientas.add(curva);
        BotonesHerramientas.add(texto);
        BotonesHerramientas.add(editar);
        
        //Crea el grupo de botones para el relleno
        TiposRelleno.add(sinRelleno);
        TiposRelleno.add(rellenar);
        TiposRelleno.add(degradado);
        
        //Establece el spiner como mínimo 1
        SpinnerNumberModel nm = new SpinnerNumberModel();
        nm.setValue(1);
        nm.setMinimum(1);
        grosor.setModel(nm);
        
        //Establece el render para el combobox de seleccion de colores
        colorSelector.setModel(new DefaultComboBoxModel(colors));
        colorSelector.setRenderer(new ColorComboBoxRenderer());
        colorSelectorRelleno.setModel(new DefaultComboBoxModel(colors));
        colorSelectorRelleno.setRenderer(new ColorComboBoxRenderer());
        colorSelectorDegradado.setModel(new DefaultComboBoxModel(colors));
        colorSelectorDegradado.setRenderer(new ColorComboBoxRenderer());
        
        //Establece la seleccion de los tipos de letra del sistema
        GraphicsEnvironment ge;
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String []fuentesSistema = ge.getAvailableFontFamilyNames();
        fuenteTexto.setModel(new DefaultComboBoxModel(fuentesSistema));
        
        //Establece el spiner del texto como mínimo 1
        SpinnerNumberModel nm2 = new SpinnerNumberModel();
        nm2.setValue(12);
        nm2.setMinimum(1);
        sizeLetra.setModel(nm2);
        
        //Oculta vistas
        direccionDegradado.setVisible(false);
        colorSelectorRelleno.setVisible(false);
        colorSelectorDegradado.setVisible(false);
        sliderTransparencia.setVisible(false);
        
    }
    
    public VentanaInterna GetVentanaImagenInternaActiva () {
        
        if (escritorio.getSelectedFrame() instanceof  VentanaInterna)
            return (VentanaInterna)escritorio.getSelectedFrame();
            
        return null;
    }
    
    public VentanaInternaVideoMedia GetVentanaImagenInternaVideoMediaActiva () {
        
        if (escritorio.getSelectedFrame() instanceof  VentanaInternaVideoMedia)
            return (VentanaInternaVideoMedia)escritorio.getSelectedFrame();
            
        return null;
    }

    public void NuevaVentanaReproduccion (File file) {
        
        VentanaInternaReproducir vir = new VentanaInternaReproducir (file);
        this.escritorio.add(vir);
        vir.setTitle(file.getName());
        vir.setVisible(true);
    }
    
    
    public void NuevaVentanaGrabacion () {
    
        JFileChooser dlg = new JFileChooser();
        
        //Establece los posibles formatos para guardar
        dlg.setAcceptAllFileFilterUsed(false);
        String[] formatos = new String[] {"wav"};
        FileFilter filtroArchivos = new FileNameExtensionFilter(formatos[0], formatos[0]);
        dlg.addChoosableFileFilter(filtroArchivos);

        int resp = dlg.showSaveDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();

                String extension = ((FileNameExtensionFilter)dlg.getFileFilter()).getExtensions()[0];

                int indice = f.getName().lastIndexOf(".");
                if (indice < 0 || !f.getName().substring(indice + 1).equals(extension))
                    f = new File(dlg.getSelectedFile() + "." + extension);

                VentanaInternaGrabacion vig = new VentanaInternaGrabacion (f);
                this.escritorio.add(vig);
                vig.setTitle(f.getName());
                vig.setVisible(true);
            }catch (Exception ex) {
                System.err.println("Error al guardar el audio");
            }
        }
    }
    
    public void NuevaVentanaVideo (File file) {
        
        VentanaInternaVLCPlayer vig = VentanaInternaVLCPlayer.getInstance(file);
        this.escritorio.add(vig);
        vig.setTitle(file.getName());
        vig.setVisible(true);
    }
    
    public void NuevaVentanaInterna () {
        
        VentanaInterna vi = new VentanaInterna(this, anchoLienzo, altoLienzo);
        vi.setTitle("Nueva Imagen");
        if (GetVentanaImagenInternaActiva() != null)
            vi.setBounds(GetVentanaImagenInternaActiva().getBounds().x + 20, GetVentanaImagenInternaActiva().getBounds().y + 20, vi.getWidth(), vi.getHeight());
        escritorio.add(vi);
        vi.setVisible(true);
        
        BufferedImage img;
        img = new BufferedImage(anchoLienzo,altoLienzo,BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        vi.getLienzo().setImagen(img);
    }
    
    public void NuevaVentanaInterna (String titulo, BufferedImage img) {
        VentanaInterna vi = new VentanaInterna(this, anchoLienzo, altoLienzo);
        vi.setTitle(titulo);
        if (GetVentanaImagenInternaActiva() != null)
            vi.setBounds(GetVentanaImagenInternaActiva().getBounds().x + 20, GetVentanaImagenInternaActiva().getBounds().y + 20, vi.getWidth(), vi.getHeight());
        escritorio.add(vi);
        vi.setVisible(true);
        
        vi.getLienzo().setImagen(img);
    }
    
    public void setHerramienta (HerramientasDibujo herramienta) {
        switch (herramienta) {
        case PUNTO:
            punto.setSelected(true);
            estado.setText("Punto");
            break;
        case LINEA:
            linea.setSelected(true);
            estado.setText("Linea");
            break;
        case RECTANGULO:
            rectangulo.setSelected(true);
            estado.setText("Rectángulo");
            break;
        case ELIPSE:
            elipse.setSelected(true);
            estado.setText("Elipse");
            break;
        case CURVA:
            curva.setSelected(true);
            estado.setText("Curva");
            break;
        case TEXTO:
            texto.setSelected(true);
            estado.setText("Texto");
            break;
        case SELECCION:
            editar.setSelected(true);
            estado.setText("Selección");
            break;
        }
        
        if (GetVentanaImagenInternaActiva() != null)
            herramientasTexto.setVisible(GetVentanaImagenInternaActiva().getLienzo().editandoTexto() || herramienta == HerramientasDibujo.TEXTO);
        else
            herramientasTexto.setVisible(herramienta == HerramientasDibujo.TEXTO);
    }
    
    public void setColorTrazo (Color color) {
        userChange = false;
        colorSelector.setEditable(true);
        colorSelector.setSelectedItem(color);
        colorSelector.setEditable(false);
        userChange = true;
    }
    
    public void setColorRelleno (Color color) {
        userChange = false;
        colorSelectorRelleno.setEditable(true);
        colorSelectorRelleno.setSelectedItem(color);
        colorSelectorRelleno.setEditable(false);
        userChange = true;
    }
    
    public void setColorDegradado (Color color) {
        userChange = false;
        colorSelectorDegradado.setEditable(true);
        colorSelectorDegradado.setSelectedItem(color);
        colorSelectorDegradado.setEditable(false);
        userChange = true;
    }
    
    public void setTrazo (boolean discontinuo) {
        tipoTrazo.setSelected(discontinuo);
    }
    
    public void setGrosorLinea (int grosor) {
        this.grosor.setValue(grosor);
    }
    
    public void setRelleno (boolean relleno, boolean degradado) {
        rellenar.setSelected(relleno);
        this.degradado.setSelected(degradado);
        sinRelleno.setSelected(!(relleno || degradado));
        
        direccionDegradado.setVisible(degradado);
        colorSelectorDegradado.setVisible(degradado);
        colorSelectorRelleno.setVisible(relleno || degradado);
    }
    
    public void setDegradado (boolean enable) {
        userChange = false;
        degradado.setSelected(enable);
        userChange = true;
    }
    
    public void setDireccionDegradado (boolean horizontal) {
        direccionDegradado.setSelected(horizontal);
    }
    
    public void setTransparencia (boolean enable) {
        userChange = false;
        transparencia.setSelected(enable);
        userChange = true;
    }
    
    public void setCantidadTransparecia (float transparencia) {
        sliderTransparencia.setValue((int) (transparencia * 10));
    }
    
    public void setAlisar (boolean enable) {
        suavizar.setSelected(enable);
    }
    
    public void setFuenteTexto (String fuente) {
        fuenteTexto.setSelectedItem(fuente);
    }
    
    public void setNegrita (boolean negrita) {
        this.negrita.setSelected(negrita);
    }
    
    public void setCursiva (boolean cursiva) {
        this.cursiva.setSelected(cursiva);
    }
    
    public void setSubrayado (boolean subrayado) {
        this.subrayado.setSelected(subrayado);
    }
    
    public void setTamanoLetra (int tamano) {
        sizeLetra.setValue(tamano);
    }
    
    public void setText (String text) {
        introducirTexto.setText(text);
    }
    
    public void setPosicion (Point posicion, Color color) {
        if (posicion != null)
            this.posicion.setText("(" + posicion.x + "," + posicion.y + ")=[" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "]");
        else
            this.posicion.setText("");
    }
    
    public void AbrirArchivo () {
        
        JFileChooser dlg = new JFileChooser();
        dlg.setPreferredSize(new Dimension(700,400));
        
        //Filtro para imágenes
        String[] formatosImagen = ImageIO.getReaderFormatNames();
        String formatosSoportados = "Imagenes [" + formatosImagen[0];
        for (int i = 1; i<formatosImagen.length; i++) {
            formatosSoportados += ", " + formatosImagen[i];
        }
        formatosSoportados += "]";
        FileFilter filtroArchivos = new FileNameExtensionFilter(formatosSoportados, formatosImagen);
        dlg.setFileFilter(filtroArchivos);
        
        //Filtro para audio
        String[] formatosAudio = new String [] {"au", "mid", "wav"};
        formatosSoportados = "Sonido [" + formatosAudio[0];
        for (int i = 1; i<formatosAudio.length; i++) {
            formatosSoportados += ", " + formatosAudio[i];
        }
        formatosSoportados += "]";
        filtroArchivos = new FileNameExtensionFilter(formatosSoportados, formatosAudio);
        dlg.addChoosableFileFilter(filtroArchivos);
        
        //Filtro para video
        String[] formatosVideo = VideoFileFilter.INSTANCE.getExtensions();
        formatosSoportados = "Video [" + formatosVideo[0];
        for (int i = 1; i<formatosVideo.length; i++) {
            formatosSoportados += ", " + formatosVideo[i];
        }
        formatosSoportados += "]";
        filtroArchivos = new FileNameExtensionFilter(formatosSoportados, formatosVideo);
        dlg.addChoosableFileFilter(filtroArchivos);
        
        //Muestra el dialogo del selector de archivos
        int resp = dlg.showOpenDialog(this);
        if( resp == JFileChooser.APPROVE_OPTION) {
            try{
                File f = dlg.getSelectedFile();
                String extension = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                
                for (String ext : formatosImagen) {
                    if (extension.equals(ext)) {
                        BufferedImage src = ImageIO.read(f);
                        NuevaVentanaInterna(f.getName(), src);
                        return;
                    }
                }
                        
                for (String ext : formatosAudio) {
                    if (extension.equals(ext)) {
                        NuevaVentanaReproduccion(f);
                        return;
                    }
                }
                
                for (String ext : formatosVideo) {
                    if (extension.equals(ext)) {
                        NuevaVentanaVideo(f);
                        return;
                    }
                }
                
                errorDialog.setLocationRelativeTo(this);
                errorDialog.setVisible(true);
                errorMessage.setText("Formato no reconocido");
                
            }catch(Exception ex){
                errorDialog.setLocationRelativeTo(this);
                errorDialog.setVisible(true);
                errorMessage.setText("Error al leer el archivo");
            }
        }
    }
    
    public void GuardarArchivo () {
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            
            JFileChooser dlg = new JFileChooser();
            
            //Establece los posibles formatos para guardar
            dlg.setAcceptAllFileFilterUsed(false);
            String[] formatos = ImageIO.getWriterFormatNames();
            for (int i = 0; i<formatos.length; i++) {
                
                FileFilter filtroArchivos = new FileNameExtensionFilter(formatos[i], formatos[i]);
                dlg.addChoosableFileFilter(filtroArchivos);
            }
            
            int resp = dlg.showSaveDialog(this);
            if (resp == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage img = vi.getLienzo().getImagen();
                    if (img != null) {
                        File f = dlg.getSelectedFile();
                        
                        String extension = ((FileNameExtensionFilter)dlg.getFileFilter()).getExtensions()[0];
                        
                        int indice = f.getName().lastIndexOf(".");
                        if (indice < 0 || !f.getName().substring(indice + 1).equals(extension))
                            f = new File(dlg.getSelectedFile() + "." + extension);
                        
                        ImageIO.write(img, extension, f);
                        vi.setTitle(f.getName());
                    }
                }catch (Exception ex) {
                    System.err.println("Error al guardar la imagen");
                }
            }
        }
    }
    
    public void AplicarFiltro (int index) {
        
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null){
                
                ColorModel cm = vi.getLienzo().getImagen().getColorModel();
                WritableRaster raster = vi.getLienzo().getImagen().copyData(null);
                boolean alfaPre = vi.getLienzo().getImagen().isAlphaPremultiplied();
                tmpIMG = new BufferedImage(cm,raster,alfaPre,null);
                
                try{
                    
                    Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_MEDIA_3x3);
                    
                    switch (index) {
                        case 1:
                            k = KernelProducer.createKernel(KernelProducer.TYPE_BINOMIAL_3x3);
                            break;
                        case 2:
                            k = KernelProducer.createKernel(KernelProducer.TYPE_ENFOQUE_3x3);
                            break;      
                        case 3:
                            k = KernelProducer.createKernel(KernelProducer.TYPE_RELIEVE_3x3);
                            break;
                        case 4:
                            k = KernelProducer.createKernel(KernelProducer.TYPE_LAPLACIANA_3x3);
                            break;
                    }
                    
                    ConvolveOp cop = new ConvolveOp(k,ConvolveOp.EDGE_NO_OP, null);
                    BufferedImage imgdest = cop.filter(tmpIMG, imgSource);
                    escritorio.repaint();
                } catch(IllegalArgumentException e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }
    
    public LookupTable seno(double w) {
        
        double K_value = 255.0; // Cte de normalización
        double valor_iteracion = ((Math.PI/2.0)/K_value);
        short valores[] = new short [256];
        
        // Código implementado f(x)=|sin(wx)|
        for (int i = 0; i < 256; i++)
            valores[i] = (short) (K_value * abs(sin(w*(valor_iteracion*i))));
        
        LookupTable lt = new ShortLookupTable(0, valores);
        
        return lt;
    }
    
    public BufferedImage RotateImage (BufferedImage img, int grados) {

        double r = Math.toRadians(grados);
        Point c = new Point(img.getWidth()/2, img.getHeight()/2);
        AffineTransform at = AffineTransform.getRotateInstance(r,c.x,c.y);
        AffineTransformOp atop;
        atop = new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR);
        BufferedImage imgdest = atop.filter(img, null);

        return imgdest;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BotonesHerramientas = new javax.swing.ButtonGroup();
        TiposRelleno = new javax.swing.ButtonGroup();
        TamannoDialog = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ancho = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        alto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        BotonCancelar = new javax.swing.JButton();
        BotonRedimensionar = new javax.swing.JButton();
        BotonEscalar = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        dialogoAcercaDe = new javax.swing.JDialog();
        nombrePrograma = new javax.swing.JLabel();
        autor = new javax.swing.JLabel();
        version = new javax.swing.JLabel();
        cerrarAcercaDe = new javax.swing.JButton();
        herramientasTexto = new javax.swing.JDialog();
        jPanel14 = new javax.swing.JPanel();
        fuenteTexto = new javax.swing.JComboBox<>();
        negrita = new javax.swing.JToggleButton();
        cursiva = new javax.swing.JToggleButton();
        subrayado = new javax.swing.JToggleButton();
        sizeLetra = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        introducirTexto = new javax.swing.JTextField();
        errorDialog = new javax.swing.JDialog();
        errorImage = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        errorTitle = new javax.swing.JLabel();
        errorMessage = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        aceptarError = new javax.swing.JButton();
        herramientas = new javax.swing.JToolBar();
        botnNuevo = new javax.swing.JButton();
        botonAbrir = new javax.swing.JButton();
        botonGuardar = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        punto = new javax.swing.JToggleButton();
        linea = new javax.swing.JToggleButton();
        rectangulo = new javax.swing.JToggleButton();
        elipse = new javax.swing.JToggleButton();
        curva = new javax.swing.JToggleButton();
        texto = new javax.swing.JToggleButton();
        editar = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        tipoTrazo = new javax.swing.JToggleButton();
        grosor = new javax.swing.JSpinner();
        colorSelector = new javax.swing.JComboBox<>();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        sinRelleno = new javax.swing.JToggleButton();
        rellenar = new javax.swing.JToggleButton();
        degradado = new javax.swing.JToggleButton();
        direccionDegradado = new javax.swing.JToggleButton();
        colorSelectorRelleno = new javax.swing.JComboBox<>();
        colorSelectorDegradado = new javax.swing.JComboBox<>();
        transparencia = new javax.swing.JToggleButton();
        sliderTransparencia = new javax.swing.JSlider();
        suavizar = new javax.swing.JToggleButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        copiar = new javax.swing.JButton();
        capturar = new javax.swing.JButton();
        panelInferior = new javax.swing.JPanel();
        estado = new javax.swing.JLabel();
        posicion = new javax.swing.JLabel();
        edicionImagen = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        brillo = new javax.swing.JSlider();
        jPanel2 = new javax.swing.JPanel();
        filtro = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        contraste = new javax.swing.JButton();
        iluminar = new javax.swing.JButton();
        oscurecer = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        sinusoidal = new javax.swing.JButton();
        sepia = new javax.swing.JButton();
        tintar = new javax.swing.JButton();
        ecualizar = new javax.swing.JButton();
        negativo = new javax.swing.JButton();
        pixelizar = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        bandas = new javax.swing.JButton();
        seleccionEspacioColor = new javax.swing.JComboBox<>();
        jPanel9 = new javax.swing.JPanel();
        sliderRotacion = new javax.swing.JSlider();
        noventaGrados = new javax.swing.JButton();
        cientoochentaGrados = new javax.swing.JButton();
        doscientossetentaGrados = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        aumentarEscala = new javax.swing.JButton();
        disminuirEscala = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        umbral = new javax.swing.JSlider();
        escritorio = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        Nuevo = new javax.swing.JMenuItem();
        Abir = new javax.swing.JMenuItem();
        Guardar = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        GrabarAudio = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        abrirWebCam = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        MostrarBarraEstado = new javax.swing.JCheckBoxMenuItem();
        mostrarBarraDeEdicion = new javax.swing.JCheckBoxMenuItem();
        MostrarBarraHerramientas = new javax.swing.JCheckBoxMenuItem();
        jMenu3 = new javax.swing.JMenu();
        TamannoImagen = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        acerca = new javax.swing.JMenuItem();

        TamannoDialog.setTitle("Tamaño Imagen");
        TamannoDialog.setAlwaysOnTop(true);
        TamannoDialog.setMinimumSize(new java.awt.Dimension(300, 200));
        TamannoDialog.setModal(true);
        TamannoDialog.setResizable(false);

        jPanel4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 20));

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel6);

        jPanel5.setLayout(new java.awt.GridLayout(2, 3, 10, 10));

        jLabel1.setText("Ancho:");
        jPanel5.add(jLabel1);

        ancho.setText("300");
        jPanel5.add(ancho);

        jLabel2.setText("px");
        jPanel5.add(jLabel2);

        jLabel3.setText("Alto:");
        jPanel5.add(jLabel3);

        alto.setText("300");
        jPanel5.add(alto);

        jLabel4.setText("px");
        jPanel5.add(jLabel4);

        jPanel4.add(jPanel5);

        TamannoDialog.getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout(5, 10));

        BotonCancelar.setText("Cancelar");
        BotonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCancelarActionPerformed(evt);
            }
        });
        jPanel3.add(BotonCancelar, java.awt.BorderLayout.LINE_END);

        BotonRedimensionar.setText("Redimensionar");
        BotonRedimensionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonRedimensionarActionPerformed(evt);
            }
        });
        jPanel3.add(BotonRedimensionar, java.awt.BorderLayout.CENTER);

        BotonEscalar.setText("Escalar");
        BotonEscalar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEscalarActionPerformed(evt);
            }
        });
        jPanel3.add(BotonEscalar, java.awt.BorderLayout.LINE_START);
        jPanel3.add(jSeparator4, java.awt.BorderLayout.PAGE_START);

        TamannoDialog.getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        dialogoAcercaDe.setTitle("Acerca De");
        dialogoAcercaDe.setAlwaysOnTop(true);
        dialogoAcercaDe.setMinimumSize(new java.awt.Dimension(400, 200));
        dialogoAcercaDe.setModal(true);
        dialogoAcercaDe.setResizable(false);
        dialogoAcercaDe.getContentPane().setLayout(new java.awt.GridLayout(4, 1, 10, 10));

        nombrePrograma.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        nombrePrograma.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nombrePrograma.setText("Sistema Multimedia En Java");
        dialogoAcercaDe.getContentPane().add(nombrePrograma);

        autor.setText("    Autor: Alejandro Guerrero Martínez");
        dialogoAcercaDe.getContentPane().add(autor);

        version.setText("    Versión: 1.15");
        dialogoAcercaDe.getContentPane().add(version);

        cerrarAcercaDe.setText("Cerrar");
        cerrarAcercaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarAcercaDeActionPerformed(evt);
            }
        });
        dialogoAcercaDe.getContentPane().add(cerrarAcercaDe);

        herramientasTexto.setTitle("Herramienta Texto");
        herramientasTexto.setAlwaysOnTop(true);
        herramientasTexto.setMinimumSize(new java.awt.Dimension(400, 120));
        herramientasTexto.setPreferredSize(new java.awt.Dimension(400, 120));
        herramientasTexto.setResizable(false);

        jPanel14.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        fuenteTexto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        fuenteTexto.setToolTipText("Fuente");
        fuenteTexto.setMinimumSize(new java.awt.Dimension(56, 33));
        fuenteTexto.setPreferredSize(new java.awt.Dimension(150, 33));
        fuenteTexto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fuenteTextoActionPerformed(evt);
            }
        });
        jPanel14.add(fuenteTexto);

        negrita.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/bold.png"))); // NOI18N
        negrita.setToolTipText("Negrita");
        negrita.setPreferredSize(new java.awt.Dimension(33, 33));
        negrita.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                negritaItemStateChanged(evt);
            }
        });
        jPanel14.add(negrita);

        cursiva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/italic.png"))); // NOI18N
        cursiva.setToolTipText("Cursiva");
        cursiva.setPreferredSize(new java.awt.Dimension(33, 33));
        cursiva.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cursivaItemStateChanged(evt);
            }
        });
        jPanel14.add(cursiva);

        subrayado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/underlined.png"))); // NOI18N
        subrayado.setToolTipText("Subrayado");
        subrayado.setPreferredSize(new java.awt.Dimension(33, 33));
        subrayado.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                subrayadoItemStateChanged(evt);
            }
        });
        jPanel14.add(subrayado);

        sizeLetra.setToolTipText("Tamaño de Letra");
        sizeLetra.setPreferredSize(new java.awt.Dimension(55, 33));
        sizeLetra.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeLetraStateChanged(evt);
            }
        });
        jPanel14.add(sizeLetra);

        herramientasTexto.getContentPane().add(jPanel14, java.awt.BorderLayout.NORTH);

        introducirTexto.setText("Lorem ipsum");
        introducirTexto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                introducirTextoActionPerformed(evt);
            }
        });
        jScrollPane1.setViewportView(introducirTexto);

        herramientasTexto.getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        errorDialog.setAlwaysOnTop(true);
        errorDialog.setBackground(new java.awt.Color(204, 204, 204));
        errorDialog.setMinimumSize(new java.awt.Dimension(460, 250));
        errorDialog.setModal(true);
        errorDialog.setResizable(false);

        errorImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/error.png"))); // NOI18N
        errorDialog.getContentPane().add(errorImage, java.awt.BorderLayout.LINE_START);

        jPanel13.setLayout(new java.awt.GridLayout(2, 1));

        errorTitle.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        errorTitle.setText("  Error:");
        jPanel13.add(errorTitle);

        errorMessage.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        errorMessage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        errorMessage.setText("No se puede acceder al archivo indicado.");
        jPanel13.add(errorMessage);

        errorDialog.getContentPane().add(jPanel13, java.awt.BorderLayout.CENTER);

        aceptarError.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        aceptarError.setText("Aceptar");
        aceptarError.setPreferredSize(new java.awt.Dimension(100, 30));
        aceptarError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aceptarErrorActionPerformed(evt);
            }
        });
        jPanel15.add(aceptarError);

        errorDialog.getContentPane().add(jPanel15, java.awt.BorderLayout.PAGE_END);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        herramientas.setRollover(true);

        botnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/nuevo.png"))); // NOI18N
        botnNuevo.setToolTipText("Nuevo");
        botnNuevo.setFocusable(false);
        botnNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botnNuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botnNuevoActionPerformed(evt);
            }
        });
        herramientas.add(botnNuevo);

        botonAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/abrir.png"))); // NOI18N
        botonAbrir.setToolTipText("Abrir");
        botonAbrir.setFocusable(false);
        botonAbrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonAbrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAbrirActionPerformed(evt);
            }
        });
        herramientas.add(botonAbrir);

        botonGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/guardar.png"))); // NOI18N
        botonGuardar.setToolTipText("Guardar");
        botonGuardar.setFocusable(false);
        botonGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGuardarActionPerformed(evt);
            }
        });
        herramientas.add(botonGuardar);
        herramientas.add(jSeparator1);

        punto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/punto.png"))); // NOI18N
        punto.setSelected(true);
        punto.setToolTipText("Punto");
        punto.setFocusable(false);
        punto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        punto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        punto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                puntoActionPerformed(evt);
            }
        });
        herramientas.add(punto);

        linea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/linea.png"))); // NOI18N
        linea.setToolTipText("Linea");
        linea.setFocusable(false);
        linea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        linea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        linea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineaActionPerformed(evt);
            }
        });
        herramientas.add(linea);

        rectangulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/rectangulo.png"))); // NOI18N
        rectangulo.setToolTipText("Rectángulo");
        rectangulo.setFocusable(false);
        rectangulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rectangulo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rectangulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rectanguloActionPerformed(evt);
            }
        });
        herramientas.add(rectangulo);

        elipse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/elipse.png"))); // NOI18N
        elipse.setToolTipText("Elipse");
        elipse.setFocusable(false);
        elipse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        elipse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        elipse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elipseActionPerformed(evt);
            }
        });
        herramientas.add(elipse);

        curva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/curva.png"))); // NOI18N
        curva.setToolTipText("Curva");
        curva.setFocusable(false);
        curva.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        curva.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        curva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                curvaActionPerformed(evt);
            }
        });
        herramientas.add(curva);

        texto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/texto.png"))); // NOI18N
        texto.setToolTipText("Texto");
        texto.setFocusable(false);
        texto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        texto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        texto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textoActionPerformed(evt);
            }
        });
        herramientas.add(texto);

        editar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/seleccion.png"))); // NOI18N
        editar.setToolTipText("Editar");
        editar.setFocusable(false);
        editar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarActionPerformed(evt);
            }
        });
        herramientas.add(editar);
        herramientas.add(jSeparator2);

        tipoTrazo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/linea_continua.png"))); // NOI18N
        tipoTrazo.setToolTipText("Tipo de trazo");
        tipoTrazo.setFocusable(false);
        tipoTrazo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tipoTrazo.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/linea_continua.png"))); // NOI18N
        tipoTrazo.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/linea_discontinua.png"))); // NOI18N
        tipoTrazo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tipoTrazo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoTrazoActionPerformed(evt);
            }
        });
        herramientas.add(tipoTrazo);

        grosor.setToolTipText("Grosor");
        grosor.setMinimumSize(new java.awt.Dimension(40, 30));
        grosor.setPreferredSize(new java.awt.Dimension(60, 28));
        grosor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                grosorStateChanged(evt);
            }
        });
        herramientas.add(grosor);

        colorSelector.setMaximumRowCount(10);
        colorSelector.setToolTipText("Color");
        colorSelector.setPreferredSize(new java.awt.Dimension(60, 28));
        colorSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                colorSelectorMouseClicked(evt);
            }
        });
        colorSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorSelectorActionPerformed(evt);
            }
        });
        herramientas.add(colorSelector);
        herramientas.add(jSeparator3);

        sinRelleno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/sin_relleno.png"))); // NOI18N
        sinRelleno.setSelected(true);
        sinRelleno.setToolTipText("Sin Relleno");
        sinRelleno.setFocusable(false);
        sinRelleno.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sinRelleno.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sinRelleno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sinRellenoActionPerformed(evt);
            }
        });
        herramientas.add(sinRelleno);

        rellenar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/rellenar.png"))); // NOI18N
        rellenar.setToolTipText("Relleno");
        rellenar.setFocusable(false);
        rellenar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rellenar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rellenar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rellenarActionPerformed(evt);
            }
        });
        herramientas.add(rellenar);

        degradado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/degradado.png"))); // NOI18N
        degradado.setToolTipText("Degradado");
        degradado.setFocusable(false);
        degradado.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        degradado.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        degradado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                degradadoActionPerformed(evt);
            }
        });
        herramientas.add(degradado);

        direccionDegradado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/vertical.png"))); // NOI18N
        direccionDegradado.setToolTipText("Dirección del degradado");
        direccionDegradado.setFocusable(false);
        direccionDegradado.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        direccionDegradado.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/vertical.png"))); // NOI18N
        direccionDegradado.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/horizontal.png"))); // NOI18N
        direccionDegradado.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        direccionDegradado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                direccionDegradadoActionPerformed(evt);
            }
        });
        herramientas.add(direccionDegradado);

        colorSelectorRelleno.setMaximumRowCount(10);
        colorSelectorRelleno.setToolTipText("Color de relleno");
        colorSelectorRelleno.setPreferredSize(new java.awt.Dimension(60, 28));
        colorSelectorRelleno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                colorSelectorRellenoMouseClicked(evt);
            }
        });
        colorSelectorRelleno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorSelectorRellenoActionPerformed(evt);
            }
        });
        herramientas.add(colorSelectorRelleno);

        colorSelectorDegradado.setMaximumRowCount(10);
        colorSelectorDegradado.setToolTipText("Segundo color de relleno");
        colorSelectorDegradado.setPreferredSize(new java.awt.Dimension(60, 28));
        colorSelectorDegradado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                colorSelectorDegradadoMouseClicked(evt);
            }
        });
        colorSelectorDegradado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorSelectorDegradadoActionPerformed(evt);
            }
        });
        herramientas.add(colorSelectorDegradado);

        transparencia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/transparencia.png"))); // NOI18N
        transparencia.setToolTipText("Transparencia");
        transparencia.setFocusable(false);
        transparencia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        transparencia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        transparencia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                transparenciaItemStateChanged(evt);
            }
        });
        herramientas.add(transparencia);

        sliderTransparencia.setMaximum(10);
        sliderTransparencia.setToolTipText("Transparencia de la imagen");
        sliderTransparencia.setValue(5);
        sliderTransparencia.setPreferredSize(new java.awt.Dimension(100, 26));
        sliderTransparencia.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderTransparenciaStateChanged(evt);
            }
        });
        herramientas.add(sliderTransparencia);

        suavizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/alisar.png"))); // NOI18N
        suavizar.setToolTipText("Alisar");
        suavizar.setFocusable(false);
        suavizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        suavizar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        suavizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suavizarActionPerformed(evt);
            }
        });
        herramientas.add(suavizar);
        herramientas.add(jSeparator8);

        copiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/copia.png"))); // NOI18N
        copiar.setToolTipText("Copiar Imagen");
        copiar.setFocusable(false);
        copiar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        copiar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        copiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copiarActionPerformed(evt);
            }
        });
        herramientas.add(copiar);

        capturar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/Capturar.png"))); // NOI18N
        capturar.setToolTipText("Hacer Captura");
        capturar.setFocusable(false);
        capturar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        capturar.setPreferredSize(new java.awt.Dimension(31, 33));
        capturar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        capturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                capturarActionPerformed(evt);
            }
        });
        herramientas.add(capturar);

        getContentPane().add(herramientas, java.awt.BorderLayout.PAGE_START);

        panelInferior.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelInferior.setLayout(new java.awt.BorderLayout());

        estado.setText("Punto");
        panelInferior.add(estado, java.awt.BorderLayout.CENTER);
        panelInferior.add(posicion, java.awt.BorderLayout.LINE_END);

        edicionImagen.setRollover(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Brillo"));

        brillo.setMaximum(250);
        brillo.setMinimum(-250);
        brillo.setToolTipText("Brillo imagen");
        brillo.setValue(0);
        brillo.setPreferredSize(new java.awt.Dimension(120, 26));
        brillo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                brilloStateChanged(evt);
            }
        });
        brillo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                brilloFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                brilloFocusLost(evt);
            }
        });
        jPanel1.add(brillo);

        edicionImagen.add(jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtro"));

        filtro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Media", "Binomial", "Enfoque", "Relieve", "Laplaciano" }));
        filtro.setToolTipText("Filtros");
        filtro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtroActionPerformed(evt);
            }
        });
        jPanel2.add(filtro);

        edicionImagen.add(jPanel2);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Contraste"));

        contraste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/contraste.png"))); // NOI18N
        contraste.setToolTipText("Contraste");
        contraste.setPreferredSize(new java.awt.Dimension(30, 26));
        contraste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contrasteActionPerformed(evt);
            }
        });
        jPanel7.add(contraste);

        iluminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/iluminar.png"))); // NOI18N
        iluminar.setToolTipText("Iluminar");
        iluminar.setPreferredSize(new java.awt.Dimension(30, 26));
        iluminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iluminarActionPerformed(evt);
            }
        });
        jPanel7.add(iluminar);

        oscurecer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/oscurecer.png"))); // NOI18N
        oscurecer.setToolTipText("Oscurecer");
        oscurecer.setPreferredSize(new java.awt.Dimension(30, 26));
        oscurecer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oscurecerActionPerformed(evt);
            }
        });
        jPanel7.add(oscurecer);

        edicionImagen.add(jPanel7);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(" "));

        sinusoidal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/sinusoidal.png"))); // NOI18N
        sinusoidal.setToolTipText("Sinusoidal");
        sinusoidal.setPreferredSize(new java.awt.Dimension(40, 26));
        sinusoidal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sinusoidalActionPerformed(evt);
            }
        });
        jPanel8.add(sinusoidal);

        sepia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/sepia.png"))); // NOI18N
        sepia.setToolTipText("Sepia");
        sepia.setPreferredSize(new java.awt.Dimension(30, 26));
        sepia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sepiaActionPerformed(evt);
            }
        });
        jPanel8.add(sepia);

        tintar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/tintar.png"))); // NOI18N
        tintar.setToolTipText("Tintado");
        tintar.setPreferredSize(new java.awt.Dimension(30, 26));
        tintar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tintarActionPerformed(evt);
            }
        });
        jPanel8.add(tintar);

        ecualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/ecualizar.png"))); // NOI18N
        ecualizar.setToolTipText("Ecualizar");
        ecualizar.setPreferredSize(new java.awt.Dimension(30, 26));
        ecualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ecualizarActionPerformed(evt);
            }
        });
        jPanel8.add(ecualizar);

        negativo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/negativo.png"))); // NOI18N
        negativo.setToolTipText("Negativo");
        negativo.setPreferredSize(new java.awt.Dimension(30, 26));
        negativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                negativoActionPerformed(evt);
            }
        });
        jPanel8.add(negativo);

        pixelizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/pixelizado.png"))); // NOI18N
        pixelizar.setToolTipText("Pixelizar");
        pixelizar.setPreferredSize(new java.awt.Dimension(30, 26));
        pixelizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pixelizarActionPerformed(evt);
            }
        });
        jPanel8.add(pixelizar);

        edicionImagen.add(jPanel8);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Color"));

        bandas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/bandas.png"))); // NOI18N
        bandas.setToolTipText("Dividir bandas");
        bandas.setPreferredSize(new java.awt.Dimension(30, 26));
        bandas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bandasActionPerformed(evt);
            }
        });
        jPanel11.add(bandas);

        seleccionEspacioColor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "sRGB", "YCC", "GREY", "YCbCr" }));
        seleccionEspacioColor.setToolTipText("Cambiar espacio de color");
        seleccionEspacioColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionEspacioColorActionPerformed(evt);
            }
        });
        jPanel11.add(seleccionEspacioColor);

        edicionImagen.add(jPanel11);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Rotacion"));

        sliderRotacion.setMaximum(360);
        sliderRotacion.setMinorTickSpacing(90);
        sliderRotacion.setPaintTicks(true);
        sliderRotacion.setToolTipText("Rotacion");
        sliderRotacion.setValue(0);
        sliderRotacion.setPreferredSize(new java.awt.Dimension(80, 26));
        sliderRotacion.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderRotacionStateChanged(evt);
            }
        });
        sliderRotacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderRotacionFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderRotacionFocusLost(evt);
            }
        });
        jPanel9.add(sliderRotacion);

        noventaGrados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/rotacion90.png"))); // NOI18N
        noventaGrados.setToolTipText("Rotar 90º");
        noventaGrados.setPreferredSize(new java.awt.Dimension(30, 26));
        noventaGrados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noventaGradosActionPerformed(evt);
            }
        });
        jPanel9.add(noventaGrados);

        cientoochentaGrados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/rotacion180.png"))); // NOI18N
        cientoochentaGrados.setToolTipText("Rotar 180º");
        cientoochentaGrados.setPreferredSize(new java.awt.Dimension(30, 26));
        cientoochentaGrados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cientoochentaGradosActionPerformed(evt);
            }
        });
        jPanel9.add(cientoochentaGrados);

        doscientossetentaGrados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/rotacion270.png"))); // NOI18N
        doscientossetentaGrados.setToolTipText("Rotar 270º");
        doscientossetentaGrados.setPreferredSize(new java.awt.Dimension(30, 26));
        doscientossetentaGrados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doscientossetentaGradosActionPerformed(evt);
            }
        });
        jPanel9.add(doscientossetentaGrados);

        edicionImagen.add(jPanel9);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Escala"));

        aumentarEscala.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/aumentar.png"))); // NOI18N
        aumentarEscala.setToolTipText("Aumentar escala");
        aumentarEscala.setPreferredSize(new java.awt.Dimension(30, 26));
        aumentarEscala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aumentarEscalaActionPerformed(evt);
            }
        });
        jPanel10.add(aumentarEscala);

        disminuirEscala.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/disminuir.png"))); // NOI18N
        disminuirEscala.setToolTipText("Disminuir escala");
        disminuirEscala.setPreferredSize(new java.awt.Dimension(30, 26));
        disminuirEscala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disminuirEscalaActionPerformed(evt);
            }
        });
        jPanel10.add(disminuirEscala);

        edicionImagen.add(jPanel10);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Umbralizacion"));

        umbral.setMaximum(255);
        umbral.setToolTipText("Grado de Umbralización");
        umbral.setValue(127);
        umbral.setPreferredSize(new java.awt.Dimension(120, 26));
        umbral.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                umbralStateChanged(evt);
            }
        });
        umbral.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                umbralFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                umbralFocusLost(evt);
            }
        });
        jPanel12.add(umbral);

        edicionImagen.add(jPanel12);

        panelInferior.add(edicionImagen, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(panelInferior, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1324, Short.MAX_VALUE)
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 565, Short.MAX_VALUE)
        );

        getContentPane().add(escritorio, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Archivo");

        Nuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK));
        Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/nuevo.png"))); // NOI18N
        Nuevo.setText("Nuevo");
        Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NuevoActionPerformed(evt);
            }
        });
        jMenu1.add(Nuevo);

        Abir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        Abir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/abrir.png"))); // NOI18N
        Abir.setText("Abrir");
        Abir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbirActionPerformed(evt);
            }
        });
        jMenu1.add(Abir);

        Guardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.ALT_MASK));
        Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/guardar.png"))); // NOI18N
        Guardar.setText("Guardar");
        Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuardarActionPerformed(evt);
            }
        });
        jMenu1.add(Guardar);
        jMenu1.add(jSeparator5);

        GrabarAudio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/record24x24.png"))); // NOI18N
        GrabarAudio.setText("Grabar Audio");
        GrabarAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GrabarAudioActionPerformed(evt);
            }
        });
        jMenu1.add(GrabarAudio);
        jMenu1.add(jSeparator6);

        abrirWebCam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/Camara.png"))); // NOI18N
        abrirWebCam.setText("Abrir WebCam");
        abrirWebCam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirWebCamActionPerformed(evt);
            }
        });
        jMenu1.add(abrirWebCam);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edición");

        MostrarBarraEstado.setSelected(true);
        MostrarBarraEstado.setText("Ver barra de estados");
        MostrarBarraEstado.setToolTipText("");
        MostrarBarraEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MostrarBarraEstadoActionPerformed(evt);
            }
        });
        jMenu2.add(MostrarBarraEstado);

        mostrarBarraDeEdicion.setSelected(true);
        mostrarBarraDeEdicion.setText("Ver barra de edicion de imágenes");
        mostrarBarraDeEdicion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrarBarraDeEdicionActionPerformed(evt);
            }
        });
        jMenu2.add(mostrarBarraDeEdicion);

        MostrarBarraHerramientas.setSelected(true);
        MostrarBarraHerramientas.setText("Ver barra de herramientas");
        MostrarBarraHerramientas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MostrarBarraHerramientasActionPerformed(evt);
            }
        });
        jMenu2.add(MostrarBarraHerramientas);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Imagen");

        TamannoImagen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        TamannoImagen.setText("Tamaño Nueva Imagen");
        TamannoImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TamannoImagenActionPerformed(evt);
            }
        });
        jMenu3.add(TamannoImagen);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Ayuda");

        acerca.setText("Acerca de");
        acerca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acercaActionPerformed(evt);
            }
        });
        jMenu4.add(acerca);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NuevoActionPerformed
        NuevaVentanaInterna();
    }//GEN-LAST:event_NuevoActionPerformed

    private void MostrarBarraEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MostrarBarraEstadoActionPerformed
        estado.setVisible(MostrarBarraEstado.isSelected());
    }//GEN-LAST:event_MostrarBarraEstadoActionPerformed

    private void MostrarBarraHerramientasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MostrarBarraHerramientasActionPerformed
        herramientas.setVisible(MostrarBarraHerramientas.isSelected());
    }//GEN-LAST:event_MostrarBarraHerramientasActionPerformed

    private void AbirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AbirActionPerformed
        AbrirArchivo();
    }//GEN-LAST:event_AbirActionPerformed

    private void GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarActionPerformed
        GuardarArchivo();
    }//GEN-LAST:event_GuardarActionPerformed

    private void puntoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_puntoActionPerformed
        if (GetVentanaImagenInternaActiva() != null) {
            GetVentanaImagenInternaActiva().getLienzo().setHerramienta(HerramientasDibujo.PUNTO);
            GetVentanaImagenInternaActiva().ActualizarPropiedadesEnVentanaPrincipal();
        }
        herramientasTexto.setVisible(false);
        estado.setText("Punto");
    }//GEN-LAST:event_puntoActionPerformed

    private void lineaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineaActionPerformed
        if (GetVentanaImagenInternaActiva() != null) {
            GetVentanaImagenInternaActiva().getLienzo().setHerramienta(HerramientasDibujo.LINEA);
            GetVentanaImagenInternaActiva().ActualizarPropiedadesEnVentanaPrincipal();
        }
        herramientasTexto.setVisible(false);
        estado.setText("Linea");
    }//GEN-LAST:event_lineaActionPerformed

    private void rectanguloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectanguloActionPerformed
        if (GetVentanaImagenInternaActiva() != null) {
            GetVentanaImagenInternaActiva().getLienzo().setHerramienta(HerramientasDibujo.RECTANGULO);
            GetVentanaImagenInternaActiva().ActualizarPropiedadesEnVentanaPrincipal();
        }
        herramientasTexto.setVisible(false);
        estado.setText("Rectángulo");
    }//GEN-LAST:event_rectanguloActionPerformed

    private void elipseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elipseActionPerformed
        if (GetVentanaImagenInternaActiva() != null) {
            GetVentanaImagenInternaActiva().getLienzo().setHerramienta(HerramientasDibujo.ELIPSE);
            GetVentanaImagenInternaActiva().ActualizarPropiedadesEnVentanaPrincipal();
        }
        herramientasTexto.setVisible(false);
        estado.setText("Elipse");
    }//GEN-LAST:event_elipseActionPerformed

    private void suavizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suavizarActionPerformed
        if (GetVentanaImagenInternaActiva() != null)
            GetVentanaImagenInternaActiva().getLienzo().setAlisar(suavizar.isSelected());
    }//GEN-LAST:event_suavizarActionPerformed

    private void botnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botnNuevoActionPerformed
        NuevaVentanaInterna();
    }//GEN-LAST:event_botnNuevoActionPerformed

    private void botonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAbrirActionPerformed
        AbrirArchivo();
    }//GEN-LAST:event_botonAbrirActionPerformed

    private void botonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGuardarActionPerformed
        GuardarArchivo();
    }//GEN-LAST:event_botonGuardarActionPerformed

    private void editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarActionPerformed
        if (GetVentanaImagenInternaActiva() != null) {
            GetVentanaImagenInternaActiva().getLienzo().setHerramienta(HerramientasDibujo.SELECCION);
            GetVentanaImagenInternaActiva().ActualizarPropiedadesEnVentanaPrincipal();
            herramientasTexto.setVisible(GetVentanaImagenInternaActiva().getLienzo().editandoTexto());
        } else {
            herramientasTexto.setVisible(false);
        }
        estado.setText("Selección");
    }//GEN-LAST:event_editarActionPerformed

    private void colorSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorSelectorActionPerformed
        if (GetVentanaImagenInternaActiva() != null && userChange) 
            GetVentanaImagenInternaActiva().getLienzo().setColorTrazo((Color) colorSelector.getSelectedItem());
    }//GEN-LAST:event_colorSelectorActionPerformed

    private void grosorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_grosorStateChanged
        if (GetVentanaImagenInternaActiva() != null)
            GetVentanaImagenInternaActiva().getLienzo().setGrosor((int)grosor.getValue());
    }//GEN-LAST:event_grosorStateChanged

    private void brilloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_brilloFocusGained
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImagen().getColorModel();
            WritableRaster raster = vi.getLienzo().getImagen().copyData(null);
            boolean alfaPre = vi.getLienzo().getImagen().isAlphaPremultiplied();
            tmpIMG = new BufferedImage(cm,raster,alfaPre,null);
        }
    }//GEN-LAST:event_brilloFocusGained

    private void brilloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_brilloFocusLost
        tmpIMG = null;
        brillo.setValue(0);
    }//GEN-LAST:event_brilloFocusLost

    private void brilloStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_brilloStateChanged
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            if(tmpIMG!=null){
                try{
                    RescaleOp rop;
                    float[] scales = {1f, 1f, 1f, 1f};
                    float[] offsets = {brillo.getValue(), brillo.getValue(), brillo.getValue(), 0f};
                    
                    if (tmpIMG.getColorModel().hasAlpha())
                        rop = new RescaleOp(scales, offsets, null);
                    else
                        rop = new RescaleOp(1.0f, brillo.getValue(), null);
                    
                    rop.filter(tmpIMG, vi.getLienzo().getImagen());
                    
                    escritorio.repaint();
                } catch(IllegalArgumentException e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_brilloStateChanged

    private void BotonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCancelarActionPerformed
        TamannoDialog.setVisible(false);
    }//GEN-LAST:event_BotonCancelarActionPerformed

    private void BotonRedimensionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonRedimensionarActionPerformed
        
        anchoLienzo = Integer.parseInt(ancho.getText());
        altoLienzo = Integer.parseInt(alto.getText());
        
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            
            if (anchoLienzo > vi.getLienzo().getAncho() || altoLienzo > vi.getLienzo().getAlto()) {
            
                BufferedImage img = new BufferedImage(anchoLienzo,altoLienzo,BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(Color.white);
                g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
                g2d.drawImage(vi.getLienzo().getImagen(),0,0,null);
                
                vi.getLienzo().setImagen(img);
            }
            
            vi.getLienzo().setTamanno(anchoLienzo, altoLienzo);
        }
        
        BotonCancelarActionPerformed(evt);
    }//GEN-LAST:event_BotonRedimensionarActionPerformed

    private void BotonEscalarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEscalarActionPerformed
        
        anchoLienzo = Integer.parseInt(ancho.getText());
        altoLienzo = Integer.parseInt(alto.getText());
        
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            vi.getLienzo().setTamanno(anchoLienzo, altoLienzo);
         
            BufferedImage imgOriginal = vi.getLienzo().getImagen();
            BufferedImage imgEscalada = new BufferedImage(anchoLienzo, altoLienzo, imgOriginal.getType());
            Graphics2D g2d = imgEscalada.createGraphics();
            g2d.drawImage(vi.getLienzo().getImagen(), 0, 0, anchoLienzo, altoLienzo, 0, 0, 
                    vi.getLienzo().getImagen().getWidth(), vi.getLienzo().getImagen().getHeight(), null);
            vi.getLienzo().setImagen(imgEscalada);
        }
        
        BotonCancelarActionPerformed(evt);
    }//GEN-LAST:event_BotonEscalarActionPerformed

    private void filtroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroActionPerformed
        AplicarFiltro(filtro.getSelectedIndex());
    }//GEN-LAST:event_filtroActionPerformed

    private void contrasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contrasteActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null){
                try{
                    int type = LookupTableProducer.TYPE_SFUNCION;
                    LookupTable lt = LookupTableProducer.createLookupTable(type);
                    LookupOp lop = new LookupOp(lt, null);
                    // Imagen origen y destino iguales
                    lop.filter( imgSource , imgSource);
                    escritorio.repaint();
                } catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_contrasteActionPerformed

    private void TamannoImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TamannoImagenActionPerformed
        TamannoDialog.setLocationRelativeTo(this);
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            ancho.setText(String.valueOf(vi.getLienzo().getAncho()));
            alto.setText(String.valueOf(vi.getLienzo().getAlto()));
        }else  {
            ancho.setText(String.valueOf(anchoLienzo));
            alto.setText(String.valueOf(altoLienzo));
        }
        TamannoDialog.setVisible(true);
    }//GEN-LAST:event_TamannoImagenActionPerformed

    private void iluminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iluminarActionPerformed
         VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null){
                try{
                    int type = LookupTableProducer.TYPE_ROOT;
                    LookupTable lt = LookupTableProducer.createLookupTable(type);
                    LookupOp lop = new LookupOp(lt, null);
                    // Imagen origen y destino iguales
                    lop.filter( imgSource , imgSource);
                    escritorio.repaint();
                } catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_iluminarActionPerformed

    private void oscurecerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oscurecerActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null){
                try{
                    int type = LookupTableProducer.TYPE_POWER;
                    LookupTable lt = LookupTableProducer.createLookupTable(type);
                    LookupOp lop = new LookupOp(lt, null);
                    // Imagen origen y destino iguales
                    lop.filter( imgSource , imgSource);
                    escritorio.repaint();
                } catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_oscurecerActionPerformed

    private void sliderRotacionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderRotacionStateChanged
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            if(tmpIMG!=null){
                vi.getLienzo().setImagen(RotateImage(tmpIMG, sliderRotacion.getValue()));
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_sliderRotacionStateChanged

    private void sliderRotacionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderRotacionFocusGained
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImagen().getColorModel();
            WritableRaster raster = vi.getLienzo().getImagen().copyData(null);
            boolean alfaPre = vi.getLienzo().getImagen().isAlphaPremultiplied();
            tmpIMG = new BufferedImage(cm,raster,alfaPre,null);
        }
    }//GEN-LAST:event_sliderRotacionFocusGained

    private void sliderRotacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderRotacionFocusLost
        tmpIMG = null;
    }//GEN-LAST:event_sliderRotacionFocusLost

    private void noventaGradosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noventaGradosActionPerformed
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if (imgSource != null) {
                vi.getLienzo().setImagen(RotateImage(imgSource, 90));
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_noventaGradosActionPerformed

    private void cientoochentaGradosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cientoochentaGradosActionPerformed
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if (imgSource != null) {
                vi.getLienzo().setImagen(RotateImage(imgSource, 180));
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_cientoochentaGradosActionPerformed

    private void doscientossetentaGradosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doscientossetentaGradosActionPerformed
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if (imgSource != null) {
                vi.getLienzo().setImagen(RotateImage(imgSource, 270));
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_doscientossetentaGradosActionPerformed

    private void aumentarEscalaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aumentarEscalaActionPerformed
        
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if (imgSource != null) {
                AffineTransform transform = AffineTransform.getScaleInstance(1.25, 1.25);
                AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                BufferedImage imgdest = op.filter(imgSource, null);
                vi.getLienzo().setImagen(imgdest);
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_aumentarEscalaActionPerformed

    private void disminuirEscalaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disminuirEscalaActionPerformed
        
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if (imgSource != null) {
                AffineTransform transform = AffineTransform.getScaleInstance(0.75, 0.75);
                AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                BufferedImage imgdest = op.filter(imgSource, null);
                vi.getLienzo().setImagen(imgdest);
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_disminuirEscalaActionPerformed

    private void sinusoidalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sinusoidalActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null){
                
                LookupTable lt = seno(2.0);
                LookupOp lop = new LookupOp(lt, null);
                // Imagen origen y destino iguales
                lop.filter(imgSource, imgSource);
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_sinusoidalActionPerformed

    private void sepiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sepiaActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null){
                SepiaOp sep = new SepiaOp();
                // Imagen origen y destino iguales
                sep.filter( imgSource , imgSource);
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_sepiaActionPerformed

    private void bandasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bandasActionPerformed
        
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null) {
                
                String title = vi.getTitle();
                
                //Creamos el modelo de color de la nueva imagen basado en un espcio de color GRAY
                ColorSpace cs = new sm.image.color.GreyColorSpace();
                ComponentColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
                int bandList[] = {0};
                
                for (int i = 0; i < imgSource.getRaster().getNumBands(); i++) {
                
                    bandList[0] = i;
                    //Creamos el nuevo raster a partir del raster de la imagen original
                    WritableRaster bandRaster = (WritableRaster)imgSource.getRaster().createWritableChild(0,0,
                        imgSource.getWidth(), imgSource.getHeight(), 0, 0, bandList);
                    //Creamos una nueva imagen que contiene como raster el correspondiente a la banda
                    BufferedImage imgBanda = new BufferedImage(cm, bandRaster, false, null);
                    NuevaVentanaInterna (title + " [Banda " + i + "]", imgBanda);
                }
                
            }
        }
    }//GEN-LAST:event_bandasActionPerformed

    private void seleccionEspacioColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionEspacioColorActionPerformed
        
         VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage src = vi.getLienzo().getImagen();
            if(src!=null) {
                
                String title = vi.getTitle();
                
                if (seleccionEspacioColor.getSelectedItem() == "sRGB") {
                    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                    ColorConvertOp cop = new ColorConvertOp(cs, null);
                    BufferedImage imgOut = cop.filter(src, null);
                    NuevaVentanaInterna(title + "[sRGB]", imgOut);
                }
                
                if (seleccionEspacioColor.getSelectedItem() == "YCC") {
                    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_PYCC);
                    ColorConvertOp cop = new ColorConvertOp(cs, null);
                    BufferedImage imgOut = cop.filter(src, null);
                    NuevaVentanaInterna(title + "[YCC]", imgOut);
                }
                
                if (seleccionEspacioColor.getSelectedItem() == "GREY") {
                    ColorSpace cs = new sm.image.color.GreyColorSpace();
                    ColorConvertOp cop = new ColorConvertOp(cs, null);
                    BufferedImage imgOut = cop.filter(src, null);
                    NuevaVentanaInterna(title + "[GREY]", imgOut);
                }
                
                if (seleccionEspacioColor.getSelectedItem() == "YCbCr") {
                    ColorSpace cs = new sm.image.color.YCbCrColorSpace();
                    ColorConvertOp cop = new ColorConvertOp(cs, null);
                    BufferedImage imgOut = cop.filter(src, null);
                    NuevaVentanaInterna(title + "[YCbCr]", imgOut);
                }
            }
        }
        
    }//GEN-LAST:event_seleccionEspacioColorActionPerformed

    private void tintarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tintarActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null){
                TintOp tintado = new TintOp((Color) colorSelector.getSelectedItem(), 0.5f);
                tintado.filter(imgSource, imgSource);
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_tintarActionPerformed

    private void ecualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ecualizarActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null){
                EqualizationOp ecualizacion = new EqualizationOp();
                ecualizacion.filter(imgSource, imgSource);
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_ecualizarActionPerformed

    private void umbralFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_umbralFocusGained
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            ColorModel cm = vi.getLienzo().getImagen().getColorModel();
            WritableRaster raster = vi.getLienzo().getImagen().copyData(null);
            boolean alfaPre = vi.getLienzo().getImagen().isAlphaPremultiplied();
            tmpIMG = new BufferedImage(cm,raster,alfaPre,null);
        }
    }//GEN-LAST:event_umbralFocusGained

    private void umbralFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_umbralFocusLost
        tmpIMG = null;
        umbral.setValue(127);
    }//GEN-LAST:event_umbralFocusLost

    private void umbralStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_umbralStateChanged
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            if(tmpIMG!=null){
                try{
                    UmbralizacionOp uop = new UmbralizacionOp(umbral.getValue());
                    uop.filter(tmpIMG, vi.getLienzo().getImagen());
                    escritorio.repaint();
                } catch(IllegalArgumentException e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_umbralStateChanged

    private void GrabarAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GrabarAudioActionPerformed
        NuevaVentanaGrabacion ();
    }//GEN-LAST:event_GrabarAudioActionPerformed

    private void abrirWebCamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirWebCamActionPerformed
        
        VentanaInternaCamara vic = VentanaInternaCamara.getInstance();
        if (vic != null) {
            escritorio.add(vic);
            vic.setTitle("WebCam");
            vic.setVisible(true);
        }
    }//GEN-LAST:event_abrirWebCamActionPerformed

    private void capturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_capturarActionPerformed
        VentanaInternaVideoMedia vivm = GetVentanaImagenInternaVideoMediaActiva();
        if (vivm != null) {
            BufferedImage tmp = vivm.GetCapture();
            BufferedImage captura = new BufferedImage (tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g2d = (Graphics2D) captura.getGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            NuevaVentanaInterna("Captura", captura);
        }
    }//GEN-LAST:event_capturarActionPerformed

    private void curvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_curvaActionPerformed
        if (GetVentanaImagenInternaActiva() != null) {
            GetVentanaImagenInternaActiva().getLienzo().setHerramienta(HerramientasDibujo.CURVA);
            GetVentanaImagenInternaActiva().ActualizarPropiedadesEnVentanaPrincipal();
        }
        herramientasTexto.setVisible(false);
        estado.setText("Curva");
    }//GEN-LAST:event_curvaActionPerformed

    private void textoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textoActionPerformed
        if (GetVentanaImagenInternaActiva() != null) {
            GetVentanaImagenInternaActiva().getLienzo().setHerramienta(HerramientasDibujo.TEXTO);
            GetVentanaImagenInternaActiva().ActualizarPropiedadesEnVentanaPrincipal();
        }
        herramientasTexto.setLocationRelativeTo(this);
        herramientasTexto.setVisible(true);
        estado.setText("Texto");
    }//GEN-LAST:event_textoActionPerformed

    private void mostrarBarraDeEdicionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostrarBarraDeEdicionActionPerformed
        edicionImagen.setVisible(mostrarBarraDeEdicion.isSelected());
    }//GEN-LAST:event_mostrarBarraDeEdicionActionPerformed

    private void acercaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acercaActionPerformed
        dialogoAcercaDe.setLocationRelativeTo(this);
        dialogoAcercaDe.setVisible(true);
    }//GEN-LAST:event_acercaActionPerformed

    private void fuenteTextoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fuenteTextoActionPerformed
        if (GetVentanaImagenInternaActiva() != null)
            GetVentanaImagenInternaActiva().getLienzo().setFuente(fuenteTexto.getSelectedItem().toString());
    }//GEN-LAST:event_fuenteTextoActionPerformed

    private void introducirTextoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_introducirTextoActionPerformed
        if (GetVentanaImagenInternaActiva() != null)
            GetVentanaImagenInternaActiva().getLienzo().setTexto(introducirTexto.getText());
    }//GEN-LAST:event_introducirTextoActionPerformed

    private void sizeLetraStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeLetraStateChanged
        if (GetVentanaImagenInternaActiva() != null)
            GetVentanaImagenInternaActiva().getLienzo().setTamanoLetra((int)sizeLetra.getValue());
    }//GEN-LAST:event_sizeLetraStateChanged

    private void subrayadoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_subrayadoItemStateChanged
        if (GetVentanaImagenInternaActiva() != null)  
            GetVentanaImagenInternaActiva().getLienzo().setSubrayado(subrayado.isSelected());
    }//GEN-LAST:event_subrayadoItemStateChanged

    private void cursivaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cursivaItemStateChanged
        if (GetVentanaImagenInternaActiva() != null)
        GetVentanaImagenInternaActiva().getLienzo().setCursiva(cursiva.isSelected());
    }//GEN-LAST:event_cursivaItemStateChanged

    private void negritaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_negritaItemStateChanged
        if (GetVentanaImagenInternaActiva() != null)
        GetVentanaImagenInternaActiva().getLienzo().setNegrita(negrita.isSelected());
    }//GEN-LAST:event_negritaItemStateChanged

    private void colorSelectorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_colorSelectorMouseClicked
        Color nuevoColor = JColorChooser.showDialog(null, "Selecciona Color",(Color)colorSelector.getSelectedItem());
        colorSelector.setEditable(true);
        colorSelector.setSelectedItem(nuevoColor);
        colorSelector.setEditable(false);
        repaint();
    }//GEN-LAST:event_colorSelectorMouseClicked

    private void colorSelectorDegradadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_colorSelectorDegradadoMouseClicked
        Color nuevoColor = JColorChooser.showDialog(null, "Selecciona Color",(Color)colorSelectorDegradado.getSelectedItem());
        colorSelectorDegradado.setEditable(true);
        colorSelectorDegradado.setSelectedItem(nuevoColor);
        colorSelectorDegradado.setEditable(false);
        repaint();
    }//GEN-LAST:event_colorSelectorDegradadoMouseClicked

    private void colorSelectorDegradadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorSelectorDegradadoActionPerformed
        if (GetVentanaImagenInternaActiva() != null && userChange) 
            GetVentanaImagenInternaActiva().getLienzo().setColorDegradado((Color) colorSelectorDegradado.getSelectedItem());
    }//GEN-LAST:event_colorSelectorDegradadoActionPerformed

    private void colorSelectorRellenoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_colorSelectorRellenoMouseClicked
        Color nuevoColor = JColorChooser.showDialog(null, "Selecciona Color",(Color)colorSelectorRelleno.getSelectedItem());
        colorSelectorRelleno.setEditable(true);
        colorSelectorRelleno.setSelectedItem(nuevoColor);
        colorSelectorRelleno.setEditable(false);
        repaint();
    }//GEN-LAST:event_colorSelectorRellenoMouseClicked

    private void colorSelectorRellenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorSelectorRellenoActionPerformed
        if (GetVentanaImagenInternaActiva() != null && userChange) 
            GetVentanaImagenInternaActiva().getLienzo().setColorRelleno((Color) colorSelectorRelleno.getSelectedItem());
    }//GEN-LAST:event_colorSelectorRellenoActionPerformed

    private void copiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copiarActionPerformed
        VentanaInterna vi = GetVentanaImagenInternaActiva();
        if (vi != null) {
            BufferedImage copia = vi.getLienzo().getImagenCopia();
            NuevaVentanaInterna("Copia " + vi.getTitle(), copia);
        }
    }//GEN-LAST:event_copiarActionPerformed

    private void sliderTransparenciaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderTransparenciaStateChanged
        if (GetVentanaImagenInternaActiva() != null)
            GetVentanaImagenInternaActiva().getLienzo().setCantidadTransparencia(sliderTransparencia.getValue()/10f);
    }//GEN-LAST:event_sliderTransparenciaStateChanged

    private void tipoTrazoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoTrazoActionPerformed
        if (GetVentanaImagenInternaActiva() != null) 
            GetVentanaImagenInternaActiva().getLienzo().setTrazo(tipoTrazo.isSelected());
    }//GEN-LAST:event_tipoTrazoActionPerformed

    private void direccionDegradadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_direccionDegradadoActionPerformed
        if (GetVentanaImagenInternaActiva() != null) 
            GetVentanaImagenInternaActiva().getLienzo().setDireccionDegradado(direccionDegradado.isSelected());
    }//GEN-LAST:event_direccionDegradadoActionPerformed

    private void transparenciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_transparenciaItemStateChanged
        if (GetVentanaImagenInternaActiva() != null && userChange) 
            GetVentanaImagenInternaActiva().getLienzo().setTransparencia(transparencia.isSelected());
        sliderTransparencia.setVisible(transparencia.isSelected());
    }//GEN-LAST:event_transparenciaItemStateChanged

    private void sinRellenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sinRellenoActionPerformed
        direccionDegradado.setVisible(false);
        colorSelectorRelleno.setVisible(false);
        colorSelectorDegradado.setVisible(false);
        if (GetVentanaImagenInternaActiva() != null) {
            GetVentanaImagenInternaActiva().getLienzo().setDegradado(false);
            GetVentanaImagenInternaActiva().getLienzo().setRelleno(false);
        }
    }//GEN-LAST:event_sinRellenoActionPerformed

    private void rellenarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rellenarActionPerformed
        direccionDegradado.setVisible(false);
        colorSelectorRelleno.setVisible(true);
        colorSelectorDegradado.setVisible(false);
        if (GetVentanaImagenInternaActiva() != null) {
            GetVentanaImagenInternaActiva().getLienzo().setDegradado(false);
            GetVentanaImagenInternaActiva().getLienzo().setRelleno(true);
        }
    }//GEN-LAST:event_rellenarActionPerformed

    private void degradadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_degradadoActionPerformed
        direccionDegradado.setVisible(true);
        colorSelectorRelleno.setVisible(true);
        colorSelectorDegradado.setVisible(true);
        if (GetVentanaImagenInternaActiva() != null) {
            GetVentanaImagenInternaActiva().getLienzo().setDegradado(true);
            GetVentanaImagenInternaActiva().getLienzo().setRelleno(false);
        }
    }//GEN-LAST:event_degradadoActionPerformed

    private void cerrarAcercaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarAcercaDeActionPerformed
        dialogoAcercaDe.setVisible(false);
    }//GEN-LAST:event_cerrarAcercaDeActionPerformed

    private void aceptarErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptarErrorActionPerformed
        errorDialog.setVisible(false);
    }//GEN-LAST:event_aceptarErrorActionPerformed

    private void negativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_negativoActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null){
                NegativoOp neg = new NegativoOp();
                // Imagen origen y destino iguales
                neg.filter( imgSource , imgSource);
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_negativoActionPerformed

    private void pixelizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pixelizarActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage imgSource = vi.getLienzo().getImagen();
            if(imgSource!=null){
                PixelizarOp neg = new PixelizarOp();
                // Imagen origen y destino iguales
                neg.filter( imgSource , imgSource);
                escritorio.repaint();
            }
        }
    }//GEN-LAST:event_pixelizarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Abir;
    private javax.swing.JButton BotonCancelar;
    private javax.swing.JButton BotonEscalar;
    private javax.swing.JButton BotonRedimensionar;
    private javax.swing.ButtonGroup BotonesHerramientas;
    private javax.swing.JMenuItem GrabarAudio;
    private javax.swing.JMenuItem Guardar;
    private javax.swing.JCheckBoxMenuItem MostrarBarraEstado;
    private javax.swing.JCheckBoxMenuItem MostrarBarraHerramientas;
    private javax.swing.JMenuItem Nuevo;
    private javax.swing.JDialog TamannoDialog;
    private javax.swing.JMenuItem TamannoImagen;
    private javax.swing.ButtonGroup TiposRelleno;
    private javax.swing.JMenuItem abrirWebCam;
    private javax.swing.JButton aceptarError;
    private javax.swing.JMenuItem acerca;
    private javax.swing.JTextField alto;
    private javax.swing.JTextField ancho;
    private javax.swing.JButton aumentarEscala;
    private javax.swing.JLabel autor;
    private javax.swing.JButton bandas;
    private javax.swing.JButton botnNuevo;
    private javax.swing.JButton botonAbrir;
    private javax.swing.JButton botonGuardar;
    private javax.swing.JSlider brillo;
    private javax.swing.JButton capturar;
    private javax.swing.JButton cerrarAcercaDe;
    private javax.swing.JButton cientoochentaGrados;
    private javax.swing.JComboBox<String> colorSelector;
    private javax.swing.JComboBox<String> colorSelectorDegradado;
    private javax.swing.JComboBox<String> colorSelectorRelleno;
    private javax.swing.JButton contraste;
    private javax.swing.JButton copiar;
    private javax.swing.JToggleButton cursiva;
    private javax.swing.JToggleButton curva;
    private javax.swing.JToggleButton degradado;
    private javax.swing.JDialog dialogoAcercaDe;
    private javax.swing.JToggleButton direccionDegradado;
    private javax.swing.JButton disminuirEscala;
    private javax.swing.JButton doscientossetentaGrados;
    private javax.swing.JButton ecualizar;
    private javax.swing.JToolBar edicionImagen;
    private javax.swing.JToggleButton editar;
    private javax.swing.JToggleButton elipse;
    private javax.swing.JDialog errorDialog;
    private javax.swing.JLabel errorImage;
    private javax.swing.JLabel errorMessage;
    private javax.swing.JLabel errorTitle;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JLabel estado;
    private javax.swing.JComboBox<String> filtro;
    private javax.swing.JComboBox<String> fuenteTexto;
    private javax.swing.JSpinner grosor;
    private javax.swing.JToolBar herramientas;
    private javax.swing.JDialog herramientasTexto;
    private javax.swing.JButton iluminar;
    private javax.swing.JTextField introducirTexto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToggleButton linea;
    private javax.swing.JCheckBoxMenuItem mostrarBarraDeEdicion;
    private javax.swing.JButton negativo;
    private javax.swing.JToggleButton negrita;
    private javax.swing.JLabel nombrePrograma;
    private javax.swing.JButton noventaGrados;
    private javax.swing.JButton oscurecer;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JButton pixelizar;
    private javax.swing.JLabel posicion;
    private javax.swing.JToggleButton punto;
    private javax.swing.JToggleButton rectangulo;
    private javax.swing.JToggleButton rellenar;
    private javax.swing.JComboBox<String> seleccionEspacioColor;
    private javax.swing.JButton sepia;
    private javax.swing.JToggleButton sinRelleno;
    private javax.swing.JButton sinusoidal;
    private javax.swing.JSpinner sizeLetra;
    private javax.swing.JSlider sliderRotacion;
    private javax.swing.JSlider sliderTransparencia;
    private javax.swing.JToggleButton suavizar;
    private javax.swing.JToggleButton subrayado;
    private javax.swing.JToggleButton texto;
    private javax.swing.JButton tintar;
    private javax.swing.JToggleButton tipoTrazo;
    private javax.swing.JToggleButton transparencia;
    private javax.swing.JSlider umbral;
    private javax.swing.JLabel version;
    // End of variables declaration//GEN-END:variables
}
