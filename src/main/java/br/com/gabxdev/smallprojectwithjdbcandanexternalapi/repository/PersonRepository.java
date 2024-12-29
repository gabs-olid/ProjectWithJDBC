package br.com.gabxdev.smallprojectwithjdbcandanexternalapi.repository;

import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.database.ConnectionFactory;
import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.domain.Address;
import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.domain.Person;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public final class PersonRepository {
    private static final Logger log = LogManager.getLogger(PersonRepository.class);

    public static List<Person> findByName(String name) {
        List<Person> persons = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createStatementFindByName(conn, name);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) persons.add(createPerson(rs));
        } catch (SQLException e) {
            log.error("Error when trying to find person by name", e);
        }
        return persons;
    }

    private static PreparedStatement createStatementFindByName(Connection conn, String name) throws SQLException {
        String sql = """
                SELECT p.cpf, p.name, p.lastName, p.housenumber, a.zipcode, a.street, a.district, a.city, a.state, a.region
                FROM projectwithjdbc.person p
                INNER JOIN projectwithjdbc.address a
                ON p.zipcode = a.zipcode
                WHERE name LIKE ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, String.format("%%%s%%", name));
        return ps;
    }

    public static Person findByCpf(String name) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createStatementFindByCpf(conn, name);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) throw new SQLException("Person does not exist");
            return createPerson(rs);
        } catch (SQLException e) {
            log.error("Error when trying to find person by cpf '{}'", e.getErrorCode());
        }
        return null;
    }

    private static PreparedStatement createStatementFindByCpf(Connection conn, String cpf) throws SQLException {
        String sql = """
                SELECT p.cpf, p.name, p.lastName, p.housenumber, a.zipcode, a.street, a.district, a.city, a.state, a.region
                FROM projectwithjdbc.person p
                INNER JOIN address a
                ON p.zipcode = a.zipcode
                WHERE p.cpf = ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, cpf);
        return ps;
    }

    public static void delete(String cpf) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createStatementDelete(conn, cpf)) {
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) throw new SQLException("Person does not exist in the database");
            log.info("Person with cpf '{}' deleted from database, rows affected '{}'", cpf, rowsAffected);
        } catch (SQLException e) {
            log.error("Unable to delete this person from the database", e);
        }
    }

    private static PreparedStatement createStatementDelete(Connection conn, String cpf) throws SQLException {
        String sql = """
                DELETE FROM projectwithjdbc.person
                WHERE cpf = ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, cpf);
        return ps;
    }

    public static void save(Person person) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createStatementSave(conn, person)) {
            int rowsAffected = ps.executeUpdate();
            log.info("The '{}' was successfully inserted into the database, rows affected '{}'", person.getName(), rowsAffected);
        } catch (SQLException e) {
            log.error("An error occurred while trying to insert '{}' into the database", person.getName(), e);
        }
    }

    private static PreparedStatement createStatementSave(Connection conn, Person person) throws SQLException {
        String sql = """
                INSERT INTO `projectwithjdbc`.`person`
                VALUES (?, ?, ?, ?, ?);
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, person.getCpf());
        ps.setString(2, person.getName());
        ps.setString(3, person.getlastName());
        ps.setInt(4, person.getHouseNumber());
        ps.setString(5, person.getAddress().getZipCode());
        return ps;
    }

    public static void update(Person person) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createStatementUpdate(conn, person)) {
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0)
                throw new SQLException("Person with " + person.getCpf() + " not found in our database");
            log.info("Updates made successfully, rows affected '{}'", rowsAffected);
        } catch (SQLException e) {
            log.info("There was an error trying to update the data", e);
        }
    }

    private static PreparedStatement createStatementUpdate(Connection conn, Person person) throws SQLException {
        String sql = """
                UPDATE `projectwithjdbc`.`person`
                SET name = ?, lastName = ?, housenumber = ?, zipcode = ?
                WHERE cpf = ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, person.getName());
        ps.setString(2, person.getlastName());
        ps.setInt(3, person.getHouseNumber());
        ps.setString(4, person.getAddress().getZipCode());
        ps.setString(5, person.getCpf());
        return ps;
    }

    private static Person createPerson(ResultSet rs) throws SQLException {
        return new Person(rs.getString("p.cpf"),
                rs.getString("p.name"),
                rs.getString("p.lastName"),
                rs.getInt("p.houseNumber"),
                new Address(rs.getString("a.zipcode"),
                        rs.getString("a.street"),
                        rs.getString("a.district"),
                        rs.getString("a.city"),
                        rs.getString("a.state"),
                        rs.getString("a.region")));
    }
}
