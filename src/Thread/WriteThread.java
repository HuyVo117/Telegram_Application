package Thread;

import Model.Message;
import Model.User;
import View.ClientFrm;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WriteThread implements Runnable{
    private Socket socket;
    private ClientFrm client;
    private ObjectOutputStream writer;
    private User user;
    private Message message;

    public WriteThread(Socket s, ClientFrm client, User user, Message msg,ObjectOutputStream out){
        this.socket = s;
        this.client = client;
        this.user = user;
        this.message = msg;
        writer = out;
    }
    @Override
    public void run() {
        try{
            writer.writeObject(message); // Gửi dữ liệu đến server  bằng ObjectOutputStream
            if (message.getType().equals("PRIVATE_MESSAGE")){
            System.out.println("Đã gửi tin nhắn đến ! " );
            }
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
