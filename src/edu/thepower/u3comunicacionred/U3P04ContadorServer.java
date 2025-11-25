package edu.thepower.u3comunicacionred;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

class GestorClienteContador implements Runnable {

    private static final AtomicInteger contador = new AtomicInteger(0);
    private final Socket socket;

    public GestorClienteContador(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String nombre = "[" + Thread.currentThread().getName() + "]";
        System.out.println(nombre + " Cliente conectado:" + socket.getInetAddress());
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = bf.readLine()) != null) {
                String comando = line.trim().toLowerCase();
                System.out.println(nombre + " Recibido de cliente: " + comando);

                String respuesta = switch (comando) {
                    case "inc" -> String.valueOf(contador.incrementAndGet());
                    case "dec" -> String.valueOf(contador.decrementAndGet());
                    case "get" -> String.valueOf(contador.get());
                    case "bye" -> "Adiós";
                    default -> "Comando no válido";
                };

                pw.println(respuesta);

            }
        } catch (IOException e) {
            System.err.println(nombre + " Error en conexión: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
            System.out.println("Cliente desconectado:" + socket.getInetAddress() + ":" + socket.getPort());
        }
    }
}

public class U3P04ContadorServer {

    public static void main(String[] args) {
        int puerto = 0;
        try {
            puerto = Validacion.validarPuerto(args);
        }catch (Exception e){
            System.err.println("Error en el formato del puerto: " + e.getMessage());
            System.exit(1);
        }
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado; esperando conexiones en puerto: " + puerto);
            while (true) {
                Socket socket = servidor.accept();
                Thread t = new Thread(new GestorClienteContador(socket));
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
