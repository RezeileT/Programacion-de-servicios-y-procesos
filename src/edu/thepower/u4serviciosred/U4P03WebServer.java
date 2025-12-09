package edu.thepower.u4serviciosred;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class U4P03WebServer {
    private static AtomicInteger contador = new AtomicInteger(0);
    private static final String HTML_1 = """
            <html>
                <head>
                    <title>Servidor web sencillo</title>
                </head>
                <body>
                    <h1>Hola mundo</h1>
                    <p>Eres el visitante número: """;
    private static final String HTML_2 = """
            </p>
            </body>
            </html>
            """;

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Servidor iniciado en el puerto:" + PORT);
            while(true) {
                Socket socket = serverSocket.accept();
                contador.incrementAndGet();
                Thread thread = new Thread(() -> atenderSolicitud(socket));
                thread.start();
            }
        }catch (IOException e){
            System.err.println("Error al iniciar el servidor");
        }
    }

    private static void atenderSolicitud(Socket socket) {
        try(BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            String line =  bf.readLine();
            System.out.println(line);
            System.out.println("Devolviendo html");
            StringBuilder respuesta = new StringBuilder();
            respuesta.append(HTML_1).append(contador.get()).append(HTML_2);
            pw.println("HTTP/1.1 200 OK");
            pw.println("Content-Type: text/html;charset=UTF-8");
            pw.println("Content-Length: " + respuesta.toString().getBytes().length);
            pw.println();
            pw.print(respuesta);
            pw.flush();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }


}
