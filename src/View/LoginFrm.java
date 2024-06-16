package View;

import Dao.DAO;
import Model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

   private DAO dao;

    public LoginFrm() {
        super();
        setTitle("Đăng Nhập");
        setContentPane(contentPanel);
        setSize(700, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dao = new DAO();


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

            // Hash the input password
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace();
            }
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            String hashedPassword = sb.toString();

            User user = new DAO().getUserInfo(nameLogin);
            if (user != null){ // kiểm tra xem tài khoản có tồn tại không
                if (user.getPassword().equals(hashedPassword)){ // kiểm tra xem mật khẩu có đúng không
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

    public boolean login(String NameLogin, String password){
        try {
            // Hash the input password
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            String hashedPassword = sb.toString();

            // Now, compare the hashed input password with the hashed password in the database
            String query = "SELECT * FROM user WHERE NameLogin = ? AND Password = ?";

            PreparedStatement stmt = dao.getConnection().prepareStatement(query);
            stmt.setString(1, NameLogin);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (NoSuchAlgorithmException | SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
