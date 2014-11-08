/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador2014.lexico;

import java.io.*;
import compilador2014.lexico.Automata;
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
   
   public Lexico(String nmbArchivo){
       nombreArchivo = nmbArchivo;
       lexBuffer = new String[500];
       contadorBuffer = 0;
       try{
       archivo = new FileReader(nombreArchivo);
       }catch(FileNotFoundException e){
           throw new RuntimeException("No se ha encontrado el archivo");
       }
   }
   
   public Lexico(){
       
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
                       nbuf[i]=' ';
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
           Automata reconocedor = new Automata();
           for(int i = 0; i < caracteresLeidos; i++){
               if(nbuf[i]==' ' || nbuf[i]=='\n' || nbuf[i]=='\0' || nbuf[i]=='\r' || nbuf[i]=='\t'){
                   if(reconocedor.elementosLeidos()>0){
                       reconocedor.reconocer();
                       reconocedor = new Automata();
                   }
               }else if(nbuf[i]=='(' || nbuf[i]==')' || nbuf[i]=='{' || nbuf[i]=='}' || nbuf[i]=='[' || nbuf[i]==']'){
                   if(reconocedor.elementosLeidos()>0){
                       reconocedor.reconocer();
                   }
                   reconocedor = new Automata();
                   reconocedor.agregar(nbuf[i]);
                   reconocedor.reconocer();
                   reconocedor = new Automata();
               }
               else{ 
                  reconocedor.agregar(nbuf[i]);
               }
               
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
