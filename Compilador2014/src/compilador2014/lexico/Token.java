/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador2014.lexico;

/**
 *
 * @author william
 */
public class Token {
    private String token;
    private String lexema;
    int linea;
    
    public Token(String tk, String lex){
        token = tk;
        lexema = lex;
        linea = 0;
    }
    
    public String obtenerToken(){
        return token;
    }
    
    public void setLinea(int l){
        linea = l;
    }
    
    public int obtenerLinea(){
        return linea;
    }
    
    public String obtenerLexema(){
        return lexema;
    }
    
    @Override
    public String toString(){
        
        return "["+token+","+lexema+"]";
    }
}
