// Copyright 2018 Google LLC
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
package com.linktag.linkapp.FaceDetectionUtil;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.linktag.linkapp.FaceDetectionUtil.common.CameraImageGraphic;
import com.linktag.linkapp.FaceDetectionUtil.common.FrameMetadata;
import com.linktag.linkapp.FaceDetectionUtil.common.GraphicOverlay;
import com.linktag.linkapp.ui.main.Main;
import com.linktag.base.user_interface.InterfaceUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Face Detector Demo.
 */

public class FaceDetectionProcessor extends VisionProcessorBase<List<FirebaseVisionFace>> {
    protected InterfaceUser mUser;
    private static final String TAG = "FaceDetectionProcessor";

    private final FirebaseVisionFaceDetector detector;

    FaceDetectionResultListener faceDetectionResultListener;


    public FaceDetectionProcessor() {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setMinFaceSize(0.8f)
                        .enableTracking()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
    }

    public FaceDetectionProcessor(FirebaseVisionFaceDetector detector) {
        this.detector=detector;
    }

    public FaceDetectionResultListener getFaceDetectionResultListener() {
        return faceDetectionResultListener;
    }

    public void setFaceDetectionResultListener(FaceDetectionResultListener faceDetectionResultListener) {
        this.faceDetectionResultListener = faceDetectionResultListener;
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionFace>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage, @NonNull List<FirebaseVisionFace> faces,  @NonNull FrameMetadata frameMetadata,  @NonNull GraphicOverlay graphicOverlay) {
            graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }

        for (int i = 0; i < faces.size(); ++i) {
            FirebaseVisionFace face = faces.get(i);
            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees  (좌우)
            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
            int id = 0;
            int cameraFacing =  frameMetadata != null ? frameMetadata.getCameraFacing() : Camera.CameraInfo.CAMERA_FACING_BACK;
            FaceGraphic faceGraphic = new FaceGraphic(graphicOverlay, face, cameraFacing);
            graphicOverlay.add(faceGraphic);  // #012
            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {  id = face.getTrackingId();
                }

            if((face.getBoundingBox().right - face.getBoundingBox().left >= 85)&& (face.getBoundingBox().bottom - face.getBoundingBox().top >= 85)){  // 피사체가 너무 멀면 사진 안찍게
                if((face.getBoundingBox().right - face.getBoundingBox().left <= 990)&&(face.getBoundingBox().bottom - face.getBoundingBox().top <= 990)) {  //피사체가 너무 가까우면 사진 안찍게

                    // ML Kit 휴먼 보정값적용
                    if (FirebaseVisionFaceLandmark.LEFT_EAR == 3 && FirebaseVisionFaceLandmark.RIGHT_EAR == 9 && FirebaseVisionFaceLandmark.LEFT_EYE == 4 && FirebaseVisionFaceLandmark.RIGHT_EYE == 10 &&
                            FirebaseVisionFaceLandmark.NOSE_BASE == 6 && FirebaseVisionFaceLandmark.LEFT_CHEEK == 1 && FirebaseVisionFaceLandmark.RIGHT_CHEEK == 7 && FirebaseVisionFaceLandmark.MOUTH_BOTTOM == 0 &&
                            FirebaseVisionFaceLandmark.MOUTH_LEFT == 5 && FirebaseVisionFaceLandmark.MOUTH_RIGHT == 11 && FirebaseVisionFaceLandmark.LEFT_EYE == 4 && FirebaseVisionFaceLandmark.RIGHT_EYE == 10) {

                        // 눈을 뜨고 있을때 감지
                        if (face.getLeftEyeOpenProbability() >= 0.3 && face.getRightEyeOpenProbability() >= 0.3) {

                            // 정면을 바라볼 때 촬영
                            if ((rotZ > -5 && rotZ < 5) && (rotY > -5 && rotY < 5)) {
                              //  ((Main) Main.mContext).onViewClicked();    // #999
                            }
                        }
                    }
                }
            }
        }
        graphicOverlay.postInvalidate();
        if(faceDetectionResultListener!=null)
            faceDetectionResultListener.onSuccess(originalCameraImage,faces,frameMetadata,graphicOverlay);
    }

    @Override
    protected void onFailure(@NonNull Exception e) {

        if(faceDetectionResultListener!=null)
            faceDetectionResultListener.onFailure(e);

        Log.e(TAG, "Face detection failed " + e);

    }
}
