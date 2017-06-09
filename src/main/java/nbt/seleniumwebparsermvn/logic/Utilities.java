/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nbt.seleniumwebparsermvn.logic;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

/**
 *
 * @author Neibot
 */
public class Utilities {
    
    public static boolean checkAvailability (String url) //проверка ссылки на работоспособность
    {
        boolean result = false;
        try (WebClient webClient = new WebClient()) 
        {            
            int code = webClient.getPage(url).getWebResponse().getStatusCode();
            if (code == 200)
                result = true;
        } catch (IOException | FailingHttpStatusCodeException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    public static void resultsToConsole(List<Map> results) // вывод коллекции с результатами в консоль
    {
        Set <String> keySet; 
        System.out.println("\n");
        for (Map result:results)                                                
        {            
            keySet = result.keySet();
            for (String key:keySet)
                System.out.print(key+": "+result.get(key)+"; ");
            System.out.println("\n");
        }
    }
    
    public static String cutString(String string, int limit) // проверка длины строки и обрезка при необходимости
    {
        return string.length() > limit ? string.substring(0,limit) : string;
    }
    
    public static void debug(String siteName, String htmlCode, TagNode html, int i) //дебаг - сохраняет код страницы в файл
    {        
        CleanerProperties props = new CleanerProperties();         
        props.setTranslateSpecialEntities(true);
        props.setTransResCharsToNCR(true);
        props.setOmitComments(true);
        
        try {            
            new PrettyXmlSerializer(props).writeToFile(html, siteName+i+".xml", "utf-8");
        } catch (IOException ex) {
            Logger.getLogger(StartPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            BufferedWriter writeFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(siteName+i+".html"),"utf-8"));
            writeFile.write(htmlCode);    
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(StartPoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StartPoint.class.getName()).log(Level.SEVERE, null, ex);
        }                                
        
        System.out.println("Сохранен код страницы "+i);
    }    

    
   
}
