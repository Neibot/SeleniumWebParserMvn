/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nbt.seleniumwebparsermvn.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Neibot
 */
public class DBConnect { // базовый класс, реализующий функционал для работы с БД
        
    protected Statement st;
    protected ResultSet rs;
    
    public DBConnect (String con_string, String con_login, String con_pass)
    {   
        try {            
            DriverManager.registerDriver(new org.postgresql.Driver());
            Connection con = DriverManager.getConnection(con_string,con_login,con_pass);
            if (con != null)                              
                st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            System.out.println("Подключение к БД не удалось!\nПроверьте правильность учетных данных!");
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    protected void select (String query) // запросы Select
    {        
        try {        
            rs = st.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Неверный запрос SELECT к БД!\n"+query);            
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
       
    protected void exec (String query) // остальные запросы
    {
        try {        
            st.execute(query);
        } catch (SQLException ex) {
            System.out.println("Неверный запрос к БД!\n"+query);
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    public void close() // отключение соединения
    {
        try {
            st.getConnection().close();            
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Object[] getColumnNames(String tableName) // Получить имена полей таблицы
    {
        String[] results = null;
        try {
            String query = "SELECT * FROM public.\""+tableName+"\" LIMIT 1";
            select(query);
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            results = new String[columnCount];
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++)
                if (rs.getMetaData().getColumnName(columnIndex) != null)
                    results[columnIndex-1] = rs.getMetaData().getColumnName(columnIndex);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return results;
    }
    
    public Object[][] getAllData(String tableName) // Получить содержимое таблицы
    {
        String[][] results = null;
        try {
            String query = "SELECT * FROM public.\""+tableName+"\"";
            select(query);
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();
            results = new String[rowCount][columnCount];
            for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++)
            {
                rs.next();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++)
                    results[rowIndex-1][columnIndex-1] = rs.getString(columnIndex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return results;
    }    
}
