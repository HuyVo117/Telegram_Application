package View;

import Dao.DAO;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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



    public SignUpFrm(Frame login){
        this.setTitle("Đăng ký");
        this.setContentPane(rootPanel);
        setSize(700,300);
        this.setLocationRelativeTo(null);
        loginFrm = (LoginFrm) login;



        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameLogin = txtNameLogin.getText();
                String nickname = txtNickname.getText();
                String password = txtPassword.getText();
                String check = txtCheck.getText();
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

                } else if (!password.equals(check)){
                    JOptionPane.showMessageDialog(rootPanel,"Mật khẩu không khớp");
                } else {
                    User user = new User(nameLogin, nickname, password);
                    if (addNewUser(user)){
                        JOptionPane.showMessageDialog(rootPanel,"Đăng ký thành công");
                        dispose();
                        loginFrm.setVisible(true);

                    } else {
                        JOptionPane.showMessageDialog(rootPanel,"Đăng ký thất bại");
                    }

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
