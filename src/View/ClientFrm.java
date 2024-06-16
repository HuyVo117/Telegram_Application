package View;

import Dao.DAO;
import Model.*;
import Thread.WriteThread;
import Thread.ReadThread;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ClientFrm extends JFrame{ // tạo giao diện cho client
    private JPanel rootPanel;
    private JScrollPane scrollPanelMsg;
    private JTextPane MessageArea;
    private JPanel inputPanel;
    private JTextArea txtMessage;
    private JButton btnSend;

    private JButton btnLike;
    private JButton btnSad;
    private JButton btnSmile;
    private JButton btnHappy;
    private JButton btnShock;
    private JButton fileButton;
    private JButton btnSetting;
    private JPanel topPanel;
    private JList jListUsers;
    private JLabel txtServerDetail;
    private JButton SendImage;
    private JButton micro;
    private HTMLDocument doc ;
    private ServerListFrm serverList;
    private DefaultListModel<User> userListModel;
    private boolean lineBreak = false;
    private JScrollBar sb = scrollPanelMsg.getVerticalScrollBar(); // tạo thanh cuộn cho khung chat
    private Socket socket;
    private User currentUser;
    private ArrayList<User> listUser;
    private ObjectOutputStream writer;
    private boolean isImageFile(File file){
        String name  = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
    }

    public boolean isLineBreak() {
        return lineBreak;
    }
    public void setLineBreak(boolean lineBreak) {
        this.lineBreak = lineBreak;
    }
    public
    ObjectOutputStream getObjectOutputStream() {
        return this.writer;
    }
    public ClientFrm(Frame serverList, Socket s, ServerDetail svdel, User user) {
        super();
        setTitle("Bạn đã đăng nhập với tên: " + user.getName());
        setContentPane(rootPanel);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.socket = s;
        this.serverList = (ServerListFrm) serverList;
        currentUser = user;
        txtServerDetail.setText("Hostname: " + svdel.getHostName() + "\nPort: " + svdel.getPort());
        btnSend.setPreferredSize(new Dimension(50, 40));
        txtMessage.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        txtMessage.setMargin(new Insets(10, 10, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 3));

        topPanel.setLayout(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        JPanel userPanel = new JPanel(new GridLayout(0, 1));
        JLabel lbName = new JLabel();
        JLabel status = new JLabel();
        JLabel icon = new JLabel();
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(getClass().getResource("/img/user-profile.png"))
                .getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        icon.setIcon(imageIcon);
        status.setForeground(Color.GREEN);
        lbName.setOpaque(true);
        status.setOpaque(true);
        icon.setOpaque(true);
        userPanel.add(lbName);
        userPanel.add(status);
        topPanel.add(userPanel, BorderLayout.CENTER);
        topPanel.add(icon, BorderLayout.WEST);


        try {
            writer = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Message disConnect = new Message("USER_DISCONNECT", ClientFrm.this.currentUser);
                Thread privateThread = new Thread(new WriteThread(s, ClientFrm.this, currentUser, disConnect, writer));
                privateThread.start();
                System.out.println("Close");
            }
        });


        doc = (HTMLDocument) MessageArea.getStyledDocument();


        userListModel = new DefaultListModel<>(); // tạo danh sách người dùng
        jListUsers.setModel(userListModel); // hiển thị danh sách người dùng
        jListUsers.setCellRenderer(new UserRendered(currentUser));


        jListUsers.addListSelectionListener(e -> {
            System.out.println("state change!");

            MessageArea.setText("<br/>");
            // find message for selected user
            User selectedUser = ((User) jListUsers.getSelectedValue());

            if (selectedUser != null) {
                lbName.setText(selectedUser.getName());
                status.setText("Online");

                if (selectedUser.getHasNewMessage() == true) {
                    selectedUser.setHasNewMessage(false);
                    updateUserList();
                    return;
                }

                String selectedUserId = selectedUser.getId();
                Message[] listMessages = MessageStore.findMessageForUser(selectedUserId);
                if (listMessages != null) {
                    for (Message msg : listMessages) {

                        if (selectedUserId.equals(msg.getFrom())) {
                            try {
                                if (msg.getPayload().equals("(y)")) {
                                    String url = ClientFrm.class.getClassLoader().getResource("img/like.png").toString();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div><pre>" + "<span style='color: red;'>" + selectedUser.getName() + ": </span>" + "<img src='" + url + "'/></pre></div><br/>");
                                } else if (msg.getPayload().equals("^_^")) {
                                    String url = ClientFrm.class.getClassLoader().getResource("img/smile.png").toString();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div><pre>" + "<span style='color: red;'>" + selectedUser.getName() + ": </span>" + "<img src='" + url + "'/></pre></div><br/>");
                                } else if (msg.getPayload().equals(">:0")) {
                                    String url = ClientFrm.class.getClassLoader().getResource("img/happy.png").toString();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div><pre>" + "<span style='color: red;'>" + selectedUser.getName() + ": </span>" + "<img src='" + url + "'/></pre></div><br/>");
                                } else if (msg.getPayload().equals(":(")) {
                                    String url = ClientFrm.class.getClassLoader().getResource("img/sad.png").toString();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div><pre>" + "<span style='color: red;'>" + selectedUser.getName() + ": </span>" + "<img src='" + url + "'/></pre></div><br/>");
                                } else if (msg.getPayload().equals(":O")) {
                                    String url = ClientFrm.class.getClassLoader().getResource("img/shocked.png").toString();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div><pre>" + "<span style='color: red;'>" + selectedUser.getName() + ": </span>" + "<img src='" + url + "'/></pre></div><br/>");
                                } else {
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div style='background-color: #ebebeb; margin: 0 0 10px 0;'><pre style='color: #000;'>"
                                                    + "<span style='color: red;'>" + selectedUser.getName() + ": </span>" + (String) msg.getPayload() + "</pre></div><br/>");
                                }
                            } catch (BadLocationException | IOException badLocationException) {
                                badLocationException.printStackTrace();
                            }
                        } else {
                            try {
                                if (msg.getPayload().equals("(y)")) {
                                    String url = ClientFrm.class.getClassLoader().getResource("img/like.png").toString();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div><pre>" + "<span style='color: #000;'>you: </span>" + "<img src='" + url + "'/></pre></div><br/>");
                                } else if (msg.getPayload().equals("^_^")) {
                                    String url = ClientFrm.class.getClassLoader().getResource("img/smile.png").toString();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div><pre>" + "<span style='color: #000;'>you: </span>" + "<img src='" + url + "'/></pre></div><br/>");
                                } else if (msg.getPayload().equals(">:0")) {
                                    String url = ClientFrm.class.getClassLoader().getResource("img/happy.png").toString();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div><pre>" + "<span style='color: #000;'>you: </span>" + "<img src='" + url + "'/></pre></div><br/>");
                                } else if (msg.getPayload().equals(":(")) {
                                    String url = ClientFrm.class.getClassLoader().getResource("img/sad.png").toString();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div><pre>" + "<span style='color: #000;'>you: </span>" + "<img src='" + url + "'/></pre></div><br/>");
                                } else if (msg.getPayload().equals(":O")) {
                                    String url = ClientFrm.class.getClassLoader().getResource("img/shocked.png").toString();
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div><pre>" + "<span style='color: #000;'>you: </span>" + "<img src='" + url + "'/></pre></div><br/>");
                                } else {
                                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                            "<div style='background-color: #05728F; margin: 0 0 10px 0;'><pre style='color: #fff'>"
                                                    + "<span style='color: yellow;'>you: </span>" + (String) msg.getPayload() + "</pre></div><br/>");
                                }
                            } catch (BadLocationException | IOException badLocationException) {
                                badLocationException.printStackTrace();
                            }
                        }
                    }
                    validate();
                    sb.setValue(sb.getMaximum());
                }
            }
        });

        Thread readThread = new Thread(new ReadThread(s, this));
        readThread.start();
        Message sessionEvent = new Message("SESSION", currentUser);
        Thread writeThread = new Thread(new WriteThread(s, this, currentUser, sessionEvent, writer));
        writeThread.start();
        btnSend.addActionListener(e -> {
            String content = txtMessage.getText();
            if (!content.equals("")) {
                String to = ((User) jListUsers.getSelectedValue()).getId();
                Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
                Thread privateThread = new Thread(new WriteThread(s, ClientFrm.this, currentUser, privateMessage, writer));
                privateThread.start();
                txtMessage.setText("");

                try {
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div style='background-color: #05728F; margin: 0 0 10px 0;'><pre style='color: #fff'>"
                                    + "<span style='color: yellow;'>you: </span>" + content + "</pre></div><br/>");
                } catch (BadLocationException | IOException badLocationException) {
                    badLocationException.printStackTrace();
                }

                MessageStore.saveMessage(to, privateMessage);


                MessageArea.setCaretPosition(MessageArea.getDocument().getLength()); // di chuyển con trỏ đến cuối văn bản

            }
        });


        btnLike.addActionListener(e -> {
            String content = "(y)";
            String to = ((User) jListUsers.getSelectedValue()).getId();
            Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
            Thread privateThread = new Thread(new WriteThread(s, ClientFrm.this, currentUser, privateMessage, writer));
            privateThread.start();
            txtMessage.setText("");

            String url = ClientFrm.class.getClassLoader().getResource("img/like.png").toString();


            try {
                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                        "<div><pre>" + "<span style='color: #000;'>you: </span>" + "<img src='" + url + "'/></pre></div><br/>");
            } catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }
            MessageStore.saveMessage(to, privateMessage);
            validate();
            sb.setValue(sb.getMaximum());
        });
        btnSmile.addActionListener(e -> {
            String content = "^_^";
            String to = ((User) jListUsers.getSelectedValue()).getId();
            Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
            Thread privateThread = new Thread(new WriteThread(s, ClientFrm.this, currentUser, privateMessage, writer));
            privateThread.start();
            txtMessage.setText("");

            String url = ClientFrm.class.getClassLoader().getResource("img/smile.png").toString();

            try {
                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                        "<div><pre>" + "<span style='color: #000;'>you: </span>" + "<img src='" + url + "'/></pre></div><br/>");
            } catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }
            MessageStore.saveMessage(to, privateMessage);
            validate();
            sb.setValue(sb.getMaximum());
        });
        btnHappy.addActionListener(e -> {
            String content = ">:0";
            String to = ((User) jListUsers.getSelectedValue()).getId();
            Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
            Thread privateThread = new Thread(new WriteThread(s, ClientFrm.this, currentUser, privateMessage, writer));
            privateThread.start();
            txtMessage.setText("");

            String url = ClientFrm.class.getClassLoader().getResource("img/happy.png").toString();

            try {
                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                        "<div><pre>" + "<span style='color: #000;'>you: </span>" + "<img src='" + url + "'/></pre></div><br/>");
            } catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }

            MessageStore.saveMessage(to, privateMessage);
            validate();
            sb.setValue(sb.getMaximum());
        });
        btnSad.addActionListener(e -> {
            String content = ":(";
            String to = ((User) jListUsers.getSelectedValue()).getId();
            Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
            Thread privateThread = new Thread(new WriteThread(s, ClientFrm.this, currentUser, privateMessage, writer));
            privateThread.start();
            txtMessage.setText("");

            String url = ClientFrm.class.getClassLoader().getResource("img/sad.png").toString();

            try {
                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                        "<div><pre>" + "<span style='color: #000;'>you: </span>" + "<img src='" + url + "'/></pre></div><br/>");
            } catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }
            MessageStore.saveMessage(to, privateMessage);
            validate();
            sb.setValue(sb.getMaximum());
        });
        btnShock.addActionListener(e -> {
            String content = ":O";
            String to = ((User) jListUsers.getSelectedValue()).getId();
            Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
            Thread privateThread = new Thread(new WriteThread(s, ClientFrm.this, currentUser, privateMessage, writer));
            privateThread.start();
            txtMessage.setText("");

            String url = ClientFrm.class.getClassLoader().getResource("img/shocked.png").toString();

            try {
                doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                        "<div><pre>" + "<span style='color: #000;'>you: </span>" + "<img src='" + url + "'/></pre></div><br/>");
            } catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }
            MessageStore.saveMessage(to, privateMessage);
            validate();
            sb.setValue(sb.getMaximum());
        });
        fileButton.addActionListener(e -> {
            User selectedUser = ((User) jListUsers.getSelectedValue());
            SendFileFrm frm = new SendFileFrm(ClientFrm.this, socket, writer,
                    selectedUser, currentUser, rootPaneCheckingEnabled);
            frm.setVisible(true);
        });
        btnSetting.addActionListener(e -> {
            SettingFrm frm = new SettingFrm(ClientFrm.this, rootPaneCheckingEnabled);
            frm.setVisible(true);
        });

        txtMessage.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !lineBreak) {
                    e.consume();
                    String content = txtMessage.getText();
                    if (!content.equals("")) {
                        String to = ((User) jListUsers.getSelectedValue()).getId();
                        Message privateMessage = new Message("PRIVATE_MESSAGE", content, currentUser.getId(), to);
                        Thread privateThread = new Thread(new WriteThread(s, ClientFrm.this, currentUser, privateMessage, writer));
                        privateThread.start();
                        txtMessage.setText("");

                        try {
                            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                                    "<div style='background-color: #05728F; margin: 0 0 10px 0;'><pre style='color: #fff'>"
                                            + "<span style='color: yellow;'>you: </span>" + content + "</pre></div><br/>");
                        } catch (BadLocationException | IOException badLocationException) {
                            badLocationException.printStackTrace();
                        }

                        MessageStore.saveMessage(to, privateMessage);
                        validate();
                        sb.setValue(sb.getMaximum());
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        // thêm chức năng gửi ảnh
        SendImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn file để gửi");
                fileChooser.setMultiSelectionEnabled(true); // Cho phép chọn nhiều file

                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        // Chấp nhận thư mục hoặc file có phần mở rộng là hình ảnh
                        if (f.isDirectory()) {
                            return true;
                        }
                        String extension = getFileExtension(f);
                        return extension != null && isImageExtension(extension);
                    }

                    @Override
                    public String getDescription() {
                        return "Image Files (jpg, jpeg, png, gif)";
                    }

                    private String getFileExtension(File file) {
                        String fileName = file.getName();
                        int dotIndex = fileName.lastIndexOf('.');
                        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                            return fileName.substring(dotIndex + 1).toLowerCase();
                        }
                        return null;
                    }

                    private boolean isImageExtension(String extension) {
                        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif");
                    }
                });

                int option = fileChooser.showOpenDialog(ClientFrm.getFrames()[0]); // Mở dialog chọn file
                if (option == JFileChooser.APPROVE_OPTION) { // Nếu chọn file thì
                    File[] files = fileChooser.getSelectedFiles(); // Lấy file đã chọn
                    for (File file : files) {
                        if (isImageFile(file)) { // Kiểm tra file có phải là ảnh không
                            try {
                                FileInputStream fileInputStream = new FileInputStream(file); // Đọc file
                                byte[] fileContent = new byte[(int) file.length()]; // Chuyển nội dung file sang byte
                                fileInputStream.read(fileContent); // Đọc nội dung file
                                fileInputStream.close(); // Đóng file
                                String fileName = file.getName(); // Lấy tên file
                                byte[] fileNameBytes = fileName.getBytes(); // Chuyển tên file sang byte

                                // Tạo đối tượng ImageIcon từ file
                                ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());

                                // Chèn hình ảnh vào JTextPane
                                StyledDocument doc = MessageArea.getStyledDocument();
                                Style style = doc.addStyle("StyleName", null);
                                StyleConstants.setIcon(style, imageIcon);
                                doc.insertString(doc.getLength(), "ignored text", style);

                                // Gửi file
                                String to = ((User) jListUsers.getSelectedValue()).getId(); // Lấy id người nhận
                                Message privateMessage = new Message("PRIVATE_FILE_MESSAGE", "file", currentUser.getId(), to); // Tạo tin nhắn gửi file
                                Thread privateThread = new Thread(() -> {
                                    try {
                                        writer.writeObject(privateMessage); // Gửi tin nhắn
                                        writer.flush(); // Xóa bộ đệm
                                        writer.writeInt(fileNameBytes.length); // Gửi độ dài tên file
                                        writer.flush(); // Xóa bộ đệm
                                        writer.write(fileNameBytes); // Gửi tên file
                                        writer.flush(); // Xóa bộ đệm
                                        writer.writeInt(fileContent.length); // Gửi độ dài nội dung file
                                        writer.flush(); // Xóa bộ đệm
                                        writer.write(fileContent); // Gửi nội dung file
                                        writer.flush(); // Xóa bộ đệm
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                });
                                privateThread.start(); // Bắt đầu gửi file
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (BadLocationException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            JOptionPane.showMessageDialog(ClientFrm.getFrames()[0], "File không phải là ảnh"); // Thông báo file không phải là ảnh
                        }
                    }
                }
            }

            private boolean isImageFile(File file) {
                String extension = getFileExtension(file);
                return extension != null && isImageExtension(extension);
            }

            private String getFileExtension(File file) {
                String fileName = file.getName();
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                    return fileName.substring(dotIndex + 1).toLowerCase();
                }
                return null;
            }

            private boolean isImageExtension(String extension) {
                return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif");
            }
        });


        micro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (micro.getText().equals("Mute")) {
                    micro.setText("Unmute");
                    micro.setIcon(new ImageIcon(getClass().getResource("/img/micro.png")));
                } else {
                    micro.setText("Mute");
                    micro.setIcon(new ImageIcon(getClass().getResource("/img/micro-mute.png")));
                }
            }
        });


    }



    public void setDefaultUserSelection() {

        jListUsers.setSelectedIndex(0);
    }

    public void updateListUsers(ArrayList<User> users) {
        listUser = users;

        for (int i=0; i<listUser.size(); i++) {
            User us = listUser.get(i);
            if(us.getId().equals(currentUser.getId())) {
                listUser.set(i, listUser.get(0));
                listUser.set(0, us);
            }
        }

        for (User u : listUser) {
            userListModel.addElement(u);
        }
    }

    public void setUserOnline(User u) {
        u.setConnected(true);
        listUser.add(u);
        userListModel.addElement(u);
    }

    public void setUserOffLine(User u) {
        u.setConnected(false);
        Iterator<User> itr = listUser.iterator();
        while (itr.hasNext()) {
            User user = itr.next();
            if(user.getId().equals(u.getId())) {
                itr.remove();
            }
        }
        User selectedUser = (User) jListUsers.getSelectedValue();
        if(selectedUser.getId().equals(u.getId())) {
            JOptionPane.showMessageDialog(rootPanel, selectedUser.getName() + " đã thoát!");
            userListModel.remove(jListUsers.getSelectedIndex());
            jListUsers.setSelectedIndex(0);
        } else {
            userListModel.removeElement(u);
        }
    }

    public void onPrivateMessage(Message msg) {
        MessageStore.saveMessage(msg.getFrom(), msg);
        User selectedUser = (User) jListUsers.getSelectedValue();
        User from = new DAO().getUserById(msg.getFrom());
        if(selectedUser.getId().equals(msg.getFrom())) {

            try {
                if(msg.getPayload().equals("(y)")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/like.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: red;'>" +from.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } else if(msg.getPayload().equals("^_^")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/smile.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: red;'>" +from.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } else if(msg.getPayload().equals(">:0")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/happy.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: red;'>" +from.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } else if(msg.getPayload().equals(":(")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/sad.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: red;'>" +from.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                } else if(msg.getPayload().equals(":O")) {
                    String url = ClientFrm.class.getClassLoader().getResource("img/shocked.png").toString();
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div><pre>" + "<span style='color: red;'>" +from.getName() +": </span>" +"<img src='"+ url + "'/></pre></div><br/>");
                }
                else {
                    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
                            "<div style='background-color: #ebebeb; margin: 0 0 10px 0;'><pre style='color: #000;'>"
                                    + "<span style='color: red;'>" + from.getName() + ": </span>" + (String) msg.getPayload() + "</pre></div><br/>");
                }

                MessageArea.setCaretPosition(MessageArea.getDocument().getLength());
            }
            catch (BadLocationException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }
        } else {

            for (User u : listUser) {
                if(u.getId().equals(from.getId())) {
                    u.setHasNewMessage(true);
                }
            }
            updateUserList();
        }
    }

    public void updateUserList() {
        int selectedIndex = jListUsers.getSelectedIndex();
        DefaultListModel<Object> usersListModel = new DefaultListModel<>();
        jListUsers.setModel(usersListModel);
        jListUsers.setCellRenderer(new UserRendered(currentUser));

        usersListModel.removeAllElements();


        for (int i=0; i<listUser.size(); i++) {
            User us = listUser.get(i);
            if(us.getId().equals(currentUser.getId())) {
                listUser.set(i, listUser.get(0));
                listUser.set(0, us);
            }
        }

        for (User u : listUser) {
            usersListModel.addElement(u);
        }
        jListUsers.setSelectedIndex(selectedIndex);
    }


    public void onPrivateFileMessage(ObjectInputStream in, User from) {
        try {
            int fileNameLength = in.readInt();
            System.out.println(fileNameLength);
            if(fileNameLength > 0) {
                byte[] fileNameBytes = new byte[fileNameLength];
                in.readFully(fileNameBytes, 0, fileNameBytes.length);
                String fileName = new String(fileNameBytes);
                System.out.println(fileName);
                int fileContentLength = in.readInt();

                if (fileContentLength > 0) {
                    byte[] fileContentBytes = new byte[fileContentLength];
                    in.readFully(fileContentBytes, 0, fileContentLength);

                    int output = JOptionPane.showConfirmDialog(rootPanel, "Bạn nhận được một file từ: " + from.getName() + "\n" +
                            "Tên file: " + fileName +"\n" +
                            "Bạn có muốn lưu lại không?" , "Có file gửi đến", JOptionPane.YES_OPTION, JOptionPane.NO_OPTION);
                    if(output == JOptionPane.YES_OPTION) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Chọn nơi bạn cần lưu");
                        fileChooser.setSelectedFile(new File(fileName));
                        int userSelection = fileChooser.showSaveDialog(rootPanel);
                        if(userSelection == JFileChooser.APPROVE_OPTION) {
                            File fileToDownLoad = fileChooser.getSelectedFile();
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(fileToDownLoad);
                                fileOutputStream.write(fileContentBytes);
                                fileOutputStream.close();
                                JOptionPane.showMessageDialog(rootPanel, "Lưu thành công!");
                            } catch (IOException err) {
                                err.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch(IOException err) {
            err.printStackTrace();
        }

    }
}