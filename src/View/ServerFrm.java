package View;

import Model.Client;
import Model.User;
import Thread.ClientThread;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerFrm extends JFrame {
    private JPanel rootPanel;
    private JTextField txtPort;
    private JButton btnStartServer;
    private JScrollPane scrollPanel;
    private JTextArea txtServerLog;
    private JTable tblClient;
    private JPanel panel;
    private DefaultTableModel clientModel;
    private ServerSocket s;
    private List<Client> listClient;
    private JScrollBar sb;
    private int PORT = 3000;
    boolean isStarting = false;
    Thread serverThread;
    private ArrayList<ClientThread> clients = new ArrayList<>(); // tạo mảng chứa các client
    private static ExecutorService pool = Executors.newFixedThreadPool(100); // tạo pool chứa các client
    private ArrayList<User> users = new ArrayList<>();// tạo mảng chứa các user

    public ServerFrm() {
        setContentPane(rootPanel);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Server Configuration");

        listClient = new ArrayList<>();
        clientModel = (DefaultTableModel) tblClient.getModel(); // tạo model cho bảng client (danh sách client

        clientModel.setColumnIdentifiers(new Object[]{"STT", "IP Address", "Port", "Username"}); // tạo cột cho bảng client


        tblClient.setModel(clientModel); // thiết lập model cho bảng client

        sb = scrollPanel.getVerticalScrollBar();

        // tạo sự kiện cho nút Start Server và Stop Server
        btnStartServer.addActionListener(e -> {
            if (isStarting) { // nếu server đang chạy thì thực hiện các lệnh sau
                stopServer();
                isStarting = false; // thiết lập lại trạng thái của server
                txtPort.setEditable(true); // cho phép chỉnh sửa port
                btnStartServer.setText("Start Server"); // thiết lập lại nút Start Server
                validate(); // kiểm tra lại
                sb.setValue(sb.getMaximum()); // thiết lập thanh cuộn
                clearClientTable(); // xóa bảng client
                return;


            }
            try {
                txtServerLog.append("Server is starting...\n");
                startServer(); // khởi động server
                isStarting = true; // thiết lập trạng thái của server
                btnStartServer.setText("Stop Server");
                validate();
                sb.setValue(sb.getMaximum()); // thiết lập thanh cuộn
                txtPort.setEditable(false); // không cho phép chỉnh sửa port

            }catch (IOException ioException){
                txtServerLog.append("Error: " + ioException.getMessage() + "\n");
                sb.setValue(sb.getMaximum());
            }
        });



    }


    private void startServer() throws  IOException{ // khởi động server
             PORT = Integer.parseInt(txtPort.getText());
             serverThread = new Thread(new Runnable(){ // tạo thread để chạy server

                 @Override
                 public void run() {
                     try{
                         s = new ServerSocket(PORT); // tạo server socket
                         txtServerLog.append("Server is running at port " + PORT + "\n");
                         while(true){
                             txtServerLog.append("Waiting for client...\n");
                             validate();
                             sb.setValue(sb.getMaximum());
                                Socket socket = s.accept();
                                txtServerLog.append("Client connected: " + socket.getInetAddress().getHostAddress() + "\n"); // lấy địa chỉ IP của client kết nối đến server
                                validate();
                                sb.setValue(sb.getMaximum());
                                ClientThread client = new ClientThread(ServerFrm.this, socket,    clients); // tạo client thread để xử lý dữ liệu từ client
                             clients.add(client); // thêm client vào danh sách client
                             pool.execute(client); // thực hiện client
                         }
                     }catch (IOException e){
                         e.printStackTrace();
                     }
                 }
             });
             serverThread.start(); // khởi động server
    }



    private void clearClientTable() {
        clientModel.setRowCount(0); // xóa bảng client
    }
    public void addNewClient(Client client) {
        listClient.add(client);
    }

    public void ServerLogAppend(String text) { // thêm dữ liệu vào server log
        txtServerLog.append(text);
        validate();
        sb.setValue( sb.getMaximum() );
    }

    public void addUser(User s) {
        this.users.add(s);
    } // thêm user vào mảng user

    public void updateClientTable() { // cập nhật bảng client
        clientModel.setRowCount(0);
        for (int i=0; i<listClient.size(); i++) { // duyệt qua danh sách client
            Client cl = listClient.get(i); // lấy client
            clientModel.addRow(new Object[] { // thêm client vào bảng
                    i + 1, cl.getIpAddress(), cl.getPort(), cl.getUsername()
            });
        }
    }

    public void onUserDisconnect(User currentUser) { // xử lý khi người dùng ngắt kết nối
        Iterator<Client> itr = listClient.iterator(); // tạo iterator để duyệt qua danh sách client
        while (itr.hasNext()) { // duyệt qua danh sách client
            Client cl = itr.next();
            if(cl.getUserId().equals(currentUser.getId())) { // nếu client có id trùng với id của người dùng thì thực hiện các lệnh sau
                itr.remove(); // xóa client
            }
        }
        updateClientTable(); // cập nhật bảng client
    }

    private void stopServer() { // dừng server
        try{
            for (ClientThread clientThread : clients){ // duyệt qua danh sách client để đóng client
                clientThread.close();
            }
            this.serverThread.interrupt();// lỗi stop server
            s.close(); // đóng server
            txtServerLog.append("Server stopped\n"); // thông báo server đã dừng
            validate(); // kiểm tra lại
            sb.setValue(sb.getMaximum()); // thiết lập thanh cuộn
        }catch (IOException e){
            txtServerLog.append("Error: " + e.getMessage() + "\n"); //thông báo lỗi trong quá trình dừng server
            sb.setValue(sb.getMaximum()); // thiết lập thanh cuộn
        }
    }
}
