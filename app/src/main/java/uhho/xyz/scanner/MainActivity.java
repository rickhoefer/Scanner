package uhho.xyz.scanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.soundcloud.android.crop.Crop;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uhho.xyz.scanner.adapter.ImageAdapter;
import uhho.xyz.scanner.entity.Document;
import uhho.xyz.scanner.utils.Utils;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SAVE = 3;

    private File storageDir;
    private File pic;
    private File crop;

    @ViewById(R.id.grid)
    GridView grid;

    @Bean
    Utils utils;

    @AfterViews
    public void afterViews() {
        storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        getGridItems();
    }


    private void getGridItems() {
        final List<Document> documentList = new ArrayList<Document>();

        try {
            documentList.addAll(Document.listAll(Document.class));
        } catch (Exception e) {
        }

        Log.i("Scanner", "Document Size: " + documentList.size());
        grid = (GridView) findViewById(R.id.grid);

        List<Bitmap> images = new ArrayList<>();
        for (Document doc : documentList) {
            try {
                Uri imageUri = Uri.parse(doc.getUri());
                Bitmap bitmap = Utils.fixOrientation(this, imageUri);
                images.add(bitmap);
            } catch (Exception e) {}
        }

        ImageAdapter adapter = new ImageAdapter(this);
        adapter.setImages(images.toArray(new Bitmap[images.size()]));

        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, documentList.get(position).getText());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                documentList.get(position).delete();
                getGridItems();
                return true;
            }
        });
    }

    @Click(R.id.fab)
    void doSomething(View view) {

        try {
            pic = File.createTempFile("test", ".jpg", storageDir);
        } catch (Exception e) {
            Log.w("Scanner", "Uhho. Something went wrong trying to create a temporary file.");
        }

         if (pic != null) {
             Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

             takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pic));
             if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                 Log.i("Scanner", "Starting Picture intent");
                 startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
             }
         }
    }

    @OnActivityResult(REQUEST_IMAGE_CAPTURE)
    public void onImageCaptureResult(Intent data) {

        try {
            crop = File.createTempFile("crop", ".jpg", storageDir);
        } catch (Exception e) {}

        Crop.of(Uri.fromFile(pic), Uri.fromFile(crop)).start(this);

    }

    @OnActivityResult(Crop.REQUEST_CROP)
    public void onCropResult(Intent result) {
        Log.i("Scanner", "Starting Picture intent");

        SaveActivity_.intent(this).extra("image", Crop.getOutput(result)).startForResult(REQUEST_SAVE);

    }

    @OnActivityResult(REQUEST_SAVE)
    public void onCropResult() {

        grid.invalidateViews();

        getGridItems();

       final Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), "Saved to Library", Snackbar.LENGTH_LONG);

        snackbar.setAction("Test", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }
}
