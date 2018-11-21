/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.Thread.ThreadPoolManager;
import java.io.File;

/**
 *
 * @author thepu
 */
public class Launcher {

    public static void main(String[] args) {
        // ThreadPoolManager.getInstance().execute(new Proxy());
        Proxy proxy = new Proxy();
        File directorio = new File("cache");
        if (!directorio.exists()) {
            directorio.mkdir();
        }
        while (true) {
            proxy.init();
            proxy.leerCliente();

        }

    }

}
