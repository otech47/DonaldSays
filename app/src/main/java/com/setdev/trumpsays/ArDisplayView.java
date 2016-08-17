package com.setdev.trumpsays;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jaredrummler.android.device.DeviceName;

import java.io.IOException;
import java.util.List;

/**
 * Created by oscarlafarga on 7/30/16.
 */
public class ArDisplayView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "ArDisplayView";

    private Context context;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    public ArDisplayView(Context context) {
        super(context);

        this.context = context;
        surfaceHolder = getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open();

            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
            Log.d(TAG, DeviceName.getDeviceName());
            if(DeviceName.getDeviceName().equals("Google Nexus 5")) {
                camera.setDisplayOrientation(270);
            } else {
                camera.setDisplayOrientation(90);
            }
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            Log.e(TAG, "surfaceCreated exception: ", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> prevSizes = params.getSupportedPreviewSizes();
        for (Camera.Size s : prevSizes)
        {
            if((s.height <= height) && (s.width <= width))
            {
                params.setPreviewSize(s.width, s.height);
                break;
            }
        }

        camera.setParameters(params);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
    }
}
