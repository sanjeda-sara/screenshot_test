package com.example.screenshottest;

import static com.example.screenshottest.ScreenCaptureService.EXTRA_RESULT_CODE;
import static com.example.screenshottest.ScreenCaptureService.EXTRA_RESULT_DATA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



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
public class MainActivity extends AppCompatActivity {



    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private ImageReader imageReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        Button btnCaptureScreenshot = findViewById(R.id.btnCaptureScreenshot);
        btnCaptureScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestScreenCapturePermission();
            }
        });
    }

    private void startScreenCapture(int resultCode, Intent resultData) {
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
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
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
    }

    private void requestScreenCapturePermission() {
        Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(permissionIntent, REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                startScreenCapture(resultCode, data);
            } else {
                Toast.makeText(this, "Screen capture permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap imageToBitmap(Image image) {
        // Implement your conversion logic from Image to Bitmap
        // This method will depend on the format and characteristics of the Image
        // For example, you can use ByteBuffer to extract pixel data and create a Bitmap
        return null;
    }

    private void saveBitmap(Bitmap bitmap) {
        // Implement your logic to save the Bitmap to a file or perform any desired actions
    }
}

