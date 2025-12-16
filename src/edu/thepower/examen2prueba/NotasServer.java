package edu.thepower.examen2prueba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class NotasServer {
    private static Map<String, Double> notas = Collections.synchronizedMap(new TreeMap<String, Double>());

    static{
        String[] claves = {"ABC1", "DEF2", "HIJ3"};
        Double[] valores = {9.0, 7.5, 5.3};
        for(int i=0; i<claves.length; i++){
            notas.put(claves[i],valores[i]);
        }
    }

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(5000)){
            System.out.println("Servidor esperando conexiones: " + serverSocket.getLocalPort());
            while(true){
                Socket socket = serverSocket.accept();
                Thread hilo = new Thread(() -> gestionarCliente(socket));
                hilo.start();
            }
        }catch (IOException e){
            System.err.println("Error al iniciar el servidor" + e.getMessage());
        }
    }

    private static void gestionarCliente(Socket socket){
        try(
                BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)
        ){
            String comando;
            String respuesta;

            while((comando = bf.readLine()) != null){
                String[] comandoDividido =  comando.split("\\s+", 3);
                respuesta = devolverRespuesta(comandoDividido);
                pw.println(respuesta);
                if (comandoDividido[0].equalsIgnoreCase("exit")) {
                    break;
                }
            }

        }catch (IOException e){
            System.err.println("Error al gestionar el cliente" + e.getMessage());
        }
    }

    private static String devolverRespuesta(String[] comando){
        return switch (comando[0].trim().toLowerCase()){
            case "add" ->{
                if(comando.length == 3){
                    notas.put(comando[1],Double.parseDouble(comando[2]));
                    yield "Alumno agregado: " + comando[1] + " - " + comando[2];
                }else{
                    yield "Uso de ADD: ADD <dni> <nota>";
                }
            }
            case "get" -> {
                if(comando.length == 2){
                    Double nota = notas.getOrDefault(comando[1], -1.0);
                    if (nota < 0) {
                        yield "No se ha encontrado el alumno";
                    } else {
                        yield "Nota: " + nota;
                    }
                }else {
                    yield "Uso de GET: GET <dni>";
                }
            }
            case "list" -> {
                if(notas.isEmpty()){
                    yield "No hay alumnos registrados";
                }
                if(comando.length == 1){
                    StringBuffer sb = new StringBuffer();
                    for (Map.Entry entrada :notas.entrySet()){
                        sb.append(entrada.getKey()).append(": ").append(entrada.getValue()).append(", ");
                    }
                    if(!sb.isEmpty())
                        sb.setLength(sb.length() - 2);
                    yield sb.toString();
                }else{
                    yield "Uso de LIST: LIST";
                }
            }
            case "avg" -> {
                if(comando.length == 1){
                    double notasSuma = 0.0;
                    int numeroNotas = 0;
                    for(Map.Entry<String, Double> entry : notas.entrySet()){
                        numeroNotas++;
                        notasSuma += entry.getValue();
                    }
                    if(numeroNotas == 0){
                        yield "No se han encontrado notas";
                    }
                    double notasAvg = notasSuma / numeroNotas;
                    yield "Promedio de las notas: " + notasAvg;
                }else {
                    yield "Uso de AVG: AVG";
                }
            }
            case "del" -> {
                if(comando.length == 2){
                    Double eliminado = notas.remove(comando[1]);
                    if (eliminado != null) {
                        yield "Alumno eliminado: " + comando[1];
                    } else {
                        yield "No se ha encontrado el alumno";
                    }
                }else{
                    yield "Uso de DEL: DEL <dni>";
                }
            }
            case "exit" -> "Bye";
            default -> "Comando invalido";
        };

    }
}
