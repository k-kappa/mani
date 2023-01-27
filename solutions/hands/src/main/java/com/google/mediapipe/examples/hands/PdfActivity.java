package com.google.mediapipe.examples.hands;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutioncore.VideoInput;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;
import com.hands.gesture.PinchGesture;
import com.hands.gesture.ThumbUpGesture;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

// per relazione ricordarsi di aggiungere le dipendenze -> iText per creare il pdf e pdfviewer per visualizzarlo

public class PdfActivity extends AppCompatActivity {

    Button zoomIn, zoomOut, share, scrollDown;
    private Hands hands;
    private VideoInput videoInput;
    private CameraInput cameraInput;
    private File pdfFile;
    private PDFView pdfView;

    PinchGesture pinchGesture = new PinchGesture();
    ThumbUpGesture thumbUpGesture = new ThumbUpGesture();
    long lastExecutionTime = 0;

    private SolutionGlSurfaceView<HandsResult> glSurfaceView;

    // Run the pipeline and the model inference on GPU or CPU.
    private static final boolean RUN_ON_GPU = true;

    private enum InputSource {
        UNKNOWN,
        IMAGE,
        VIDEO,
        CAMERA,
    }

    private ActivityResultLauncher<Intent> imageGetter;
    private InputSource inputSource2 = InputSource.UNKNOWN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        setupLiveDemoUiComponents();

        //inizializzazione bottoni da eliminare quando colleghiamo le gesture
        zoomIn = findViewById(R.id.zoom_in);
        zoomOut = findViewById(R.id.zoom_out);
        share = findViewById(R.id.condividi);
        scrollDown = findViewById(R.id.scroll_down);

        pdfView = (PDFView) findViewById(R.id.pdf_viewer);
        final LinearLayout buttonBar = findViewById(R.id.button_bar);

        //caricamento del pdf
        //pdfFile = new File(getFilesDir(), "documento.pdf");
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
        scrollDown.setOnClickListener(v -> pdfView.jumpTo(pdfView.getPageAtPositionOffset(0), true));


    }

    /*
        FUNZIONI PER MESSAGGIO E CREAZIONE PDF
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void createPDF(String documento) {
        pdfFile = new File(getFilesDir(), documento);
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

    //METODI ATTIVAZIONE PIPELINE CAMERA
    private void stopCurrentPipeline() {
        if (cameraInput != null) {
            cameraInput.setNewFrameListener(null);
            cameraInput.close();
        }
        if (videoInput != null) {
            videoInput.setNewFrameListener(null);
            videoInput.close();
        }
        if (glSurfaceView != null) {
            glSurfaceView.setVisibility(View.GONE);
        }
        if (hands != null) {
            hands.close();
        }
    }

    private void setupStreamingModePipeline(InputSource inputSource) {
        HandsResultGlRenderer handsResultGlRenderer = new HandsResultGlRenderer();

        this.inputSource2 = inputSource;
        // Initializes a new MediaPipe Hands solution instance in the streaming mode.
        hands =
                new Hands(
                        this,
                        HandsOptions.builder()
                                .setStaticImageMode(false)
                                .setMaxNumHands(2)
                                .setRunOnGpu(RUN_ON_GPU)
                                .build());

        if (inputSource == InputSource.CAMERA) {
            cameraInput = new CameraInput(this);
            cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
        } else if (inputSource == inputSource.VIDEO) {
            videoInput = new VideoInput(this);
            videoInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
        }

        // Initializes a new Gl surface view with a user-defined HandsResultGlRenderer.
        glSurfaceView =
                new SolutionGlSurfaceView<>(this, hands.getGlContext(), hands.getGlMajorVersion());
        glSurfaceView.setSolutionResultRenderer(handsResultGlRenderer);
        glSurfaceView.setRenderInputImage(true);
        hands.setResultListener(
                handsResult -> {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            boolean checkPinch = pinchGesture.checkGesture(handsResult);
                            boolean checkThumbUp = thumbUpGesture.checkGesture(handsResult);

                            if (checkPinch && !checkThumbUp) {
                                //zoom 1 - 3, pinch 10 - 50
                                Log.d("GESTURE", "Pinch distance = " + pinchGesture.getPinchDistance(handsResult.multiHandWorldLandmarks()));
                                //pdfView.zoomTo(pinchGesture.getPinchDistance(handsResult.multiHandWorldLandmarks()));
                                pdfView.zoomWithAnimation(pinchGesture.getPinchDistance(handsResult.multiHandWorldLandmarks()));
                            }

                            if (checkThumbUp && (System.currentTimeMillis() - lastExecutionTime) > 5000) {
                                lastExecutionTime = System.currentTimeMillis();
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setType("text/plain");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"destinatario@example.com"});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Oggetto email");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body email");
                                emailIntent.putExtra(Intent.EXTRA_STREAM, pdfFile);
                                startActivity(Intent.createChooser(emailIntent, "Invia email..."));
                            }
                        }
                    });
                    glSurfaceView.setRenderData(handsResult);
                    glSurfaceView.requestRender();
                });


        // The runnable to start camera after the gl surface view is attached.
        // For video input source, videoInput.start() will be called when the video uri is available.
        if (inputSource == InputSource.CAMERA) {
            glSurfaceView.post(this::startCamera);
        }

        // Updates the preview layout.
        FrameLayout frameLayout = findViewById(R.id.frame_layout);
        //imageView.setVisibility(View.GONE);
        frameLayout.removeAllViewsInLayout();
        frameLayout.addView(glSurfaceView);

        glSurfaceView.setVisibility(View.VISIBLE);
        frameLayout.requestLayout();
    }

    private void startCamera() {
        cameraInput.start(
                this,
                hands.getGlContext(),
                CameraInput.CameraFacing.FRONT,
                glSurfaceView.getWidth(),
                glSurfaceView.getHeight());
    }

    private void setupLiveDemoUiComponents() {
        if (inputSource2 == InputSource.CAMERA) {
            return;
        }
        stopCurrentPipeline();
        setupStreamingModePipeline(InputSource.CAMERA);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (inputSource2 == InputSource.CAMERA) {
            // Restarts the camera and the opengl surface rendering.
            cameraInput = new CameraInput(this);
            cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
            glSurfaceView.post(this::startCamera);
            glSurfaceView.setVisibility(View.VISIBLE);
        } else if (inputSource2 == InputSource.VIDEO) {
            videoInput.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (inputSource2 == InputSource.CAMERA) {
            glSurfaceView.setVisibility(View.GONE);
            cameraInput.close();
        } else if (inputSource2 == InputSource.VIDEO) {
            videoInput.pause();
        }
    }
}
