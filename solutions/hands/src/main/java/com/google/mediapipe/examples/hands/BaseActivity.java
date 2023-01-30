package com.google.mediapipe.examples.hands;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;
import com.hands.gesture.CrabGesture;
import com.hands.gesture.FourGesture;
import com.hands.gesture.PinchGesture;
import com.hands.gesture.ScrollPageGesture;
import com.hands.gesture.ThreeGesture;
import com.hands.gesture.ThumbUpGesture;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;

public class BaseActivity extends AppCompatActivity {

    protected Hands hands;

    // Run the pipeline and the model inference on GPU or CPU.
    protected static final boolean RUN_ON_GPU = true;

    protected enum InputSource {
        UNKNOWN,
        CAMERA,
    }
    protected InputSource inputSource = InputSource.UNKNOWN;
    // Live camera demo UI and camera components.
    protected CameraInput cameraInput;

    protected SolutionGlSurfaceView<HandsResult> glSurfaceView;
    protected HandsResultGlRenderer handsResultGlRenderer;

    protected Button btn;
    protected File pdfFile;

    // Gestures
    protected PinchGesture pinchGesture = new PinchGesture();
    protected ThumbUpGesture thumbUpGesture = new ThumbUpGesture();
    protected CrabGesture crabGesture = new CrabGesture();
    protected FourGesture fourGesture = new FourGesture();
    protected ThreeGesture threeGesture = new ThreeGesture();
    protected ScrollPageGesture scrollPageGesture = new ScrollPageGesture();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (inputSource == MainActivity.InputSource.CAMERA) {
            // Restarts the camera and the opengl surface rendering.
            cameraInput = new CameraInput(this);
            cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
            glSurfaceView.post(this::startCamera);
            glSurfaceView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (inputSource == MainActivity.InputSource.CAMERA) {
            glSurfaceView.setVisibility(View.GONE);
            cameraInput.close();
        }
    }

    protected void setupLiveDemoUiComponents() {
        if (inputSource == InputSource.CAMERA) {
            return;
        }
        stopCurrentPipeline();

    }

    protected void startCamera() {
        cameraInput.start(
                this,
                hands.getGlContext(),
                CameraInput.CameraFacing.FRONT,
                glSurfaceView.getWidth(),
                glSurfaceView.getHeight());
    }

    protected void stopCurrentPipeline() {
        if (cameraInput != null) {
            cameraInput.setNewFrameListener(null);
            cameraInput.close();
        }
        if (glSurfaceView != null) {
            glSurfaceView.setVisibility(View.GONE);
        }
        if (hands != null) {
            hands.close();
        }
    }

    protected void firstSetupStreamingModePipeline(InputSource inputSource) {

        handsResultGlRenderer = new HandsResultGlRenderer();

        this.inputSource = inputSource;
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
        }

        // Initializes a new Gl surface view with a user-defined HandsResultGlRenderer.
        glSurfaceView =
                new SolutionGlSurfaceView<>(this, hands.getGlContext(), hands.getGlMajorVersion());
        glSurfaceView.setSolutionResultRenderer(handsResultGlRenderer);
        glSurfaceView.setRenderInputImage(true);

    }

    protected void lastSetupStreamingModePipeline(InputSource inputSource, LinearLayout gestureChecksLayout) {
        // The runnable to start camera after the gl surface view is attached.
        // For video input source, videoInput.start() will be called when the video uri is available.
        if (inputSource == InputSource.CAMERA) {
            glSurfaceView.post(this::startCamera);
        }

        // Updates the preview layout.
        FrameLayout frameLayout;
        if (gestureChecksLayout != null){
            frameLayout = findViewById(R.id.preview_display_layout);
            frameLayout.removeAllViewsInLayout();
            frameLayout.addView(glSurfaceView);
            // add linear layout view to frame layout
            frameLayout.addView(gestureChecksLayout);
        }
        else{
            frameLayout = findViewById(R.id.frame_layout);
            frameLayout.removeAllViewsInLayout();
            frameLayout.addView(glSurfaceView);
        }

        glSurfaceView.setVisibility(View.VISIBLE);
        frameLayout.requestLayout();
    }

}
