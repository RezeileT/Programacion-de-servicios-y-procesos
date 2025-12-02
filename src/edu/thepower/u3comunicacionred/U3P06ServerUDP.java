package edu.thepower.u3comunicacionred;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class U3P06ServerUDP {
    public static void main(String[] args) throws IOException {
        try (DatagramSocket ds = new DatagramSocket(2100)) {
            System.out.println("Servidor escuchando en el puerto 2100");
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            ds.receive(dp);
            System.out.println("Mensaje recibido");
        } catch (IOException e) {
            System.err.println("Error en el servidor" + e.getMessage());
        }
    }
}
