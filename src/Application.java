import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import vn.pipeline.VnCoreNLP;
import vn.pipeline.Annotation;
import vn.pipeline.Word;

public class Application {
    public static void main(String args[]) throws IOException {
        // Config VNCoreNLP
        String[] annotators = {"wseg"};
        VnCoreNLP pipeline = new VnCoreNLP(annotators);

        // Config Selenium
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        // Get data from article
        driver.navigate().to("https://www.tdtu.edu.vn/tin-tuc/2022-01/doan-thanh-nien-chuc-ra-quan-chien-dich-xuan-tinh-nguyen-2022");
        String data = driver.findElement(By.id("main")).getText();

        // Word segmentation
        Annotation annotation = new Annotation(data);
        pipeline.annotate(annotation);

        // Access translation website
        driver.navigate().to("https://hvdic.thivien.net/hv");

        Hashtable<String, String> dic = new Hashtable<String, String>();

        // Segmentation words array
        for (Word w: annotation.getWords()) {
            // w.getForm() = "Cơ_quan"
            // word2 = "Cơ quan"
            String word = w.getForm().replace("_"," ");
                System.out.print(word);

            driver.findElement(By.className("main-input")).sendKeys(word);   // Input
            driver.findElement(By.className("submit-btn")).click();             // Click button

            // Handle result of translation
            try {
                String translatedWord = driver.findElement(By.xpath("(//div[@class='hvres-word han'])[2]")).getText();
                dic.put(word, translatedWord);
            } catch(Exception e) {
                try {
                    String translatedWord = driver.findElement(By.xpath("(//div[@class='hvres-word han'])")).getText();
                    dic.put(word, translatedWord);
                } catch(Exception ex) {}
            }
            driver.findElement(By.className("main-input")).clear();
        }

        driver.quit();

        System.out.println("\n" + dic);

        // Replace dictionary on article
        Enumeration<String> e = dic.keys();
        while (e.hasMoreElements()) {

            // Getting the key of a particular entry
            String key = e.nextElement();

            data = data.replace(key,dic.get(key));
        }

        System.out.println("\n" + data);
    }
}
