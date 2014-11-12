/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package semantico;
import semantico.TablaDeSimbolos;
import java.util.Stack;
/**
 *
 * @author william
 */
public class Semantico {
    public TablaDeSimbolos tablaDeSimbolos;
    private Stack<String> expresionStack;
    public String ultipoTipo;
    
    public Semantico(){
            tablaDeSimbolos = new TablaDeSimbolos();
            expresionStack = new Stack<String>();
    }
    
    public void verPila(){
        int len = expresionStack.size();
        for(int i = 0; i < len; i++){
            System.out.print(expresionStack.elementAt(i));
        }
        System.out.println();
    }
    
    public void vaciarPila(){
        expresionStack = new Stack<String>();
    }
    
    public boolean pilaVacia(){
        return expresionStack.empty();
    }
    
    public void ingresarAPila(String elemento){
        expresionStack.push(elemento);
    }
    
    public String operar(String tipo1, String operador, String tipo2){
        
        if(operador.equals("<")||operador.equals(">")||operador.equals("<=")||operador.equals(">=")||operador.equals("==")||operador.equals("!=")){
            if(tipo1.equals("int")){
                if(tipo2.equals("int")) return "int";
            }if(tipo1.equals("char")){
                if(tipo2.equals("char")) return "char";
            }
        }
        
        if(operador.equals("+")||operador.equals("-")){
            if(tipo1.equals("int")){
                if(tipo2.equals("int")) return "int";
            }if(tipo1.equals("char")){
                if(tipo2.equals("char")) return "char";
            }
        }
        
        if(operador.equals("*")||operador.equals("/")){
            if(tipo1.equals("int")){
                if(tipo2.equals("int")) return "int";
            }if(tipo1.equals("char")){
                if(tipo2.equals("char")) return "char";
            }
        }
        
        if(operador.equals("=")){
            if(tipo1.equals("int")){
                if(tipo2.equals("int")) return "int";
            }if(tipo1.equals("char")){
                if(tipo2.equals("char")) return "char";
            }
        }
        
        if(operador.equals("#")){
            if(tipo1.equals("int")){
                if(tipo2.equals("int")) return "int";
                if(tipo2.equals("char")) return "int";
            }if(tipo1.equals("char")){
                if(tipo2.equals("int")) return "int";
                if(tipo2.equals("char")) return "int";
            }if(tipo1.equals("void")){
                if(tipo2.equals("void")) return "void";
            }
            
        }
        
        return null;
    }
}
