package edu.thepower.examen2prueba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TodoWebServer {

    private static final int PORT = 8081;

    // Lista de tareas compartida
    private static final List<String> tareas =
            Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor TODO iniciado en el puerto " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                Thread hilo = new Thread(() -> atenderSolicitud(socket));
                hilo.start();
            }

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    private static void atenderSolicitud(Socket socket) {
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String line = br.readLine();
            if (line == null || line.isBlank()) {
                socket.close();
                return;
            }

            System.out.println("Petición: " + line);

            // Leer y descartar el resto de cabeceras
            String header;
            while ((header = br.readLine()) != null && !header.isBlank()) {
                // Puedes imprimirlas si quieres verlas
                // System.out.println(header);
            }

            String[] partes = line.split(" ");
            String metodo = partes[0].trim();
            String ruta = partes.length > 1 ? partes[1].trim() : "/";

            String estado;
            String cuerpoHtml;

            if (!metodo.equalsIgnoreCase("GET")) {
                estado = "405 Method Not Allowed";
                cuerpoHtml = generarHtmlBasico("Método no permitido");
            } else {
                // Solo GET
                if (ruta.equals("/") || ruta.equals("/index")) {
                    estado = "200 OK";
                    cuerpoHtml = paginaPrincipal();
                } else if (ruta.startsWith("/add?texto=")) {
                    String texto = extraerTextoDeRuta(ruta);
                    if (texto == null || texto.isBlank()) {
                        estado = "200 OK";
                        cuerpoHtml = generarHtmlBasico("No se ha indicado texto para la tarea");
                    } else {
                        tareas.add(texto);
                        estado = "200 OK";
                        cuerpoHtml = generarHtmlBasico(
                                "Tarea añadida: " + escapeHtml(texto) +
                                        "<br><a href=\"/list\">Ver lista de tareas</a>"
                        );
                    }
                } else if (ruta.equals("/list")) {
                    estado = "200 OK";
                    cuerpoHtml = paginaListaTareas();
                } else {
                    estado = "404 Not Found";
                    cuerpoHtml = generarHtmlBasico("Ruta no encontrada");
                }
            }

            enviarRespuestaHttp(pw, estado, cuerpoHtml);

        } catch (IOException e) {
            System.err.println("Error atendiendo solicitud: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) { }
        }
    }

    // Genera la página principal con un pequeño menú
    private static String paginaPrincipal() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Servidor TODO</title></head><body>");
        sb.append("<h1>Servidor TODO</h1>");
        sb.append("<p>Rutas disponibles:</p>");
        sb.append("<ul>");
        sb.append("<li><a href=\"/list\">/list</a> - Ver lista de tareas</li>");
        sb.append("<li>/add?texto=Estudiar - Añadir tarea \"Estudiar\"</li>");
        sb.append("</ul>");
        sb.append("</body></html>");
        return sb.toString();
    }

    // Genera la página con la lista de tareas
    private static String paginaListaTareas() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Lista de tareas</title></head><body>");
        sb.append("<h1>Lista de tareas</h1>");

        synchronized (tareas) {
            if (tareas.isEmpty()) {
                sb.append("<p>No hay tareas en la lista.</p>");
            } else {
                sb.append("<ul>");
                for (String t : tareas) {
                    sb.append("<li>").append(escapeHtml(t)).append("</li>");
                }
                sb.append("</ul>");
            }
        }

        sb.append("<p><a href=\"/\">Volver a inicio</a></p>");
        sb.append("</body></html>");
        return sb.toString();
    }

    // HTML básico con un mensaje centrado
    private static String generarHtmlBasico(String mensaje) {
        return "<html><head><title>Servidor TODO</title></head>" +
                "<body><h1>" + mensaje + "</h1>" +
                "<p><a href=\"/\">Volver a inicio</a></p>" +
                "</body></html>";
    }

    // Extrae el texto de /add?texto=ALGO
    private static String extraerTextoDeRuta(String ruta) {
        int idx = ruta.indexOf("texto=");
        if (idx == -1) return null;
        String valor = ruta.substring(idx + "texto=".length());
        // Aquí podrías hacer URLDecode si quisieras soportar espacios codificados
        return valor;
    }

    // Envía la respuesta HTTP completa
    private static void enviarRespuestaHttp(PrintWriter pw, String estado, String cuerpoHtml) {
        byte[] bytes = cuerpoHtml.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;

        pw.println("HTTP/1.1 " + estado);
        pw.println("Content-Type: text/html; charset=UTF-8");
        pw.println("Content-Length: " + length);
        pw.println(); // línea en blanco
        pw.print(cuerpoHtml);
        pw.flush();
    }

    // Escapar caracteres básicos para HTML
    private static String escapeHtml(String texto) {
        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
