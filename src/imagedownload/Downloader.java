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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author wytsang
 */
public class Downloader {
    
    public void download(){
        Properties config= new Properties();
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
        WebDriver driver= new ChromeDriver();
        WebDriver.Window win= driver.manage().window();
        win.maximize();
        int screenHeight= win.getSize().getHeight();
        int screenWidth= win.getSize().getWidth();
        int browserHeight= Integer.parseInt(config.getProperty("browser.height"));
        int browserWidth= Integer.parseInt(config.getProperty("browser.width"));
        win.setSize(new Dimension(browserWidth, browserHeight));
        System.out.println("Screen: "+screenHeight+"*"+screenWidth);
        System.out.println("Browser: "+win.getSize().getHeight()+"*"+win.getSize().getWidth());
//        win.setPosition(new Point(screenWidth, screenHeight));
        
        String firstPage = config.getProperty("website");
        String page = firstPage;
        
        driver.get(page);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); 
        
        boolean minSizeFilter= Boolean.parseBoolean(config.getProperty("filter.minSize"));
        int minHeight= Integer.parseInt(config.getProperty("filter.minSize.height"));
        int minWidth= Integer.parseInt(config.getProperty("filter.minSize.width"));
        
        List<WebElement> imgList= driver.findElements(By.tagName("img"));
        List<String> filteredList= new ArrayList<String>();
        for(WebElement img: imgList){
//            System.out.println("Image location: "+img.getAttribute("src"));
//            System.out.println("Image size: "+img.getSize().getHeight()+"*"+img.getSize().getWidth());
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
                String filePath= config.getProperty("output.folder")+config.getProperty("output.namePrefix")+cnt+"."+ext;
                System.out.println("File location: "+location);
                System.out.println("File name: "+urlArr[urlArr.length-1]);
                System.out.println("File ext: "+nameArr[nameArr.length-1]);
                System.out.println("Output: "+filePath);
                
//                URL url = new URL(location);
//                URLConnection conn= url.openConnection();
//                conn.setRequestProperty("Accept", "image/webp,image/*,*/*;q=0.8");
//                conn.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
//                conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
//                conn.setRequestProperty("Host", "m.iprox.xyz");
//                conn.setRequestProperty("Referer", "http://18h.animezilla.com/manga/527/1");
//                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
//                imgIn = new BufferedInputStream(url.openStream());
//                imgOut = new BufferedOutputStream(new FileOutputStream(filePath));
//                for (int i; (i = imgIn.read()) != -1;) {
//                    imgOut.write(i);
//                }
//                imgIn.close();  
//                imgOut.close();
                
                
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
        driver.quit();
    }
    
}
