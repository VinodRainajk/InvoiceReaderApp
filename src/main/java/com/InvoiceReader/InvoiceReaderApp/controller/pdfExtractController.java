package com.InvoiceReader.InvoiceReaderApp.controller;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@RestController
public class pdfExtractController {

    @PostMapping("/api/pdf/extractText")
    public @ResponseBody ResponseEntity<String>
    extractTextFromPDFFile() {
        try {
            // Load file into PDFBox class
            File file = new File("F:\\Learning\\Projects\\InvoiceReaderApp\\Sample_pdf.pdf\\");
            System.out.println("inside the load");
            PDDocument document = Loader.loadPDF(file);
            System.out.println("inside the load 1");
            PDFTextStripper stripper = new PDFTextStripper();
            System.out.println("inside the load 2");
            String strippedText = stripper.getText(document);
            System.out.println("check if it is a scanned document");
            // Check text exists into the file
           // if (strippedText.trim().isEmpty()){
                System.out.println("scanned document");
                strippedText = extractTextFromScannedDocument(document);
           // }

            JSONObject obj = new JSONObject();
            obj.put("fileName", "Sample_pdf.pdf");
            obj.put("text", strippedText.toString());
            System.out.println("details are "+strippedText.toString());
            return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private String extractTextFromScannedDocument(PDDocument document)
            throws IOException, TesseractException {

        // Extract images from file
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder out = new StringBuilder();

        ITesseract _tesseract = new Tesseract();
        _tesseract.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR\\tessdata");
        _tesseract.setLanguage("eng"); // choose your language

        for (int page = 0; page < document.getNumberOfPages(); page++)
        {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

            // Create a temp image file
            File temp = File.createTempFile("tempfile_" + page, ".png");
            ImageIO.write(bim, "png", temp);

            String result = _tesseract.doOCR(temp);
            out.append(result);

            // Delete temp file
            temp.delete();

        }

        return out.toString();

    }
}
