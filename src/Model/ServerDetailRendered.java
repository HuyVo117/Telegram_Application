package Model;

import javax.swing.*;
import java.awt.*;
// Lớp này sử dụng để hiển thị thông tin của server trên JList của client
public class ServerDetailRendered extends JPanel implements ListCellRenderer<ServerDetail> { // tạo một lớp ServerDetailRendered kế thừa từ JPanel và implements ListCellRenderer<ServerDetail>
    private JLabel lbIcon = new JLabel();  // ListCellRenderer<ServerDetail> là một interface, nó có một phương thức duy nhất là getListCellRendererComponent có tác dụng là hiển thị thông tin của một đối tượng ServerDetail lên JList
   //
    private JLabel lbHostName = new JLabel();
    private JLabel lbPort = new JLabel();

    public ServerDetailRendered() { // tạo constructor của lớp ServerDetailRendered
       setLayout(new BorderLayout(5,5)); // tạo layout cho panel
       setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.gray)); // tạo border cho panel
       JPanel panelText = new JPanel(new GridLayout(0,1)); // tạo panelText với layout là GridLayout
       panelText.add(lbHostName); // thêm lbHostName vào panelText có tác dụng hiển thị hostName của server
         panelText.add(lbPort); // thêm lbPort vào panelText có tác dụng hiển thị port của server
         add(lbIcon, BorderLayout.WEST); // thêm lbIcon vào panel có tác dụng hiển thị icon của server
         add(panelText, BorderLayout.CENTER);// thêm panelText vào panel có tác dụng hiển thị thông tin của server

    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ServerDetail> list, ServerDetail value, int index, boolean isSelected, boolean cellHasFocus) { // ghi đè phương thức getListCellRendererComponent
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/img/server.png")); // tạo một ImageIcon từ file server.png
        lbIcon.setIcon(imageIcon); // set icon cho lbIcon
        lbHostName.setText(value.getHostName()); // set text cho lbHostName có tác dụng hiển thị hostName của server
        lbPort.setText("Port: " + String.valueOf(value.getPort()));// set text cho lbPort có tác dụng hiển thị port của server
        lbPort.setForeground(Color.BLUE); // set màu cho lbPort
        lbHostName.setOpaque(true); // set opaque cho lbHostNamed có tác dụng hiển thị màu nền
        lbPort.setOpaque(true); // set opaque cho lbPort có tác dụng hiển thị màu nền
        lbIcon.setOpaque(true); // set opaque cho lbIcon có tác dụng hiển thị màu nền
        if (isSelected) { // nếu item được chọn thì set màu nền cho các thành phần
            lbHostName.setBackground(list.getSelectionBackground());// set màu nền cho lbHostName
            lbPort.setBackground(list.getSelectionBackground());// set màu nền cho lbPort
            lbIcon.setBackground(list.getSelectionBackground());// set màu nền cho lbIcon
            setBackground(list.getSelectionBackground());  // set màu nền cho panel
        } else { // nếu không được chọn thì set màu nền cho các thành phần sau
            lbHostName.setBackground(list.getBackground());
            lbPort.setBackground(list.getBackground());
            lbIcon.setBackground(list.getBackground());
            setBackground(list.getBackground());
        }

       return this; // trả về panel và hiển thị lên JList của client thông tin của server
    }
}
