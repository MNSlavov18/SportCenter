package org.example.model;

import java.util.EnumMap;

public class Seating {
    private final String groupId;
    private final int entranceNo;
    private final EnumMap<SeatCategory, Integer> seated;
    private final EnumMap<SeatCategory, Integer> rejected;

    public Seating(String groupId,
                   int entranceNo,
                   EnumMap<SeatCategory, Integer> seated,
                   EnumMap<SeatCategory, Integer> rejected) {
        this.groupId = groupId;
        this.entranceNo = entranceNo;
        this.seated = new EnumMap<>(seated);
        this.rejected = new EnumMap<>(rejected);
    }

    public String groupId() { return groupId; }
    public int entranceNo() { return entranceNo; }
    public EnumMap<SeatCategory, Integer> seated() { return new EnumMap<>(seated); }
    public EnumMap<SeatCategory, Integer> rejected() { return new EnumMap<>(rejected); }

    @Override
    public String toString() {
        return "[Entrance " + entranceNo + "] " + groupId +
                " seated=" + seated +
                " rejected=" + rejected;
    }
}
