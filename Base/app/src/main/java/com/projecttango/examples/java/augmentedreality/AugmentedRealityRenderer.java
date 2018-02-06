/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.projecttango.examples.java.augmentedreality;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import com.google.atap.tangoservice.TangoPoseData;
import com.google.tango.support.TangoSupport;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.ScreenQuad;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

/**
 * Renderer that implements a basic augmented reality scene using Rajawali.
 * It creates a scene with a background quad taking the whole screen, where the color camera is
 * rendered and a sphere with the texture of the earth floats ahead of the start position of
 * the Tango device.
 */
public class AugmentedRealityRenderer extends Renderer implements OnObjectPickedListener {
    private static final String TAG = AugmentedRealityRenderer.class.getSimpleName();

    private float[] textureCoords0 = new float[]{0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F};

    // Rajawali texture used to render the Tango color camera.
    private ATexture mTangoCameraTexture;

    // Keeps track of whether the scene camera has been configured.
    private boolean mSceneCameraConfigured;
    public boolean isPicked;

    private ScreenQuad mBackgroundQuad;

    private ObjectColorPicker mOnePicker;

    private AugmentedRealityActivity _augmentedRealityActivity;

    public AugmentedRealityRenderer(Context context, AugmentedRealityActivity augmentedRealityActivity) {
        super(context);
        _augmentedRealityActivity = augmentedRealityActivity;
    }

    @Override
    protected void initScene() {
        mOnePicker = new ObjectColorPicker(this);
        mOnePicker.setOnObjectPickedListener(this);

        // Create a quad covering the whole background and assign a texture to it where the
        // Tango color camera contents will be rendered.
        Material tangoCameraMaterial = new Material();
        tangoCameraMaterial.setColorInfluence(0);

        if (mBackgroundQuad == null) {
            mBackgroundQuad = new ScreenQuad();
            mBackgroundQuad.getGeometry().setTextureCoords(textureCoords0);
        }
        // We need to use Rajawali's {@code StreamingTexture} since it sets up the texture
        // for GL_TEXTURE_EXTERNAL_OES rendering.
        mTangoCameraTexture =
                new StreamingTexture("camera", (StreamingTexture.ISurfaceListener) null);
        try {
            tangoCameraMaterial.addTexture(mTangoCameraTexture);
            mBackgroundQuad.setMaterial(tangoCameraMaterial);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception creating texture for RGB camera contents", e);
        }
        getCurrentScene().addChildAt(mBackgroundQuad, 0);

        // Add a directional light in an arbitrary direction.
        DirectionalLight light = new DirectionalLight(1, 0.2, -1);
        light.setColor(1, 1, 1);
        light.setPower(0.8f);
        light.setPosition(3, 2, 4);
        getCurrentScene().addLight(light);

        // Create sphere with earth (or another planet) texture and place it in space 3m forward from the origin.
        Material earthMaterial = new Material();
        Integer sphereMap = MainActivity.sphereMap;
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0,R.drawable.mercury);
        map.put(1,R.drawable.venus);
        map.put(2,R.drawable.earth);
        map.put(3,R.drawable.mars);
        map.put(4,R.drawable.jupiter);
        map.put(5,R.drawable.saturn);
        map.put(6,R.drawable.uranus);
        map.put(7,R.drawable.neptune);
        try {
            Texture t = new Texture("earth", map.get(sphereMap));
            earthMaterial.addTexture(t);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception generating earth texture", e);
        }
        earthMaterial.setColorInfluence(0);
        earthMaterial.enableLighting(true);
        earthMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        // Change the size of the planet
        Integer sphereSize = MainActivity.sphereSize;
        Object3D earth = new Sphere(sphereSize*0.008f, 20, 20);
        earth.setMaterial(earthMaterial);
        earth.setPosition(0, 0, -3);
        getCurrentScene().addChild(earth);

        // Rotate around its Y axis
        Animation3D animEarth = new RotateOnAxisAnimation(Vector3.Axis.Y, 0, -360);
        animEarth.setInterpolator(new LinearInterpolator());
        animEarth.setDurationMilliseconds(60000);
        animEarth.setRepeatMode(Animation.RepeatMode.INFINITE);
        animEarth.setTransformable3D(earth);
        getCurrentScene().registerAnimation(animEarth);
        animEarth.play();

        mOnePicker.registerObject(earth);
        mOnePicker.registerObject(mBackgroundQuad);
    }

    /**
     * Update background texture's UV coordinates when device orientation is changed (i.e., change
     * between landscape and portrait mode).
     * This must be run in the OpenGL thread.
     */
    public void updateColorCameraTextureUvGlThread(int rotation) {
        if (mBackgroundQuad == null) {
            mBackgroundQuad = new ScreenQuad();
        }

        float[] textureCoords =
                TangoSupport.getVideoOverlayUVBasedOnDisplayRotation(textureCoords0, rotation);
        mBackgroundQuad.getGeometry().setTextureCoords(textureCoords, true);
        mBackgroundQuad.getGeometry().reload();
    }

    /**
     * Update the scene camera based on the provided pose in Tango start of service frame.
     * The camera pose should match the pose of the camera color at the time of the last rendered
     * RGB frame, which can be retrieved with this.getTimestamp();
     * <p/>
     * NOTE: This must be called from the OpenGL render thread; it is not thread-safe.
     */
    public void updateRenderCameraPose(TangoPoseData cameraPose) {
        float[] rotation = cameraPose.getRotationAsFloats();
        float[] translation = cameraPose.getTranslationAsFloats();
        Quaternion quaternion = new Quaternion(rotation[3], rotation[0], rotation[1], rotation[2]);
        // Conjugating the Quaternion is needed because Rajawali uses left-handed convention for
        // quaternions.
        getCurrentCamera().setRotation(quaternion.conjugate());
        getCurrentCamera().setPosition(translation[0], translation[1], translation[2]);
    }

    /**
     * It returns the ID currently assigned to the texture where the Tango color camera contents
     * should be rendered.
     * NOTE: This must be called from the OpenGL render thread; it is not thread-safe.
     */
    public int getTextureId() {
        return mTangoCameraTexture == null ? -1 : mTangoCameraTexture.getTextureId();
    }

    /**
     * We need to override this method to mark the camera for re-configuration (set proper
     * projection matrix) since it will be reset by Rajawali on surface changes.
     */
    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        mSceneCameraConfigured = false;
    }

    public boolean isSceneCameraConfigured() {
        return mSceneCameraConfigured;
    }

    /**
     * Sets the projection matrix for the scene camera to match the parameters of the color camera,
     * provided by the {@code TangoCameraIntrinsics}.
     */
    public void setProjectionMatrix(float[] matrixFloats) {
        getCurrentCamera().setProjectionMatrix(new Matrix4(matrixFloats));
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset,
                                 float xOffsetStep, float yOffsetStep,
                                 int xPixelOffset, int yPixelOffset) {
    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        public void run() {
            Log.i(TAG, "Long press!");
            longPress = true;
        }
    };

    static boolean longPress = false;
    boolean mBooleanIsPressed = false;
    float pointerDownX = 0;
    float pointerDownY = 0;
    float pointerUpX = 0;
    float pointerUpY = 0;

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Pick object attempt");
            handler.postDelayed(runnable, 1000);
            mBooleanIsPressed = true;

            pointerDownX = event.getX();
            pointerDownY = event.getY();

        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            mOnePicker.getObjectAt(event.getX(), event.getY());

            pointerUpX = event.getX();
            pointerUpY = event.getY();

            if(mBooleanIsPressed) {
                mBooleanIsPressed = false;
                handler.removeCallbacks(runnable);
            }
        }
        //Log.d("msg", "Detected: " + pointerDownX + "|" + pointerDownY + "|" + pointerUpX + "|" + pointerUpY);

    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        Log.d(TAG, "Picked object: " + object.toString());
        if(object.toString().contains("Sphere")){
            if (longPress) {
                longPress = false;

                _augmentedRealityActivity.sendSpeech();
                Log.d(TAG,"Voice assistant here");
            }
            else {
                Log.d(TAG,"Spotify here");

                //TODO: simple gesture detection

                if(!AugmentedRealityActivity.isLoggedIn()){
                    //Do nothing if user didn't log in yet
                }
                else {

                    if (Math.abs(pointerDownX - pointerUpX) > Math.abs(pointerDownY - pointerUpY)) {
                        if (pointerDownX < pointerUpX) {
                            //right
                            Log.d("TOUCH", "RIGHT");
                            AugmentedRealityActivity.mPlayer.skipToNext(AugmentedRealityActivity.mOperationCallback);
                        } else if (pointerDownX > pointerUpX) {
                            //left
                            Log.d("TOUCH", "LEFT");
                            AugmentedRealityActivity.mPlayer.skipToPrevious(AugmentedRealityActivity.mOperationCallback);
                        }
                    } else {
                        if (pointerDownY < pointerUpY) {
                            AugmentedRealityActivity.mPlayer.setRepeat(AugmentedRealityActivity.mOperationCallback, true);
                            Log.d("TOUCH", "DOWN");
                        } else if (pointerDownY > pointerUpY) {
                            AugmentedRealityActivity.mPlayer.setShuffle(AugmentedRealityActivity.mOperationCallback, true);
                            Log.d("TOUCH", "UP");
                        }
                    }

                    // If the sphere is clicked first time after login the playlist will be played after that each click will be resume/pause
                    if (AugmentedRealityActivity.FirstTimeClicked) {
                        AugmentedRealityActivity.mPlayer.playUri(AugmentedRealityActivity.mOperationCallback, "spotify:user:spotify:playlist:37i9dQZF1DWWxPM4nWdhyI", 0, 0);
                        AugmentedRealityActivity.FirstTimeClicked = false;
                    } else if (AugmentedRealityActivity.mCurrentPlaybackState != null && AugmentedRealityActivity.mCurrentPlaybackState.isPlaying) {
                        AugmentedRealityActivity.mPlayer.pause(AugmentedRealityActivity.mOperationCallback);
                    } else {
                        AugmentedRealityActivity.mPlayer.resume(AugmentedRealityActivity.mOperationCallback);
                    }

                }
            }
        }
    }

    @Override
    public void onNoObjectPicked() {
        Log.d(TAG, "Picked no object");
    }


}
