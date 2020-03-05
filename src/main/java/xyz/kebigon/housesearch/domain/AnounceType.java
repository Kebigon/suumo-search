package xyz.kebigon.housesearch.domain;

public enum AnounceType
{
    NEW_CONDO("010"), USED_CONDO("011"), NEW_HOUSE("020"), USED_HOUSE("021"), RENT("040");

    private final String code;

    private AnounceType(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
}
