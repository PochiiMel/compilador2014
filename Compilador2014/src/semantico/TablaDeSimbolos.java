/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package semantico;
import semantico.Simbolo;
/**
 *
 * @author william
 */
public class TablaDeSimbolos {
    public Simbolo[][] tablaDeSimbolos;
    public int[] cantSimbolos;
    
    public TablaDeSimbolos(){
        tablaDeSimbolos = new Simbolo[50][500];
        cantSimbolos = new int[50];
        for(int i = 0; i < 50; i++){
            cantSimbolos[i] = 0;
        }
    }
    
    public void nuevoSimbolo(Simbolo smb, int ambito){
        if(ambito < 50 ){
            if(cantSimbolos[ambito]<500){
                tablaDeSimbolos[ambito][cantSimbolos[ambito]++] = smb;
            }
        }
    }
    
    public int existeSimbolo(String id){
        for(int i = 0; i < 50; i++){
            for(int j = 0; j < cantSimbolos[i]; j++){
                if(tablaDeSimbolos[i][j].obtenerId().equals(id)){
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int existeSimbolo(String id, int ambito){
        if(ambito < 50){
            for(int j = 0; j < cantSimbolos[ambito]; j++){
                if(tablaDeSimbolos[ambito][j].obtenerId().equals(id)){
                    return ambito;
                }
            }
        }
        return -1;
    }
    
    public Simbolo obtenerSimbolo(String id, int ambito){
        if(ambito < 50){
            for(int j = 0; j < cantSimbolos[ambito]; j++){
                if(tablaDeSimbolos[ambito][j].obtenerId().equals(id)){
                     return tablaDeSimbolos[ambito][j];
                }
            }
        }
        return null;
    }
}
