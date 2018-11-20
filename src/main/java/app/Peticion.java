/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author thepu
 */
public class Peticion {

    private Map<String, String> cabeceras;
    private String metodo;
    private String protocol;
    private String uri;
    private String host;

    public Peticion(List<String> cabeceras) {
        if(cabeceras.size()>0){
        this.cabeceras = new HashMap<>();

        StringTokenizer st = new StringTokenizer(cabeceras.get(0));
        if (st.countTokens() > 2) {
            this.metodo = st.nextToken();
            this.uri = st.nextToken();
            this.protocol = st.nextToken();
        }
        String cab;
        String contentcab;
        for (int i = 1; i < cabeceras.size(); i++) {
            String valor = cabeceras.get(i);
            System.out.println("VALOR: "+valor+" - "+valor.length());
            if(cabeceras.get(i).length()>1){
            cab = cabeceras.get(i).substring(0,cabeceras.get(i).indexOf(":"));
            contentcab = cabeceras.get(i).substring(cabeceras.get(i).indexOf(":"), cabeceras.get(i).length());
            this.cabeceras.put(cab, contentcab);
            }
        }
        }
    }

    public Map<String, String> getCabeceras() {
        return cabeceras;
    }

   

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    
    public String toString(){
        String  result= "";
        result = this.host +" "+this.uri+" "+this.protocol+"\n";
        for(Map.Entry<String, String> datos : this.cabeceras.entrySet()){
            result = result.concat(datos.getKey()+" "+datos.getValue());
        }
        
        
        return result;
    }
    

}
