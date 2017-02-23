/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagedownload;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

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
        win.setSize(new Dimension(450, 400));
//        System.out.println("Screen: "+screenHeight+"*"+screenWidth);
//        System.out.println("Browser: "+win.getSize().getHeight()+"*"+win.getSize().getWidth());
        win.setPosition(new Point(screenWidth-win.getSize().getWidth(), screenHeight-win.getSize().getHeight()));
        
        String firstPage = config.getProperty("website");
        driver.get(firstPage);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); 
        
        boolean minSizeFilter= Boolean.parseBoolean(config.getProperty("filter.minSize"));
        int minHeight= Integer.parseInt(config.getProperty("filter.minSize.height"));
        int minWidth= Integer.parseInt(config.getProperty("filter.minSize.width"));
        
        List<WebElement> imgList= driver.findElements(By.tagName("img"));
        List<WebElement> filterList= new ArrayList<WebElement>();
        for(WebElement img: imgList){
            System.out.println("Image location: "+img.getAttribute("src"));
            System.out.println("Image size: "+img.getSize().getHeight()+"*"+img.getSize().getWidth());
            if(minSizeFilter){
                if(img.getSize().getHeight()>=minHeight && img.getSize().getWidth()>=minWidth){
                    filterList.add(img);
                }
            }
        }
        
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        System.out.println("<end of filter>");
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        
        int cnt=0;
        InputStream imgIn= null;
        OutputStream imgOut = null;
        for(WebElement img: filterList){
            cnt++;
            try {
                System.out.println("Image location: "+img.getAttribute("src"));
                System.out.println("Image size: "+img.getSize().getHeight()+"*"+img.getSize().getWidth());
                
                String location= img.getAttribute("src").trim();
                driver.get(location);
                WebElement image =driver.findElement(By.tagName("img"));
                
//                System.out.println("start robot");
//                //Rihgt click on Image using contextClick() method.
//                Actions action= new Actions(driver);
//                action.contextClick(img).build().perform();
//                action.sendKeys(Keys.CONTROL, "v").build().perform();
//                
//                Robot robot = new Robot();
//                robot.keyPress(KeyEvent.VK_D);
//                robot.keyPress(KeyEvent.VK_SHIFT);
//                robot.keyPress(KeyEvent.VK_SEMICOLON);
//                robot.keyRelease(KeyEvent.VK_SHIFT);
//                robot.keyPress(KeyEvent.VK_BACK_SLASH);
//                robot.keyPress(KeyEvent.VK_I);
//                robot.keyPress(KeyEvent.VK_M);
//                robot.keyPress(KeyEvent.VK_A);
//                robot.keyPress(KeyEvent.VK_G);
//                robot.keyPress(KeyEvent.VK_E);
//                robot.keyPress(KeyEvent.VK_PERIOD);
//                robot.keyPress(KeyEvent.VK_I);
//                robot.keyPress(KeyEvent.VK_M);
//                robot.keyPress(KeyEvent.VK_G);
//                // To press Save button.
//                robot.keyPress(KeyEvent.VK_ENTER);  
//                System.out.println("end robot");
                
                String[] urlArr = location.split("\\/");
                System.out.println("File name: "+urlArr[urlArr.length-1]);
                String[] nameArr= urlArr[urlArr.length-1].split("\\.");
                System.out.println("File ext: "+nameArr[nameArr.length-1]);
                String ext= nameArr[nameArr.length-1];
                String filePath= config.getProperty("output.folder")+config.getProperty("output.namePrefix")+cnt+"."+ext;
                
                URL url = new URL(location);
                imgIn = new BufferedInputStream(url.openStream());
                imgOut = new BufferedOutputStream(new FileOutputStream(filePath));
                for (int i; (i = imgIn.read()) != -1;) {
                    imgOut.write(i);
                }
                imgIn.close();  
                imgOut.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                ex.printStackTrace();
                Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (AWTException ex) {
//                Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
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
        
//        try {
//            Thread.sleep(Long.valueOf(3000));
//        } catch (InterruptedException ex) {
//            Logger.getLogger(ImageDownload.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        driver.quit();
    }
    
}
