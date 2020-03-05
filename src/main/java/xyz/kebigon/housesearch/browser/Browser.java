package xyz.kebigon.housesearch.browser;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Browser implements Closeable
{
    private final WebDriver driver;

    protected Browser()
    {
        driver = new HtmlUnitDriver();
    }

    @Override
    public void close() throws IOException
    {
        driver.quit();
    }

    protected void navigateTo(String url)
    {
        log.info("Navigate to {}", url);
        driver.navigate().to(url);
    }

    protected List<WebElement> findElements(String xpathExpression)
    {
        return driver.findElements(By.xpath(xpathExpression));
    }

    protected boolean click(String xpathExpression)
    {
        try
        {
            driver.findElement(By.xpath(xpathExpression)).click();
            return true;
        }
        catch (final NoSuchElementException e)
        {
            return false;
        }
    }
}
