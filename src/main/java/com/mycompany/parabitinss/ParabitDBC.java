/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.parabitinss;

import java.sql.*;

public class ParabitDBC {
    Connection con;
    Statement stm;
    ResultSet rs;
    PreparedStatement ps;
    public ParabitDBC(){
        try{ 
            Class.forName("com.mysql.cj.jdbc.Driver");                                                                        //  "jdbc:mysql"  is protocol and // is for server and \\ for system  (protocol//ip Address of database//PortNo//database name)
            con=DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/parabitinss", "root", "");  //(url,user,password abhi blank rakho)
            stm=con.createStatement();     //ip address par 2 port hote hai ek request and response port aur response=request+1
        }catch(Exception e){           
            System.out.print(e);
        }
    }
    
    public static void main(String s){
        ParabitDBC ob=new  ParabitDBC();
    }
    
}
