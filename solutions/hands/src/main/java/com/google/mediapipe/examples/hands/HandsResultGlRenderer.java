// Copyright 2021 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.mediapipe.examples.hands;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.formats.proto.LandmarkProto.Landmark;
import com.google.mediapipe.formats.proto.LandmarkProto.LandmarkList;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.solutioncore.ResultGlRenderer;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsResult;
import com.hands.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;


/**
 * A custom implementation of {@link ResultGlRenderer} to render {@link HandsResult}.
 */
public class HandsResultGlRenderer implements ResultGlRenderer<HandsResult> {
    private static final String TAG = "HandsResultGlRenderer";

    private static final float[] LEFT_HAND_CONNECTION_COLOR = new float[]{0.2f, 1f, 0.2f, 1f};
    private static final float[] RIGHT_HAND_CONNECTION_COLOR = new float[]{1f, 0.2f, 0.2f, 1f};
    private static final float CONNECTION_THICKNESS = 25.0f;
    private static final float[] LEFT_HAND_HOLLOW_CIRCLE_COLOR = new float[]{0.2f, 1f, 0.2f, 1f};
    private static final float[] RIGHT_HAND_HOLLOW_CIRCLE_COLOR = new float[]{1f, 0.2f, 0.2f, 1f};
    private static final float HOLLOW_CIRCLE_RADIUS = 0.01f;
    private static final float[] LEFT_HAND_LANDMARK_COLOR = new float[]{1f, 0.2f, 0.2f, 1f};
    private static final float[] RIGHT_HAND_LANDMARK_COLOR = new float[]{0.2f, 1f, 0.2f, 1f};
    private static final float LANDMARK_RADIUS = 0.008f;
    private static final int NUM_SEGMENTS = 120;
    private static final String VERTEX_SHADER =
            "uniform mat4 uProjectionMatrix;\n"
                    + "attribute vec4 vPosition;\n"
                    + "void main() {\n"
                    + "  gl_Position = uProjectionMatrix * vPosition;\n"
                    + "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n"
                    + "uniform vec4 uColor;\n"
                    + "void main() {\n"
                    + "  gl_FragColor = uColor;\n"
                    + "}";
    private int program;
    private int positionHandle;
    private int projectionMatrixHandle;
    private int colorHandle;

    public String log = "";

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void setupRendering() {
        program = GLES20.glCreateProgram();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        projectionMatrixHandle = GLES20.glGetUniformLocation(program, "uProjectionMatrix");
        colorHandle = GLES20.glGetUniformLocation(program, "uColor");
    }

    @Override
    public void renderResult(HandsResult result, float[] projectionMatrix) {
        if (result == null) {
            return;
        }
        GLES20.glUseProgram(program);
        GLES20.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0);
        GLES20.glLineWidth(CONNECTION_THICKNESS);

        int numHands = result.multiHandLandmarks().size();
        List<LandmarkList> landmarks = result.multiHandWorldLandmarks();
        for (int i = 0; i < numHands; ++i) {
            boolean isLeftHand = result.multiHandedness().get(i).getLabel().equals("Left");
            drawConnections(
                    result.multiHandLandmarks().get(i).getLandmarkList(),
                    isLeftHand ? LEFT_HAND_CONNECTION_COLOR : RIGHT_HAND_CONNECTION_COLOR);
            for (NormalizedLandmark landmark : result.multiHandLandmarks().get(i).getLandmarkList()) {
                // Draws the landmark.
                drawCircle(
                        landmark.getX(),
                        landmark.getY(),
                        isLeftHand ? LEFT_HAND_LANDMARK_COLOR : RIGHT_HAND_LANDMARK_COLOR);
                // Draws a hollow circle around the landmark.
                drawHollowCircle(
                        landmark.getX(),
                        landmark.getY(),
                        isLeftHand ? LEFT_HAND_HOLLOW_CIRCLE_COLOR : RIGHT_HAND_HOLLOW_CIRCLE_COLOR);
                drawDistanceLine(landmarks.get(0).getLandmarkList(), RIGHT_HAND_CONNECTION_COLOR);
            }
            ///////////
            LandmarkProto.Landmark point_4 =landmarks.get(0).getLandmark(4);
            LandmarkProto.Landmark point_5 =landmarks.get(0).getLandmark(5);
            LandmarkProto.Landmark point_6 =landmarks.get(0).getLandmark(6);
            LandmarkProto.Landmark point_7 =landmarks.get(0).getLandmark(7);
            LandmarkProto.Landmark point_8 =landmarks.get(0).getLandmark(8);
            float radius = Utils.getDistance(point_5.getX(),point_5.getY(),point_5.getZ(),point_7.getX(),point_7.getY(),point_7.getZ());
            //////////

            //log = "Thumb - Wrist: " + getDistance(landmarks.get(0).getLandmarkList().get(0), landmarks.get(0).getLandmarkList().get(4)) + "\n";
            //log += "Index - Wrist: " + getDistance(landmarks.get(0).getLandmarkList().get(0), landmarks.get(0).getLandmarkList().get(8)) + "\n";
            //log += "Middle - Wrist: " + getDistance(landmarks.get(0).getLandmarkList().get(0), landmarks.get(0).getLandmarkList().get(12)) + "\n";
            //log += "Ring - Wrist: " + getDistance(landmarks.get(0).getLandmarkList().get(0), landmarks.get(0).getLandmarkList().get(16)) + "\n";
            //log += "Pinky - Wrist: " + getDistance(landmarks.get(0).getLandmarkList().get(0), landmarks.get(0).getLandmarkList().get(20)) + "\n";
            log = "x: "+landmarks.get(0).getLandmark(8).getX()+",y: "+landmarks.get(0).getLandmark(8).getY()+",z: "+landmarks.get(0).getLandmark(8).getZ();
            log += "\n1->: "+ Utils.isInsideSphere(point_5.getX(),point_5.getY(),point_5.getZ(),point_8.getX(),point_8.getY(),point_8.getZ(),radius);//verifico a video come si comportano le variabili booleane per avere un riscontro immediato
            log += "\n2->: "+ !Utils.isInsideSphere(point_4.getX(),point_4.getY(),point_4.getZ(),point_8.getX(),point_8.getY(),point_8.getZ(),radius);
        }

    }

    private String getDistance(Landmark landmark, Landmark landmark1) {
        return this.getDistance(landmark, landmark1, 4);
    }

    private String getDistance(Landmark landmark, Landmark landmark1, int maxDecimalPlaces) {
        double distance = Math.sqrt(Math.pow(landmark.getX() - landmark1.getX(), 2) + Math.pow(landmark.getY() - landmark1.getY(), 2) + Math.pow(landmark.getZ() - landmark1.getZ(), 2));
        return String.format("%." + maxDecimalPlaces + "f", distance);
    }

    //_____________________________________________________
    // nuovo codice test scrive su feed camera
    private void drawDistanceLine(List<Landmark> handLandmarkList, float[] colorArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0);
        Landmark wrist = handLandmarkList.get(0);
        float x = 0.25f;
        float y = 0.05f;
        for (int i = 8; i < 21; i += 4) {
            Landmark finger = handLandmarkList.get(i);
            float distance = (float) Math.sqrt(Math.pow(finger.getX() - wrist.getX(), 2) + Math.pow(finger.getY() - wrist.getY(), 2) + Math.pow(finger.getZ() - wrist.getZ(), 2));
            float[] vertex = {x + (0.05f * ((i - 8) / 4)), y, x + (0.05f * ((i - 8) / 4)), y + distance * 2};
            FloatBuffer vertexBuffer =
                    ByteBuffer.allocateDirect(vertex.length * 4)
                            .order(ByteOrder.nativeOrder())
                            .asFloatBuffer()
                            .put(vertex);
            vertexBuffer.position(0);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
        }
    }
    //_____________________________________________________

    /**
     * Deletes the shader program.
     *
     * <p>This is only necessary if one wants to release the program while keeping the context around.
     */
    public void release() {
        GLES20.glDeleteProgram(program);
    }

    private void drawConnections(List<NormalizedLandmark> handLandmarkList, float[] colorArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0);
        for (Hands.Connection c : Hands.HAND_CONNECTIONS) {
            NormalizedLandmark start = handLandmarkList.get(c.start());
            NormalizedLandmark end = handLandmarkList.get(c.end());
            float[] vertex = {start.getX(), start.getY(), end.getX(), end.getY()};
            FloatBuffer vertexBuffer =
                    ByteBuffer.allocateDirect(vertex.length * 4)
                            .order(ByteOrder.nativeOrder())
                            .asFloatBuffer()
                            .put(vertex);
            vertexBuffer.position(0);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
        }
    }

    private void drawCircle(float x, float y, float[] colorArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0);
        int vertexCount = NUM_SEGMENTS + 2;
        float[] vertices = new float[vertexCount * 3];
        vertices[0] = x;
        vertices[1] = y;
        vertices[2] = 0;
        for (int i = 1; i < vertexCount; i++) {
            float angle = 2.0f * i * (float) Math.PI / NUM_SEGMENTS;
            int currentIndex = 3 * i;
            vertices[currentIndex] = x + (float) (LANDMARK_RADIUS * Math.cos(angle));
            vertices[currentIndex + 1] = y + (float) (LANDMARK_RADIUS * Math.sin(angle));
            vertices[currentIndex + 2] = 0;
        }
        FloatBuffer vertexBuffer =
                ByteBuffer.allocateDirect(vertices.length * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer()
                        .put(vertices);
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
    }

    private void drawHollowCircle(float x, float y, float[] colorArray) {
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0);
        int vertexCount = NUM_SEGMENTS + 1;
        float[] vertices = new float[vertexCount * 3];
        for (int i = 0; i < vertexCount; i++) {
            float angle = 2.0f * i * (float) Math.PI / NUM_SEGMENTS;
            int currentIndex = 3 * i;
            vertices[currentIndex] = x + (float) (HOLLOW_CIRCLE_RADIUS * Math.cos(angle));
            vertices[currentIndex + 1] = y + (float) (HOLLOW_CIRCLE_RADIUS * Math.sin(angle));
            vertices[currentIndex + 2] = 0;
        }
        FloatBuffer vertexBuffer =
                ByteBuffer.allocateDirect(vertices.length * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer()
                        .put(vertices);
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount);
    }
}