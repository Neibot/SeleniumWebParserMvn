/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nbt.seleniumwebparsermvn.logic;

import java.util.List;
import java.util.Map;
import nbt.seleniumwebparsermvn.forms.ZooZooRuResultsViewForm;
import static nbt.seleniumwebparsermvn.logic.Utilities.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 *
 * @author Neibot
 */
public class StartPoint {
    
    public static void main(String[] args) 
    {
        Site site = new ZooZooRu(); // инициализируем конкретную имплементацию парсера для конкретного сайта         
        WebDriver driver = new HtmlUnitDriver(); // и конкретный драйвер браузера.        
        site.setDriver(driver); // передаём экземпляр драйвера парсеру       
        List <Map> results = site.parseData("dogs", 1); // получаем данные по нужной категории, 
                                                        // указывая категорию и количество страниц, начиная с первой;
                                                        // при значении -1 будут обработаны все имеющиеся в категории страницы
        resultsToConsole(results); // выводим данные в консоль для контроля
        site.uploadDataToDatabase(results); // выводим данные в базу          
        ZooZooRuResultsViewForm viewForm = new ZooZooRuResultsViewForm(site); // инициализируем форму для просмотра, передаём туда экземпляр парсера
    }    
}
