package IU;

import java.awt.image.BufferedImage;
import java.io.File;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VentanaInternaVLCPlayer extends VentanaInternaVideoMedia {

    private EmbeddedMediaPlayer vlcplayer = null;
    private File fMedia;
    private EmbeddedMediaPlayerComponent aVisual;
    
    private VentanaInternaVLCPlayer(File f) {
        initComponents();
        
        playGroup.add(playButton);
        playGroup.add(stopButton);
        
        fMedia = f;
        aVisual = new EmbeddedMediaPlayerComponent();
        getContentPane().add(aVisual,java.awt.BorderLayout.CENTER);
        vlcplayer = aVisual.getMediaPlayer();
        vlcplayer.addMediaPlayerEventListener(new VideoListener());
    }

    public static VentanaInternaVLCPlayer getInstance(File f){
        VentanaInternaVLCPlayer v = new VentanaInternaVLCPlayer(f);
        return (v.vlcplayer!=null?v:null);
    }
    
    public void play() {
        if (vlcplayer != null) {
            if(vlcplayer.isPlayable()){
                //Si se estaba reproduciendo
                vlcplayer.play();
            } else {
                vlcplayer.playMedia(fMedia.getAbsolutePath());
            }
        }
    }
    public void stop() {
        if (vlcplayer != null) {
            if (vlcplayer.isPlaying()) {
                vlcplayer.pause();
            } else {
                vlcplayer.stop();
            }
        }
    }
    
    public BufferedImage GetCapture () {
        
        return vlcplayer.getSnapshot();
    }
    
    
    private class VideoListener extends MediaPlayerEventAdapter {
        @Override
        public void finished(MediaPlayer mediaPlayer) {
            stopButton.setSelected(true);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        playButton = new javax.swing.JToggleButton();
        stopButton = new javax.swing.JToggleButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setPreferredSize(new java.awt.Dimension(450, 300));
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

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/PlayPressed_48x48.png"))); // NOI18N
        playButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/PlayPressed_48x48.png"))); // NOI18N
        playButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/PlayDisabled_48x48.png"))); // NOI18N
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        jPanel1.add(playButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/StopNormalRed_48x48.png"))); // NOI18N
        stopButton.setSelected(true);
        stopButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/StopNormalRed_48x48.png"))); // NOI18N
        stopButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/StopDisabled_48x48.png"))); // NOI18N
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        jPanel1.add(stopButton);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        stop();
        vlcplayer = null;
    }//GEN-LAST:event_formInternalFrameClosing

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        play();
    }//GEN-LAST:event_playButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        stop ();
    }//GEN-LAST:event_stopButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToggleButton playButton;
    private javax.swing.ButtonGroup playGroup;
    private javax.swing.JToggleButton stopButton;
    // End of variables declaration//GEN-END:variables
}
