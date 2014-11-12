/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador2014.lexico;

import compilador2014.lexico.Token;
import javax.swing.JTextArea;

/**
 *
 * @author william
 */
public class Automata {
    private int cantChars;
    private final int tamBuffer = 1000;
    private char[] buffer;
    private JTextArea salida;
    
    public Automata(){
        cantChars = 0;
        buffer = new char[tamBuffer];
        salida = null;
    }
    
    public void establecerSalidaErrores(JTextArea output){
        salida = output;
    }
    
    public void salidaErrores(String texto){
        if(salida==null){
            System.out.println(texto);
        }else{
            salida.append(texto+"\n");
        }
    }
    
    public void agregar(char elemento){
        buffer[cantChars++] = elemento;
    }
    
    public int elementosLeidos(){
        return cantChars;
    }
    
    public Token reconocer(){
        String palabra = String.valueOf(buffer).trim();
        if(KW(palabra)){
            return new Token("key-word",palabra);
        }
        else if(ID()){
            return new Token("identifier",palabra);
        }else if(DL(palabra)){
            return new Token("block-delimiter",palabra);
        }else if(ADDOP(palabra)){
            return new Token("add-operator",palabra);
        }else if(MULOP(palabra)){
            return new Token("mul-operator",palabra);
        }else if(CM(palabra)){
            return new Token("coma-separator",palabra);
        }else if(SC(palabra)){
            return new Token("semicolon",palabra);
        }else if(EQUOP(palabra)){
            return new Token("equal-operator",palabra);
        }else if(RELOP(palabra)){
            return new Token("relative-operator",palabra);
        }else if(ASIG(palabra)){
            return new Token("asig",palabra);
        }else if(CONST()){
            return new Token("constant",palabra);
        }else if(CONST_CHAR()){
            return new Token("constant-char",palabra);
        }else if(EXP(palabra)){
            return new Token("params-expantion",palabra);
        }else{
            return new Token("lex-error",palabra);
        }
    }
    
    private boolean KW(String palabra){
        switch(palabra){
            case "main":
                return true;
            case "int":
                return true;
            case "void":
                return true;
            case "printf":
                return true;
            case "scanf":
                return true;
            case "char":
                return true;
            case "return":
                return true;
            case "if":
                return true;
            case "else":
                return true;
            case "while":
                return true;
        }
        
        return false;
    }
    
    private boolean CONST_CHAR(){
        int estado = 0;
        for(int i = 0; i < cantChars; i++){
            switch(buffer[i]){
                case '\'':
                    if(estado==0) {estado = 1;}
                    else if(estado==3) {estado = 2;}
                    else if(estado==1) {estado = 5;}
                    break;
                default:
                    if(estado==1) {estado = 3;}
                    else if(estado==3) {estado = 4;}
                    break;
            }
        }
        
        if(estado==4) salidaErrores("[Advertencia]: Variable demasiado larga para su tipo. Su contenido sera truncado");
        if(estado==5) salidaErrores("[Advertencia]: Constante de caracter vacia");
        if(estado==2) return true;
        return false;
    }
    
    private boolean CM(String palabra){
        return palabra.equals(",");
    }
    
    private boolean EXP(String palabra){
        return palabra.equals("...");
    }
    
    private boolean ASIG(String palabra){
        return palabra.equals("=");
    }
    
    private boolean CONST(){
        int estado = 0;
        for(int i = 0; i < cantChars; i++){
            switch(buffer[i]){
                case '0':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '1':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '2':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '3':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 1;
                    break;
                case '4':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '5':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '6':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '7':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '8':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '9':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                default:
                    return false;
            }
        }
        
        if(estado==1) return true;
        return false;
    }
    
    private boolean SC(String palabra){
        return palabra.equals(";");
    }
    
    private boolean ADDOP(String palabra){
        switch(palabra){
            case "+":
                return true;
            case "-":
                return true;    
        }
        return false;
    }
    
    private boolean EQUOP(String palabra){
        switch(palabra){
            case "==":
                return true;
            case "!=":
                return true;    
        }
        return false;
    }
    
    private boolean RELOP(String palabra){
        switch(palabra){
            case ">":
                return true;
            case "<":
                return true;
            case ">=":
                return true;
            case "<=":
                return true;    
        }
        return false;
    }
    
    private boolean MULOP(String palabra){
        switch(palabra){
            case "*":
                return true;
            case "/":
                return true;    
        }
        return false;
    }
    
    private boolean DL(String palabra){
        switch(palabra){
            case "(":
                return true;
            case ")":
                return true;
            case "[":
                return true;
            case "]":
                return true;
            case "{":
                return true;
            case "}":
                return true;
        }
        
        return false;
    }
    
    private boolean ID(){
        // aceptacion = 1
        // error = 2
        int estado = 0;
        for(int i = 0; i < cantChars; i++){
            switch(buffer[i]){
                case '0':
                    if(estado==0)
                        estado = 2;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '1':
                    if(estado==0)
                        estado = 2;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '2':
                    if(estado==0)
                        estado = 2;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '3':
                    if(estado==0)
                        estado = 2;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '4':
                    if(estado==0)
                        estado = 2;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '5':
                    if(estado==0)
                        estado = 2;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '6':
                    if(estado==0)
                        estado = 2;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '7':
                    if(estado==0)
                        estado = 2;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '8':
                    if(estado==0)
                        estado = 2;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '9':
                    if(estado==0)
                        estado = 2;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case '_':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'a':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'b':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'c':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'd':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'e':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'f':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'g':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'h':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'i':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'j':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'k':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'l':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'm':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'n':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'o':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'p':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'q':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'r':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 's':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                   else if(estado==2)
                        estado = 2;
                    break;
                case 't':
                   if(estado==0)
                        estado = 1;
                   else if(estado==1)
                        estado = 1;
                   else if(estado==2)
                        estado = 2;
                    break;
                case 'u':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'v':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'w':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'x':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'y':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'z':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'A':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'B':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'C':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'D':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'E':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'F':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'G':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'H':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'I':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'J':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'K':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'L':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'M':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'N':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'O':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'P':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'Q':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'R':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'S':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'T':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'U':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'V':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'W':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'X':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'Y':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                case 'Z':
                    if(estado==0)
                        estado = 1;
                    else if(estado==1)
                        estado = 1;
                    else if(estado==2)
                        estado = 2;
                    break;
                default:
                    estado = 2;
                    break;
            }
        }
        
        if(estado==1) return true;
        return false;
    }

}
