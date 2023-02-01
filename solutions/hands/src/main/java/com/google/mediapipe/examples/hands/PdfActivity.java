package com.google.mediapipe.examples.hands;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.net.URISyntaxException;

// per relazione ricordarsi di aggiungere le dipendenze -> iText per creare il pdf e pdfviewer per visualizzarlo

public class PdfActivity extends BaseActivity {

    Button zoomIn, zoomOut, share, scrollDown;
    private File pdfFile;
    private PDFView pdfView;
    int a;

    //AlertDialog
    private AlertDialog dialog;
    private View dialogView;
    private Button pdf1, pdf2, pdf3;

    long lastExecutionTime = 0;
    long lastExecutionTime2 = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        this.setupLiveDemoUiComponents();

        //inizializzazione bottoni da eliminare quando colleghiamo le gesture
        zoomIn = findViewById(R.id.zoom_in);
        zoomOut = findViewById(R.id.zoom_out);
        share = findViewById(R.id.condividi);
        scrollDown = findViewById(R.id.scroll_down);

        pdfView = findViewById(R.id.pdf_viewer);
        final LinearLayout buttonBar = findViewById(R.id.button_bar);

        //caricamento del pdf
        pdfFile = new File(getFilesDir(), "documento1.pdf");
        pdfView.fromFile(pdfFile)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();
        pdfView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .start();


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

        showToast("Effettuare una gesture");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    // METODI PER L'ATTIVAZIONE DELLA PIPELINE IN MODALITA' PDF

    private void setupStreamingModePipeline(InputSource inputSource) {

        super.firstSetupStreamingModePipeline(inputSource);

        hands.setResultListener(
                handsResult -> {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int direzione_gesture = scrollPageGesture.checkGesture(handsResult.multiHandLandmarks());
                            boolean checkPinch = pinchGesture.checkGesture(handsResult);
                            boolean checkThumbUp = thumbUpGesture.checkGesture(handsResult);
                            boolean checkCrab = crabGesture.checkGesture(handsResult);
                            boolean checkOpenHand = fourGesture.checkGesture(handsResult);
                            boolean check3Hand = threeGesture.checkGesture(handsResult);

                            if (checkPinch && !checkThumbUp && direzione_gesture <= 0) {
                                //zoom 1 - 3, pinch 10 - 50
                                Log.d("GESTURE", "Pinch distance = " + pinchGesture.getPinchDistance(handsResult.multiHandWorldLandmarks()));
                                //pdfView.zoomTo(pinchGesture.getPinchDistance(handsResult.multiHandWorldLandmarks()));
                                pdfView.zoomWithAnimation(pinchGesture.getPinchDistance(handsResult.multiHandWorldLandmarks()));
                            }

                            if (checkThumbUp && (System.currentTimeMillis() - lastExecutionTime) > 5000) {

                                lastExecutionTime = System.currentTimeMillis();
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setType("application/pdf");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"destinatario@example.com"});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Oggetto email");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body email");
                                try {
                                    emailIntent.putExtra(Intent.EXTRA_STREAM, Intent.parseUri(pdfFile.toString(), 0));
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                                startActivity(Intent.createChooser(emailIntent, "Invia email..."));
                                emailIntent.setType("text/plain");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"destinatario@example.com"});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Oggetto email");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body email");
                                emailIntent.putExtra(Intent.EXTRA_STREAM, pdfFile);
                                startActivity(Intent.createChooser(emailIntent, "Invia email..."));
                            }


                            if (checkCrab) {
                                float x = crabGesture.getVettoreX() * 500;
                                float y = crabGesture.getVettoreY() * 500;
                                pdfView.moveRelativeTo(x, y);
                            }

                            if (checkOpenHand) {
                                pdfView.moveRelativeTo(0, -40);
                            }

                            //thumbUpGesture.checkGesture(handsResult.multiHandWorldLandmarks());
                            if (direzione_gesture >= 0 && !checkPinch){
                                if (direzione_gesture==1){

                                    pdfView.jumpTo(pdfView.getCurrentPage() +1, true);
                                } else if (direzione_gesture==2){
                                    pdfView.jumpTo(pdfView.getCurrentPage() -1, true);
                                }
                            }


                            if (check3Hand && (System.currentTimeMillis() - lastExecutionTime2) > 5000) {
                                lastExecutionTime2 = System.currentTimeMillis();
                                showToast("Scegli il pdf");
                                LayoutInflater inflater = getLayoutInflater();
                                dialogView = inflater.inflate(R.layout.scegli_dialog, null);

                                //inizializzazione
                                pdf1 = dialogView.findViewById(R.id.btn1);
                                pdf2 = dialogView.findViewById(R.id.btn2);
                                pdf3 = dialogView.findViewById(R.id.btn3);

                                //Create Alert Dialog per custom layout
                                AlertDialog.Builder builder = new AlertDialog.Builder(PdfActivity.this);
                                builder.setView(dialogView);
                                builder.setCancelable(true);
                                dialog = builder.create();

                                //setto il listener per i bottoni
                                pdf1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pdfFile = new File(getFilesDir(), "documento.pdf");
                                        pdfView.fromFile(pdfFile)
                                                .enableSwipe(true)
                                                .swipeHorizontal(false)
                                                .enableDoubletap(true)
                                                .defaultPage(0)
                                                .load();
                                        pdfView.animate()
                                                .scaleX(1f)
                                                .scaleY(1f)
                                                .setDuration(1000)
                                                .start();
                                        dialog.dismiss();
                                    }
                                });

                                pdf2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        a = 1;
                                        pdfFile = new File(getFilesDir(), "documento1.pdf");
                                        pdfView.fromFile(pdfFile)
                                                .enableSwipe(true)
                                                .swipeHorizontal(false)
                                                .enableDoubletap(true)
                                                .defaultPage(0)
                                                .load();
                                        pdfView.animate()
                                                .scaleX(1f)
                                                .scaleY(1f)
                                                .setDuration(10000)
                                                .start();
                                        dialog.dismiss();
                                    }
                                });

                                pdf3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        a = 1;
                                        pdfFile = new File(getFilesDir(), "documento2.pdf");
                                        pdfView.fromFile(pdfFile)
                                                .enableSwipe(true)
                                                .swipeHorizontal(false)
                                                .enableDoubletap(true)
                                                .defaultPage(0)
                                                .load();
                                        pdfView.animate()
                                                .scaleX(1f)
                                                .scaleY(1f)
                                                .setDuration(1000)
                                                .start();
                                        dialog.dismiss();
                                    }
                                });

                                //mostro il dialog
                                dialog.show();
                            }

                        }
                    });
                    glSurfaceView.setRenderData(handsResult);
                    glSurfaceView.requestRender();
                });

        super.lastSetupStreamingModePipeline(inputSource, null);
    }

    protected void setupLiveDemoUiComponents() {
        super.setupLiveDemoUiComponents();
        this.setupStreamingModePipeline(InputSource.CAMERA);
    }



    // METODO DI UTILITA'
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}