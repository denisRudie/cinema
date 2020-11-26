package ru.cinema.model;

import ru.cinema.store.PsqlStore;

import java.util.Objects;

public class Seat {

    private int id;
    private int row;
    private int col;
    private int accountId;
    private int customID;
    private float price;

    public Seat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void generateCustomId() {
        this.customID = Integer.parseInt("" + row + col);
    }

    public int getCustomID() {
        return customID;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return id == seat.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static void main(String[] args) {
        PsqlStore.instOf().getAllSeats().stream().map(Seat::getCustomID).forEach(System.out::println);
    }
}
