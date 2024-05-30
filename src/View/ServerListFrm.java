package View;

import Model.ServerDetail;
import Model.ServerDetailRendered;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerListFrm  extends JFrame {
    private JPanel rootPanel;
    private JList<ServerDetail> listServer;
    private JButton btnAddConnect;
    private JButton btnDelete;
    private JButton btnEdit;
    private JButton btnConnect;
    private JPanel panel;
    private LoginFrm login;
    private User currentUser;
    private List<ServerDetail> ls_server = new ArrayList<>(); // tạo mảng chứa các server detail từ file config
    DefaultListModel<ServerDetail> model; // tạo model cho list server để hiển thị lên giao diện

    public void readFileConfig() { // đọc file config.txt để lấy thông tin server
        try {
            FileReader fr = new FileReader(new File("config.txt")); // tạo luồng đọc file
            BufferedReader br = new BufferedReader(fr); // tạo bộ đệm để đọc file
            String thisline = null;
            while ((thisline = br.readLine()) != null) { // đọc từng dòng trong file config.txt
                String[] host = thisline.split(","); // tách chuỗi theo dấu , để lấy thông tin host và port
                ServerDetail sv = new ServerDetail(host[0], Integer.parseInt(host[1]), "chat server"); // tạo server detail từ thông tin host và port
                ls_server.add(sv);// thêm server detail vào mảng

            }
            fr.close(); // đóng luồng đọc file
            br.close(); // đóng bộ đệm
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerListFrm(Frame login, User user) {
        super();
        login = (LoginFrm) login; // ép kiểu login thành LoginFrm để sử dụng các phương thức trong LoginFrm
        currentUser = user; // gán user hiện tại bằng user truyền vào
        setTitle("Chọn server");
        setContentPane(rootPanel);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        readFileConfig(); // đọc file config.txt


        model = new DefaultListModel<>(); // tạo model cho list server
        listServer.setModel(model); // thiết lập model cho list server
        listServer.setCellRenderer(new ServerDetailRendered()); // thiết lập renderer cho list server để hiển thị thông tin server
        updateListServer(); // cập nhật danh sách server


        //Them action cho nut

        btnAddConnect.addActionListener(e -> {
            InputServerFrm frm = new InputServerFrm(ServerListFrm.this, rootPaneCheckingEnabled); // tạo form nhập thông tin server mới
            frm.setVisible(true);
        });

        btnConnect.addActionListener(e -> {
            ServerDetail server = listServer.getSelectedValue(); // lấy server được chọn từ list server
            Socket s = null;
            try {
                s = new Socket(server.getHostName(), server.getPort());
                JOptionPane.showMessageDialog(rootPanel, "Kết nối thành công !");
                ClientFrm frm = new ClientFrm(ServerListFrm.this, s, server, currentUser); // tạo form client mới để kết nối tới server được chọn
                frm.setVisible(true);
                setVisible(false);

            } catch (IOException ioException) {
                ioException.printStackTrace();
                JOptionPane.showMessageDialog(rootPanel, "Kết nối thất bại !");
            }

        });


        btnDelete.addActionListener(e -> {
            int index = listServer.getSelectedIndex(); // lấy vị trí server được chọn trong list server
            if (ls_server.isEmpty()) {
                JOptionPane.showMessageDialog(rootPanel, "Không có server nào để xóa !");
            } else if (index == -1) {
                JOptionPane.showMessageDialog(rootPanel, "Vui lòng chọn server cần xóa !");
            } else {
                int output = JOptionPane.showConfirmDialog(rootPanel, "Bạn có chắc chắn muốn xóa server này không ?", "Xác nhận", JOptionPane.YES_NO_OPTION); // hiển thị hộp thoại xác nhận xóa server

                if (output == JOptionPane.YES_OPTION) {
                    ls_server.remove(index); // xóa server khỏi mảng
                    writeToFile(); // ghi lại file config.txt
                    updateListServer(); // cập nhật danh sách server
                }
            }
        });
        btnEdit.addActionListener(e -> {
            int index = listServer.getSelectedIndex(); // lấy vị trí server được chọn trong list server
            if (ls_server.isEmpty()) {
                JOptionPane.showMessageDialog(rootPanel, "Không có server nào để sửa !");
            } else if (index == -1) {
                JOptionPane.showMessageDialog(rootPanel, "Vui lòng chọn server cần sửa !");
            } else {
                EditServerFrm frm = new EditServerFrm(ServerListFrm.this, rootPaneCheckingEnabled); // tạo form sửa thông tin server được chọn
                frm.setEditData(ls_server.get(index)); // thiết lập thông tin server cần sửa vào form
                frm.setVisible(true);

            }
        });

    }



    public void writeToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt", false)); // tạo luồng ghi file config.txt
            for (ServerDetail sv : ls_server) { // duyệt qua mảng server detail để ghi thông tin vào file
                bw.write(sv.getHostName() + "," + sv.getPort() + "\n"); // ghi thông tin host và port vào file config.txt
            }
            bw.close(); // đóng luồng ghi file
        } catch (IOException e) {
            e.printStackTrace();
        }}

        public void updateListServer () {
        model.removeAllElements();
        for (ServerDetail sv : ls_server) { // duyệt qua mảng server detail để thêm vào model
            model.addElement(sv); // thêm server detail vào model

        }
        }

        public void addConnection (ServerDetail sv){
        ls_server.add(sv); // thêm server detail vào mảng
            model.addElement(sv); // thêm server detail vào model để hiển thị lên giao diện
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt", true)); // tạo luồng ghi file config.txt để thêm thông tin server mới
                bw.write(sv.getHostName() + "," + sv.getPort() + "\n"); // ghi thông tin host và port vào file
                bw.close(); // đóng luồng ghi file

            }catch (IOException e){
                e.printStackTrace();
            }
        }



    }

