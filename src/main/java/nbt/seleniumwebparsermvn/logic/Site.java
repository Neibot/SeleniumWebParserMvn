/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nbt.seleniumwebparsermvn.logic;

import java.util.List;
import java.util.Map;
import javax.swing.table.TableModel;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Neibot
 */
public interface Site {
    
    void setDriver(WebDriver driver); //геттер и сеттер для драйвера браузера
    WebDriver getDriver();    
    List <Map> parseData(String category, int pages); // метод, принимающий категорию и количество страниц и выдающий готовую коллекцию с результатами
    void uploadDataToDatabase(List<Map> data); // метод, выгружающий данные в БД
    TableModel generateTableModel (); // метод, генерирующий модель таблицы для формы просмотра
            
}
