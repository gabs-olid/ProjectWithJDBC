package br.com.gabxdev.smallprojectwithjdbcandanexternalapi.service;

import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.dominio.Address;
import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.dominio.AddressFromBuscaCep;
import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.external.ApiBuscaCep;
import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.repository.AddressRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Scanner;

@Log4j2
public final class AddressService {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Logger log = LogManager.getLogger(AddressService.class);

    public static void menu(int op) {
        switch (op) {
            case 1 -> findByStreet();
            case 2 -> InsertAddress();
        }
    }

    private static void findByStreet() {
        System.out.print("Type the street or empty to all ");
        String street = SCANNER.nextLine();
        AddressRepository.findByStreet(street).
                forEach(e -> System.out.println(e.toString()));
    }

    private static void save(Address address) {
        try {
            AddressRepository.save(address);
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                log.info("This zip code is already in our database");
            }
        }
    }

    private static void InsertAddress() {
        System.out.print("type the zip code [example '33333111']: ");

        try {
            AddressFromBuscaCep address = checkZipCode(SCANNER.nextLine());
            System.out.println(address.toString());
            System.out.print("Are you sure you want to enter the address above Y/N? ");
            String choice = SCANNER.nextLine();
            if (!"y".equalsIgnoreCase(choice)) return;
            saveAddress(address);
        } catch (IllegalArgumentException e) {
            log.error("There was an error trying to enter the address '{}'", e.getMessage());
        }
    }

    public static Address saveAddress(AddressFromBuscaCep addressFromBuscaCep) {

        Address address = new Address(addressFromBuscaCep.cep(),
                addressFromBuscaCep.logradouro(),
                addressFromBuscaCep.bairro(),
                addressFromBuscaCep.localidade(),
                addressFromBuscaCep.estado(),
                addressFromBuscaCep.regiao());

        save(address);

        return address;
    }

    public static AddressFromBuscaCep checkZipCode(String zipCode) throws IllegalArgumentException {
        AddressFromBuscaCep addressFromBuscaCep = ApiBuscaCep.getAddress(zipCode);
        if (addressFromBuscaCep == null) throw new IllegalArgumentException("Zip Code invalid");
        if (Boolean.parseBoolean(addressFromBuscaCep.erro())) throw new IllegalArgumentException("ZIP code not found");
        return addressFromBuscaCep;
    }
}
