/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author thepu
 */
public class Proxy implements Runnable {

    private ServerSocket ss;
    private Socket socket;
    BufferedReader br;
    BufferedWriter bw;
    Peticion pet;
    Respuesta respDelServidor;
    Respuesta respParaCliente;

    public Proxy() {
        try {
            ss = new ServerSocket(3000);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void init() {
        try {
            System.out.println("Esperando conexión del cliente");
            socket = ss.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
    
    public void processPet(){
        //Reenviar peticion
        this.pet = leerCliente();
            enviarPetServidor();
        //Procesar respuesta del servidor
        //Descargar contenido
        downloadFiles(this.pet.getRuta());
        //Enviar respuesta al cliente
    }

    public Peticion leerCliente() {
        Peticion  lapeticion = null;
        String mensaje;
        List<String> lineas = new ArrayList<>();
        try {

            System.out.println("Esperando para leer");
            while (br.ready() && (mensaje = br.readLine()) != null) {
                //System.out.println(mensaje);
                lineas.add(mensaje);

            }
            //Procesar la lineas
            if (lineas.size() > 0) {
                 lapeticion = new Peticion(lineas);
                
                // System.out.println("Imprimiento MAP");
                // System.out.println(pet.toString());
                bw.write("Recibido");
                bw.flush();
                bw.close();
                br.close();
                
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return lapeticion;

    }

    @Override
    public void run() {
        // while (true) {
        System.out.println("Se ha iniciado un hilo");
        init();
        leerCliente();
        init();
        leerCliente();
        //  }

    }

    public void stop() {
        try {
            this.socket.close();
            this.ss.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public Respuesta sendToServer() {
        Respuesta respuesta = null;
        String result = "";
        List<String> lineas = new ArrayList<>();
        try {
            String host = pet.getHost().trim();
            System.out.println("HOST: " + host);
            if (host.contains("upv.es")) {
                host = "www.upv.es";
            }
            Socket soc = new Socket(host, 80);
            soc.setSoTimeout(5000);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
            String mensaje = pet.toString();
            bw.write(mensaje + "\r\n");
            bw.flush();

            //Leer del servidor
            InputStream in = soc.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);

            String line = "";
//           
            int contadorsaltos = 0;
            String rutafinal="";
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    contadorsaltos++;
                } else {
                    contadorsaltos = 0;
                }
                if (contadorsaltos == 1) {
                      rutafinal =   saveFile(pet.getUri(), pet.getRuta());
                } else {
                    //Leer por lineas
                    lineas.add(line);
                }
            }
            //Respuesta del servidor
            respuesta = new Respuesta(lineas, rutafinal);
            
            br.close();
            bw.close();

        } catch (IOException ioe) {
        }
        System.out.println("Lineas \n" + lineas);
        return respuesta;
    }


    public String saveFile(String lauri, String file) {
        FileOutputStream fos;
        String rutafinal = "cache/"+file;
        try{
        BufferedInputStream bis = new BufferedInputStream(new URL(lauri).openStream());
         fos = new FileOutputStream(rutafinal);
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = bis.read(dataBuffer, 0, 1024)) != -1) {
             fos.write(dataBuffer, 0, bytesRead);
        }
        bis.close();
        fos.close();
            System.out.println("FICHERO: "+lauri+" Guardado");
        }catch(IOException ioe ){ioe.printStackTrace();}
        return rutafinal;
        
    }

    public void downloadFiles(String path) {
        String ruta = "cache/";
        StringTokenizer st = new StringTokenizer(path, "/");
        String cadena = st.nextToken();

        File directorio = new File(ruta.concat(cadena));
        if (!directorio.exists()) {
            directorio.mkdir();
        }
        ruta = ruta.concat(cadena);
        ruta = ruta.concat("/");
        while (st.hasMoreTokens()) {
            cadena = st.nextToken();
            if (cadena.contains(".")) {
                //Es un fichero
                sendToServer();
            } else {
                //Es una carpeta
                directorio = new File(ruta.concat(cadena));
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }
            }

        }

    }

}
