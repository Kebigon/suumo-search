package xyz.kebigon.housesearch.domain;

public enum Area
{
    HOKKAIDO("010"), TOHOKU("020"), KANTO("030"), KOSHINETSU("040"), TOKAI("050"), KANSAI("060"), SHIKOKU("070"), CHUGOKU("080"), KYUSHU("090");

    private final String code;

    private Area(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
}
