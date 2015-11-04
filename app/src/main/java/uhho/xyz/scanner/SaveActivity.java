package uhho.xyz.scanner;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.soundcloud.android.crop.Crop;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import uhho.xyz.scanner.entity.Document;
import uhho.xyz.scanner.utils.Utils;

@EActivity(R.layout.activity_save)
public class SaveActivity extends Activity {

    @Extra("image")
    Uri imageUri;

    @StringArrayRes(R.array.loading)
    String[] loading;

    @ViewById(R.id.loading_message)
    TextView loadingMessage;

    private static final Random random = new Random();

    @AfterViews
    void after() {
        loadingMessage.setText(loading[random.nextInt(loading.length)]);
        doSomeWork();
    }
    @Background
    void doSomeWork() {


        TessBaseAPI base = new TessBaseAPI();

        base.init("/sdcard", "eng");
        Bitmap bitmap = null;
        try {
            bitmap = Utils.fixOrientation(this, imageUri);
        } catch (Exception e) {

        }

        base.setImage(bitmap);

        String text = base.getUTF8Text();
        base.end();
        Document doc = new Document();

        doc.setUri(imageUri.toString());
        doc.setText(text);

        doc.save();
        finish();

    }


}
