package com.tailor.managementsystem.utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.awt.Color;
import com.tailor.managementsystem.model.Order;

public class InvoiceGenerator {

    public static byte[] generateBill(Order order) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
           
            // Logo for Invoice
            Image logo = Image.getInstance("src/main/resources/static/images/logo.jpg");
            logo.scaleToFit(120, 60);
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);
           
            //  Shop Name Header
            Font shopFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.DARK_GRAY);
            Paragraph shopTitle = new Paragraph("★ Yash Mens Wear ★", shopFont);
            shopTitle.setAlignment(Element.ALIGN_CENTER);
            shopTitle.setSpacingAfter(10);
            document.add(shopTitle);

            //  Invoice Title
            Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Paragraph invoiceTitle = new Paragraph("Customer Invoice", titleFont);
            invoiceTitle.setAlignment(Element.ALIGN_CENTER);
            invoiceTitle.setSpacingAfter(15);
            document.add(invoiceTitle);

            //  Table with order details
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(15f);
            table.setWidths(new float[]{2f, 5f});

            Font cellFont = new Font(Font.HELVETICA, 12);

            addRow(table, "Customer ID", order.getId(), cellFont);
            addRow(table, "Garment Type", order.getGarmentType(), cellFont);
            addRow(table, "Price", "₹ " + order.getPrice(), cellFont);
            addRow(table, "Delivery Date", order.getDeliveryDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")), cellFont);
            addRow(table, "Additional Notes", order.getNotes(), cellFont);

            document.add(table);

            //  Footer Message
            Font footerFont = new Font(Font.HELVETICA, 11, Font.ITALIC, Color.GRAY);

            Paragraph thankYou = new Paragraph("Thank you for choosing Yash Mens Wear — Where Style Meets Precision.", footerFont);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            thankYou.setSpacingBefore(20);
            document.add(thankYou);
            
            Paragraph contactInfo = new Paragraph("📞 +91-7057327442   📍 Savarkar Chowk, Brahmanpuri, Miraj, Sangli, Maharashtra - 416410", footerFont);
            contactInfo.setAlignment(Element.ALIGN_CENTER);
            contactInfo.setSpacingBefore(5);
            document.add(contactInfo);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addRow(PdfPTable table, String label, Long id, Font cellFont) {
		// TODO Auto-generated method stub
		
	}

	private static void addRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, font));
        PdfPCell cell2 = new PdfPCell(new Phrase(value != null ? value : "-", font));
        cell1.setBorderWidth(1);
        cell2.setBorderWidth(1);
        table.addCell(cell1);
        table.addCell(cell2);
    }
}