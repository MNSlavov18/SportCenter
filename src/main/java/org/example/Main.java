package org.example;

import org.example.core.SportCenter;
import org.example.model.Group;
import org.example.model.SeatCategory;
import org.example.model.Seating;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // 1) Капацитет по категории (примерни стойности)
        EnumMap<SeatCategory, Integer> capacity = new EnumMap<>(SeatCategory.class);
        capacity.put(SeatCategory.VIP, 20);
        capacity.put(SeatCategory.A, 50);
        capacity.put(SeatCategory.B, 80);
        capacity.put(SeatCategory.C, 120);

        SportCenter hall = new SportCenter(capacity);

        // 2) Генерираме минимум 10 групи (тук правим 12), разпределени по вход 1..4
        List<Group> groups = generateGroups(12);

        // 3) 4 входа = 4 нишки
        ExecutorService entrancesPool = Executors.newFixedThreadPool(4);

        // 4) Пускаме конкурентно всички групи като задачи
        List<Future<Seating>> futures = new ArrayList<>();
        for (Group g : groups) {
            futures.add(entrancesPool.submit(() -> {
                // симулация: групата "идва" след малко време
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
                Seating result = hall.seatGroup(g);
                System.out.println(g);
                System.out.println("    RESULT -> " + result);
                return result;
            }));
        }

        // 5) Изчакваме всички групи да приключат
        for (Future<Seating> f : futures) {
            try {
                f.get();
            } catch (ExecutionException e) {
                System.err.println("Task failed: " + e.getCause());
            }
        }

        entrancesPool.shutdown();
        entrancesPool.awaitTermination(5, TimeUnit.SECONDS);

        // 6) Финален отчет: брой заети места по категории
        System.out.println("\n=== FINAL OCCUPANCY (seated) ===");
        var occ = hall.getOccupiedSnapshot();
        var cap = hall.getCapacitySnapshot();
        for (SeatCategory cat : SeatCategory.values()) {
            int o = occ.getOrDefault(cat, 0);
            int c = cap.getOrDefault(cat, 0);
            System.out.printf("%-3s : %d / %d%n", cat.name(), o, c);
        }
    }

    private static List<Group> generateGroups(int count) {
        Random rnd = new Random();
        List<Group> groups = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            int entrance = 1 + rnd.nextInt(4); // 1..4

            // Примерна логика: групата иска места в няколко категории
            // (можеш да я промениш според предпочитанията ти)
            EnumMap<SeatCategory, Integer> req = new EnumMap<>(SeatCategory.class);
            req.put(SeatCategory.VIP, rnd.nextInt(0, 6)); // 0..5
            req.put(SeatCategory.A, rnd.nextInt(2, 12));  // 2..11
            req.put(SeatCategory.B, rnd.nextInt(3, 18));  // 3..17
            req.put(SeatCategory.C, rnd.nextInt(4, 25));  // 4..24

            groups.add(new Group("G" + i, entrance, req));
        }
        return groups;
    }
}