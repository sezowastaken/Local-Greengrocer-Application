package com.group25.greengrocer.util;

import com.group25.greengrocer.model.Order;
import com.group25.greengrocer.model.OrderItem;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for generating PDF invoice documents.
 * 
 * This class provides functionality to create professional PDF invoices
 * from Order and OrderItem data using the iText PDF library. The generated
 * invoices include order details, item listings in a table format, and
 * financial summaries (subtotal, discount, VAT, total).
 * 
 * The generated PDF is returned as a byte array, which can be saved to a file,
 * sent via email, or displayed to the user.
 */
public class PdfInvoiceUtil {

        /**
         * Generates a PDF invoice document from an Order and its associated OrderItems.
         * 
         * The generated PDF includes:
         *   Invoice title and header
         *   Order details (ID, date, customer ID, carrier ID if assigned, notes)
         *   Item table with columns: Item name, Quantity, Unit Price, Total
         *   Financial summary: Subtotal, Discount, VAT, Total
         * 
         * The PDF is formatted with proper alignment, styling, and a clean layout.
         * 
         * @param order The Order object containing order metadata and financial information
         * @param items The list of OrderItem objects representing the products in the order
         * @return byte array containing the PDF document data, or null if generation fails. All monetary values are displayed with two decimal places.
         */
        public static byte[] generateInvoice(Order order, List<OrderItem> items) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        PdfWriter writer = new PdfWriter(baos);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf);

                        // Title
                        document.add(new Paragraph("INVOICE")
                                        .setFontSize(20)
                                        .setBold()
                                        .setTextAlignment(TextAlignment.CENTER));

                        // Order Details
                        document.add(new Paragraph("\n"));
                        document.add(new Paragraph("Order ID: " + order.getId()));
                        document.add(new Paragraph(
                                        "Date: " + order.getOrderTime()
                                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
                        document.add(new Paragraph("Customer ID: " + order.getCustomerId()));
                        if (order.getCarrierId() != null) {
                                document.add(new Paragraph("Carrier ID: " + order.getCarrierId()));
                        }
                        if (order.getNote() != null && !order.getNote().isEmpty()) {
                                document.add(new Paragraph("Note: " + order.getNote()));
                        }

                        document.add(new Paragraph("\n"));

                        // Table
                        Table table = new Table(UnitValue.createPercentArray(new float[] { 4, 2, 2, 2 }))
                                        .useAllAvailableWidth();
                        table.addHeaderCell(
                                        new Cell().add(new Paragraph("Item").setBold()
                                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)));
                        table.addHeaderCell(
                                        new Cell().add(new Paragraph("Quantity").setBold()
                                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)));
                        table.addHeaderCell(new Cell()
                                        .add(new Paragraph("Unit Price").setBold()
                                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)));
                        table.addHeaderCell(
                                        new Cell().add(new Paragraph("Total").setBold()
                                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)));

                        for (OrderItem item : items) {
                                table.addCell(new Paragraph(item.getProductName()));
                                table.addCell(new Paragraph(item.getFormattedQuantity() + " " + item.getUnit()));
                                table.addCell(new Paragraph(item.getFormattedUnitPrice()));
                                table.addCell(new Paragraph(item.getFormattedLineTotal()));
                        }

                        document.add(table);

                        // Totals
                        document.add(new Paragraph("\n"));
                        document.add(new Paragraph(String.format("Subtotal: $%.2f", order.getSubtotal()))
                                        .setTextAlignment(TextAlignment.RIGHT));
                        document.add(new Paragraph(String.format("Discount: -$%.2f", order.getDiscountTotal()))
                                        .setTextAlignment(TextAlignment.RIGHT));
                        document.add(new Paragraph(String.format("VAT: $%.2f", order.getVatTotal()))
                                        .setTextAlignment(TextAlignment.RIGHT));
                        document.add(new Paragraph(String.format("Total: $%.2f", order.getTotal())).setBold()
                                        .setFontSize(14)
                                        .setTextAlignment(TextAlignment.RIGHT));

                        document.close();
                        return baos.toByteArray();
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
        }
}
