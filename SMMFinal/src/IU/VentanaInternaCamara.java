package IU;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class VentanaInternaCamara extends VentanaInternaVideoMedia {

    private Webcam camara = null;
    
    private VentanaInternaCamara() {
        initComponents();
        camara = Webcam.getDefault();
        if (camara != null) {
            Dimension resoluciones[] = camara.getViewSizes();
            Dimension maxRes = resoluciones[resoluciones.length-1];
            camara.setViewSize(maxRes);
            areaVisual = new WebcamPanel(camara);
            
            if (areaVisual!= null) {
                getContentPane().add(areaVisual, BorderLayout.CENTER);
                pack();
            }
        }
    }
    
    public static VentanaInternaCamara getInstance(){
        VentanaInternaCamara v = new VentanaInternaCamara();
        return (v.camara!=null?v:null);
    }
    
    public BufferedImage GetCapture () {
        
        return camara.getImage();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        areaVisual = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        areaVisual.setLayout(new java.awt.BorderLayout());
        getContentPane().add(areaVisual, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        camara.close();
    }//GEN-LAST:event_formInternalFrameClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel areaVisual;
    // End of variables declaration//GEN-END:variables
}
