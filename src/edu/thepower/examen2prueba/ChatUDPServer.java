package edu.thepower.examen2prueba;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class ChatUDPServer {

    private static final int PORT = 6000;

    public static void main(String[] args) {
        Set<SocketAddress> clientes = new HashSet<>();

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("Servidor de chat UDP escuchando en el puerto " + socket.getLocalPort());

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
                socket.receive(paqueteRecibido);

                String mensaje = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength()).trim();
                SocketAddress remitente = paqueteRecibido.getSocketAddress();

                System.out.println("Recibido de " + remitente + " -> " + mensaje);

                String[] partes = mensaje.split("\\s+", 2);
                String comando = partes[0].toUpperCase();
                String resto = partes.length > 1 ? partes[1] : "";

                switch (comando) {
                    case "JOIN" -> {
                        clientes.add(remitente);
                        String resp = "OK Bienvenido " + resto;
                        enviarMensaje(socket, resp, remitente);
                        System.out.println("Cliente unido: " + remitente + " como " + resto);
                    }
                    case "MSG" -> {
                        if (!clientes.contains(remitente)) {
                            enviarMensaje(socket, "ERROR Debes hacer JOIN primero", remitente);
                            break;
                        }
                        String texto = resto;
                        String chatMsg = "CHAT " + remitente + " " + texto;
                        System.out.println("Reenviando a " + clientes.size() + " clientes: " + chatMsg);
                        for (SocketAddress destino : clientes) {
                            enviarMensaje(socket, chatMsg, destino);
                        }
                    }
                    case "LEAVE" -> {
                        clientes.remove(remitente);
                        enviarMensaje(socket, "OK Adiós", remitente);
                        System.out.println("Cliente salido: " + remitente);
                    }
                    default -> {
                        enviarMensaje(socket, "ERROR Comando inválido", remitente);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor UDP: " + e.getMessage());
        }
    }

    private static void enviarMensaje(DatagramSocket socket, String mensaje, SocketAddress destino) throws IOException {
        byte[] datos = mensaje.getBytes();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length,
                ((InetSocketAddress) destino).getAddress(),
                ((InetSocketAddress) destino).getPort());
        socket.send(paquete);
    }
}
