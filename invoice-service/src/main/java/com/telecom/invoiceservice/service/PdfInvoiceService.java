package com.telecom.invoiceservice.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import com.telecom.invoiceservice.model.Invoice;

@Service
public class PdfInvoiceService {

    public byte[] generatePdf(Invoice invoice) {

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDType1Font regularFont =
                    new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            PDType1Font boldFont =
                    new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page)) {

                float y = 750;

                contentStream.beginText();
                contentStream.setFont(boldFont, 20);
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("TELECOM BILLING INVOICE");
                contentStream.endText();

                y -= 40;

                writeLine(
                        contentStream,
                        boldFont,
                        14,
                        50,
                        y,
                        "Invoice Number: " + invoice.getInvoiceNumber()
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "Invoice Date: " + invoice.getInvoiceDate()
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "Billing ID: " + invoice.getBillingId()
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "Customer ID: " + invoice.getCustomerId()
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "Mobile Line ID: " + invoice.getMobileLineId()
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "Subscription ID: " + invoice.getSubscriptionId()
                );

                y -= 40;

                writeLine(
                        contentStream,
                        boldFont,
                        14,
                        50,
                        y,
                        "Billing Period"
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        invoice.getBillingPeriodStart()
                                + " to "
                                + invoice.getBillingPeriodEnd()
                );

                y -= 40;

                writeLine(
                        contentStream,
                        boldFont,
                        14,
                        50,
                        y,
                        "Usage"
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "Call Minutes: " + invoice.getCallMinutes()
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "Data MB: " + invoice.getDataMb()
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "SMS Count: " + invoice.getSmsCount()
                );

                y -= 40;

                writeLine(
                        contentStream,
                        boldFont,
                        14,
                        50,
                        y,
                        "Charges"
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "Call Charges: " + invoice.getCallCharges()
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "Data Charges: " + invoice.getDataCharges()
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "SMS Charges: " + invoice.getSmsCharges()
                );

                y -= 25;

                writeLine(
                        contentStream,
                        regularFont,
                        12,
                        50,
                        y,
                        "Plan Charges: " + invoice.getPlanCharges()
                );

                y -= 40;

                writeLine(
                        contentStream,
                        boldFont,
                        16,
                        50,
                        y,
                        "TOTAL: " + invoice.getTotalAmount()
                );
            }

            document.save(outputStream);

            return outputStream.toByteArray();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to generate invoice PDF",
                    exception
            );
        }
    }

    private void writeLine(
            PDPageContentStream contentStream,
            PDType1Font font,
            float fontSize,
            float x,
            float y,
            String text) throws IOException {

        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}