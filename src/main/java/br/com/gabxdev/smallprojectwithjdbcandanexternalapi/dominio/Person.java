package br.com.gabxdev.smallprojectwithjdbcandanexternalapi.dominio;

public final class Person {
    private final String cpf;
    private final String name;
    private final String lastName;
    private final Integer houseNumber;
    private final Address address;

    public Person(String cpf, String name, String lastName, Integer houseNumber, Address address) {
        this.cpf = cpf;
        this.name = name;
        this.lastName = lastName;
        this.houseNumber = houseNumber;
        this.address = address;
    }

    public String getCpf() {
        return cpf;
    }

    public String getName() {
        return name;
    }

    public String getlastName() {
        return lastName;
    }

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Person{" +
               "cpf='" + cpf + '\'' +
               ", name='" + name + '\'' +
               ", lastName='" + lastName + '\'' +
               ", houseNumber=" + houseNumber +
               ", address=" + address +
               '}';
    }
}
