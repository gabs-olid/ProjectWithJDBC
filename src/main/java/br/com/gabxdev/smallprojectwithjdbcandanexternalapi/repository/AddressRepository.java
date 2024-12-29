package br.com.gabxdev.smallprojectwithjdbcandanexternalapi.repository;

import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.database.ConnectionFactory;
import br.com.gabxdev.smallprojectwithjdbcandanexternalapi.domain.Address;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class AddressRepository {
    private static final Logger log = LogManager.getLogger(AddressRepository.class);

    public static List<Address> findByStreet(String street) {
        List<Address> addresses = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createStatementFindByStreet(conn, street);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) addresses.add(createAddress(rs));
        } catch (SQLException e) {
            log.error("Error when trying to find address by street", e);
        }
        return addresses;
    }

    private static PreparedStatement createStatementFindByStreet(Connection conn, String street) throws SQLException {
        String sql = """
                SELECT a.zipcode, a.street, a.district, a.city, a.state, a.region
                FROM projectwithjdbc.address a
                WHERE street LIKE ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, String.format("%%%s%%", street));
        return ps;
    }

    public static Address findByZipCode(String zipCode) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createStatementFindByZipCode(conn, zipCode);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) throw new SQLException("Address does not exist");
            return createAddress(rs);
        } catch (SQLException e) {
            log.error("Error when trying to find address by zip code and house number", e);
        }
        return null;
    }

    private static PreparedStatement createStatementFindByZipCode(Connection conn, String zipCode) throws SQLException {
        String sql = """
                SELECT a.zipcode, a.street, a.district, a.city, a.state, a.region
                FROM projectwithjdbc.address a
                WHERE a.zipcode = ?;
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, zipCode);
        return ps;
    }

    public static void save(Address address) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = createStatementSave(conn, address)) {
            int rowsAffected = rowsAffected = ps.executeUpdate();
            log.info("The '{}' was successfully inserted into the database, rows affected '{}'", address.getZipCode(), rowsAffected);
        } catch (SQLException e) {
            log.error("An error occurred while trying to insert '{}' into the database", address.getZipCode());
            throw e;
        }
    }

    private static PreparedStatement createStatementSave(Connection conn, Address address) throws SQLException {
        String sql = """
                INSERT INTO `projectwithjdbc`.`address`
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, address.getZipCode());
        ps.setString(2, address.getStreet());
        ps.setString(3, address.getDistrict());
        ps.setString(4, address.getCity());
        ps.setString(5, address.getState());
        ps.setString(6, address.getRegion());
        return ps;
    }

    public static Address createAddress(ResultSet rs) throws SQLException {
        return new Address(rs.getString("a.zipcode"),
                rs.getString("a.street"),
                rs.getString("a.district"),
                rs.getString("a.city"),
                rs.getString("a.state"),
                rs.getString("a.region"));
    }
}