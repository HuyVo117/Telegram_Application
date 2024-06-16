package View;

import Model.Message;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SendFileFrm extends JDialog{
    private JPanel contentPane;
    private JLabel jlFileName;
    private JButton btnSendFile;
    private JButton btnChooseFile;
    private JPanel panel;
    private ClientFrm clientFrm;

    private File[] filesToSend = new File[1];

    public SendFileFrm(Frame chatClient, Socket s , ObjectOutputStream writer , User selectedUser, User currentUser, boolean modal){
        super(chatClient, modal);
        setContentPane(contentPane);
        setModal(true); // cố định dialog
        getRootPane().setDefaultButton(btnSendFile); // set button mặc
        // định
        this.setSize(450,450);
        this.setLocationRelativeTo(null);
        clientFrm =(ClientFrm) chatClient;


        //Gọi onCancel khi nhấn vô dấu x
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                onCancel();
            }
        });
        // goi onCancel khi nhấn escape
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        // gửi file
        btnSendFile.addActionListener(e -> {
            if (filesToSend[0] == null) { // kiểm tra file đã chọn chưa
               JOptionPane.showMessageDialog(contentPane, "Chọn file trước khi gửi");
            } else {
               try { // gửi file
                   FileInputStream fileInputStream = new FileInputStream(filesToSend[0].getAbsolutePath()); // đọc file


                   String fileName = filesToSend[0].getName(); // lấy tên file
                   byte[] fileNameBytes = fileName.getBytes();// chuyển tên file sang byte

                   byte[] fileContentBytes = new byte[(int) filesToSend[0].length()]; // chuyển nội dung file sang byte
                   fileInputStream.read(fileContentBytes); // đọc nội dung file
                   fileInputStream.close(); // đóng file

                   System.out.println("Đang gửi file " + fileName  + " cho " + selectedUser.getUsername()); // thông báo gửi file

                   Message filemMessage = new Message("PRIVATE_FILE_MESSAGE","file",currentUser.getId(),selectedUser.getId()); // tạo tin nhắn gửi file

                   Thread sendFile = new Thread(() -> {
                       try{
                           writer.writeObject(filemMessage); // gửi độ dài tên file
                           if (filemMessage.getType().equals("PRIVATE_FILE_MESSAGR")){ // kiểm tra loại tin nhắn
                               System.out.println("Gửi tin nhắn riêng"); //
                           }
                           writer.flush(); // xóa bộ đệm
                           writer.writeInt(fileNameBytes.length); // gửi độ dài tên file
                           writer.flush();
                           writer.write(fileNameBytes); // gửi tên file
                           writer.flush();
                           writer.writeInt(fileContentBytes.length); // gửi độ dài nội dung file
                           writer.flush();
                           writer.write(fileContentBytes); // gửi nội dung file
                           writer.flush();
                       } catch (IOException e1){
                           e1.printStackTrace();
                       }
                   });
                                   sendFile.start(); // bắt đầu gửi file
                                   dispose(); // đóng dialog
               } catch (IOException err){
                   err.printStackTrace();
               }
            }
        });
        // chọn file
        btnChooseFile.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setDialogTitle("Chọn file để gửi") ; // tiêu đề
            if (jFileChooser.showOpenDialog(clientFrm) == JFileChooser.APPROVE_OPTION ){ // mở dialog chọn file
                filesToSend[0] = jFileChooser.getSelectedFile(); // lấy file đã chọn
                jlFileName.setText("File đã chọn: " + filesToSend[0].getName()); // hiển thị tên file đã chọn
            }
        });


    }

    private void onCancel() {
        dispose();
    }



}
