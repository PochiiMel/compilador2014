/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package asm;

import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.swing.JTextArea;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

/**
 *
 * @author william
 */
public class Interprete {
    private String nombreArchivo;
    private JTextArea salida;
    private FileReader archivo;
    private boolean code;
    private boolean data;
    private Hashtable<String,String> variables;
    
    public Interprete(String na, JTextArea sa){
        nombreArchivo = na;
        salida = sa;
        code = false;
        data = false;
        variables = new Hashtable<String,String>();
        variables.put("_ASMReg_ax", "0");
        variables.put("_ASMReg_bx", "0");
        variables.put("_ASMReg_cx", "0");
        variables.put("_ASMReg_dx", "0");
    }
    
    private String obtenerOperando(String op){
        if(op.equals("ax")) return "_ASMReg_ax";
        if(op.equals("bx")) return "_ASMReg_bx";
        if(op.equals("cx")) return "_ASMReg_cx";
        if(op.equals("dx")) return "_ASMReg_dx";
        
        return op;
    }
    
    public void imprimir(String texto){
        salida.append(texto+"\n");
    }
    
    public void ejecutar(){
        try{
            archivo = new FileReader(nombreArchivo);
        }catch(FileNotFoundException e){
           throw new RuntimeException("No se ha encontrado el archivo");
        }
        BufferedReader entrada;
        try{
            entrada = new BufferedReader(archivo);
            String linea = "";
            while(entrada.ready()){
                linea = entrada.readLine();
                interpretar(linea);
            }
        }catch (IOException e) {
            
        }
    }
    
    public void interpretar(String linea){
        String comando    = linea.split(" ")[0];
        if(comando.equals(".data")) {data = true; code  = false;}
        if(comando.equals(".code")) {data = false; code = true;}
        
        if(code){
            String operandos = "";
            if(!comando.equals(".code")){
                if(linea.split(" ").length == 2)
                    operandos    =  linea.split(" ")[1];

                String operando1 = obtenerOperando(operandos.split(",")[0]);
                String operando2 = "";
                if(operandos.split(",").length == 2)
                    operando2    = obtenerOperando(operandos.split(",")[1]);
                
                switch(comando){
                    case "MOV":
                        if(operando1.equals("ah")){
                            if(variables.containsKey(operando2))
                                imprimir(variables.get(operando2));
                            else
                                imprimir(operando2);
                        }else{
                            if(variables.containsKey(operando2))
                                variables.put(operando1, variables.get(operando2));
                            else
                                variables.put(operando1, operando2);
                        }
                        break;
                    case "ADD":
                        variables.put("_ASMReg_ax",Integer.toString(Integer.parseInt(variables.get(operando1)) + Integer.parseInt(variables.get(operando2))));  
                        break;
                    case "MUL":
                        variables.put("_ASMReg_ax",Integer.toString(Integer.parseInt(variables.get(operando1)) * Integer.parseInt(variables.get("_ASMReg_ax"))));
                        break;
                    case "SUB":
                        variables.put("_ASMReg_ax",Integer.toString(Integer.parseInt(variables.get(operando2)) - Integer.parseInt(variables.get(operando1))));
                        break;
                    case "DIV":
                        variables.put("_ASMReg_ax",Integer.toString(Integer.parseInt(variables.get(operando1)) / Integer.parseInt(variables.get("_ASMReg_ax"))));
                        break;
                }
            }
        }
        if(data){
            String variable = comando;
            if(!comando.equals(".data")){
                String valor    = linea.split(" ")[2];
                if(valor.equals("0h")) valor = "0";
                String tipo     = linea.split(" ")[1];
                variables.put(variable, valor);
            }
        }
    }
}
