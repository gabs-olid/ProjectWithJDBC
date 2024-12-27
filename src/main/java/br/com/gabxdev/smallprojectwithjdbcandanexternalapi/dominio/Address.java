package br.com.gabxdev.smallprojectwithjdbcandanexternalapi.dominio;

public final class Address {
    private final String zipCode;
    private final String street;
    private final String district;
    private final String city;
    private final String state;
    private final String region;

    public Address(String zipCode, String street, String district, String city, String state, String region) {
        this.zipCode = zipCode;
        this.street = street;
        this.district = district;
        this.city = city;
        this.state = state;
        this.region = region;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return "Address{" +
               "zipCode='" + zipCode + '\'' +
               ", street='" + street + '\'' +
               ", district='" + district + '\'' +
               ", city='" + city + '\'' +
               ", state='" + state + '\'' +
               ", region='" + region + '\'' +
               '}';
    }
}
