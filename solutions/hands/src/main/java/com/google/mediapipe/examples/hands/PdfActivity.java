package com.google.mediapipe.examples.hands;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutioncore.VideoInput;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;
import com.hands.gesture.CrabGesture;
import com.hands.gesture.FourGesture;
import com.hands.gesture.PinchGesture;
import com.hands.gesture.ScrollPageGesture;
import com.hands.gesture.ThumbUpGesture;
import com.hands.gesture.ThreeGesture;

import java.io.File;
import java.net.URISyntaxException;

// per relazione ricordarsi di aggiungere le dipendenze -> iText per creare il pdf e pdfviewer per visualizzarlo

public class PdfActivity extends AppCompatActivity {

    Button zoomIn, zoomOut, share, scrollDown;
    private Hands hands;
    private VideoInput videoInput;
    private CameraInput cameraInput;
    private File pdfFile;
    private PDFView pdfView;
    int a;

    //AlertDialog
    private AlertDialog dialog;
    private View dialogView;
    private TextView dialogTitle;
    private Button pdf1, pdf2, pdf3;

    //gestures
    PinchGesture pinchGesture = new PinchGesture();
    ThumbUpGesture thumbUpGesture = new ThumbUpGesture();
    CrabGesture crabGesture = new CrabGesture();
    FourGesture fourGesture = new FourGesture();
    ThreeGesture threeGesture = new ThreeGesture();
    long lastExecutionTime = 0;
    long lastExecutionTime2 = 0;

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
        a = 0;
        if (a == 0) {
            pdfFile = new File(getFilesDir(), "documento1.pdf");
            //System.out.println("PDF FILE: " + pdfFile);
            //showToast("PDF FILE: " + pdfFile);
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
        }


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

    /*
        FUNZIONI PER MESSAGGIO E CREAZIONE PDF
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
        //ThumbUpGesture thumbUpGesture = new ThumbUpGesture();
        ScrollPageGesture scrollPageGesture = new ScrollPageGesture();

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
                            boolean checkCrab = crabGesture.checkGesture(handsResult);
                            boolean checkOpenHand = fourGesture.checkGesture(handsResult);
                            boolean check3Hand = threeGesture.checkGesture(handsResult);

                            if (checkPinch && !checkThumbUp) {
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
                                //showToast("Crab");
                                //Log.println(Log.DEBUG,"debug",x+" "+ y);
                            }

                            if (checkOpenHand) {
                                pdfView.moveRelativeTo(0, -40);
                            }

                            //thumbUpGesture.checkGesture(handsResult.multiHandWorldLandmarks());
                            int direzione_gesture = scrollPageGesture.checGesture(handsResult.multiHandLandmarks());
                            if (direzione_gesture==1){

                                pdfView.jumpTo(pdfView.getCurrentPage() +1, true);
                            } else if (direzione_gesture==2){
                                pdfView.jumpTo(pdfView.getCurrentPage() -1, true);
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
                            /*if (thumbUpGesture.checkGesture(handsResult.multiHandWorldLandmarks())) {
                                pdfView.jumpTo(pdfView.getPageAtPositionOffset(0), true);
                                showToast("thumb up");
                            }*/

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


                            //per scroll in su
                            //pdfView.jumpTo(pdfView.getPageAtPositionOffset(0), true));
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
