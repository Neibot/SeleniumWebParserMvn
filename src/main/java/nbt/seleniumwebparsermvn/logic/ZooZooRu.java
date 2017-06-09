/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nbt.seleniumwebparsermvn.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import static nbt.seleniumwebparsermvn.logic.Utilities.*;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Neibot
 */
public class ZooZooRu implements Site {

    private WebDriver driver = null;
    private final String siteURL = "http://www.zoo-zoo.ru";
    private ZooZooRuDBConnect dbCon = new ZooZooRuDBConnect();
    
    public ZooZooRu()
    {   
        Logger.getLogger("").setLevel(Level.SEVERE); // убираем предупреждения из вывода                 
    }
        
    @Override
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public WebDriver getDriver() {
        return driver;
    }       

    @Override
    public List<Map> parseData(String category, int pages) {
        List <Map> results = new ArrayList<>();
        if(checkAvailability(siteURL+"/"+category))
        {            
            List<String> urls = getURLsOfCategory(category, pages);
            for (int i=0; i<urls.size(); i++) 
            {
                results.add(extractInfoFromURL(urls.get(i)));
                System.out.println("Обработано объявление "+(i+1)+" из "+urls.size());
            }
        }
        else
            System.out.println("Не могу открыть категорию "+category+"!\nПроверьте подключение и имя категории!");
        return results;
    }
    
    @Override
    public void uploadDataToDatabase(List<Map> data) {                
        dbCon.clearDatabase();
        dbCon.uploadData(data);
    }
    
    @Override
    public TableModel generateTableModel() { 
        return new DefaultTableModel(dbCon.getAllData("Results"), dbCon.getColumnNames("Results"));
    }
    
    // метод возвращает ссылки на объявления с нужного количества страниц категории
    private List<String> getURLsOfCategory(String category, int pages) { 
        ArrayList <String> results = new ArrayList<>();
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode html;
        int pageCount = 1;
        boolean searchEnd = false;
        while (!searchEnd && (pages == -1 || pageCount <= pages))
        {
            driver.get(siteURL+"/"+category+"/page"+pageCount+".html");                        
            html = cleaner.clean(driver.getPageSource());
            //debug("ZooZooRu",driver.getPageSource(), html, pageCount);
            Object [] elements = null;
            
            try {
                elements = html.evaluateXPath("/body/div[3]/div[1]/div[1]//div[@class='media-wrapper']/div/div[2]/div/h2/a/@href");
                for (Object element:elements)
                    results.add(siteURL+String.valueOf(element));
            } catch (XPatherException ex) {
                Logger.getLogger(ZooZooRu.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try { // по состоянию кнопки перехода на следующую страницу определяем окончание цикла
                elements = html.evaluateXPath("/body/div[3]/div[1]/div[1]/div[last()]/ul/li[12]/@class");
                if (elements != null && elements.length > 0)
                    if (elements[0].equals("disabled"))
                        searchEnd = true;
            } catch (XPatherException ex) {
                Logger.getLogger(ZooZooRu.class.getName()).log(Level.SEVERE, null, ex);
            }            
            
            System.out.println("Обработана страница "+pageCount);
            pageCount++;
        }
        return results;
    }

    // метод принимает ссылку и возвращает коллекцию с данными
    private HashMap extractInfoFromURL(String url) {
        HashMap<String,String> results = new HashMap<>();        
        if(checkAvailability(url))
        {
            Object[] elements = null;
            HtmlCleaner cleaner = new HtmlCleaner();
            driver.get(url);            
            
            TagNode html = cleaner.clean(driver.getPageSource());
            //debug("ZooZooRu", driver.getPageSource(), html, 1);
            String xPath = "/body/div[3]/div[1]/div[1]/div[4]/div[1]/span/a[1]/text()";
            addElementToResults(elements, html, results, xPath, "Категория");
            xPath = "/body/div[3]/div[1]/div[1]/div[4]/div[1]/span/a[2]/text()";
            addElementToResults(elements, html, results, xPath, "Тип");
            xPath = "/body/div[3]/div[1]/div[1]/div[2]/div[1]/span[2]/text()";
            addElementToResults(elements, html, results, xPath, "Дата");
            xPath = "/body/div[3]/div[1]/div[1]/h1/text()";
            addElementToResults(elements, html, results, xPath, "Заголовок");
            xPath = "/body/div[3]/div[1]/div[1]/div[4]/div[2]/div[2]/span[3]/a/text()";
            addElementToResults(elements, html, results, xPath, "Порода");
            xPath = "/body/div[3]/div[1]/div[1]/div[4]/div[2]/div[1]/span[3]/text()";
            addElementToResults(elements, html, results, xPath, "Город");
            xPath = "/body/div[3]/div[1]/div[1]/div[4]/div[3]/blockquote/footer/text()";
            addElementToResults(elements, html, results, xPath, "Комментарий");
            xPath = "/body/div[3]/div[1]/div[2]/div[1]/div[3]/text()";
            addElementToResults(elements, html, results, xPath, "Хозяин");
            
            String searchString = "$('#phone-result').html('<div><span class=\"glyphicon glyphicon-phone-alt\"></span>";
            String htmlCode = driver.getPageSource();
            int indexFrom = htmlCode.indexOf(searchString)+searchString.length();
            int indexTo = htmlCode.indexOf("</div>');", indexFrom);
            String phoneNumber = "";
            if (indexFrom > 0 && indexTo > 0)    
                phoneNumber = cutString(htmlCode.substring(indexFrom,indexTo).trim(), 100);
            results.put("Телефон", phoneNumber);
            
            results.put("Ссылка", cutString(url, 1000));
        }
        else            
            System.out.println("Не могу открыть ссылку:\n"+url);
        
        return results;
    }
    
    // метод добавляет поля с данными в коллекцию с результатами
    private void addElementToResults(Object[] elements, TagNode html, HashMap<String,String> results, String xPath, String fieldName)
    {
        try {
        elements = html.evaluateXPath(xPath);
        if (elements != null && elements.length > 0)
            switch (fieldName)
            {
                case "Дата":
                    String date = cutString(String.valueOf(elements[0]).trim().split(" ")[2],20);                    
                    results.put(fieldName, date);
                    break;
                case "Комментарий":
                    String comment = cutString(String.valueOf(elements[0]).trim().replaceAll("\\s+", " "),1000);
                    results.put(fieldName, comment);
                    break;
                case "Хозяин":
                    String name = cutString(String.valueOf(elements[0]).trim().split("\n")[0].trim(), 100);
                    results.put(fieldName, name);
                    break;
                default: 
                    String string = cutString(String.valueOf(elements[0]).trim(), 100);
                    results.put(fieldName, string);
                    break;
            }
        else
            results.put(fieldName, "");
        } catch (XPatherException ex) {
                Logger.getLogger(ZooZooRu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
