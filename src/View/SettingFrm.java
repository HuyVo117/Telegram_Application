package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class SettingFrm extends JDialog{
    private JPanel panel;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton lineBreakRadio;
    private JRadioButton sendMsgRadio;
    private ClientFrm clientFrm;

    public  SettingFrm(Frame parent, boolean modal){ // tạo dialog setting
        super(parent, modal); // set parent và modal
        setContentPane(contentPane);
        setModal(true); // set dialog là modal
        pack(); // đóng gói dialog lại
        setLocationRelativeTo(parent);
        getRootPane().setDefaultButton(buttonOK); // set buttonOK là nút mặc định
        clientFrm = (ClientFrm)  parent;

        if (clientFrm.isLineBreak()){
            lineBreakRadio.setSelected(true); // nếu line break được chọn thì set selected cho line break radio
        }else {
            sendMsgRadio.setSelected(true); // nếu send message được chọn thì set selected cho send message radio
        }

        buttonOK.addActionListener(e -> {
            if (lineBreakRadio.isSelected()){ //
                clientFrm.setLineBreak(true); // nếu line break được chọn thì set line break là true
            }else {
                clientFrm.setLineBreak(false); // nếu send message được chọn thì set line break là false
            }
            dispose(); // đóng dialog
        });
        buttonCancel.addActionListener(e -> {
            onCancel(); // hủy bỏ
        });
        // goi den onCancel khi click vao nut cancel
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // set default close operation
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                onCancel();
            }
        });
        // goi den onCancel khi click vao nut escape
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(  KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT); // set key event cho escape

    }
    public void onOK(){
        dispose();
    }
    public void onCancel(){
        dispose();
    }




}
