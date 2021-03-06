package com.alexaut.kroniax.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class Camera {
    private OrthographicCamera mCamera;
    
    Vector2 mCameraOffset;
    float mRotation;
    
    Vector2 mCacheOffset;
    float mCacheRotation;

    public Camera() {
        mCamera = new OrthographicCamera();
        mCameraOffset = new Vector2(300, 0);
        mRotation = 0;
        
        mCacheOffset = new Vector2(mCameraOffset);
        mCacheRotation = 0;
    }
    
    public void addCheckpoint() {
        mCacheOffset.set(mCameraOffset);
        mCacheRotation = mRotation;
    }
    
    public void resetToCheckpoint() {
        mCameraOffset.set(mCacheOffset);
        mCamera.rotate(mCacheRotation - mRotation);
        mRotation = mCacheRotation;
    }

    public void update(Vector2 mPlayerPosition, boolean fixedCamera) {
        mCamera.position.x = Math.round(mPlayerPosition.x + mCameraOffset.x);
        // If it's a fixed camera always be at 360 (middle of the screen)

        if (!fixedCamera)
            mCamera.position.y = Math.round(mPlayerPosition.y + mCameraOffset.y);
        else
            mCamera.position.y = 360.f;

        mCamera.update();
    }

    public void updateViewport(int width, int height) {
        mCamera.setToOrtho(false, width, height);
        mCamera.update();
    }

    public void changeOffset(float changeX, float changeY) {
        mCameraOffset.add(changeX, changeY);
    }
    
    public void changeRotation(float angle) {
        mRotation += angle;
        mCamera.rotate(angle);
    }

    public OrthographicCamera getOrthoCamera() {
        return mCamera;
    }

    public Matrix4 getTransform() {
        return mCamera.combined;
    }
}
