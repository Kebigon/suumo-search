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
    private WebDriver driver;

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

    protected void restartBrowser()
    {
        final String url = driver.getCurrentUrl();
        log.info("Restarting browser, navigate to {}", url);

        driver.quit();
        driver = new HtmlUnitDriver();
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
            final WebElement element = driver.findElement(By.xpath(xpathExpression));
            log.info("Click on {}, navigate to {}", element.getText(), element.getAttribute("href"));
            element.click();
            return true;
        }
        catch (final NoSuchElementException e)
        {
            log.warn("Unable to click on '{}'", xpathExpression);
            return false;
        }
    }
}
