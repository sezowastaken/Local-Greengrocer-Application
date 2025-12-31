package com.group25.greengrocer.service;

import com.group25.greengrocer.dao.OrderDao;
import com.group25.greengrocer.dao.OrderItemDao;
import com.group25.greengrocer.dao.ProductDao;
import com.group25.greengrocer.model.Order;
import com.group25.greengrocer.model.OrderItem;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderDao orderDao = new OrderDao();
    private final OrderItemDao orderItemDao = new OrderItemDao();
    private final ProductDao productDao = new ProductDao();

    /**
     * Places an order in a single atomic transaction.
     * 1. Validates and deducts stock for all items.
     * 2. Creates the Order record.
     * 3. Creates OrderItem records.
     * 4. Commits transaction.
     */
    public void placeOrder(Order order, List<OrderItem> items) throws SQLException {
        Connection conn = null;
        try {
            conn = DbAdapter.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // Check stock and update first (lock rows logic here)
            for (OrderItem item : items) {
                // Pass connection to ensure we are in the same transaction
                boolean stockUpdated = productDao.reduceStock(item.getProductId(), item.getQuantity(), conn);
                if (!stockUpdated) {
                    throw new SQLException("Insufficient stock for product ID: " + item.getProductId());
                }
            }

            // Create Order
            long orderId = orderDao.create(order, conn);
            order.setId(orderId);

            // Create Items
            for (OrderItem item : items) {
                item.setOrderId(orderId);
                orderItemDao.create(item, conn);
            }

            conn.commit(); // Commit all changes
            System.out.println("Order placed successfully. ID: " + orderId);

            // Generate and Save Invoice PDF
            try {
                byte[] invoicePdf = com.group25.greengrocer.util.PdfInvoiceUtil.generateInvoice(order, items);
                // Note: saveInvoice inside OrderDao manages its own connection, which is fine
                // here after commit.
                orderDao.saveInvoice(orderId, invoicePdf);
                System.out.println("Invoice generated and saved for Order ID: " + orderId);
            } catch (Exception e) {
                System.err.println("Failed to generate/save invoice: " + e.getMessage());
                e.printStackTrace();
                // We don't rollback the order if invoice fails, just log it.
                // In a real app, we might retry or queue it.
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.err.println("Transaction failed, rolling back. Error: " + e.getMessage());
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Re-throw to controller
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset for future use
                    // We do not close the connection here because DbAdapter manages a
                    // singleton-like connection
                    // that might be reused. In a connection pool scenario, we would close it
                    // (return to pool).
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
