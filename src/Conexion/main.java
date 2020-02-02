/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Conexion;
import java.sql.Connection;
/**
 *
 * @author Alex
 */
public class main {
    public static void main(String []args){
        Conectar conn ;
        conn = new Conectar();
        Connection reg= conn.getConnection();
    }
    
}
