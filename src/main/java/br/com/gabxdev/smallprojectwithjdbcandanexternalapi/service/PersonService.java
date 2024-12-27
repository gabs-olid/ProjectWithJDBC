package br.com.gabxdev.smallprojectwithjdbcandanexternalapi.service;

import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.dominio.Address;
import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.dominio.Person;
import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.repository.PersonRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Scanner;


@Log4j2
public class PersonService {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Logger log = LogManager.getLogger(PersonService.class);

    public static void menu(int op) {
        switch (op) {
            case 1 -> findByName();
            case 2 -> delete();
            case 3 -> save();
            case 4 -> update();
        }
    }

    private static void findByName() {
        System.out.print("Type the name or empty to all ");
        String name = SCANNER.nextLine();
        PersonRepository.findByName(name).
                forEach(e -> System.out.println(e.toString()));
    }

    private static void delete() {
        System.out.print("Type the cpf of the person you want to delete - example '000.000.00-00' ");
        String cpf = SCANNER.next();
        if (!wantToTakeAction()) return;
        PersonRepository.delete(cpf);
    }

    private static void save() {
        System.out.print("Type the cpf: [example '111-111-111-11']  ");
        String cpf = SCANNER.nextLine();

        System.out.print("Type the name:  ");
        String name = SCANNER.nextLine();

        System.out.print("Type the last name: ");
        String lastName = SCANNER.nextLine();

        System.out.print("Type the house number: ");
        Integer houseNumber = Integer.parseInt(SCANNER.nextLine());

        System.out.print("Type the zip code: [example '33333111'] ");
        String zipCode = SCANNER.nextLine();

        if (!wantToTakeAction()) return;

        try {
            Address address = AddressService.saveAddress(AddressService.checkZipCode(zipCode));
            PersonRepository.save(new Person(cpf, name, lastName, houseNumber, address));
        } catch (SQLException | IllegalArgumentException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                log.info("You are already registered in our database");
                return;
            }
            log.error(e.getMessage());
        }
    }

    private static void update() {
        System.out.print("Type the cpf of the person you want to update [example '111-111-111-11'] ");
        String cpf = SCANNER.nextLine();
        Person personFromDb = PersonRepository.findByCpf(cpf);
        if (personFromDb == null) {
            System.out.println("Person not found");
            return;
        }

        System.out.println("Person found: " + personFromDb.toString());

        System.out.print("Type the name or enter to keep the same: ");
        String name = SCANNER.nextLine();
        name = name.isEmpty() ? personFromDb.getName() : name;

        System.out.print("Type the last name or enter to keep the same: ");
        String lastName = SCANNER.nextLine();
        lastName = lastName.isEmpty() ? personFromDb.getlastName() : lastName;

        System.out.print("Type the house number or enter to keep the same: ");
        String houseNumberOp = SCANNER.nextLine();
        Integer houseNumber = houseNumberOp.isEmpty() ? personFromDb.getHouseNumber() : Integer.parseInt(houseNumberOp);

        System.out.print("Type the zip code or enter to keep the same [example '33333111']: ");
        String zipCode = SCANNER.nextLine();

        if (!wantToTakeAction()) return;

        try {
            Address address = zipCode.isEmpty() ? personFromDb.getAddress() : AddressService.saveAddress(AddressService.checkZipCode(zipCode));
            PersonRepository.update(new Person(cpf, name, lastName, houseNumber, address));
        } catch (IllegalArgumentException e) {
            log.error("There was an error trying to update the data '{}'", e.getMessage());
        }
    }

    private static boolean wantToTakeAction() {
        System.out.print("Are you sure? Y/N ");
        String choice = SCANNER.next();
        if ("y".equalsIgnoreCase(choice)) return true;
        return false;
    }
}