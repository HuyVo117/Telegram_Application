package View;

import Model.ServerDetail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import static javax.swing.SwingUtilities.getRootPane;

public class InputServerFrm extends JDialog{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtHostName;
    private JTextField txtPort;
    private JPanel panel;
    private ServerListFrm serverListFrm;

    public InputServerFrm(Frame parent, boolean modal){
        super(parent, modal);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        pack();
        setLocationRelativeTo(parent);
        serverListFrm = (ServerListFrm) parent;



        buttonOK.addActionListener(e -> {
            String address = txtHostName.getText();
            Integer port = Integer.valueOf(txtPort.getText());
            ServerDetail sv = new ServerDetail(address,port,"icon name");
            serverListFrm.addConnection(sv);
            dispose();
        });


        buttonCancel.addActionListener(e -> onCancel());
        // Gọi đến onCancel( khi được click vào dấu X
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                onCancel();
            }
        });
        // Gọi đến onCancel  khi được click vào nút ESC
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void onCancel() {
        dispose();
    }

    private void onOK(){
        dispose();
    }


}
