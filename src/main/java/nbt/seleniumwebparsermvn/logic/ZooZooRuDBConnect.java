/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nbt.seleniumwebparsermvn.logic;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Neibot
 */
public class ZooZooRuDBConnect extends DBConnect {
    
    public ZooZooRuDBConnect() { // для тестового примера данные для подключения захардкожены
        super("jdbc:postgresql://localhost:5432/SiteParsingDemoBase","postgres","123456");
    }
    
    void uploadData(List<Map> results) //выгрузка коллекции с результатами в БД
    {         
        String query;
        for (Map result:results)                                                
        {            
            query = 
            "INSERT INTO public.\"Results\""+            
            "(category, type, date, heading, breed, city, comment, owner, phone, url) VALUES "+
            "("+
                "'"+result.get("Категория")+"', "+
                "'"+result.get("Тип")+"', "+
                "'"+result.get("Дата")+"', "+
                "'"+result.get("Заголовок")+"', "+
                "'"+result.get("Порода")+"', "+
                "'"+result.get("Город")+"', "+
                "'"+result.get("Комментарий")+"', "+
                "'"+result.get("Хозяин")+"', "+
                "'"+result.get("Телефон")+"', "+   
                "'"+result.get("Ссылка")+"' "+     
            ")";
            super.exec(query);
        }
    } 
    
    void clearDatabase() // очистка БД
    {
        String query = "DELETE FROM public.\"Results\"; "+
                       "ALTER SEQUENCE auto_id RESTART WITH 1; ";
        super.exec(query);
    }
    
}
