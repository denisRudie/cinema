package ru.cinema.store;

import ru.cinema.model.Account;
import ru.cinema.model.Seat;

import java.util.Collection;

public interface Store {

    public Collection<Seat> getAllSeats();

    public void tempSeatOwner(int seatId, int accId);

    void setSetSeatOwner(int ownerId);

    public Seat getSeatById(int seatId);

    public Collection<Seat> getSeatByOwner(int accId);

    public void addAcc(Account acc);

    public Account getAccById(int id);

    public Account getAccByLogPwd(String login, String pwd);
}
