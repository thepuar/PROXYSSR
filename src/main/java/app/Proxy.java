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

    public Proxy() {
        try {
            ss = new ServerSocket(3000);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void init() {
        try {

            socket = ss.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public void leerCliente() {
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
                pet = new Peticion(lineas);
                // System.out.println("Imprimiento MAP");
                // System.out.println(pet.toString());
                bw.write("Recibido");
                bw.flush();
                bw.close();
                br.close();
                System.out.println("La ruta es: " + pet.getRuta());
                this.createFolder(pet.getRuta());
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

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

    public void sendToServer() {
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

//            int c;
//            String respuesta = "";
//            
//            while ((c = br.read()) != -1) {
//                respuesta+= ((char) c);
//            }
            String line ="";
//            int contadorlineas = 1;
//            while ((line = br.readLine()) != null) {
//                byte[] array = line.getBytes();
//                System.out.println("Imprimiendo linea "+contadorlineas);
//                for(int i = 0; i<array.length;i++){
//                System.out.println("BYTE["+i+"]:"+String.format("%02X", array[i]));
//                }
//                lineas.add(line);
//                //lineas.add(new String (line.getBytes(),"UTF-8"));
//                contadorlineas++;
//            }
            
            int contadorsaltos = 0;
            byte[] contenido = new byte[0];
            int tamanyo = 0;
            while((line = br.readLine())!=null){
                if(line.isEmpty())contadorsaltos++;
                else contadorsaltos = 0;
                if(contadorsaltos == 1){
                    //Leer por bytes
                    int bite = 0;
                    int i = 0;
                    while((bite = br.read())!=-1){
                        System.out.println(String.format("%04X", bite));
                        contenido[i] = (byte)bite;
                        i++;
                    }
                }else{
                    //Leer por lineas
                    lineas.add(line);
                    if(line.contains("Content-Length:")){
                    String stamanyo = line.substring(line.indexOf(":")+1, line.length()).trim();
                    tamanyo = Integer.parseInt(stamanyo);
                    contenido = new byte[tamanyo];
                    }
                }
                
            }
            
            
            Respuesta respuesta = new Respuesta(lineas,contenido);
           

            System.out.println("RESPUESTA LEIDA");
            saveFile(contenido);

//            System.out.println("RESPUESTA: "+respuesta);
//            while (br.ready() && (mensaje = br.readLine()) != null) {
//                //System.out.println(mensaje);
//                lineas.add(mensaje);
//            }
            br.close();
            bw.close();

        } catch (IOException ioe) {
        }
        System.out.println("Lineas \n" + lineas);
    }

    public void saveFile(byte[] myByteArray) {

        try (FileOutputStream fos = new FileOutputStream("cache/"+pet.getRuta())) {
            fos.write(myByteArray);
            //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public void createFolder(String path) {
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
