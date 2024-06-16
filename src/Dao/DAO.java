package Dao;

import Model.User;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

public class DAO {
    private Connection conn;

    public DAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javachatappdb", "root", "Tolaco@#!123");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }



    //ham connection
    public ArrayList<User> getAllUser() {
        ArrayList<User> ls = new ArrayList<>();
        String sql = "SELECT * FROM User";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String nameLogin = rs.getString("NameLogin");
                String name = rs.getString("Name");
                String password = rs.getString("Password");
                User user = new User(id, nameLogin, name, password);
                ls.add(user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ls;
    }

    public User getUserInfo(String nameLogin){
        String sql = "Select * from User where NameLogin = ?";
        User user = null;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,nameLogin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                String id = rs.getString("id");
                String NameLogin = rs.getString("NameLogin");
                String name = rs.getString("Name");
                String password = rs.getString("Password");
                user = new User(id,NameLogin,name,password);
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return user;
    }

    public User getUserById(String id){
        String sql = "Select * from User where id = ?";
        User user = null;
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                String id1 = rs.getString("id");
                String nameLogin = rs.getString("NameLogin");
                String name = rs.getString("Name");
                String password = rs.getString("Password");
                user = new User(id1,nameLogin,name,password);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
return user;
    }
    public boolean addNewUser(User s){
        String sql = "Insert into User(NameLogin,Name,Password) value (?,?,?)";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,s.getNameLogin());
            ps.setString(2,s.getName());
            ps.setString(3,s.getPassword());
            return ps.executeUpdate()>0;
    }catch (SQLException throwables){
        throwables.printStackTrace();

    }
        return false;
    }

    public boolean login(String NameLogin, String password){
        try {
            // Hash the password
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            password = sb.toString();

            // Now, compare the hashed password with the hashed password in the database
            String query = "SELECT * FROM user WHERE NameLogin = ? AND Password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, NameLogin);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                return true;
            }
        } catch (NoSuchAlgorithmException | SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



    public Connection getConnection() {
        return conn;
    }
}
