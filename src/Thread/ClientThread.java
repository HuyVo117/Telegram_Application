package Thread;


import Model.Client;
import Model.Message;
import Model.User;
import View.ServerFrm;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.net.InetSocketAddress;
import java.util.Iterator;

public class ClientThread implements Runnable{
private Socket client;
private ArrayList<ClientThread> clients;
private User currentUser;
private ObjectInputStream in;
private ObjectOutputStream out;
private ServerFrm serverFrm;

    public  ClientThread(Frame server, Socket client, ArrayList<ClientThread> clients) throws IOException {
        this.client = client;
        this.clients = clients;
        serverFrm = (ServerFrm) server;
        in = new ObjectInputStream(new BufferedInputStream(client.getInputStream())); // Đọc dữ liệu từ client bằng ObjectInputStream
        out = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream())); //   Ghi dữ liệu từ server đến client bằng ObjectOutputStream

    }
    public Socket getClient() {
        return client;
    }

    public void getClient(Socket client){
       this.client=client ;

    }

    @Override
    public void run() {
        try{
            Message request = null;
            do {
                request = (Message) in.readObject();  // Đọc dữ liệu từ client
                if (request.getType().equals("SESSION")){ // Nếu dữ liệu từ client là SESSION thì thực hiện
                    currentUser = (User) request.getPayload();
                    serverFrm.ServerLogAppend("A user with id : "+currentUser.getId()+" has connected");

                    String address = ((InetSocketAddress) client.getRemoteSocketAddress()).getAddress().toString(); // Lấy địa chỉ IP của client
                    Client cl = new Client(address,this.client.getPort(), currentUser.getName(), currentUser.getId()); // Tạo một client mới
                    serverFrm.addNewClient(cl); // Thêm client mới vào bảng
                    serverFrm.updateClientTable(); //   Cập nhật bảng client

                    // thong bao la nguoi dung nay da ton tai

                    Message userConnEvent = new Message("USER_CONN", currentUser);
                    broadcastAllExcludeCurrentSocket(userConnEvent);

                    // lay tat ca nguoi dung


                    ArrayList<User> list_user = fecthAllUser();

                    // thong bao nguoi dung moi ket noi
                    Message emitFecthUser = new Message("FETCH_USERS", list_user);
                    this.sendMessage(emitFecthUser);


                } else if (request.getType().equals("PRIVATE_MESSAGE")) {
                    String recipient = request.getTo();
                    this.forwardPrivateMessage(recipient, request); // Gửi tin nhắn riêng tư đến người nhận
                } else if (request.getType().equals("PRIVATE_FILE_MESSAGE")) {
                        System.out.println("PRIVATE_FILE_MESSAGE");

                        int fileNameLength = in.readInt();
                        System.out.println(fileNameLength); // Đọc độ dài của tên file
                        if (fileNameLength >0){
                            byte [] fileNameBytes = new byte[fileNameLength];       // Tạo một mảng byte để lưu tên file
                            in.readFully(fileNameBytes, 0, fileNameBytes.length); // Đọc tên file từ client
                            String fileName = new String(fileNameBytes);
                            System.out.println(fileName);
                            int fileContentLength = in.readInt();

                            if (fileContentLength>0){
                                byte [] fileContentBytes = new byte[fileContentLength]; // Tạo một mảng byte để lưu nội dung file
                                in.readFully(fileContentBytes, 0, fileContentBytes.length); // Đọc nội dung file từ client

                                // chuyen tiep file den client
                                String recipient = request.getTo(); // Lấy người nhận
                                this.forwardPrivateMessage(recipient, request); // Gửi tin nhắn riêng tư đến người nhận
                                this.forwardFileStreamToClient(recipient, fileNameBytes, fileContentBytes); // Gửi file đến người nhận
                            }
                        }
                } else if (request.getType().equals("USER_DISCONNECT")) {

                    // thong bao cho tat ca client khac la co 1 thak dbrr vua out nhom chat
                    serverFrm.ServerLogAppend("A user with id : "+currentUser.getId()+" has disconnected");// Thông báo cho server biết người dùng đã ngắt kết nối
                    this.broadcastAllExcludeCurrentSocket(request);

                    serverFrm.onUserDisconnect((User) request.getPayload());

                    removeClientThread();

                    client.close();
                }
            }while (true);
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }finally {
            try{
                in.close();
                out.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }
    public void removeClientThread(){ // Xóa client khỏi danh sách client
        Iterator<ClientThread> i = clients.iterator();
        while(i.hasNext()){
            ClientThread ct = i.next();
            if (ct == this){
                i.remove();
            }
        }
    }
    public static String getFileExtension(String fileName) { // Lấy đuôi file từ tên file
        int i = fileName.lastIndexOf("."); // Tìm vị trí xuất hiện cuối cùng của dấu chấm
        if(i > 0) {
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
        }
    }


    private void broadcastAllExcludeCurrentSocket(Message  msg){ // Gửi tin nhắn đến tất cả client trừ client hiện tại
        for (ClientThread aClient : clients){
            if (aClient != this){
                aClient.sendMessage(msg);
            }
        }
    }



    public void forwardPrivateMessage(String userId, Message serverMessage) { // Gửi tin nhắn riêng tư đến người nhận
        for (ClientThread client : clients) {
            if(client.getUser().getId().equals(userId)) {
                client.sendMessage(serverMessage);
            }
        }
    }


    public void forwardFileStreamToClient(String userID, byte[] fileNameBytes, byte [] fileContentBytes) throws IOException {
        for (ClientThread client : clients){ // Duyệt qua danh sách client
            if (client.getUser().getId().equals(userID)){ // Nếu client có id trùng với id của người nhận
                ObjectOutputStream outStream = client.getObjectOutputStream(); // Lấy ObjectOutputStream của client
                Thread forwardFileThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            outStream.writeInt(fileNameBytes.length);  // Gửi độ dài của tên file
                            outStream.write(fileNameBytes); // Gửi tên file
                            outStream.writeInt(fileContentBytes.length); // Gửi độ dài của nội dung file
                            outStream.write(fileContentBytes); //  Gửi nội dung file
                            outStream.flush();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                forwardFileThread.start();
                break;
            }
        }
    }

    public ObjectOutputStream getObjectOutputStream(){
        return out;
    } // Lấy ObjectOutputStream của client
    public void sendMessage(Message serverMessage){
        try{
            out.writeObject(serverMessage);
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public ArrayList<User>  fecthAllUser(){ // Lấy tất cả người dùng
        ArrayList<User> ls = new ArrayList<>();
        for (ClientThread cl : clients) {
            User u = cl.getUser();
            u.setConnected(true);
            ls.add(u);
        }
        return ls;

    }
   public  User getUser(){
        return this.currentUser;
   }

    public void close() {
        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
