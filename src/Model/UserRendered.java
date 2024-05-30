package Model;

import javax.swing.*;
import java.awt.*;

public class UserRendered extends JPanel implements ListCellRenderer<User> { // tạo một lớp UserRendered kế thừa từ JPanel và implements ListCellRenderer<User> có tác dụng hiển thị thông tin của người dùng lên JList
    private JLabel lbIcon = new JLabel();
    private JLabel lbUserName = new JLabel();
    private JLabel lbStatus = new JLabel();
    private JLabel lbNewMessage = new JLabel();
    private User currentUser;

    public UserRendered (User currentUser) {    // tạo constructor của lớp UserRendered
        this.currentUser= currentUser;
        setLayout(new BorderLayout(10,10));// tạo layout cho panel
        //setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.gray)); // tạo border cho panel
        setBorder(BorderFactory.createEmptyBorder(3,5,3,5));        // tạo border cho panel
        JPanel panelText = new JPanel(new GridLayout(0,1));// tạo panelText với layout là GridLayout
        panelText.add(lbUserName);// thêm lbUserName vào panelText có tác dụng hiển thị tên người dùng
        panelText.add(lbStatus);// thêm lbStatus vào panelText có tác dụng hiển thị trạng thái của người dùng
        panelText.add(lbNewMessage);// thêm lbNewMessage vào panelText có tác dụng hiển thị tin nhắn mới của người dùng
        add(lbIcon, BorderLayout.WEST);// thêm lbIcon vào panel có tác dụng hiển thị icon của người dùng
        add(panelText, BorderLayout.CENTER);// thêm panelText vào panel có tác dụng hiển thị thông tin của người dùng
    }



    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, User value, int index, boolean isSelected, boolean cellHasFocus) {// ghi đè phương thức getListCellRendererComponent
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/img/user-profile.png")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));// tạo một ImageIcon từ file user-profile.png
        lbIcon.setIcon(imageIcon);//
        if (value.getId().equals(currentUser.getId())){// set icon cho lbIcon
            lbUserName.setText(value.getName() + " (You)");// set text cho lbUserName có tác dụng hiển thị tên người dùng
        } else {
            lbUserName.setText(value.getName());
        } if (value.getHasNewMessage()){
            lbNewMessage.setText("Bạn có tin nhắn mới");// set text cho lbNewMessage có tác dụng hiển thị tin nhắn mới của người dùng
        }else {
            lbNewMessage.setText("");
        }
        lbStatus.setText(value.getConnected() ? "Online" : "Offline");// set text cho lbStatus có tác dụng hiển thị trạng thái của người dùng
        lbStatus.setForeground(Color.GREEN);
        lbNewMessage.setForeground(Color.RED);
        lbUserName.setOpaque(true);
        lbStatus.setOpaque(true);
        lbNewMessage.setOpaque(true);
        lbIcon.setOpaque(true);

        if(isSelected){// nếu item được chọn thì set màu nền cho các thành phần
            lbUserName.setBackground(list.getSelectionBackground());
            lbStatus.setBackground(list.getSelectionBackground());
            lbNewMessage.setBackground(list.getSelectionBackground());
            lbIcon.setBackground(list.getSelectionBackground());
            setBackground(list.getSelectionBackground());
        } else {// nếu không được chọn thì set màu nền cho các thành phần sau
            lbUserName.setBackground(list.getBackground());
            lbStatus.setBackground(list.getBackground());
            lbNewMessage.setBackground(list.getBackground());
            lbIcon.setBackground(list.getBackground());
            setBackground(list.getBackground());
        }
        return this;
    }

}
