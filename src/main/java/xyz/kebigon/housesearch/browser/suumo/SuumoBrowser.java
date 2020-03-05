package xyz.kebigon.housesearch.browser.suumo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import xyz.kebigon.housesearch.browser.Browser;
import xyz.kebigon.housesearch.domain.Posting;
import xyz.kebigon.housesearch.domain.SearchConditions;
import xyz.kebigon.housesearch.domain.SearchConditionsValidator;
import xyz.kebigon.housesearch.file.SearchArchive;

public class SuumoBrowser extends Browser
{
    public Collection<Posting> search(SearchConditions conditions, SearchArchive archive)
    {
        navigateTo(SuumoSearchURLBuilder.build(conditions));

        final Collection<Posting> postings = new ArrayList<Posting>();

        do
        {
            postings.addAll(findElements("//div[@class='property_unit-content']").parallelStream() //
                    .map(SuumoBrowser::createPosting)//
                    .filter(archive::filter) //
                    .filter(posting -> SearchConditionsValidator.validateBasicConditions(posting, conditions)) //
                    .collect(Collectors.toList()));
        } while (click("//a[text()='次へ']"));

        return postings;
    }

    private static Posting createPosting(WebElement posting)
    {
        final String url = posting.findElement(By.xpath("./div[@class='property_unit-header']/h2/a")).getAttribute("href");

        final String priceField = getField(posting, "販売価格");
        final int priceSubstringIndex = priceField.indexOf("万円");
        final long price = Long.parseLong(priceField.substring(0, priceSubstringIndex)) * 10000;

        final String ageField = getField(posting, "築年月");
        final int ageSubstringIndex = ageField.indexOf("年");
        final int age = LocalDate.now().getYear() - Integer.parseInt(ageField.substring(0, ageSubstringIndex));

        final Double landSurface = parseSurfaceField(posting, "土地面積");
        final Double houseSurface = parseSurfaceField(posting, "建物面積");

        // ＪＲ常磐線「荒川沖」徒歩33分
        final String stationField = getField(posting, "沿線・駅");

        final int walkTimeToStationSubstringIndex = stationField.indexOf("徒歩");
        final Integer walkTimeToStation = walkTimeToStationSubstringIndex != -1
                ? Integer.parseInt(stationField.substring(walkTimeToStationSubstringIndex + 2, stationField.indexOf("分", walkTimeToStationSubstringIndex)))
                : null;

        final int stationOpenBracketIndex = stationField.indexOf('「');
        final int stationCloseBracketIndex = stationField.indexOf('」');
        final String station = stationOpenBracketIndex != -1 && stationCloseBracketIndex != -1
                ? stationField.substring(stationOpenBracketIndex + 1, stationCloseBracketIndex)
                : null;

        return new Posting(url, price, age, landSurface, houseSurface, walkTimeToStation, station);
    }

    private static Double parseSurfaceField(WebElement posting, String fieldName)
    {
        final String surfaceField = getField(posting, fieldName);

        int surfaceSubstringIndex = surfaceField.indexOf("m2");
        if (surfaceSubstringIndex == -1)
            surfaceSubstringIndex = surfaceField.indexOf("㎡");

        return surfaceSubstringIndex != -1 ? //
                Double.parseDouble(surfaceField.substring(0, surfaceSubstringIndex)) : //
                null;
    }

    private static String getField(WebElement posting, String fieldName)
    {
        return posting.findElement(By.xpath(".//dl[dt='" + fieldName + "']/dd")).getText();
    }
}
