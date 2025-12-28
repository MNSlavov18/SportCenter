package org.example.core;

import org.example.model.Group;
import org.example.model.SeatCategory;
import org.example.model.Seating;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SportCenter {
    private final EnumMap<SeatCategory, Integer> capacity;
    private final EnumMap<SeatCategory, Integer> occupied;
    private final ReentrantLock lock = new ReentrantLock(true); // fair lock

    public SportCenter(Map<SeatCategory, Integer> capacity) {
        this.capacity = new EnumMap<>(SeatCategory.class);
        this.capacity.putAll(capacity);

        this.occupied = new EnumMap<>(SeatCategory.class);
        for (SeatCategory c : SeatCategory.values()) {
            this.occupied.put(c, 0);
        }
    }

    /**
     * Настанява група, като за всяка категория слага колкото може (до капацитета).
     * Методът е thread-safe: една група се обработва атомарно.
     */
    public Seating seatGroup(Group group) {
        lock.lock();
        try {
            EnumMap<SeatCategory, Integer> req = group.getRequested();
            EnumMap<SeatCategory, Integer> seated = new EnumMap<>(SeatCategory.class);
            EnumMap<SeatCategory, Integer> rejected = new EnumMap<>(SeatCategory.class);

            for (SeatCategory cat : SeatCategory.values()) {
                int want = req.getOrDefault(cat, 0);
                int free = capacity.getOrDefault(cat, 0) - occupied.getOrDefault(cat, 0);

                int put = Math.max(0, Math.min(want, free));
                int rej = want - put;

                seated.put(cat, put);
                rejected.put(cat, rej);

                occupied.put(cat, occupied.get(cat) + put);
            }

            return new Seating(group.getId(), group.getEntranceNo(), seated, rejected);
        } finally {
            lock.unlock();
        }
    }

    public EnumMap<SeatCategory, Integer> getOccupiedSnapshot() {
        lock.lock();
        try {
            return new EnumMap<>(occupied);
        } finally {
            lock.unlock();
        }
    }

    public EnumMap<SeatCategory, Integer> getCapacitySnapshot() {
        lock.lock();
        try {
            return new EnumMap<>(capacity);
        } finally {
            lock.unlock();
        }
    }
}