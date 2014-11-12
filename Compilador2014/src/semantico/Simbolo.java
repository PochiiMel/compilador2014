/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package semantico;
import java.util.Hashtable;
/**
 *
 * @author william
 */
public class Simbolo {
    private String tipo;
    private String valor;
    private String id;
    private Hashtable<String,String> extra;
    public String[] paramsTypes;
    
    public Simbolo(String t, String i, String v){
        tipo = t;
        id = i;
        valor = v;
        extra = new Hashtable<String,String>();
        paramsTypes = new String[25];
    }
    
    public String obtenerTipo(){
        return tipo;
    }
    
    public String obtenerId(){
        return id;
    }
    
    public String obtenerValor(){
        return valor;
    }
    
    public void actualizarValor(String v){
        valor = v;
    }
    
    public void nuevaPropiedad(String prop, String val){
        extra.put(prop, val);
    }
    
    public boolean existePropiedad(String prop){
        return extra.containsKey(prop);
    }
    
    public String obtenerValor(String prop){
        if(existePropiedad(prop)) return extra.get(prop); else return null;
    }
}
