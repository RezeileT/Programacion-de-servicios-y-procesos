package edu.thepower.u3comunicacionred;

import java.io.*;
import java.net.Socket;

public class U3P00EchoClient {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", 4000)){
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            PrintWriter pw = new PrintWriter(out, true);

            pw.println("Esto es una prueba");
            System.out.println("Devuelto por el servidor: " + bf.readLine());
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
        System.out.println("Comunicación finalizada");
    }
}
