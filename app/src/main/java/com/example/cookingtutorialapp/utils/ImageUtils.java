package com.example.cookingtutorialapp.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {
    public static String saveImageToInternalStorage(Context context, Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            return saveImageToInternalStorage(context, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveImageToInternalStorage(Context context, Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(context);
        // Path to /data/data/com.example.cookingtutorialapp/app_images
        File directory = cw.getDir("images", Context.MODE_PRIVATE);

        // Create a unique image name based on timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        // Create the file path
        File path = new File(directory, imageFileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            // Use compress method to write into the file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            return path.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadImageFromStorage(Context context, ImageView imageView, String path) {
        try {
            File f = new File(path);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }
}