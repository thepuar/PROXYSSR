/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.Util.Util;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thepu
 */
public class Respuesta {

    byte[] contenido;
    int tamanyo;
    List<String> cabeceras;
    public Respuesta(List<String> cabeceras, byte[]contenido){
        this.contenido = contenido;
        this.cabeceras = cabeceras;
    }
    
    
    public Respuesta(List<String> respuesta) {
        
        boolean vacio = true;
        List<String> cabeceras = new ArrayList<>();
        int posicion = 0;
        for (int i = 0; vacio; i++) {
            String aux = respuesta.get(i);
            if(aux.contains("Content-Length:")){
                System.out.println("Asignando tamaño del contenido");
                String stamanyo = aux.substring(aux.indexOf(":")+1, aux.length()).trim();
                tamanyo = Integer.parseInt(stamanyo);
                contenido = new byte[tamanyo];
            }
            if (aux.isEmpty()) {
                vacio = false;
                posicion = i + 1;
            } else {
                
            }
            cabeceras.add(aux);

        }
        Util util = new Util();
        for (int i = posicion; i < respuesta.size(); i++) {

            contenido = util.concatenateByteArrays(contenido, respuesta.get(i).getBytes());
        }
        
        System.out.println("CONTENIDO: ");
    }

   
    
    public byte[] getContenido(){
        return this.contenido;
    }

}
