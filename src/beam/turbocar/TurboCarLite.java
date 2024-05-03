package beam.turbocar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class TurboCarLite extends JFrame{

    private JMenu fileMenu;
    private JMenu connectMenu;
    private JMenu restartMenu;
    private JMenuBar menuBar;
    private JMenuItem exitMenu;
    private JPanel topPanel;
    private JLabel vocabLabel;
    private JLabel timeLabel;


    TurboCarComponent turboCarComponent;


    public TurboCarLite() throws IOException, ClassNotFoundException, FontFormatException {

        turboCarComponent = new TurboCarComponent("test");
        initComponents();
        turboCarComponent.setFocusable(true);
        turboCarComponent.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pcEvt) {
                if (pcEvt.getPropertyName().equals(TurboCarComponent.REMAIN_TIME_CHANGED)) {
                    String text = "" + pcEvt.getNewValue();
                    timeLabel.setText(text);
                } else if (pcEvt.getPropertyName().equals(TurboCarComponent.VOCAB_CHANGED))  {
                    String text = "   " + pcEvt.getNewValue();
                    vocabLabel.setText(text);
                }
            }
        });

        add(turboCarComponent, BorderLayout.CENTER);

        this.setPreferredSize(turboCarComponent.getPrefferedSize());
        this.setMaximumSize(turboCarComponent.getPrefferedSize());

        this.pack();

        this.setLocationRelativeTo(null);
    }

    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        fileMenu = new JMenu("File");
        connectMenu = new JMenu("Connect");
        restartMenu = new JMenu("Restart-Game");
        exitMenu = new JMenuItem("Exit");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        menuBar.add(fileMenu);
        fileMenu.add(exitMenu);

        connectMenu.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                connectMenuSelected();
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


        restartMenu.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                restartMenuSelected();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                restartMenu.setSelected(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                restartMenu.setSelected(false);
            }
        });


        menuBar.add(connectMenu);
        menuBar.add(restartMenu);

        setJMenuBar(menuBar);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent event) {
                turboCarComponent.requestFocusInWindow();
            }
        });

        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setFocusable(false);
        timeLabel = new JLabel("0");
        timeLabel.setPreferredSize(new Dimension(100, 50));
        timeLabel.setOpaque(true);
        timeLabel.setBackground(Color.black);
        timeLabel.setForeground(Color.green);
        timeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        vocabLabel = new JLabel("   คำอะไรก็ได้");
        if (TurboCarComponent.myCharacterFace.font == null)
            vocabLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 32));
        else
            vocabLabel.setFont(TurboCarComponent.myCharacterFace.font.deriveFont(Font.PLAIN, 32));
        topPanel.add(timeLabel, BorderLayout.WEST);
        topPanel.add(vocabLabel, BorderLayout.CENTER);

        this.add(topPanel, BorderLayout.NORTH);


    }

    private void connectMenuSelected() {
        connectMenu.setSelected(false);
        String inputHostName = JOptionPane.showInputDialog("Remote Host:", "localhost");
        if (inputHostName == null) return;

        try {
            turboCarComponent.actionConnect(inputHostName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Can't connect remote host:" + inputHostName);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Internal error!!!");
        }

    }

    private void restartMenuSelected() {
        restartMenu.setSelected(false);

        int selectedOption = JOptionPane.showConfirmDialog(null,
                "Do you wanna RESTART new game?",
                "Restart ",
                JOptionPane.YES_NO_OPTION);
        if (selectedOption == JOptionPane.YES_OPTION) {
            try {
                boolean success = turboCarComponent.actionRestart();             // CALL
                if (!success)
                    JOptionPane.showMessageDialog(this, "Start new game fail!!!");

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Server OFFLINE");
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Internal error!!!");
            }
        }


    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */

        try {
            Process process = new ProcessBuilder("java", "-cp", "TurboCar.jar", "beam.turbocar.SoundServer").start();

            new TurboCarLite().setVisible(true);
        } catch (IOException | ClassNotFoundException | FontFormatException e) {
            throw new RuntimeException(e);
        }


    }

}
