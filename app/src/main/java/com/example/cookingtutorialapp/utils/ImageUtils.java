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

/**
 * Lớp tiện ích xử lý các thao tác liên quan đến hình ảnh
 * - Lưu hình ảnh từ Uri và Bitmap vào bộ nhớ trong của ứng dụng
 * - Tải và hiển thị hình ảnh lên ImageView
 */
public class ImageUtils {
    public static String saveImageToInternalStorage(Context context, Uri imageUri) {
        try {
            // Chuyển đổi Uri thành Bitmap thông qua ContentResolver
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            // Gọi phương thức lưu Bitmap
            return saveImageToInternalStorage(context, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveImageToInternalStorage(Context context, Bitmap bitmap) {
        // Bọc Context để truy cập thư mục ứng dụng
        ContextWrapper cw = new ContextWrapper(context);
        // Tạo thư mục "images" trong bộ nhớ riêng của ứng dụng
        // Đường dẫn sẽ là: /data/data/com.example.cookingtutorialapp/app_images
        File directory = cw.getDir("images", Context.MODE_PRIVATE);

        // Tạo tên file hình ảnh duy nhất dựa trên thời gian hiện tại
        // Định dạng: IMG_yyyyMMdd_HHmmss.jpg
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        // Tạo đường dẫn đầy đủ đến file
        File path = new File(directory, imageFileName);

        FileOutputStream fos = null;
        try {
            // Mở FileOutputStream để ghi dữ liệu
            fos = new FileOutputStream(path);
            // Nén Bitmap sang định dạng JPEG với chất lượng 80%
            // Giúp giảm kích thước file mà vẫn giữ chất lượng hình ảnh tốt
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            return path.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // Đảm bảo luôn đóng stream để tránh rò rỉ tài nguyên
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
            // Tạo đối tượng File từ đường dẫn
            File f = new File(path);
            // Đọc file và decode thành Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            // Hiển thị Bitmap lên ImageView
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Nếu không tìm thấy file, hiển thị hình ảnh mặc định (biểu tượng lỗi)
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }
}