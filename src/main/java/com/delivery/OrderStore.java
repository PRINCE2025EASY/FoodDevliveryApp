package com.delivery;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

   //Single source of truth for every order placed

public final class OrderStore {

        private static final Path STORAGE_FILE =
            Paths.get(System.getProperty("user.dir"), "orders_data.txt");

    private static final OrderStore INSTANCE = new OrderStore();

    private final ObservableList<Order> orders = FXCollections.observableArrayList();
    private final AtomicInteger sequence = new AtomicInteger(0);

    private OrderStore() {
        loadFromDisk();
    }

    public static OrderStore getInstance() {
        return INSTANCE;
    }

    /** Live, observable view of every order currently known to the store (newest first). */
    public ObservableList<Order> getOrders() {
        return orders;
    }

    /** Records a freshly placed order, persists it, and returns the created {@link Order}. */
    public synchronized Order recordOrder(String placedByUsername, Customer customer,
                                           List<MenuItem> items, double total) {
        String orderId = "ORD-" + String.format("%04d", sequence.incrementAndGet());
        Order order = new Order(orderId, placedByUsername, customer, items, total);
        orders.add(0, order);
        saveToDisk();
        return order;
    }

    public double getTotalRevenue() {
        double sum = 0.0;
        for (Order o : orders) {
            sum += o.getTotalAmount();
        }
        return sum;
    }

    /** Re-reads the flat file, picking up any orders placed by other app runs. */
    public synchronized void reload() {
        orders.clear();
        loadFromDisk();
    }

    private void loadFromDisk() {
        if (!Files.exists(STORAGE_FILE)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(STORAGE_FILE, StandardCharsets.UTF_8);
            int maxSeq = 0;
            List<Order> loaded = new ArrayList<>();
            for (String line : lines) {
                if (line == null || line.isEmpty()) {
                    continue;
                }
                Order order = Order.fromRecordLine(line);
                if (order != null) {
                    loaded.add(order);
                    maxSeq = Math.max(maxSeq, extractSequence(order.getOrderId()));
                }
            }
            loaded.sort((a, b) -> b.getPlacedAt().compareTo(a.getPlacedAt())); // newest first
            orders.addAll(loaded);
            sequence.set(maxSeq);
        } catch (IOException e) {
            System.err.println("Failed to load persisted orders: " + e.getMessage());
        }
    }

    private int extractSequence(String orderId) {
        try {
            return Integer.parseInt(orderId.replace("ORD-", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void saveToDisk() {
        try (BufferedWriter writer = Files.newBufferedWriter(STORAGE_FILE, StandardCharsets.UTF_8)) {
            List<Order> chronological = new ArrayList<>(orders);
            chronological.sort((a, b) -> a.getPlacedAt().compareTo(b.getPlacedAt())); // oldest first on disk
            for (Order o : chronological) {
                writer.write(o.toRecordLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to persist orders: " + e.getMessage());
        }
    }
}
