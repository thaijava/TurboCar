/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package beam.turbocar;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * @author papin
 */
public class TurboCar extends JFrame {

    TurboCarComponent turboCarComponent;


    public TurboCar() throws IOException, ClassNotFoundException {
        initComponents();
        this.setLayout(new BorderLayout());

        turboCarComponent = new TurboCarComponent();
        turboCarComponent.setFocusable(true);
        add(turboCarComponent, BorderLayout.CENTER);

        this.setPreferredSize(turboCarComponent.getPrefferedSize());
        this.setMaximumSize(turboCarComponent.getPrefferedSize());

        this.pack();
        this.setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new JMenu("File");
        connectMenu = new JMenu("Connect");
        exitMenu = new JMenuItem("Exit");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenuBar1.add(fileMenu);
        fileMenu.add(exitMenu);

        connectMenu.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                connecMenuSelected();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                connectMenu.setSelected(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                connectMenu.setSelected(false);
            }
        });

        connectMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
                connecMenuSelected();
            }

            public void menuDeselected(javax.swing.event.MenuEvent evt) {

            }

            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
        });


        jMenuBar1.add(connectMenu);

        setJMenuBar(jMenuBar1);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent event) {
                turboCarComponent.requestFocusInWindow();
            }
        });

    }

    private void connecMenuSelected()  {
        // TODO add your handling code here:
        System.out.println("Hello connect");

        connectMenu.setSelected(false);

        String hostName = JOptionPane.showInputDialog("Remote Host:", "localhost");
        try {
            turboCarComponent.actionConnect(hostName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Can't remote host:" + hostName);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Internal error!!!");
        }


    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {


        /* Create and display the form */

        try {
            new TurboCar().setVisible(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    // Variables declaration - do not modify
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu connectMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private JMenuItem exitMenu;
    // End of variables declaration
}
