package com.google.mediapipe.examples.hands;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

// per relazione ricordarsi di aggiungere le dipendenze -> iText per creare il pdf e pdfviewer per visualizzarlo

public class PdfActivity extends AppCompatActivity {

    private Button zoomIn, zoomOut, share, scrollDown, debug;

    private float zoomLevel = 0.5f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        //inizializzazione bottoni da eliminare quando colleghiamo le gesture
        zoomIn = findViewById(R.id.zoom_in);
        zoomOut = findViewById(R.id.zoom_out);
        share = findViewById(R.id.condividi);
        scrollDown = findViewById(R.id.scroll_down);

        debug = findViewById(R.id.debug);

        PDFView pdfView = (PDFView) findViewById(R.id.pdf_viewer);
        final LinearLayout buttonBar = findViewById(R.id.button_bar);

        //caricamento del pdf
        File pdfFile = new File(getFilesDir(), "documento.pdf");
        createPDF("documento.pdf");
        pdfView.fromFile(pdfFile)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();

        //collego bottoni a funzioni
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float currentZoom = pdfView.getScaleX();
                pdfView.setScaleX(currentZoom + 0.25f);
                pdfView.setScaleY(currentZoom + 0.25f);
                buttonBar.bringToFront();
                buttonBar.setBackgroundColor(Color.WHITE);
            }
        });

        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float currentZoom = pdfView.getScaleX();
                pdfView.setScaleX(currentZoom - 0.25f);
                pdfView.setScaleY(currentZoom - 0.25f);
                buttonBar.bringToFront();
                buttonBar.setBackgroundColor(Color.WHITE);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"destinatario@example.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Oggetto email");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body email");
                emailIntent.putExtra(Intent.EXTRA_STREAM, pdfFile);
                startActivity(Intent.createChooser(emailIntent, "Invia email..."));
            }
        });
        scrollDown.setOnClickListener(v -> pdfView.jumpTo(pdfView.getPageAtPositionOffset(0) , true));

        debug.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (int i = 0; i < 500; i++) {
                            pdfView.zoomTo(zoomLevel);
                            pdfView.scrollBy(0, 1);
                            zoomLevel += 0.01f;
                        }
                        Log.i("X: " + pdfView.getScrollX(), "Y: " + pdfView.getScrollY());
                    }
                }
        );


    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void createPDF(String documento){
        File pdfFile = new File(getFilesDir(), documento);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            document.addTitle("Titolo");
            document.addAuthor("Autore");
            document.addSubject("Soggetto");
            document.addKeywords("Parole chiave");
            //sarebbe carino secondo me inserire la nostra relazione qui
            document.add(new Paragraph("Cristoforo Colombo, also known as Christopher Columbus, was an Italian explorer and navigator who completed four voyages across the Atlantic Ocean, opening the way for the widespread European exploration and colonization of the Americas.\n"));
            document.add(new Paragraph("Colombo was born in Genoa, Italy, in 1451. As a young man, he became a sailor and began making trips to the Mediterranean, eventually becoming a skilled navigator and cartographer.\n\n\n"));
            //inserisco una tabella per grafica
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.25f, 0.5f, 0.25f});
            String[] header = {"Header 1", "Header 2", "Header 3"};
            for (String s : header) {
                table.addCell(s);
            }
            for (int i = 0; i < 10; i++) {
                table.addCell("Cell " + i);
                table.addCell("Cell " + i);
                table.addCell("Cell " + i);
            }
            document.add(table);
            document.add(new Paragraph("In 1477, he moved to Lisbon, Portugal, where he began seeking funding for his proposed voyage across the Atlantic. He made several unsuccessful attempts to secure sponsorship from the Portuguese court before eventually receiving backing from King Ferdinand and Queen Isabella of Spain in 1492.\n"));
            document.add(new Paragraph("With three ships, the Niña, the Pinta and the Santa Maria, Columbus set sail on August 3, 1492. After a difficult voyage, he landed in the Bahamas on October 12, 1492. He made three more voyages to the Americas, but he never set foot on mainland North America.\n"));
            document.add(new Paragraph("Despite the fact that Columbus did not actually discover the Americas, as they were already inhabited by indigenous peoples, his voyages did open the way for the widespread exploration and colonization of the Americas by Europeans, leading to the displacement and mistreatment of the native populations.\n"));
            document.add(new Paragraph("Today, Columbus is celebrated as a hero by some and criticized by others for his treatment of indigenous peoples. His legacy is a complex one and is still debated by historians and scholars.\n"));
            document.newPage();
            document.add(new Paragraph("Cristoforo Colombo, also known as Christopher Columbus, was an Italian explorer and navigator who completed four voyages across the Atlantic Ocean, opening the way for the widespread European exploration and colonization of the Americas.\n"));
            document.add(new Paragraph("Colombo was born in Genoa, Italy, in 1451. As a young man, he became a sailor and began making trips to the Mediterranean, eventually becoming a skilled navigator and cartographer.\n\n\n"));
            //inserisco una tabella per grafica
            PdfPTable table1 = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.25f, 0.5f, 0.25f});
            String[] header1 = {"Header 1", "Header 2", "Header 3"};
            for (String s : header1) {
                table1.addCell(s);
            }
            for (int i = 0; i < 10; i++) {
                table1.addCell("Cell " + i);
                table1.addCell("Cell " + i);
                table1.addCell("Cell " + i);
            }
            document.add(table1);
            document.add(new Paragraph("In 1477, he moved to Lisbon, Portugal, where he began seeking funding for his proposed voyage across the Atlantic. He made several unsuccessful attempts to secure sponsorship from the Portuguese court before eventually receiving backing from King Ferdinand and Queen Isabella of Spain in 1492.\n"));
            document.add(new Paragraph("With three ships, the Niña, the Pinta and the Santa Maria, Columbus set sail on August 3, 1492. After a difficult voyage, he landed in the Bahamas on October 12, 1492. He made three more voyages to the Americas, but he never set foot on mainland North America.\n"));
            document.add(new Paragraph("Despite the fact that Columbus did not actually discover the Americas, as they were already inhabited by indigenous peoples, his voyages did open the way for the widespread exploration and colonization of the Americas by Europeans, leading to the displacement and mistreatment of the native populations.\n"));
            document.add(new Paragraph("Today, Columbus is celebrated as a hero by some and criticized by others for his treatment of indigenous peoples. His legacy is a complex one and is still debated by historians and scholars.\n"));
            document.newPage();
            document.add(new Paragraph("Cristoforo Colombo, also known as Christopher Columbus, was an Italian explorer and navigator who completed four voyages across the Atlantic Ocean, opening the way for the widespread European exploration and colonization of the Americas.\n"));
            document.add(new Paragraph("Colombo was born in Genoa, Italy, in 1451. As a young man, he became a sailor and began making trips to the Mediterranean, eventually becoming a skilled navigator and cartographer.\n\n\n"));
            //inserisco una tabella per grafica
            PdfPTable table2 = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.25f, 0.5f, 0.25f});
            String[] header2 = {"Header 1", "Header 2", "Header 3"};
            for (String s : header2) {
                table2.addCell(s);
            }
            for (int i = 0; i < 10; i++) {
                table2.addCell("Cell " + i);
                table2.addCell("Cell " + i);
                table2.addCell("Cell " + i);
            }
            document.add(table2);
            document.add(new Paragraph("In 1477, he moved to Lisbon, Portugal, where he began seeking funding for his proposed voyage across the Atlantic. He made several unsuccessful attempts to secure sponsorship from the Portuguese court before eventually receiving backing from King Ferdinand and Queen Isabella of Spain in 1492.\n"));
            document.add(new Paragraph("With three ships, the Niña, the Pinta and the Santa Maria, Columbus set sail on August 3, 1492. After a difficult voyage, he landed in the Bahamas on October 12, 1492. He made three more voyages to the Americas, but he never set foot on mainland North America.\n"));
            document.add(new Paragraph("Despite the fact that Columbus did not actually discover the Americas, as they were already inhabited by indigenous peoples, his voyages did open the way for the widespread exploration and colonization of the Americas by Europeans, leading to the displacement and mistreatment of the native populations.\n"));
            document.add(new Paragraph("Today, Columbus is celebrated as a hero by some and criticized by others for his treatment of indigenous peoples. His legacy is a complex one and is still debated by historians and scholars.\n"));
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
