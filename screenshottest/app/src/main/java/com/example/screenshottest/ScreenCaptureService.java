package com.example.screenshottest;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.view.WindowManager;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.ImageFormat;



import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.graphics.ImageFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenCaptureService extends Service {

    private MediaProjection mediaProjection;
    private MediaProjectionManager mediaProjectionManager;
    private ImageReader imageReader;
    private int resultCode;
    private Intent resultData;
    private boolean isCapturing = false;

    private static final String TAG = "ScreenCaptureService";

    public static final String EXTRA_RESULT_CODE = "extra_result_code";
    public static final String EXTRA_RESULT_DATA = "extra_result_data";

    @Override
    public void onCreate() {
        super.onCreate();
        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("START_CAPTURE".equals(action) && !isCapturing) {
                resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
                resultData = intent.getParcelableExtra(EXTRA_RESULT_DATA);
                startScreenCapture();
            }
        }
        return START_STICKY;
    }

    public void startScreenCapture() {
        Log.d(TAG, "=========================================startScreenCapture: " +
                "Started==============================================");
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        imageReader = ImageReader.newInstance(width, height, ImageFormat.RGB_565, 2);
        mediaProjection.createVirtualDisplay(
                "ScreenCapture",
                width,
                height,
                metrics.densityDpi,
                android.hardware.display.DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                null,
                null
        );

        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireLatestImage();
                if (image != null) {
                    Bitmap bitmap = imageToBitmap(image);
                    saveBitmap(bitmap);
                    image.close();
                }
            }
        }, new Handler(Looper.getMainLooper()));

        isCapturing = true;

        Log.d(TAG, "=======================================startScreenCapture: " +
                "Completed==================================");
    }

    private Bitmap imageToBitmap(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        int width = image.getWidth();
        int height = image.getHeight();
        return Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    }

    private void saveBitmap(Bitmap bitmap) {
        String fileName = "Screenshot_" + System.currentTimeMillis() + ".png";
        try {
            FileOutputStream out = new FileOutputStream(getExternalFilesDir(null) + "/" + fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
        if (imageReader != null) {
            imageReader.close();
        }
        isCapturing = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
