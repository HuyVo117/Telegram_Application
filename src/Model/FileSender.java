package Model;

import View.ClientFrm;
import View.SendFileFrm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

public class FileSender {
    private SendFileFrm message;
    int fileID;
    String fileEtensions;
    File file;
    private long fileSize;
    private RandomAccessFile accFile;
    private Socket socket;
    private String getExtensions(String fileName){
        return fileName.substring(fileName.lastIndexOf("."), fileName.length()); // lấy đuôi file
    }



    public FileSender(File file , Socket socket, SendFileFrm message) throws IOException {
        accFile = new RandomAccessFile(file, "r");
        this.file = file;
        this.socket = socket;
        this.message = message;
        fileEtensions = getExtensions(file.getName());
        fileSize = accFile.length();
    }
    public synchronized byte[]readFile() throws IOException { // đọc file và chuyển thành mảng byte
        long filePointer = accFile.getFilePointer();
        if (filePointer != fileSize){
            int max = 2000;
            long length = filePointer+max >= fileSize? fileSize - filePointer : max; // đọc file theo từng phần nhỏ
            byte[] data = new byte[(int) length]; // chuyển file thành mảng byte
            accFile.read(data);
            return data;
        }else {
            return null;
        }
    }

    public void startSend(int fileID){

    }
    public void sendingFile() throws IOException {

    }

    public double getPercentage() throws IOException {
        double percentage ;
        long filePointer = accFile.getFilePointer();
        percentage = filePointer*100/fileSize;
        return percentage;
    }
    public void close() throws IOException {
        accFile.close();
    }

    public SendFileFrm getMessage() {
        return message;
    }

    public void setMessage(SendFileFrm message) {
        this.message = message;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getFileEtensions() {
        return fileEtensions;
    }

    public void setFileEtensions(String fileEtensions) {
        this.fileEtensions = fileEtensions;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public RandomAccessFile getAccFile() {
        return accFile;
    }

    public void setAccFile(RandomAccessFile accFile) {
        this.accFile = accFile;
    }

    public Socket getSocke() {
        return socket;
    }

    public void setSocke(Socket socke) {
        this.socket = socke;
    }


}
