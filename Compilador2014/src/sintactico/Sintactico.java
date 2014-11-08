/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sintactico;

import compilador2014.lexico.Lexico;
import compilador2014.lexico.Token;
/**
 *
 * @author william
 */
public class Sintactico {
    private Lexico analizadorLexico;
    private Token tokenActual;
    private String nombreArchivo;
    
    public Sintactico(String nmbArchivo){
        nombreArchivo = nmbArchivo;
        analizadorLexico = new Lexico(nmbArchivo);
        analizadorLexico.generarTokens();
    }
    
    public void iniciarAnalisis(){
        program();
    }
    
    private void program(){
        System.out.println("**** Inicio del programa ****");
        declaration();
        declaration_list();
    }
    
    private void declaration_list(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals(";") || tokenActual.obtenerLexema().equals("}")){
            declaration();
            declaration_list();
        }
    }
    
    private void declaration(){
        type_specifier();
        identifier();
        tokenActual  = analizadorLexico.consumirToken();
        switch(tokenActual.obtenerLexema()){
            case ";":
                analizadorLexico.retroceso();
                declaration_list();
                break;
            case "[":
                num();
                closebrace();
                semicolon();
                break;
            case "(":
                tokenActual = analizadorLexico.consumirToken();
                if(!tokenActual.obtenerLexema().equals("void")){
                    if(!tokenActual.obtenerLexema().equals(")")){
                        analizadorLexico.retroceso();
                        param();
                        param_list();
                    }else{
                        analizadorLexico.retroceso();
                    }
                }
                tokenActual = analizadorLexico.consumirToken();
                if(tokenActual.obtenerLexema().equals(")")){
                    tokenActual = analizadorLexico.consumirToken();
                    if(tokenActual.obtenerLexema().equals("{")){
                        compound_stmt();
                        tokenActual = analizadorLexico.consumirToken();
                        if(tokenActual.obtenerLexema().equals("}")){
                            // pasa
                        }else{
                            error(10,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                        }
                    }else{
                        error(9,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                    }
                }else{
                    error(8,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }
                break;
            default:
                error(5,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
        }
    }
    
    private void compound_stmt(){
        //System.out.println("CMP_STMT");
        local_declarations();
        statement_list();
    }
    
    private void local_declarations(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals("int") || tokenActual.obtenerLexema().equals("char") || tokenActual.obtenerLexema().equals("void")){
            analizadorLexico.retroceso();
            var_declaration();
            local_declarations();
        }else{
            analizadorLexico.retroceso();
        }
    }
    
    private void var_declaration(){
        type_specifier();
        identifier();
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals(";")){
            local_declarations();
        }else{
            error(5,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
        }
    }
    
    private void statement_list(){
        
    }
    
    private void param_list(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals(",")){
            param();
            param_list();
        }else{
            analizadorLexico.retroceso();
        }
    }
    
    private void param(){
        type_specifier();
        identifier();
    }
    
    private void type_specifier(){
         tokenActual = analizadorLexico.consumirToken();
         if(tokenActual.obtenerLexema().equals("int") || tokenActual.obtenerLexema().equals("void") || tokenActual.obtenerLexema().equals("char")){
             // pasa
         }else{
             error(3, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
         }
    }
    
    private void identifier(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerToken()=="identifier"){
             // pasa
        }else{
             if(tokenActual.obtenerToken()=="lex-error")
                error(1, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
             else
                error(4, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
        }
    }
    
    private void num(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerToken()=="constant"){
             // pasa
        }else{
             error(6, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
        }
    }
    
    private void closebrace(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals("]")){
             // pasa
        }else{
             error(7, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
        }
    }
    
    private void semicolon(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals(";")){
             // pasa
        }else{
             error(5, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
        }
    }
    
    private void error(int errorcode, String info, int linea){
        switch(errorcode){
            case 1:
                System.out.println("lexico linea["+linea+"]: no se reconoce la secuencia \""+info+"\"");
                break;
            case 2:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba una declaracion cerca de \""+info+"\"");
                break;
            case 3:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba una especificacion de tipo cerca de \""+info+"\"");
                break;
            case 4:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba un identificador cerca de \""+info+"\""); 
                break;
            case 5:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba \";\" cerca de \""+info+"\"");
                break;
            case 6:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba una constante cerca de \""+info+"\"");
                break;
            case 7:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba \"]\" cerca de \""+info+"\"");
                break;
            case 8:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba \")\" cerca de \""+info+"\"");
                break;
            case 9:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba \"{\" cerca de \""+info+"\"");
                break;
            case 10:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba \"}\" cerca de \""+info+"\"");
                break;
        }
       System.exit(errorcode);
    }
}
