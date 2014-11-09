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
         
         tokenActual = analizadorLexico.consumirToken();
         if(tokenActual.obtenerLexema().equals("int") || tokenActual.obtenerLexema().equals("char") || tokenActual.obtenerLexema().equals("void")){
             analizadorLexico.retroceso();
             local_declarations();
         }
         
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
        tokenActual = analizadorLexico.consumirToken();
        //System.out.println("STATEMENT_LIST: " +  tokenActual.obtenerLexema());
        if(!tokenActual.obtenerLexema().equals("}")){
            analizadorLexico.retroceso();
            statement();
            statement_list();
        }else{
            //System.out.println("SALTO DE STATEMENT_LIST POR: " +  tokenActual.obtenerLexema());
            analizadorLexico.retroceso();
        }
    }
    
    private void statement(){
        //System.out.println("STATEMENT: " +  tokenActual.obtenerLexema());
        expresion_stmt();
        //compound_stmt();
    }
    
    private void expresion_stmt(){
        tokenActual = analizadorLexico.consumirToken();
        System.out.println("EXPRESION_STMT: " +  tokenActual.obtenerLexema());
        if(tokenActual.obtenerLexema().equals(";")){
            // pasa
        }else{
            analizadorLexico.retroceso();
            expresion();
        }
    }
    
    private void expresion(){
        tokenActual = analizadorLexico.consumirToken();
        System.out.println("EXPRESION: " +  tokenActual.obtenerLexema());
        if(tokenActual.obtenerToken().equals("identifier")){
            tokenActual = analizadorLexico.consumirToken();
            if(tokenActual.obtenerLexema().equals("[")){
                expresion();
                tokenActual = analizadorLexico.consumirToken();
                if(!tokenActual.obtenerLexema().equals("]")){
                    error(7, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }else{
                    tokenActual = analizadorLexico.consumirToken();
                    if(tokenActual.obtenerLexema().equals("=")){
                        expresion();
                    }else{
                        error(14, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                    }
                }
            }else if(tokenActual.obtenerLexema().equals("=")){
                expresion();
            }else{
                error(14, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
            }
        }else{
            simple_expresion();
        }
    }
    
    private void simple_expresion(){
        System.out.println("SIMPLE_EXPRESION: " +  tokenActual.obtenerLexema());
        if(tokenActual.obtenerToken().equals("relative-operator")||tokenActual.obtenerToken().equals("equal-operator")){
            tokenActual = analizadorLexico.consumirToken();
            aditive_expresion();   
        }else{
            aditive_expresion();
        }
    }
    
    private void aditive_expresion(){
         System.out.println("ADITIVE_EXPRESION: " +  tokenActual.obtenerLexema());
         if(tokenActual.obtenerToken().equals("add-operator")){
             tokenActual = analizadorLexico.consumirToken();
             aditive_expresion();
         }else{
             term();
         }
    }
    
    private void term(){
        System.out.println("TERM: " +  tokenActual.obtenerLexema());
        if(!tokenActual.obtenerToken().equals("mul-operator")){
           factor();
        }else{
              tokenActual = analizadorLexico.consumirToken();
              term();
        }
    }
    
    private void factor(){
        // (expresion)
        //tokenActual = analizadorLexico.consumirToken();
        System.out.println("FACTOR: " +  tokenActual.obtenerLexema());
        if(tokenActual.obtenerLexema().equals("(")){
            expresion();
            if(!tokenActual.obtenerLexema().equals(")")){
                error(8, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
            }
        }else if(tokenActual.obtenerToken().equals("identifier")){
            tokenActual = analizadorLexico.consumirToken();
            if(tokenActual.obtenerLexema().equals("[")){
                expresion();
                tokenActual = analizadorLexico.consumirToken();
                if(!tokenActual.obtenerLexema().equals("]")){
                    error(7, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }
            }else if(tokenActual.obtenerLexema().equals("(")){
                args();
                if(!tokenActual.obtenerLexema().equals(")")){
                    error(8, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }
            }else{
                analizadorLexico.retroceso();
            } 
        }else if(tokenActual.obtenerToken().equals("constant")){
            // pasa
        }else if(tokenActual.obtenerLexema().equals(")")){
            
        }else{
            error(15, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
        }
    }
    
    private void args(){
        arg_list();
    }
    
    private void arg_list(){
        arg_list();
        expresion();
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
            case 11:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba \"(\" cerca de \""+info+"\"");
                break;
            case 12:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba un operador (*,/) cerca de \""+info+"\"");
                break;
            case 13:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba un operador (+,-) cerca de \""+info+"\"");
                break;
            case 14:
                System.out.println("Error sintáctico linea["+linea+"]: se esperaba un \"=\" cerca de \""+info+"\"");
                break; 
            case 15:
                System.out.println("Error sintáctico linea["+linea+"]: Falta un operando cerca de \""+info+"\"");
                break;     
        }
       System.exit(errorcode);
    }
}
