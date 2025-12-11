package edu.thepower.u4serviciosred;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;

public class U4P04FTPClient {
    public static void main(String[] args){
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect("eu-central-1.sftpcloud.io");
            System.out.println("Connected to ftp server");
            if(ftp.login("47388e0be4bc4b5183098b8bf11b553f", "DEyW61xISxnEnkfzGMlVx6ijDiuGvFUI")){
                System.out.println("Login successful");
                ftp.enterLocalPassiveMode();
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                InputStream is = new FileInputStream("resources/vocales.txt");
                String nombreRemoto = "archivo_remoto.txt";
                if(ftp.storeFile(nombreRemoto, is)){
                    System.out.println("File uploaded");
                }else{
                    System.out.println("File not uploaded");
                }
                FTPFile[] files = ftp.listFiles();
                for(FTPFile f : files){
                    System.out.println(f.getName());
                }
                OutputStream os = new FileOutputStream("resources/archivo_descargado.txt");
                if(ftp.retrieveFile(nombreRemoto, os)){
                    System.out.println("File downloaded");
                }else {
                    System.out.println("File not downloaded");
                }
            }else {
                System.out.println("Login failed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
