/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagedownload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author wytsang
 */
public class Downloader {
    
    Properties config;
    
    // config browser
    int browserHeight;
    int browserWidth;
    // config pagination
    boolean pagination;
    String next;
    String xpath;
    int pageLimit;
    // config imaage size filter 
    boolean minSizeFilter;
    int minHeight;
    int minWidth;
    // website
    String firstPage;
        
    public Downloader(){
        config= new Properties();
        InputStream in= getClass().getClassLoader().getResourceAsStream("config/config.properties");
        if(in!=null){
            try {
                config.load(in);
            } catch (IOException ex) {
                Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("File not found!");
        }
        System.setProperty("webdriver.chrome.driver", config.getProperty("webdriver.chrome.driver"));
        
        browserHeight= Integer.parseInt(config.getProperty("browser.height"));
        browserWidth= Integer.parseInt(config.getProperty("browser.width"));
        pagination= Boolean.parseBoolean(config.getProperty("pagination"));
        next= config.getProperty("pagination.next");
        xpath= config.getProperty("pagination.xpath");
        pageLimit= Integer.parseInt(config.getProperty("pagination.pageLimit"));
        minSizeFilter= Boolean.parseBoolean(config.getProperty("filter.minSize"));
        minHeight= Integer.parseInt(config.getProperty("filter.minSize.height"));
        minWidth= Integer.parseInt(config.getProperty("filter.minSize.width"));
        firstPage = config.getProperty("website");
    }
    
    public void download(){
        System.out.println((new Date()).toString());
        
        WebDriver driver= new ChromeDriver();
        WebDriver.Window win= driver.manage().window();
        win.maximize();
        int screenHeight= win.getSize().getHeight();
        int screenWidth= win.getSize().getWidth();
        win.setSize(new Dimension(browserWidth, browserHeight));
        System.out.println("Screen: "+screenHeight+"*"+screenWidth);
        System.out.println("Browser: "+win.getSize().getHeight()+"*"+win.getSize().getWidth());
        win.setPosition(new Point(screenWidth-browserWidth, screenHeight-browserHeight));
        
        String page = firstPage;
        boolean hasNextPage= pagination;
        int pageCnt= 0;
        while(hasNextPage && pageCnt<pageLimit){
            pageCnt++;
            driver.get(page);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); 
            
            hasNextPage= false;
            page="";
            List<WebElement> checkList= driver.findElements(By.xpath(xpath));
            for(WebElement elm: checkList){
                String text= elm.getAttribute("innerHTML");
                System.out.println(text);
                if(text.equals(next)){
                    hasNextPage= true;
                    page= elm.getAttribute("href");
                    break;
                }
            }
            System.out.println("Next page: "+hasNextPage+", "+page);
            
            List<WebElement> imgList= driver.findElements(By.tagName("img"));
            List<String> filteredList= new ArrayList<String>();
            for (WebElement img : imgList) {
                if(minSizeFilter){
                    if(img.getSize().getHeight()>=minHeight && img.getSize().getWidth()>=minWidth){
                        filteredList.add(img.getAttribute("src").trim());
                    }
                }
            }

            int cnt=0;
            InputStream imgIn= null;
            OutputStream imgOut = null;
            for(String location: filteredList){
                cnt++;
                try {
                    String[] urlArr = location.split("\\/");
                    String[] nameArr= urlArr[urlArr.length-1].split("\\.");
                    String ext= nameArr[nameArr.length-1];
                    String filePath= config.getProperty("output.folder")+config.getProperty("output.namePrefix")+"_"+pageCnt+"_"+cnt+"."+ext;
                    System.out.println("File location: "+location);
                    System.out.println("File name: "+urlArr[urlArr.length-1]);
                    System.out.println("File ext: "+nameArr[nameArr.length-1]);
                    System.out.println("Output: "+filePath);

                    driver.get(location);
                    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); 
                    WebElement image =driver.findElement(By.tagName("img"));
                    System.out.println("Image element: "+image.getAttribute("src"));

                    AutoSave.save(driver, image, filePath);
                } finally {
                    try {
                        if(imgIn!=null){
                            imgIn.close();
                        }
                        if(imgOut!=null){
                            imgOut.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }  
        }
        
        driver.quit();
        System.out.println((new Date()).toString());
    }
    
}
