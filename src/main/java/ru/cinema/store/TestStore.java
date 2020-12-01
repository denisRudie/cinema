package ru.cinema.store;

import ru.cinema.model.Seat;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestStore {

    private static final TestStore INST = new TestStore();
    private Map<Integer, Seat> store = new ConcurrentHashMap<>();

    private TestStore() {
        Seat seat1 = new Seat();
        seat1.setId(1);
        seat1.setRow(1);
        seat1.setCol(1);
        seat1.generateCustomId();
        store.put(1, seat1);

        Seat seat2 = new Seat();
        seat2.setId(2);
        seat2.setRow(1);
        seat2.setCol(2);
        seat2.generateCustomId();
        store.put(2, seat2);

        Seat seat3 = new Seat();
        seat3.setId(3);
        seat3.setRow(1);
        seat3.setCol(3);
        seat3.generateCustomId();
        store.put(3, seat3);
    }

    public static TestStore getInst() {
        return INST;
    }

    public Collection<Seat> getAllSeats() {
        return store.values();
    }

    public void setSeatOwner(int sId, int oId) {
        Seat seat = store.get(sId);
        seat.setAccountId(oId);
        store.computeIfPresent(sId, (k, v) -> seat);
    }

    public Seat getSeatById(int sId) {
        return store.get(sId);
    }
}
