/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador2014;
import compilador2014.lexico.Lexico;
/**
 *
 * @author Jorge Luis
 */
public class Compilador2014 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Lexico analizadorLexico = new Lexico("C:\\Users\\william\\Documents\\cminuscompiler\\prueba.txt");
        analizadorLexico.generarTokens();
    }
}
