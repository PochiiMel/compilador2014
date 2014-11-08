/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador2014.lexico;

import java.io.*;
import compilador2014.lexico.Automata;
import compilador2014.lexico.Token;
/**
 *
 * @author william
 */
public class Lexico {
   private String nombreArchivo;
   private FileReader archivo;
   private String[] lexBuffer;
   private int contadorBuffer;
   private final int maxchar = 1000;
   private Token[] tokens;
   private int cantTokens;
   private int contTokens;
   
   public Lexico(String nmbArchivo){
       nombreArchivo = nmbArchivo;
       lexBuffer = new String[500];
       tokens = new Token[500];
       contadorBuffer = 0;
       cantTokens = 0;
       contTokens = 0;
       try{
       archivo = new FileReader(nombreArchivo);
       }catch(FileNotFoundException e){
           throw new RuntimeException("No se ha encontrado el archivo");
       }
   }
   
   private void nuevoToken(Token nt){
       tokens[cantTokens++] = nt;
   }
   
   public Token consumirToken(){
       if(contTokens < cantTokens)
         return tokens[contTokens++];
       else return null;
   }
   
   public void retroceso(){
       if(contTokens > 0) contTokens--;
   }
   
   public void establecerLinea(int linea){
       tokens[cantTokens-1].setLinea(linea);
   }
   
   public void generarTokens(){
       separarPalabras();
   }
   
   private void separarPalabras(){
       char[] cbuf = new char[maxchar];
       char[] nbuf = new char[maxchar];
       int caracteresLeidos  = 0;
       int comentarioAbierto = 0;
       try{
           BufferedReader buffer = new BufferedReader(archivo);
           caracteresLeidos = buffer.read(cbuf);
           for(int i = 0; i < caracteresLeidos; i++){
               //System.out.println(cbuf[i]);
               nbuf[i] = cbuf[i];
               if(i>=2){
                   if((cbuf[i-2]=='/') && (cbuf[i-1]=='*') && (comentarioAbierto==0)) {
                       //System.out.println("*** Abre comentario ***");
                       comentarioAbierto=1;
                       nbuf[i-2]=' ';
                       nbuf[i-1]=' ';
                   }
                   if(comentarioAbierto==1){
                       if(nbuf[i]!='\n')nbuf[i]=' ';
                       //System.out.println("Ignora: " + cbuf[i]);
                       if((cbuf[i-2]=='*') && (cbuf[i-1]=='/')){
                           comentarioAbierto = 0;
                          // System.out.println("*** Cierra comentario ***");
                           nbuf[i-2]=' ';
                           nbuf[i-1]=' ';
                       }
                   }
               }
           }
           int espacio = 0;
           int delimitador = 0;
           int const_char = 0;
           int linea = 1;
           Automata reconocedor = new Automata();
           for(int i = 0; i < caracteresLeidos; i++){
               if(const_char == 0){
                    if(nbuf[i]==' ' || nbuf[i]=='\n' || nbuf[i]=='\0' || nbuf[i]=='\r' || nbuf[i]=='\t'){
                        if(nbuf[i]=='\n') linea ++;
                        if(reconocedor.elementosLeidos()>0){
                            nuevoToken(reconocedor.reconocer());
                            establecerLinea(linea);
                            reconocedor = new Automata();
                        }
                    }else if(nbuf[i]=='(' || nbuf[i]==')' || nbuf[i]=='{' || nbuf[i]=='}' || nbuf[i]=='[' || nbuf[i]==']'){
                        if(reconocedor.elementosLeidos()>0){
                            nuevoToken(reconocedor.reconocer());
                            establecerLinea(linea);
                        }
                        reconocedor = new Automata();
                        reconocedor.agregar(nbuf[i]);
                        nuevoToken(reconocedor.reconocer());
                        establecerLinea(linea);
                        reconocedor = new Automata();
                    }else if(nbuf[i]=='+' || nbuf[i]=='-' || nbuf[i]=='*' || nbuf[i]=='/'){
                        if(reconocedor.elementosLeidos()>0){
                            nuevoToken(reconocedor.reconocer());
                            establecerLinea(linea);
                        }
                        reconocedor = new Automata();
                        reconocedor.agregar(nbuf[i]);
                        nuevoToken(reconocedor.reconocer());
                        establecerLinea(linea);
                        reconocedor = new Automata();
                    }else if(nbuf[i]=='=' || nbuf[i]=='>' || nbuf[i]=='<'){
                        if(reconocedor.elementosLeidos()>0){
                            nuevoToken(reconocedor.reconocer());
                            establecerLinea(linea);
                        }
                        reconocedor = new Automata();
                        reconocedor.agregar(nbuf[i]);
                        if( ((i+1)<caracteresLeidos) && (nbuf[i+1]=='=') ){
                            reconocedor.agregar(nbuf[i+1]);
                            i++;
                        }
                        nuevoToken(reconocedor.reconocer());
                        establecerLinea(linea);
                        reconocedor = new Automata();
                    }else if(nbuf[i]==','){
                        if(reconocedor.elementosLeidos()>0){
                            nuevoToken(reconocedor.reconocer());
                            establecerLinea(linea);
                        }
                        reconocedor = new Automata();
                        reconocedor.agregar(nbuf[i]);
                        nuevoToken(reconocedor.reconocer());
                        establecerLinea(linea);
                        reconocedor = new Automata();
                    }else if(nbuf[i]=='!'){
                        if(reconocedor.elementosLeidos()>0){
                            nuevoToken(reconocedor.reconocer());
                            establecerLinea(linea);
                        }
                        reconocedor = new Automata();
                        reconocedor.agregar(nbuf[i]);
                        if( ((i+1)<caracteresLeidos) && (nbuf[i+1]=='=') ){
                            reconocedor.agregar(nbuf[i+1]);
                            i++;
                        }
                        nuevoToken(reconocedor.reconocer());
                        establecerLinea(linea);
                        reconocedor = new Automata();
                    }else if(nbuf[i]==';'){
                        if(reconocedor.elementosLeidos()>0){
                            nuevoToken(reconocedor.reconocer());
                            establecerLinea(linea);
                        }
                        reconocedor = new Automata();
                        reconocedor.agregar(nbuf[i]);
                        nuevoToken(reconocedor.reconocer());
                        establecerLinea(linea);
                        reconocedor = new Automata();
                    }else if((nbuf[i]=='"')){
                        const_char = 1;
                        if(reconocedor.elementosLeidos()>0){
                            nuevoToken(reconocedor.reconocer());
                            establecerLinea(linea);
                        }
                        
                        reconocedor = new Automata();
                        reconocedor.agregar(nbuf[i]);
                    }                
                    else{ 
                       reconocedor.agregar(nbuf[i]);
                    }
               }else{
                    if(nbuf[i]=='"'){
                        if(i>=1){
                            if(nbuf[i-1]!='\\'){
                                const_char = 0;
                                reconocedor.agregar(nbuf[i]);
                                nuevoToken(reconocedor.reconocer());
                                establecerLinea(linea);
                                reconocedor = new Automata();
                            }
                        }
                    }else{
                        reconocedor.agregar(nbuf[i]);
                    }
               }        
           }
           if(reconocedor.elementosLeidos()>0){
                nuevoToken(reconocedor.reconocer());
                establecerLinea(linea);
           }
       }catch(IOException e){
           throw new RuntimeException("No se puede leer el archivo");
       }
   }
   
   public void verPalabras(){
       for(int i = 0; i < contadorBuffer; i++){
           System.out.println(lexBuffer[i]);
       }
   }
}
