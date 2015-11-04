package uhho.xyz.scanner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.androidannotations.annotations.EBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@EBean
public class Utils {

   public static Bitmap fixOrientation(Context context, Uri uri) {

        Bitmap sourceBitmap = null;
        Bitmap fixedBitmap = null;
        try {

            sourceBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

            ExifInterface exif = new ExifInterface(uri.getPath());

            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            Log.d("Scanner", "Rotation: " + rotation);
            if (rotation != 0f) {
                matrix.preRotate(exifToDegrees(rotation));
            }

            fixedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);

        } catch (Exception e) {

        }

        return fixedBitmap;

    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
}
