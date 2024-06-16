package View;

import Dao.DAO;

import Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;



public class SignUpFrm extends JFrame {
    private JPanel rootPanel;
    private javax.swing.JLabel JLabel;
    private JLabel JLabel_1;
    private JTextField txtNickname;
    private JTextField txtNameLogin;
    private JPasswordField txtCheck;
    private JPasswordField txtPassword;
    private JButton btnSignUp;
    private JButton thoátButton;
    private LoginFrm loginFrm;

    private DAO connection;




    public SignUpFrm(Frame login){
        this.setTitle("Đăng ký");
        this.setContentPane(rootPanel);
        setSize(700,300);
        this.setLocationRelativeTo(null);
        loginFrm = (LoginFrm) login;







        btnSignUp.addActionListener(e -> {
            String nameLogin = txtNameLogin.getText();
            String nickname = txtNickname.getText();
            String password = new String(txtPassword.getPassword());
            String check = new String(txtCheck.getPassword());
            if (nameLogin.length()<8||nameLogin.length()>30|| nameLogin.contains(" ")){
                JOptionPane.showMessageDialog(rootPanel,"Tên đăng nhập phải từ 8-30 ký tự và không chứa khoảng trắng");
                return;
            }
            if(nickname.length()<2||nickname.length()>30){
                JOptionPane.showMessageDialog(rootPanel,"NickName hiển thị phải từ 2-30 ký tự");
                return;
            }
            if(password.length()<8||password.contains(" ")||password.length()>30){
                JOptionPane.showMessageDialog(rootPanel,"Mật khẩu phải từ 8-30 ký tự và không chứa khoảng trắng");
                return;
            }

            else if (!password.equals(check)){
                JOptionPane.showMessageDialog(rootPanel,"Mật khẩu không khớp");
            } else {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    md.update(password.getBytes());
                    byte[] bytes = md.digest();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < bytes.length; i++) {
                        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                    }
                    password = sb.toString();
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                }

                User user = new User(nameLogin, nickname, password);
                if (addNewUser(user)){
                    JOptionPane.showMessageDialog(rootPanel,"Đăng ký thành công");
                    dispose();
                    loginFrm.setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(rootPanel, "Đăng ký thất bại");

                }


            }


        });

        thoátButton.addActionListener(e -> {
            dispose();
            loginFrm.setVisible(true);

        });
    }

    private boolean addNewUser(User user) {
if (new DAO().addNewUser(user)){
    return true;
    }
return false;
    }


}
