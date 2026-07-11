package com.delivery;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Order {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String orderId;
    private final String placedByUsername;
    private final String customerId;
    private final String customerName;
    private final String customerEmail;
    private final String deliveryAddress;
    private final List<MenuItem> items;
    private final double totalAmount;
    private final LocalDateTime placedAt;

    /** Used when a brand-new order is placed through the checkout form. */
    public Order(String orderId, String placedByUsername, Customer customer,
                 List<MenuItem> items, double totalAmount) {
        this(orderId, placedByUsername, customer.getUserId(), customer.getName(),
                customer.getEmail(), customer.getDeliveryAddress(), items, totalAmount,
                LocalDateTime.now());
    }

    /** Used when reconstructing an order previously persisted to disk. */
    public Order(String orderId, String placedByUsername, String customerId, String customerName,
                 String customerEmail, String deliveryAddress, List<MenuItem> items,
                 double totalAmount, LocalDateTime placedAt) {
        this.orderId = orderId;
        this.placedByUsername = placedByUsername;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.deliveryAddress = deliveryAddress;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.placedAt = placedAt;
    }

    public String getOrderId() { return orderId; }
    public String getPlacedByUsername() { return placedByUsername; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public List<MenuItem> getItems() { return new ArrayList<>(items); }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getPlacedAt() { return placedAt; }
    public String getFormattedTimestamp() { return placedAt.format(TIMESTAMP_FORMAT); }

    //Full invoice
    public String toInvoiceString() {
        StringBuilder invoice = new StringBuilder();
        invoice.append("=== INVOICE DISPATCH SLIP ===\n");
        invoice.append("Order ID: ").append(orderId).append("\n");
        invoice.append("Placed At: ").append(getFormattedTimestamp()).append("\n");
        invoice.append("Placed By (login): ").append(placedByUsername).append("\n");
        invoice.append("Customer UUID ID: ").append(customerId).append("\n");
        invoice.append("Name Profile: ").append(customerName).append("\n");
        invoice.append("Contact Email: ").append(customerEmail).append("\n");
        invoice.append("Shipping Delivery Destination: ").append(deliveryAddress).append("\n");
        invoice.append("--------------------------------------------------------------------\n");
        invoice.append("Items Ordered Inventory Cluster:\n");
        for (MenuItem item : items) {
            invoice.append(" -> ").append(item.toString()).append("\n");
        }
        invoice.append("--------------------------------------------------------------------\n");
        invoice.append("Total Order Gross Invoice Computation Settlement Amount: \u20B5")
                .append(String.format("%.2f", totalAmount)).append("\n");
        invoice.append("Status: Order routing dispatched successfully to local hub system matrix!");
        return invoice.toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | \u20B5%.2f", orderId, customerName, getFormattedTimestamp(), totalAmount);
    }

    /** Encodes this order as a single pipe-delimited line for flat-file persistence. */
    public String toRecordLine() {
        String itemsEncoded = items.stream()
                .map(i -> i.getId() + "~" + i.getName() + "~" + i.getPrice() + "~" + i.getCategory())
                .collect(Collectors.joining(";"));
        return String.join("|",
                orderId,
                placedByUsername,
                customerId,
                customerName,
                customerEmail,
                escape(deliveryAddress),
                itemsEncoded,
                String.valueOf(totalAmount),
                placedAt.toString());
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("|", "/").replace("\n", " ").replace("~", "-").replace(";", ",");
    }

    /** Decodes a line previously produced by {@link #toRecordLine()}. Returns null on malformed input. */
    public static Order fromRecordLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 9) {
            return null;
        }
        try {
            String orderId = parts[0];
            String placedByUsername = parts[1];
            String customerId = parts[2];
            String customerName = parts[3];
            String customerEmail = parts[4];
            String deliveryAddress = parts[5];
            String itemsEncoded = parts[6];
            double total = Double.parseDouble(parts[7]);
            LocalDateTime placedAt = LocalDateTime.parse(parts[8]);

            List<MenuItem> items = new ArrayList<>();
            if (!itemsEncoded.isEmpty()) {
                for (String itemStr : itemsEncoded.split(";")) {
                    String[] ip = itemStr.split("~", -1);
                    if (ip.length == 4) {
                        items.add(new MenuItem(ip[0], ip[1], Double.parseDouble(ip[2]), ip[3]));
                    }
                }
            }
            return new Order(orderId, placedByUsername, customerId, customerName, customerEmail,
                    deliveryAddress, items, total, placedAt);
        } catch (Exception ex) {
            // Malformed/corrupted line in the flat file - skip it rather than crash the app.
            return null;
        }
    }
}
