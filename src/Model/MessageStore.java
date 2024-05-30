package Model;

import java.util.HashMap;
import java.util.Map;
// Lớp này sử dụng để lưu trữ tin nhắn của người dùng
// Lưu trữ dưới dạng một Map với key là userId và value là một mảng các tin nhắn
// Cung cấp các phương thức để tìm kiếm tin nhắn của người dùng và lưu trữ tin nhắn
public class MessageStore {
    private static Map<String, Message[]> data = new HashMap (); // tìm kiếm tin nhắn của người dùng theo userId và trả về mảng các tin nhắn của người dùng đó

    public static  Message[] findMessageForUser(String userId){
        if (data.get(userId)!=null){
            return data.get(userId);
        } else {
            return  null;
        }
    }

    public static void saveMessage(String userId, Message msg){ // lưu trữ tin nhắn của người dùng theo userId và tin nhắn đó vào Map data
        if(data.get(userId)!=null){
            Message[] oldMessages = data.get(userId);
            Message[] newMessages = new Message[oldMessages.length+1];
            System.arraycopy(oldMessages, 0, newMessages, 0, oldMessages.length);
            newMessages[oldMessages.length] = msg;
            data.put(userId, newMessages);
        }else {
            Message[] listMsg = new Message[1];
            listMsg[0] = msg;
            data.put(userId, listMsg);
        }
    }
}
