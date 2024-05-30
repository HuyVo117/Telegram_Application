package View;

import Model.ServerDetail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.security.Key;

public class EditServerFrm extends JDialog{ // tạo một lớp EditServerFrm kế thừa từ JDialog
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtHostname;
    private JTextField txtPort;
    private JPanel content;
    private ServerDetail selectedServer;
    private ServerListFrm frm;

    public EditServerFrm(Frame parent , boolean modal){ // tạo constructor của lớp EditServerFrm
        super(parent, modal); // gọi constructor của lớp cha với tham số truyền vào là parent và modal
        setContentPane(contentPane); // set contentPane cho JDialog
        setModal(true); // set JDialog hiển thị lên trước các cửa sổ khác
        getRootPane().setDefaultButton(buttonOK); // set buttonOK là nút mặc định
        pack(); // tự động điều chỉnh kích thước của JDialog
        setLocationRelativeTo(parent); // hiển thị JDialog giữa màn hình
        frm= (ServerListFrm) parent; // ép kiểu parent về ServerListFrm và gán vào biến frm


        // tạo sự kiện cho buttonOK khi click vào button đó thì thực hiện các lệnh sau
        buttonOK.addActionListener(e -> {
            String hostname = txtHostname.getText(); // lấy dữ liệu từ txtHostname và gán vào biến hostname
            Integer port = Integer.valueOf(txtPort.getText()); // lấy dữ liệu từ txtPort và gán vào biến port
            selectedServer.setHostname(hostname); // set hostname cho selectedServer
            selectedServer.setPort(port); // set port cho selectedServer
            frm.writeToFile(); // ghi dữ liệu vào file
            frm.updateListServer();// cập nhật danh sách server
            dispose(); // đóng JDialog
        });
        // tạo sự kiện cho buttonCancel khi click vào button đó thì thực hiện các lệnh sau
        buttonCancel.addActionListener(e -> {
            onCancel(); // gọi phương thức onCancel
        });


        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // set hành động khi click vào nút đóng
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                onCancel(); // gọi phương thức onCancel
            }
        });
        // tạo sự kiện cho contentPane khi click vào button đó thì thực hiện các lệnh sau
        contentPane.registerKeyboardAction(e -> {
            onCancel(); // gọi phương thức onCancel
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT); // set phím tắt cho contentPane khi nhấn phím ESC thì thực hiện phương thức onCancel
            }

            private void onOK(){
        dispose();
            }

            public void setEditData(ServerDetail server){ // tạo phương thức setEditData với tham số truyền vào là server
        selectedServer = server; // gán server cho selectedServer
        txtHostname.setText(server.getHostName()); // set hostname cho txtHostname
        txtPort.setText(server.getPort().toString()); // set port cho txtPort
            }


    private void onCancel() {
        dispose();
    }


}
