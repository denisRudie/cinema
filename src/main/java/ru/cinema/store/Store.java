package ru.cinema.store;

import ru.cinema.model.Account;
import ru.cinema.model.Seat;

import java.util.Collection;
import java.util.List;

public interface Store {

    Collection<Seat> getAllSeats();

    void tempSeatOwner(int seatId, int accId);

    void setSetSeatOwner(int ownerId);

    Seat getSeatById(int seatId);

    Collection<Seat> getSeatByOwner(int accId);

    int addAcc(Account acc);

    Account getAccById(int id);

    Account getAccByLogPwd(String login, String pwd);
}
