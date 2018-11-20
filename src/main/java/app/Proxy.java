/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thepu
 */
public class Proxy implements Runnable {

    private ServerSocket ss;
    private Socket socket;
    BufferedReader br;
    BufferedWriter bw;

    public Proxy() {
        try{
        ss = new ServerSocket(3000);
        }catch(IOException ioe){ioe.printStackTrace();}
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

               // if (mensaje.length() < 1) {
               //     break;
               // }

            }
            //Procesar la lineas
           Peticion pet = new Peticion(lineas);
           System.out.println("Imprimiento MAP");
           pet.toString();
            bw.write("Recibido");
            bw.flush();
            bw.close();
            br.close();
            

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

}
