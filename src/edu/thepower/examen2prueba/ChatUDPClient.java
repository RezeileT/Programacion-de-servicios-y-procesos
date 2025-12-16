package edu.thepower.examen2prueba;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class ChatUDPClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6000;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress servidor = InetAddress.getByName(HOST);
            Scanner sc = new Scanner(System.in);

            // JOIN
            System.out.print("Introduce tu nombre: ");
            String nombre = sc.nextLine().trim();
            String joinMsg = "JOIN " + nombre;
            enviar(socket, joinMsg, servidor, PORT);

            // Recibir respuesta JOIN
            String respJoin = recibir(socket);
            System.out.println("Servidor: " + respJoin);

            // Hilo receptor
            Thread receptor = new Thread(() -> {
                try {
                    while (!socket.isClosed()) {
                        String msg = recibir(socket);
                        System.out.println(">> " + msg);
                    }
                } catch (IOException e) {
                    // Probablemente socket cerrado al salir
                }
            });
            receptor.setDaemon(true);
            receptor.start();

            // Hilo emisor (en main)
            while (true) {
                String linea = sc.nextLine();
                if (linea.equalsIgnoreCase("/salir")) {
                    enviar(socket, "LEAVE", servidor, PORT);
                    break;
                } else {
                    String msg = "MSG " + linea;
                    enviar(socket, msg, servidor, PORT);
                }
            }

            socket.close();
            System.out.println("Cliente cerrado.");

        } catch (IOException e) {
            System.err.println("Error en el cliente UDP: " + e.getMessage());
        }
    }

    private static void enviar(DatagramSocket socket, String mensaje, InetAddress host, int port) throws IOException {
        byte[] datos = mensaje.getBytes();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, host, port);
        socket.send(paquete);
    }

    private static String recibir(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return new String(paquete.getData(), 0, paquete.getLength()).trim();
    }
}
