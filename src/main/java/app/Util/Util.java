/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.Util;

/**
 *
 * @author thepu
 */
public class Util {
    int posicion;
    public Util(){
     posicion = 0;
    }
    
     public byte[] concatenateByteArrays(byte[] a, byte[] b) {
        for(int i = 0; i<b.length;i++,posicion++){
            a[posicion]=b[i];
            System.out.println("BYTE "+posicion+": "+String.format("%02X", a[posicion]));
            
        }
        return a;
    }
    
}
