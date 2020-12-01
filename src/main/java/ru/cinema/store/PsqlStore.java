package ru.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cinema.model.Account;
import ru.cinema.model.Seat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PsqlStore implements Store {
    private static final String FIND_ALL_SEATS = "SELECT * FROM cinema_hall";
    private static final String FIND_ALL_ACCOUNTS = "SELECT * FROM account";
    private static final String SET_SEAT_OWNER = "UPDATE cinema_hall set account_id = ? where id = ?";
    private static final String CREATE_ACCOUNT = "INSERT into account (name, email, password) values " +
            "(?, ?, crypt(?, gen_salt('bf', 8)))";
    private static final String FIND_ACCOUNT_BY_LOG_PWD = "SELECT * FROM account where email = ? and password = crypt(?, password)";

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class);

    private final ConcurrentHashMap<Integer, Seat> cachedSeats = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Account> cachedAccounts = new ConcurrentHashMap<>();
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);

//        init seats from db to cache
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_ALL_SEATS)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    Seat seat = new Seat();
                    seat.setId(it.getInt("id"));
                    seat.setRow(it.getInt("row"));
                    seat.setCol(it.getInt("col"));
                    seat.setAccountId(it.getInt("account_id"));
                    seat.generateCustomId();
                    seat.setPrice(it.getFloat("price"));
                    cachedSeats.put(seat.getId(), seat);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }


//        init accounts from db to cache
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_ALL_ACCOUNTS)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    Account acc = new Account();
                    acc.setId(it.getInt("id"));
                    acc.setName(it.getString("name"));
                    acc.setEmail(it.getString("email"));
                    cachedAccounts.put(acc.getId(), acc);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Seat> getAllSeats() {
        return new ArrayList<>(cachedSeats.values());
    }

    @Override
    public void setSeatOwner(int seatId, int accId) {
        Seat updatedSeat = cachedSeats.get(seatId);
        updatedSeat.setAccountId(accId);
        cachedSeats.computeIfPresent(seatId, (k, v) -> updatedSeat);

        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SET_SEAT_OWNER)
        ) {
            ps.setInt(1, accId);
            ps.setInt(2, seatId);
            ps.execute();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public Seat getSeatById(int seatId) {
        return cachedSeats.get(seatId);
    }

    @Override
    public Collection<Seat> getSeatByOwner(int accId) {
        return cachedSeats.values().stream()
                .filter(seat -> seat.getAccountId() == accId)
                .collect(Collectors.toList());
    }

    @Override
    public void addAcc(Account acc) {
        cachedAccounts.put(acc.getId(), acc);

        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(CREATE_ACCOUNT)
        ) {
            ps.setString(1, acc.getName());
            ps.setString(2, acc.getEmail());
            ps.setString(3, acc.getPassword());
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public Account getAccById(int id) {
        return cachedAccounts.get(id);
    }

    @Override
    public Account getAccByLogPwd(String login, String pwd) {
        Account acc = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_ACCOUNT_BY_LOG_PWD)) {
            ps.setString(1, login);
            ps.setString(2, pwd);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    acc = new Account();
                    acc.setId(it.getInt("id"));
                    acc.setName(it.getString("name"));
                    acc.setEmail(it.getString("email"));
                }
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return acc;
    }
}
