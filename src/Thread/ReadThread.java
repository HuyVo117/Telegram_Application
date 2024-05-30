package Thread;

import Dao.DAO;
import Model.Message;
import Model.User;
import View.ClientFrm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ReadThread implements Runnable{ // tạo thread để đọc dữ liệu  và xử lý dữ liệu từ server
private ObjectInputStream reader;
private Socket socket;
private ClientFrm client;
public ReadThread(Socket s, ClientFrm c){ // tạo constructor
    this.socket = s;
    this.client = c;

}
    @Override
    public void run() {
try {
    reader = new ObjectInputStream(socket.getInputStream()); // tạo luồng đọc dữ liệu từ server

}catch (Exception e){
    e.printStackTrace();
}
        Message response = null; // tạo biến để lưu dữ liệu đọc được từ server , biến ban đầu chưa có gì nên ược gán giá trị null
do {
    try{
        response = (Message) reader.readObject(); // đọc dữ liệu từ server
        switch (response.getType()){ // xử lý dữ liệu đọc được từ server
            case "FETCH_USERS"->{ // nếu dữ liệu đọc được từ server là FETCH_USERS thì thực hiện các lệnh sau
                System.out.println(response.getType()); // in ra màn hình FETCH_USERS

                client.updateListUsers((ArrayList<User>) response.getPayload()); // cập nhật danh sách người dùng
                client.setDefaultUserSelection(); // thiết lập người dùng mặc định
                break;

            }
            case "USER_CONN"->{ // nếu dữ liệu đọc được từ server là USER_CONN thì thực hiện các lệnh sau
                System.out.println("USER_CONN"); // in ra màn hình USER_CONN
                client.setUserOnline((User) response.getPayload());// thiết lập người dùng online
                break;
            }
            case "PRIVATE_MESSAGE" -> { // nếu dữ liệu đọc được từ server là PRIVATE_MESSAGE thì thực hiện các lệnh sau
                System.out.println("Bạn có tin nhắn mới !"); // in ra màn hình Bạn có tin nhắn mới
                client.onPrivateMessage(response);// hiển thị tin nhắn riêng
                break;
            }
            case "PRIVATE_FILE_MESSAGE"->{// nếu dữ liệu đọc được từ server là PRIVATE_FILE_MESSAGE thì thực hiện các lệnh sau
                System.out.println("Bạn có 1 file mới !"); // in ra màn hình Bạn có 1 file mới
                User from = new DAO().getUserById(response.getFrom());// lấy thông tin người gửi file
                client.onPrivateFileMessage(reader,from);// hiển thị file
                break;
            }
            case "USER_DISCONNECT"->{// nếu dữ liệu đọc được từ server là USER_DISCONNECT thì thực hiện các lệnh sau
                System.out.println("Có một người dùng vừa rời khỏi đoạn chat ! ");// in ra màn hình Có một người dùng vừa rời khỏi đoạn chat
                client.setUserOffLine((User) response.getPayload());// thiết lập người dùng offline
                break;

            }
        }
    } catch (IOException | ClassNotFoundException e){
        e.printStackTrace();
    }
}while (true); // vòng lặp vô hạn
    }
}
