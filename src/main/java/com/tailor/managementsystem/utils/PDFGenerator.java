package com.tailor.managementsystem.utils;

import com.tailor.managementsystem.model.Customer;
import com.tailor.managementsystem.model.Order;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {

    public static ByteArrayInputStream generateBillPDF(Customer customer, Order order) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Tailor Bill Invoice", font);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // empty line

            // Customer Details
            document.add(new Paragraph("Customer Name: " + customer.getName()));
            document.add(new Paragraph("Mobile: " + customer.getMobile()));
            document.add(new Paragraph("Garment Type: " + order.getGarmentType()));
            document.add(new Paragraph("Price: ₹" + order.getPrice()));
            document.add(new Paragraph("Delivery Date: " + order.getDeliveryDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
            document.add(new Paragraph("Notes: " + order.getNotes()));

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}