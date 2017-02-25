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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    Properties config;
    
    // config browser
    boolean hide;
    int browserHeight;
    int browserWidth;
    // config pagination
    boolean pagination;
    String[] next;
    String xpath;
    int pageLimit;
    // config duplicate image filter
    boolean duplicateFilter;
    // config imaage size filter 
    boolean minSizeFilter;
    int minHeight;
    int minWidth;
    // website
    String firstPage;
    Map<String, String> visitedPages;
    Map<String, String> savedImages;
        
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
        
        hide=Boolean.parseBoolean(config.getProperty("browser.hide"));
        browserHeight= Integer.parseInt(config.getProperty("browser.height"));
        browserWidth= Integer.parseInt(config.getProperty("browser.width"));
        pagination= Boolean.parseBoolean(config.getProperty("pagination"));
        next= config.getProperty("pagination.next").split(",");
        for(String t: next){
            System.out.println("Next: "+t);
        }
        xpath= config.getProperty("pagination.xpath");
        pageLimit= Integer.parseInt(config.getProperty("pagination.pageLimit"));
        duplicateFilter= Boolean.parseBoolean(config.getProperty("filter.duplicate"));
        minSizeFilter= Boolean.parseBoolean(config.getProperty("filter.minSize"));
        minHeight= Integer.parseInt(config.getProperty("filter.minSize.height"));
        minWidth= Integer.parseInt(config.getProperty("filter.minSize.width"));
        firstPage = config.getProperty("website");
        visitedPages= new HashMap<String, String>();
        savedImages= new HashMap<String, String>();
    }
    
    public void download(){
        System.out.println((new Date()).toString());
        
        WebDriver driver= new ChromeDriver();
        WebDriver.Window win= driver.manage().window();
        // get the full screen size (exculde desktop toolbar)
        win.maximize();
        int screenHeight= win.getSize().getHeight();
        int screenWidth= win.getSize().getWidth();
        System.out.println("Screen: "+screenHeight+"*"+screenWidth);
        // set browser size and position it to bottom right
        win.setSize(new Dimension(browserWidth, browserHeight));
        System.out.println("Browser: "+win.getSize().getHeight()+"*"+win.getSize().getWidth());
        win.setPosition(new Point(screenWidth-browserWidth, screenHeight-browserHeight));
        // position the browser outside of windows
        if(hide){
            win.setPosition(new Point(screenWidth, screenHeight));
        }
        
        String page = firstPage;
        boolean hasNextPage= true;
        int pageCnt= 0;
        while(hasNextPage && pageCnt<pageLimit){
            pageCnt++;
            if(visitedPages.get(page)!=null){
                System.out.println("Looped back. Quit download.");
                break;
            }
            visitedPages.put(page, page);
            driver.get(page);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); 
            
            hasNextPage= false;
            if(pagination){
                // if no xpath configured, default search all <a> hyperlinks
                if(xpath==null || xpath.trim().equals("")){
                    xpath= determineNextPage(driver);
                }
                if(xpath!=null && !xpath.trim().equals("")){
                    page="";
                    System.out.println("Xpath: "+xpath);
                    List<WebElement> checkList= driver.findElements(By.xpath(xpath));
                    for(WebElement elm: checkList){
                        String text= elm.getAttribute("innerHTML");
                        for(String t: next){
                            if(text.equals(t)){
                                hasNextPage= true;
                                page= elm.getAttribute("href");
                                break;
                            }
                        }
                    }
                    System.out.println("Next page: "+hasNextPage+", "+page);
                }
            }
            
            List<WebElement> imgList= driver.findElements(By.tagName("img"));
            List<String> filteredList= new ArrayList<String>();
            for (WebElement img : imgList) {
                if(img.getAttribute("src")==null){
                    continue;
                }
                if(duplicateFilter){
                     if(savedImages.get(img.getAttribute("src"))!=null){
                        continue;
                    }
                }
                if(minSizeFilter){
                    if(img.getSize().getHeight()<minHeight || img.getSize().getWidth()<minWidth){
                        continue;
                    }
                }
                filteredList.add(img.getAttribute("src").trim());
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
                    savedImages.put(location, location);
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
    
    private String determineNextPage(WebDriver driver){
        List<WebElement> checkList= driver.findElements(By.xpath("//a"));
        for(WebElement elm: checkList){
            String text= elm.getAttribute("innerHTML");
            System.out.println("<a>: "+text);
            for(String t: next){
                if(text.equalsIgnoreCase(t)){
                    String xpath= getXpath(driver, elm, "");
                    System.out.println("matched, new xpath is: "+xpath);
                    return xpath;
                }
            }
            
        }
        return null;
    }
    
    // sample xpath /html/body/div[@id='page']/div[@id='content']//article//div[@class='wp-pagenavi']/a[@class='nextpostslink']
    // /html/body[@class='single single-manga postid-527 paged-94 single-paged-94 ']/div[@id='page']/div[@id='content']/div[@id='primary']/main[@id='main']/article[@id='post-527']/div[@class='entry-content']/div[@class='wp-pagenavi']/a[@class='nextpostslink']
    private String getXpath(WebDriver driver, WebElement elm, String currentPath){
        System.out.println("Current path: "+currentPath);
        StringBuilder path= new StringBuilder();
        path.append("/"+elm.getTagName());
        path.append(currentPath);
//        if(elm.getAttribute("id")!=null && !elm.getAttribute("id").equals("")){
//            subPath.append("[@id='"+elm.getAttribute("id")+"']");
//        }else if(elm.getAttribute("class")!=null && !elm.getAttribute("class").equals("")){
//            subPath.append("[@class='"+elm.getAttribute("class")+"']");
//        }
        JavascriptExecutor executor= (JavascriptExecutor)driver;
        WebElement parentElement = (WebElement)executor.executeScript("return arguments[0].parentNode;", elm);
        return (elm.getTagName().equalsIgnoreCase("html") || parentElement==null)?path.toString():getXpath(driver, parentElement, path.toString());
    }
    
}
