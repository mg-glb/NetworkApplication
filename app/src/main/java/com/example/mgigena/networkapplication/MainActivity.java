package com.example.mgigena.networkapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private Bitmap mBitmap = null;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternetConnection()) {
                    downloadImage("http://www.tutorialspoint.com/green/images/logo.png");
                }
            }
        });
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connect
                = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connect.getActiveNetworkInfo()!= null) {
            boolean connected = connect.getActiveNetworkInfo().isConnectedOrConnecting();
            if (connected) {
                Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
                return true;
            } else {
                Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void downloadImage(String urlStr) {
        final String url = urlStr;

        new Thread() {
            public void run() {
                InputStream in;
                Message msg = Message.obtain();
                msg.what = 1;
                try {
                    in = openHttpConnection(url);
                    mBitmap = BitmapFactory.decodeStream(in);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("bitmap", mBitmap);
                    msg.setData(bundle);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                messageHandler.sendMessage(msg);
            }
        }.start();
    }

    private Handler messageHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap((Bitmap) (msg.getData().getParcelable("bitmap")));
        }
    };

    private InputStream openHttpConnection(String urlStr) {
        InputStream in = null;
        int resCode;

        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();
            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            resCode = httpConn.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }
}
