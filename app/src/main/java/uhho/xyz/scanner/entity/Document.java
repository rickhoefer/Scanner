package uhho.xyz.scanner.entity;

import android.graphics.Bitmap;

import com.orm.SugarRecord;

/**
 * Created by rhoefer on 11/2/15.
 */

public class Document extends SugarRecord<Document> {

    private String uri;
    private String text;

    public Document() {

    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
