package View;

import Dao.DAO;
import Model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrm extends JFrame {
    private JPanel contentPanel;
    private JLabel txtfile_1;
    private JLabel JLabel_2;
    private JLabel JLabel_3;
    private JTextField txtUsername;
    private JButton btnLogin;
    private JButton btnSignUp;
    private JButton btnExit;
    private JPasswordField txtPassword;

    public LoginFrm() {
        super();
        setTitle("Đăng Nhập");
        setContentPane(contentPanel);
        setSize(700, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // chuyển sang form đăng ký khi click vào nút đăng ký
        btnSignUp.addActionListener(e -> {
            SignUpFrm frm = new SignUpFrm(LoginFrm.this);
            frm.setVisible(true); // hiển thị form đăng ký lên
            setVisible(false); // ẩn form đăng nhập

        });
        // xử lý sự kiện khi click vào nút đăng nhập
        btnLogin.addActionListener(e -> {
            String nameLogin = txtUsername.getText();
            String password = txtPassword.getText();
            User user = new DAO().getUserInfo(nameLogin);
            if (user != null){ // kiểm tra xem tài khoản có tồn tại không
                if (user.getPassword().equals(password)){ // kiểm tra xem mật khẩu có đúng không
                    JOptionPane.showMessageDialog(null, "Đăng nhập thành công");
                    ServerListFrm frm = new ServerListFrm(LoginFrm.this, user); // chuyển sang form danh sách server
                    frm.setVisible(true); // hiển thị form danh sách server
                    setVisible(false); // ẩn form đăng nhập


                }else {
                    JOptionPane.showMessageDialog(contentPanel, "Tài khoản hoặc mật khẩu không đúng");
                }

            }else {
                JOptionPane.showMessageDialog(contentPanel, "Tài khoản không tồn tại");
            }

        });
        btnExit.addActionListener(e -> {
            System.exit(0); // thoát chương trình

        });
    }

}
