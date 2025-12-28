package org.example.model;

import java.util.EnumMap;
import java.util.Map;

public class Group {
    private final String id;
    private final int entranceNo;
    private final EnumMap<SeatCategory, Integer> requested;

    public Group(String id, int entranceNo, Map<SeatCategory, Integer> requested) {
        this.id = id;
        this.entranceNo = entranceNo;
        this.requested = new EnumMap<>(SeatCategory.class);
        this.requested.putAll(requested);
    }

    public String getId() {
        return id;
    }

    public int getEntranceNo() {
        return entranceNo;
    }

    public EnumMap<SeatCategory, Integer> getRequested() {
        return new EnumMap<>(requested);
    }

    public int totalPeople() {
        int sum = 0;
        for (int v : requested.values()) sum += v;
        return sum;
    }

    @Override
    public String toString() {
        return "Group " + id +
                " (entrance " + entranceNo + ")" +
                " REQUEST=" + requested +
                " (" + totalPeople() + " people)";
}
}