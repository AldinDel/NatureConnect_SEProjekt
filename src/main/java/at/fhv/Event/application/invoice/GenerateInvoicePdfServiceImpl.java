package at.fhv.Event.application.invoice;

import at.fhv.Event.domain.model.invoice.Invoice;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class GenerateInvoicePdfServiceImpl
        implements GenerateInvoicePdfService {

    @Override
    public byte[] generate(Invoice invoice) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11);
            Font smallGray = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
            Font totalFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(31, 122, 79));

            Paragraph title = new Paragraph("INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_RIGHT);
            document.add(title);

            Paragraph invoiceNr = new Paragraph(
                    "Invoice #" + invoice.getId(), normalFont);
            invoiceNr.setAlignment(Element.ALIGN_RIGHT);
            document.add(invoiceNr);

            document.add(Chunk.NEWLINE);

            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            metaTable.setWidths(new float[]{3, 2});

            metaTable.addCell(noBorderCell(
                    "NATUREConnect\n" +
                            "Hochschulstraße 1\n" +
                            "6850 Dornbirn\n" +
                            "Austria\n\n" +
                            "Email: info@natureconnect.at\n" +
                            "Phone: +43 000 000000",
                    normalFont
            ));

            String date = invoice.getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String time = invoice.getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("HH:mm"));

            metaTable.addCell(noBorderCell(
                    "Status: " + invoice.getStatus()
                            + "\nDate: " + date
                            + "\nTime: " + time,
                    normalFont));

            document.add(metaTable);
            document.add(Chunk.NEWLINE);

            if (invoice.getStatus().name().equals("INTERIM")) {
                Paragraph interimInfo = new Paragraph(
                        "INTERIM INVOICE – FOR REVIEW ONLY",
                        new Font(Font.HELVETICA, 11, Font.BOLD, Color.RED)
                );
                interimInfo.setSpacingAfter(10);
                document.add(interimInfo);
            }

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setWidths(new float[]{4, 1, 2, 2});

            table.addCell(headerCell("Description"));
            table.addCell(headerCell("Qty"));
            table.addCell(headerCell("Unit price"));
            table.addCell(headerCell("Total"));

            if (invoice.getLines() == null || invoice.getLines().isEmpty()) {
                PdfPCell empty = new PdfPCell(
                        new Phrase("No invoice items available", normalFont));
                empty.setColspan(4);
                empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                empty.setPadding(10);
                table.addCell(empty);
            } else {
                for (var line : invoice.getLines()) {
                    table.addCell(bodyCell(line.getDescription()));
                    table.addCell(bodyCell(String.valueOf(line.getQuantity())));
                    table.addCell(bodyCell(line.getUnitPrice() + " €"));
                    table.addCell(bodyCell(line.getTotal() + " €"));
                }
            }

            document.add(table);

            Paragraph total = new Paragraph(
                    "Total: " + invoice.getTotal() + " €",
                    totalFont
            );
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(15);
            document.add(total);

            document.add(Chunk.NEWLINE);

            Paragraph paymentTitle = new Paragraph(
                    "Payment Information", headerFont);
            document.add(paymentTitle);

            Paragraph paymentNote = new Paragraph(
                    "Please transfer the invoice amount to the following bank account:",
                    normalFont);
            paymentNote.setSpacingAfter(8);
            document.add(paymentNote);

            PdfPTable bankTable = new PdfPTable(2);
            bankTable.setWidthPercentage(100);
            bankTable.setWidths(new float[]{2, 3});

            bankTable.addCell(labelCell("Account Holder"));
            bankTable.addCell(valueCell("NatureConnect GmbH"));

            bankTable.addCell(labelCell("IBAN"));
            bankTable.addCell(valueCell("AT89 3704 0044 0532 0130"));

            bankTable.addCell(labelCell("BIC"));
            bankTable.addCell(valueCell("RVVGAT2B404"));

            bankTable.addCell(labelCell("Reference"));
            bankTable.addCell(valueCell("Invoice #" + invoice.getId()));

            document.add(bankTable);

            Paragraph footer = new Paragraph(
                    "Please include the invoice number as reference in your transfer.",
                    smallGray);
            footer.setSpacingBefore(6);
            document.add(footer);

            document.add(Chunk.NEWLINE);

            Paragraph imprintFooter = new Paragraph(
                    "Responsible for content:\nNATUREConnect Team",
                    new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY)
            );
            imprintFooter.setAlignment(Element.ALIGN_CENTER);
            imprintFooter.setSpacingBefore(20);

            document.add(imprintFooter);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Could not generate invoice PDF", e);
        }
    }


    private PdfPCell headerCell(String text) {
        Font font = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(11, 37, 69));
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell bodyCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell noBorderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell labelCell(String text) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text, new Font(Font.HELVETICA, 10, Font.BOLD)));
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell valueCell(String text) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text, new Font(Font.HELVETICA, 10)));
        cell.setPadding(6);
        return cell;
    }
}
