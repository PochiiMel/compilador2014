/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sintactico;

import compilador2014.lexico.Lexico;
import compilador2014.lexico.Token;
import javax.swing.JTextArea;
import java.util.*;
import java.io.*;
import semantico.Semantico;
import semantico.Simbolo;
import asm.Interprete;
/**
 *
 * @author william
 */
public class Sintactico {
    private Lexico analizadorLexico;
    private Semantico analizadorSemantico;
    private Interprete interpreteEnsamblador;
    private Token tokenActual;
    private String nombreArchivo;
    private FileWriter asm;
    private JTextArea salida;
    private boolean main;
    int errores;
    int CURRENT_SCOPE;
    final int GLOBAL_SCOPE = 0;
    int COUNTER_SCOPE;
    String[] dataSimb;
    boolean simbPrep;
    private String sa_asm;
    private String salida_asm;
    private String buffer_declaracion;
    private String buffer_programa;
    
    public Sintactico(String nmbArchivo, String t){
        nombreArchivo = nmbArchivo;
        analizadorLexico = new Lexico(nmbArchivo);
        analizadorLexico.generarTokens();
        analizadorSemantico = new Semantico();
        salida = null;
        main = false;
        errores = 0;
        CURRENT_SCOPE = 0;
        COUNTER_SCOPE = 0;
        dataSimb = new String[3];
        simbPrep = false;
        sa_asm = t;
        buffer_declaracion = "";
        buffer_programa    = ".code\n";
        salida_asm = sa_asm.split("\\.")[0] + ".asm";
        try{
            asm = new FileWriter(salida_asm);
       }catch(IOException e){
           throw new RuntimeException("No se puede crear el archivo");
       }
    }
    
    public void establecerSalidaErrores(JTextArea output){
        salida = output;
        interpreteEnsamblador = new Interprete(salida_asm, output);
        analizadorLexico.establecerSalidaErrores(output);
    }
    
    public void salidaErrores(String texto){
        if(salida==null){
            System.out.println(texto);
        }else{
            salida.append(texto+"\n");
        }
    }
    
    public void limipiarSalidaErrores(){
        if(salida!=null){
            salida.setText("");
        }
    }
    
    /**
     * inicia el analisis sintactico
     */ 
    public void iniciarAnalisis(){
        // analisis del programa 
        Simbolo _iprintf = new Simbolo("void","iprintf","");
        _iprintf.nuevaPropiedad("numparams","1");
        _iprintf.paramsTypes[0] = "int";
        analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(_iprintf, GLOBAL_SCOPE);
        Simbolo _cprintf = new Simbolo("void","cprintf","");
        _cprintf.nuevaPropiedad("numparams","1");
        _cprintf.paramsTypes[0] = "char";
        analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(_cprintf, GLOBAL_SCOPE);
        Simbolo _iscanf = new Simbolo("int","iscanf","");
        
        analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(_iscanf, GLOBAL_SCOPE);
        Simbolo _cscanf = new Simbolo("char","cscanf","");
        analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(_cscanf, GLOBAL_SCOPE);
        program(); 
        if(!main){
            errores++;
            salidaErrores("No se encuentra ningun punto de entrada (main) en el programa");
        }
        salidaErrores("**** FIN DE COMPILACION Errores("+errores+")****");
        if(errores==0){
            escribir_asm(buffer_declaracion);
            escribir_asm(buffer_programa);
            salidaErrores("Compilacion exitosa!!!");
        }else{
            salidaErrores("Se encontraron algunos errores!!!");
        }
        try{
            asm.close();
            salidaErrores("Corriendo...");
            interpreteEnsamblador.ejecutar();
            salidaErrores("Programa terminado con exito...");
        }catch(IOException e){
            throw new RuntimeException("No se puede cerrar el archivo");
        }
    }
    
    /**
     * Directivas de ensamblador, traducion asm
     * 
     */
    public void ASM_DECLARACION(String varName){
        // todas son inicializadas en vacio
        buffer_declaracion += varName+ " " + "db 0h\n";
    }
    
    public void ASM_MOV_AX(String valor){
        buffer_programa += "MOV ax,"+valor+"\n";
    }
    
    public void ASM_MOV_CX(String valor){
        buffer_programa += "MOV cx,"+valor+"\n";
    }
    
    public void ASM_MOV_BX(String valor){
        buffer_programa += "MOV bx,"+valor+"\n";
    }
    
    public void ASM_ASIGNACION_CONSTANTE(String varName, String valor){
        buffer_programa += "MOV ax,"+valor+"\n";
        buffer_programa += "MOV "+varName+",ax\n";
    }
    
    public void ASM_ASIGNACION_VARIABLE(String varName){
        buffer_programa += "MOV "+varName+",ax\n";
    }
    
    public void ASM_SUMAR(String a, String b){
	buffer_programa += "MOV ax,"+a+"\n";
	buffer_programa += "ADD ax,"+b+"\n";
    }
    
    public void ASM_SUMAR_ACUM(String b){
	//buffer_programa += "MOV ax, "+a+" \t\t;copiando el valor de a en ax\n";
	buffer_programa += "ADD ax,"+b+"\n";
    }
    
    public void ASM_RESTAR(String a, String b){
	buffer_programa += "MOV ax,"+a+"\n";
	buffer_programa += "SUB ax,"+b+"\n";
    }
    
    public void ASM_RESTAR_ACUM(String b){
	//buffer_programa += "MOV ax, "+a+" \t\t;copiando el valor de a en ax\n";
	buffer_programa += "SUB ax,"+b+"\n";
    }
    
    public void ASM_MULTIPLICAR(String a, String b){
	buffer_programa += "MOV ax,"+a+"\n";
	buffer_programa += "MOV bx,"+b+"\n";
	buffer_programa += "MUL bx\n";
        //buffer_programa += "MOV ax, bx    \t\t;dejamos el resultado en acumulador ax\n";
    }
    
    public void ASM_MULTIPLICAR_ACUM(String b){
	//buffer_programa += "MOV ax, "+a+" \t\t;copiando el valor de a en ax\n";
	buffer_programa += "MOV bx,"+b+"\n";
	buffer_programa += "MUL bx\n";
        //buffer_programa += "MOV ax, bx    \t\t;dejamos el resultado en acumulador ax\n";
    }
    
    public void ASM_DIVIDIR(String a, String b){
	buffer_programa += "MOV ax,"+a+"\n";
	buffer_programa += "MOV bx,"+b+"\n";
	buffer_programa += "DIV bx\n";
        //buffer_programa += "MOV ax, bx    \t\t;dejamos el resultado en acumulador ax\n";
    }
    
    public void ASM_DIVIDIR_ACUM(String b){
	//buffer_programa += "MOV ax, "+a+" \t\t;copiando el valor de a en ax\n";
	buffer_programa += "MOV bx,"+b+"\n";
	buffer_programa += "DIV bx\n";
       // buffer_programa += "MOV ax, bx    \t\t;dejamos el resultado en acumulador ax\n";
    }
    
    public void ASM_IMPRIMIR_AX(){
        buffer_programa += "MOV ah,ax\n";
    }
    
    public void ASM_NEG_AX(){
        buffer_programa += "NEG ax\n";
    }
    
    public void ASM_NEG_BX(){
        buffer_programa += "NEG bx\n";
    }
    
    /**
     * Un programa esta compuesto por una declaracion o una lista de declaraciones, La ultima declaracion del programa debe 
     * ser la declaracion de una funcion de nombre main.
     */
    private void program(){
        Calendar fecha = new GregorianCalendar();
        //Obtenemos el valor del año, mes, día,
        //hora, minuto y segundo del sistema
        //usando el método get y el parámetro correspondiente
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int hora = fecha.get(Calendar.HOUR_OF_DAY);
        int minuto = fecha.get(Calendar.MINUTE);
        int segundo = fecha.get(Calendar.SECOND);
        salidaErrores("DATE: "+ dia + "/" + (mes+1) + "/" + año+" "+hora+":"+minuto+":"+segundo);
        salidaErrores("**** INICIO DE COMPILACION ****");
        escribir_asm(".model small \n" +
                     ".stack \n" +
                     ".data \n");
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual!=null){
            if(tokenActual.obtenerLexema().equals("void") || tokenActual.obtenerLexema().equals("int") || tokenActual.obtenerLexema().equals("char")){
                analizadorLexico.retroceso();
                declaration_list();
            }
        }
    }
    
    public void escribir_asm(String con){
        try{
            asm.write(con);
        }catch(IOException e){
            
        }
    }
    
    /**
     * Una lista de declaraciones esta compuesta de una declaracion mas otra lista de declaraciones que a su vez puede estar vacia
     * contener una declaracion o bien una declaracion y otra lista de declaraciones. (definicion recursiva).
     */
    private void declaration_list(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual!=null){
            if(tokenActual.obtenerLexema().equals("void") || tokenActual.obtenerLexema().equals("int") || tokenActual.obtenerLexema().equals("char")){
                tokenActual = analizadorLexico.consumirToken();
                if(tokenActual.obtenerToken().equals("identifier")){
                    analizadorLexico.retroceso();
                    analizadorLexico.retroceso();
                    declaration();
                    declaration_list();
                }else if(tokenActual.obtenerLexema().equals("main")){
                    analizadorLexico.retroceso();
                    analizadorLexico.retroceso();
                    main_declaration();
                }
            }else if(tokenActual.obtenerLexema().equals(";")){
                declaration();
                declaration_list();
            }else if(tokenActual.obtenerLexema().equals("}")){
                declaration();
                declaration_list();
            }else{
                error(4, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
            }
        }else{
            analizadorLexico.retroceso();
        }
    }
    
    private void main_declaration(){
        main = true;
        type_specifier();                                   // pregunta por el especificador de tipo
        tokenActual = analizadorLexico.consumirToken();
        if(!tokenActual.obtenerLexema().equals("main")){
            error(16,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
        }
       
        tokenActual  = analizadorLexico.consumirToken();    // consume el siguiente token para identificar la regla de produccion que sera aplicada a continuciacion
        switch(tokenActual.obtenerLexema()){
            case "(":                                               // si el siguiente token es la apertura de un parentesis nos encontramos con la declaracion de una funcion
                tokenActual = analizadorLexico.consumirToken();     // el parametro siguiente podria ser void (vacio)
                if(!tokenActual.obtenerLexema().equals("void")){    // si no es void deberia seguir una lista de parametros o bien el cierre del parentesis
                    if(!tokenActual.obtenerLexema().equals(")")){
                        analizadorLexico.retroceso();
                        param("main");
                        param_list("main");
                    }else{
                        analizadorLexico.retroceso();
                    }
                }
                tokenActual = analizadorLexico.consumirToken();
                if(tokenActual.obtenerLexema().equals(")")){        // cierre de parentesis de la declaracion de la funcion
                    tokenActual = analizadorLexico.consumirToken();
                    if(tokenActual.obtenerLexema().equals("{")){    // se abren llaves
                        COUNTER_SCOPE++;
                        CURRENT_SCOPE = COUNTER_SCOPE;
                        compound_stmt();                            // se busca un conjunto de sentencias (cuerpo de funcion)
                        tokenActual = analizadorLexico.consumirToken();
                        if(tokenActual!=null){
                            if(tokenActual.obtenerLexema().equals("}")){  // fin de declaracion de funcion      
                                CURRENT_SCOPE = CURRENT_SCOPE - COUNTER_SCOPE;    
                            }else{
                                error(10,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                            }
                        }else{
                            error(10,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                        }
                    }else{
                        error(9,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                    }
                }else{
                    error(8,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());    // falta el parentesis de cierre de la declaracion de la funcion
                }
                break;
            default:
                error(11,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());        // no se encontro ni ";", "(" o "[" lanzamos un error preguntando por el ";" 
        }
    }
    
    /**
     * Una declaracion puede ser de dos tipos. La declaracion de una variable o la declaracion de una funcion. 
     * La declaracion de una variable puede ser una variable o un arreglo. Para todos los casos se espera un especificador de tipo
     * y un identificador y posteriormente se identifica el caso de declaracion. 
     */
    private void declaration(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual!=null){
            analizadorLexico.retroceso();
            type_specifier();                                   // pregunta por el especificador de tipo
            identifier();                                       // pregunta por el identificador
            tokenActual  = analizadorLexico.consumirToken();    // consume el siguiente token para identificar la regla de produccion que sera aplicada a continuciacion
            switch(tokenActual.obtenerLexema()){
                case ";":                                       // si el siguiente token es un punto y coma se llega al fin de la declaracion, retrocedemos un token para volver a comprobar si existen mas declaraciones
                    dataSimb[2] = "";
                    if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(dataSimb[1], CURRENT_SCOPE)<0){
                        ASM_DECLARACION(dataSimb[1]+"_"+Integer.toString(CURRENT_SCOPE));
                        Simbolo nuevaVariable = new Simbolo(dataSimb[0], dataSimb[1], dataSimb[0]);
                        analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(nuevaVariable, CURRENT_SCOPE);
                    }else{
                        error(17,dataSimb[1],tokenActual.obtenerLinea());
                    }
                    analizadorLexico.retroceso();
                    declaration_list();
                    break;
                case "[":                                       // si el siguiente token es la apertura de un corchete nos encontramos con la declaracion de un arreglo
                    num();                                      // debemos validar que despues del corchete se encuente un numero
                    closebrace();                               // luego que se cierre el corchete abierto
                    semicolon();                                // y que posteriormente tengamos el cierre del punto y coma
                    dataSimb[2] = "";
                    if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(dataSimb[1], CURRENT_SCOPE)<0){
                        Simbolo nuevaVariable = new Simbolo(dataSimb[0], dataSimb[1], dataSimb[0]);
                        analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(nuevaVariable, CURRENT_SCOPE);
                    }else{
                        error(17,dataSimb[1],tokenActual.obtenerLinea());
                    }
                    break;
                case "(":                                               // si el siguiente token es la apertura de un parentesis nos encontramos con la declaracion de una funcion
                    dataSimb[2] = "";
                    if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(dataSimb[1], GLOBAL_SCOPE)<0){
                        Simbolo nuevaVariable = new Simbolo(dataSimb[0], dataSimb[1], dataSimb[0]);
                        analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(nuevaVariable, CURRENT_SCOPE);
                    }else{
                        error(20,dataSimb[1],tokenActual.obtenerLinea());
                    }
                    tokenActual = analizadorLexico.consumirToken();     // el parametro siguiente podria ser void (vacio)
                    if(!tokenActual.obtenerLexema().equals("void")){    // si no es void deberia seguir una lista de parametros o bien el cierre del parentesis
                        if(!tokenActual.obtenerLexema().equals(")")){
                            analizadorLexico.retroceso();
                            String funcdef = dataSimb[1];
                            param(funcdef);
                            dataSimb[2] = "";
                            if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(dataSimb[1], COUNTER_SCOPE + 1)<0){
                                 Simbolo nuevaVariable = new Simbolo(dataSimb[0], dataSimb[1], dataSimb[0]);
                                 analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(nuevaVariable, COUNTER_SCOPE + 1);
                             }else{
                                  error(17,dataSimb[1],tokenActual.obtenerLinea());
                             }
                            param_list(funcdef);
                        }else{
                            analizadorLexico.retroceso();
                        }
                    }
                    tokenActual = analizadorLexico.consumirToken();
                    if(tokenActual.obtenerLexema().equals(")")){        // cierre de parentesis de la declaracion de la funcion
                        tokenActual = analizadorLexico.consumirToken();
                        if(tokenActual.obtenerLexema().equals("{")){    // se abren llaves
                            COUNTER_SCOPE++;
                            CURRENT_SCOPE = COUNTER_SCOPE;
                            compound_stmt();                            // se busca un conjunto de sentencias (cuerpo de funcion)
                            tokenActual = analizadorLexico.consumirToken();
                            if(tokenActual!=null){
                                if(tokenActual.obtenerLexema().equals("}")){  // fin de declaracion de funcion      
                                    CURRENT_SCOPE = CURRENT_SCOPE - COUNTER_SCOPE;    
                                }else{
                                    error(10,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                                }
                            }else{
                                error(10,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                            }
                        }else{
                            error(9,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                        }
                    }else{
                        error(8,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());    // falta el parentesis de cierre de la declaracion de la funcion
                    }
                    break;
                default:
                    error(5,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());        // no se encontro ni ";", "(" o "[" lanzamos un error preguntando por el ";" 
            }
        }
    }
    
    /**
     * Conjunto de sentencias, puede contener declaraciones locales o una lista de sentencias
     * Declaracion: Obligatoriamente inicia por un especificador de tipo
     * Sentencias: Puede iniciar por ";" (linea vacia), un identificador de una variable o bien por una constante (caracter o entero)
     */
    private void compound_stmt(){
         tokenActual = analizadorLexico.consumirToken(); // consume el token para validar si se trada de una declaracion o de otro tipo de sentencia
         //salidaErrores("Endrada compound: " + tokenActual.obtenerLexema());
         if(tokenActual.obtenerLexema().equals("int") || tokenActual.obtenerLexema().equals("char") || tokenActual.obtenerLexema().equals("void")){
             // aca tenemos declaraciones
             analizadorLexico.retroceso();
             local_declarations();
         }
         
         // lista de sentencias
         analizadorLexico.retroceso();
         statement_list();
    }
    
    /**
     * Una declaracion local consiste de una definicion recursiva, esta determinada por una declaracion de variable y otro conjunto de delcaraciones locales
     */
    private void local_declarations(){
        // volvemos a verificar si se trata de declaraciones
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual!=null){
            if(tokenActual.obtenerLexema().equals("int") || tokenActual.obtenerLexema().equals("char") || tokenActual.obtenerLexema().equals("void")){
                analizadorLexico.retroceso();
                var_declaration();      // declaracion de una variable
                local_declarations();   // busca mas declaraciones
            }else{
                analizadorLexico.retroceso(); // No era una declaracion, devolvemos la posicion del analizador a donde se encontraba anteriormente
            }
        }else{
            analizadorLexico.retroceso();
        }
    }
    
    /**
     * Una declaracion de variable puede definirse por un identificador de tipo, un identificador y un punto y como
     * DEBE SOPORTAR ARREGLOS PERO AUN NO SE HA IMPLEMENTADO, RECORDAR HACERLO DESPUES
     */
    private void var_declaration(){
        
        type_specifier();   // se espera un especificador de tipo
        identifier();       // se espera un identificador
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals(";")){
            dataSimb[2] = "";
            if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(dataSimb[1], CURRENT_SCOPE)<0){
                ASM_DECLARACION(dataSimb[1]+"_"+Integer.toString(CURRENT_SCOPE));
                Simbolo nuevaVariable = new Simbolo(dataSimb[0], dataSimb[1], dataSimb[0]);
                analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(nuevaVariable, CURRENT_SCOPE);
            }else{
                error(17,dataSimb[1],tokenActual.obtenerLinea());
            }
            local_declarations();   // se buscan mas declaraciones
        }else if(tokenActual.obtenerLexema().equals("[")){
            num();                                      // debemos validar que despues del corchete se encuente un numero
            closebrace();                               // luego que se cierre el corchete abierto
            semicolon();
            dataSimb[2] = "";
            if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(dataSimb[1], CURRENT_SCOPE)<0){
                Simbolo nuevaVariable = new Simbolo(dataSimb[0], dataSimb[1], dataSimb[0]);
                analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(nuevaVariable, CURRENT_SCOPE);
            }else{
                error(17,dataSimb[1],tokenActual.obtenerLinea());
            }
            local_declarations();
        }else{
            // falta el ";" de final de la declaracion
            error(5,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
        }
    }
    /**
     * Lista de sentencias. Una lista de sentencias sera posible encontrala cuando el siguiente token sea cualquier cosa menos
     * un especificador de tipo o bien una llave de cierre
     */
    private void statement_list(){
        tokenActual = analizadorLexico.consumirToken();
        //salidaErrores("STATEMENT_LIST: " +  tokenActual.obtenerLexema());
        if(tokenActual != null){
            if((!tokenActual.obtenerLexema().equals("}"))){
                analizadorLexico.retroceso(); // verificamos el token que proseguia y por lo tanto retrocedemos a la posicion anterior para analizar
                statement();        // verificamos que era una sentencia, consumimos dicha sentencia 
                statement_list();   // se ha consumido la sentencia encontrada y ahora buscamos mas sentencias
            }else{
                //salidaErrores("SALTO DE STATEMENT_LIST POR: " +  tokenActual.obtenerLexema());
                analizadorLexico.retroceso(); // no era una sentencia, por lo tanto regresamos a la posicion anterior
            }
        }else{
            analizadorLexico.retroceso();
        }
    }
    
    /**
     * Una sentencia puede ser una expresion, una sentencia compuesta (auto recursiva), un condicional, etc. Todo lo que no sea una declaracion de tipo
     * un expresion_stmt puede ser un ";", una asignacion a una variable o una expresion matermatica. Cualquier cosa que no sea un bucle o un return
     */
    private void statement(){
        //salidaErrores("STATEMENT: " +  tokenActual.obtenerLexema());
        // vamos a verificar el token que sigue
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerToken().equals("identifier") || tokenActual.obtenerLexema().equals(";") || tokenActual.obtenerToken().equals("(") || tokenActual.obtenerToken().equals("constant")){
            analizadorLexico.retroceso();
            expresion_stmt();
        }else if(tokenActual.obtenerLexema().equals("void") || tokenActual.obtenerLexema().equals("char") || tokenActual.obtenerLexema().equals("int")){
            analizadorLexico.retroceso();
            compound_stmt();
        }else if(tokenActual.obtenerLexema().equals("if")){
            analizadorLexico.retroceso();
            selection_stmt();
        }else if(tokenActual.obtenerLexema().equals("while")){
            analizadorLexico.retroceso();
            iteration_stmt();
        }else if(tokenActual.obtenerLexema().equals("return")){
            analizadorLexico.retroceso();
            return_stmt();
        }
    }
    
    private void return_stmt(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals("return")){
            tokenActual = analizadorLexico.consumirToken();
            if(tokenActual.obtenerLexema().equals(";")){
                // pasa
            }else{
                simple_expresion();
                tokenActual = analizadorLexico.consumirToken();
                if(!tokenActual.obtenerLexema().equals(";")){
                     error(5, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }
            }
        }
    }
    
    private void selection_stmt(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals("if")){
            tokenActual = analizadorLexico.consumirToken();
            if(tokenActual.obtenerLexema().equals("(")){
                simple_expresion();
                if(!tokenActual.obtenerLexema().equals(")")){
                    error(8, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }
            }else{
                 error(11, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
            }
        }
    }

    private void iteration_stmt(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals("while")){
            tokenActual = analizadorLexico.consumirToken();
            if(tokenActual.obtenerLexema().equals("(")){
                simple_expresion();
                if(!tokenActual.obtenerLexema().equals(")")){
                    error(8, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }
            }else{
                 error(11, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
            }
        }
    }
    
    private void expresion_stmt(){
        tokenActual = analizadorLexico.consumirToken();
        //salidaErrores("EXPRESION_STMT: " +  tokenActual.obtenerLexema());
        if(tokenActual.obtenerLexema().equals(";")){
            // pasa
        }else{
            analizadorLexico.retroceso();
            expresion();
            tokenActual = analizadorLexico.consumirToken();
            if(!tokenActual.obtenerLexema().equals(";")){
                error(5, tokenActual.obtenerLexema(),tokenActual.obtenerLinea()); 
            }
        }
    }
    
    private void expresion(){
        tokenActual = analizadorLexico.consumirToken();
        String tipo1 = "";
        String id = tokenActual.obtenerLexema();
        //salidaErrores("EXPRESION: " +  tokenActual.obtenerLexema());
        if(tokenActual.obtenerToken().equals("identifier")){
            tokenActual = analizadorLexico.consumirToken();
            if(tokenActual.obtenerLexema().equals("[")){
                if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(id, CURRENT_SCOPE)<0){
                    error(18,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }else{
                    tipo1 = analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(tokenActual.obtenerLexema(), CURRENT_SCOPE).obtenerTipo();
                }
                tokenActual = analizadorLexico.consumirToken();
                simple_expresion();
                tokenActual = analizadorLexico.consumirToken();
                if(!tokenActual.obtenerLexema().equals("]")){
                    error(7, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }else{
                    tokenActual = analizadorLexico.consumirToken();
                    if(tokenActual.obtenerLexema().equals("=")){
                        tokenActual = analizadorLexico.consumirToken();
                        simple_expresion();
                        String tipo2 = analizadorSemantico.ultipoTipo;
                        if(analizadorSemantico.operar(tipo1, "=", tipo2)==null){
                            String info = "No se puede convertir el tipo "+ tipo2 +" a "+tipo1;
                            error(22, info,tokenActual.obtenerLinea());
                        }
                        
                    }else{
                        error(14, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                    }
                }
            }else if(tokenActual.obtenerLexema().equals("=")){
                if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(id, CURRENT_SCOPE)<0){
                    error(18,tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }else{
                    tipo1 = analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(id, CURRENT_SCOPE).obtenerTipo();
                }
                tokenActual = analizadorLexico.consumirToken();
                simple_expresion();
                ASM_ASIGNACION_VARIABLE(id+"_"+Integer.toString(CURRENT_SCOPE));
                String tipo2 = analizadorSemantico.ultipoTipo;
                if(analizadorSemantico.operar(tipo1, "=", tipo2)==null){
                      String info = "No se puede convertir el tipo "+ tipo2 +" a "+tipo1;
                        error(22, info,tokenActual.obtenerLinea());
                }
            }else if(tokenActual.obtenerLexema().equals("(")){
                int cant_args = args(id);
                if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(id, GLOBAL_SCOPE)<0){
                    error(19,id,tokenActual.obtenerLinea());
                }else{
                    if(id.equals("iprintf")||id.equals("cprintf")){
                        ASM_IMPRIMIR_AX();
                    }
                    analizadorSemantico.ultipoTipo = analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(id, GLOBAL_SCOPE).obtenerTipo();
                    int numParams = Integer.parseInt(analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(id, GLOBAL_SCOPE).obtenerValor("numparams"));
                    if(numParams!=cant_args){
                        error(21,Integer.toString(numParams),tokenActual.obtenerLinea());
                    }
                    
                }
                if(!tokenActual.obtenerLexema().equals(")")){
                    error(8, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }
            }else{
                error(14, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
            }
        }else{
            simple_expresion();
        }
    }
    
    private void simple_expresion(){
        //salidaErrores("SIMPLE_EXPRESION: " +  tokenActual.obtenerLexema());
        //salidaErrores("ADITIVE_EXPRESION: " +  tokenActual.obtenerLexema());
         tokenActual = analizadorLexico.consumirToken();
         if(tokenActual.obtenerToken().equals("equal-operator")||tokenActual.obtenerToken().equals("relative-operator")){
             analizadorLexico.retroceso();
             analizadorLexico.retroceso();
             tokenActual = analizadorLexico.consumirToken();
             aditive_expresion();
             tokenActual = analizadorLexico.consumirToken();
             tokenActual = analizadorLexico.consumirToken();
             aditive_expresion();
         }else{
             analizadorLexico.retroceso();
             analizadorLexico.retroceso();
             tokenActual = analizadorLexico.consumirToken();
             //salidaErrores("RE: " +  tokenActual.obtenerLexema());
             aditive_expresion();
         }
    }
    
    private void aditive_expresion(){
         //salidaErrores("ADITIVE_EXPRESION: " +  tokenActual.obtenerLexema());
         tokenActual = analizadorLexico.consumirToken();
         if(tokenActual.obtenerToken().equals("add-operator")){
             analizadorLexico.retroceso();
             analizadorLexico.retroceso();
             tokenActual = analizadorLexico.consumirToken();
             term();
             String tipo1 = analizadorSemantico.ultipoTipo;
             tokenActual = analizadorLexico.consumirToken();
             String operador = tokenActual.obtenerLexema();
             ASM_MOV_BX("ax");
             tokenActual = analizadorLexico.consumirToken();
             aditive_expresion();
             if(operador.equals("+"))
                 ASM_SUMAR_ACUM("bx");
             if(operador.equals("-")){
                 ASM_RESTAR_ACUM("bx");
             }
             String tipo2 = analizadorSemantico.ultipoTipo;
             if(analizadorSemantico.operar(tipo1, operador, tipo2)==null){
                 String info = "No se puede convertir el tipo "+ tipo2 +" a "+tipo1;
                 error(22, info,tokenActual.obtenerLinea());
             }
         }else{
             analizadorLexico.retroceso();
             analizadorLexico.retroceso();
             tokenActual = analizadorLexico.consumirToken();
             //salidaErrores("RE: " +  tokenActual.obtenerLexema());
             term();
         }
    }
    
    private void term(){
        //salidaErrores("TERM: " +  tokenActual.obtenerLexema());
        tokenActual = analizadorLexico.consumirToken();
         if(tokenActual.obtenerToken().equals("mul-operator")){
             analizadorLexico.retroceso();
             analizadorLexico.retroceso();
             tokenActual = analizadorLexico.consumirToken();
             factor();
             String tipo1 = analizadorSemantico.ultipoTipo;
             tokenActual = analizadorLexico.consumirToken();
             String operador = tokenActual.obtenerLexema();
             ASM_MOV_BX("ax");
             tokenActual = analizadorLexico.consumirToken();
             term();
             if(operador.equals("*"))
                 ASM_MULTIPLICAR_ACUM("bx");
             if(operador.equals("/"))
                 ASM_DIVIDIR_ACUM("bx");
             String tipo2 = analizadorSemantico.ultipoTipo;
             if(analizadorSemantico.operar(tipo1, operador, tipo2)==null){
                 String info = "No se puede convertir el tipo "+ tipo2 +" a "+tipo1;
                 error(22, info,tokenActual.obtenerLinea());
             }
         }else{
             analizadorLexico.retroceso();
             analizadorLexico.retroceso();
             tokenActual = analizadorLexico.consumirToken();
             //salidaErrores("RE: " +  tokenActual.obtenerLexema());
             factor();
         }
    }
    
    private void factor(){
        // (expresion)
        //tokenActual = analizadorLexico.consumirToken();
        //salidaErrores("FACTOR: " +  tokenActual.obtenerLexema());
        if(tokenActual.obtenerLexema().equals("(")){
            tokenActual = analizadorLexico.consumirToken();
            simple_expresion();
            tokenActual = analizadorLexico.consumirToken();
            if(!tokenActual.obtenerLexema().equals(")")){
                error(8, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
            }
            
        }else if(tokenActual.obtenerToken().equals("identifier")){
            String id = tokenActual.obtenerLexema();
            tokenActual = analizadorLexico.consumirToken();
            if(tokenActual.obtenerLexema().equals("[")){
                if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(id, CURRENT_SCOPE)<0){
                    error(18,id,tokenActual.obtenerLinea());
                }else{
                    analizadorSemantico.ultipoTipo = analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(id, CURRENT_SCOPE).obtenerTipo();
                }
                tokenActual = analizadorLexico.consumirToken();
                simple_expresion();
                tokenActual = analizadorLexico.consumirToken();
                if(!tokenActual.obtenerLexema().equals("]")){
                    error(7, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }
            }else if(tokenActual.obtenerLexema().equals("(")){
                int cant_args = args(id);
                if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(id, GLOBAL_SCOPE)<0){
                    error(19,id,tokenActual.obtenerLinea());
                }else{
                    analizadorSemantico.ultipoTipo = analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(id, GLOBAL_SCOPE).obtenerTipo();
                    int numParams;
                    if(analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(id, GLOBAL_SCOPE).existePropiedad("numparams")){
                        numParams= Integer.parseInt(analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(id, GLOBAL_SCOPE).obtenerValor("numparams"));
                    }else{
                        numParams = 0;
                    }
                    if(numParams!=cant_args){
                        error(21,Integer.toString(numParams),tokenActual.obtenerLinea());
                    }
                    
                }
                if(!tokenActual.obtenerLexema().equals(")")){
                    error(8, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
                }
            }else{
                if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(id, CURRENT_SCOPE)<0){
                    error(18,id,tokenActual.obtenerLinea());
                }else{
                    ASM_MOV_AX(id+"_"+Integer.toString(CURRENT_SCOPE));
                    analizadorSemantico.ultipoTipo = analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(id, CURRENT_SCOPE).obtenerTipo();
                }
                analizadorLexico.retroceso();
            } 
        }else if(tokenActual.obtenerToken().equals("constant")){
            analizadorSemantico.ultipoTipo = "int";
            ASM_MOV_AX(tokenActual.obtenerLexema());
        }else if(tokenActual.obtenerToken().equals("constant-char")){
            ASM_MOV_AX(tokenActual.obtenerLexema().replace("'",""));
            analizadorSemantico.ultipoTipo = "char";
        }else if(tokenActual.obtenerLexema().equals(")")){
            
        }else{
            error(15, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
        }
    }
    
    private int args(String funcdef){
        return arg_list(funcdef);
    }
    
    private int arg_list(String funcdef){
        tokenActual = analizadorLexico.consumirToken();
        Simbolo simb = analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(funcdef, GLOBAL_SCOPE);
        int numParams;
        if(simb!=null){
            if(simb.existePropiedad("numparams")){
                numParams = Integer.parseInt(simb.obtenerValor("numparams"));
            }else{
                numParams = 0;
            }
        }else{
            numParams = 0;
        }
        if(!tokenActual.obtenerLexema().equals(")")){
            if(tokenActual.obtenerLexema().equals(",")){
                tokenActual = analizadorLexico.consumirToken();
                simple_expresion();
                String tipo = analizadorSemantico.ultipoTipo;
                int count = 1 + arg_list(funcdef); 
                if((count-1) < (numParams)){
                    if(!simb.paramsTypes[(numParams - count)].equals(tipo)){
                        String info = "Parametro "+ (numParams - count + 1) +" se esperaba de tipo "+simb.paramsTypes[count-1];
                        error(23, info,tokenActual.obtenerLinea());
                    }
                    //System.out.println(count);
                }
                return count;
            }else{
                analizadorLexico.retroceso();
                tokenActual = analizadorLexico.consumirToken();
                simple_expresion();
                String tipo = analizadorSemantico.ultipoTipo;
                int count = 1 + arg_list(funcdef);
                if((count-1)<numParams){
                    if(!simb.paramsTypes[(numParams - count)].equals(tipo)){
                        String info = "Parametro "+ (numParams - count + 1) +" se esperaba de tipo "+simb.paramsTypes[count-1];
                        error(23, info,tokenActual.obtenerLinea());
                    }
                    //System.out.println(count);
                }
                //System.out.println(count);
                return count;
            }
        }
        
        return 0;
    }
    
    private void param_list(String funcdef){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerLexema().equals(",")){
            param(funcdef);
            dataSimb[2] = "";
            if(analizadorSemantico.tablaDeSimbolos.existeSimbolo(dataSimb[1], COUNTER_SCOPE + 1)<0){
                 Simbolo nuevaVariable = new Simbolo(dataSimb[0], dataSimb[1], dataSimb[0]);
                 analizadorSemantico.tablaDeSimbolos.nuevoSimbolo(nuevaVariable, COUNTER_SCOPE + 1);
             }else{
                  error(17,dataSimb[1],tokenActual.obtenerLinea());
             }
            param_list(funcdef);
        }else{
            analizadorLexico.retroceso();
        }
    }
    
    private void param(String funcdef){
        type_specifier();
        identifier();
        Simbolo simb;
        if((simb = analizadorSemantico.tablaDeSimbolos.obtenerSimbolo(funcdef, 0))!= null){
            if(!simb.existePropiedad("numparams")){
                simb.nuevaPropiedad("numparams", "1");
                simb.paramsTypes[0] = dataSimb[0];
                //System.out.println("Parametro[0]="+dataSimb[0]);
            }else{
                int numparams = Integer.parseInt(simb.obtenerValor("numparams"));
                simb.paramsTypes[numparams] = dataSimb[0];
                //System.out.println("Parametro["+numparams+"]="+dataSimb[0]);
                numparams++;
                simb.nuevaPropiedad("numparams", Integer.toString(numparams));
            }
        }
    }
    
    private void type_specifier(){
         tokenActual = analizadorLexico.consumirToken();
         if(tokenActual.obtenerLexema().equals("int") || tokenActual.obtenerLexema().equals("void") || tokenActual.obtenerLexema().equals("char")){
             dataSimb[0] = tokenActual.obtenerLexema();
         }else{
             error(3, tokenActual.obtenerLexema(),tokenActual.obtenerLinea());
         }
    }
    
    private void identifier(){
        tokenActual = analizadorLexico.consumirToken();
        if(tokenActual.obtenerToken()=="identifier"){
             dataSimb[1] = tokenActual.obtenerLexema();
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
        errores++;
        switch(errorcode){
            case 1:
                salidaErrores("lexico linea["+linea+"]: no se reconoce la secuencia \""+info+"\"");
                break;
            case 2:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba una declaracion cerca de \""+info+"\"");
                break;
            case 3:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba una especificacion de tipo cerca de \""+info+"\"");
                break;
            case 4:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba un identificador cerca de \""+info+"\""); 
                break;
            case 5:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba \";\" cerca de \""+info+"\"");
                break;
            case 6:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba una constante cerca de \""+info+"\"");
                break;
            case 7:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba \"]\" cerca de \""+info+"\"");
                break;
            case 8:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba \")\" cerca de \""+info+"\"");
                break;
            case 9:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba \"{\" cerca de \""+info+"\"");
                break;
            case 10:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba \"}\" cerca de \""+info+"\"");
                break;
            case 11:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba \"(\" cerca de \""+info+"\"");
                break;
            case 12:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba un operador (*,/) cerca de \""+info+"\"");
                break;
            case 13:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba un operador (+,-) cerca de \""+info+"\"");
                break;
            case 14:
                salidaErrores("Error sintáctico linea["+linea+"]: se esperaba un \"=\" cerca de \""+info+"\"");
                break; 
            case 15:
                salidaErrores("Error sintáctico linea["+linea+"]: Falta un operando cerca de \""+info+"\"");
                break;
            case 16:
                salidaErrores("Error sintáctico linea["+linea+"]: Se esperaba main cerca de \""+info+"\"");
                break;
            case 17:
                salidaErrores("Error semántico linea["+linea+"]: Declaracion multiple de la variable \""+info+"\", ya ha sido declarada en este ámbito");
                break;
            case 18:
                salidaErrores("Error semántico linea["+linea+"]: La variable \""+info+"\" no ha sido declarada en este ámbito");
                break;
            case 19:
                salidaErrores("Error semántico linea["+linea+"]: La funcion \""+info+"\" no ha sido declarada");
                break;
            case 20:
                salidaErrores("Error semántico linea["+linea+"]: La funcion \""+info+"\" ya fue declarada");
                break; 
            case 21:
                salidaErrores("Error semántico linea["+linea+"]: La funcion esperaba recibir \""+info+"\" parametros");
                break;
            case 22:
                salidaErrores("Error semántico linea["+linea+"]: Tipos incompatibles, \""+info+"\"");
                break;
            case 23:
                salidaErrores("Error semántico linea["+linea+"]: Tipos incompatibles, \""+info+"\"");
                break;      
        }
       //System.exit(errorcode);
    }
}
