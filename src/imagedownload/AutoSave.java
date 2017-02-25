/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagedownload;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 *
 * @author wytsang
 */
public class AutoSave {
    
    public static void save(WebDriver driver, WebElement image, String filePath){
        filePath= filePath.toLowerCase();
        
        try{
            // Rihgt click on Image using contextClick() method.
            Actions action= new Actions(driver);
            action.contextClick(image).build().perform();
            Thread.sleep(500);
                
            Robot robot= new Robot();
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            Thread.sleep(500);
            
            char[] seq= filePath.toCharArray();
            for (int i=0; i<seq.length; i++){
                Keys k= Keys.lookup(seq[i]);
                int[] keys= k.getKeys();
//                System.out.println("Key identified: "+k.getLabel()+", "+keys);
                for(int j=0; j<keys.length; j++){
                    robot.keyPress(keys[j]);
                }
                for(int j=keys.length-1; j>=0; j--){
                    robot.keyRelease(keys[j]);
                }
            }
            
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER); 
            boolean saveComplete= false;
            while(!saveComplete){
                File f= new File(filePath);
                saveComplete= f.exists();
                System.out.println("Save complete: "+saveComplete);
                Thread.sleep(100);
            }
        } catch (AWTException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AutoSave.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}
