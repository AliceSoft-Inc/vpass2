package com.example.vpass2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private FileUtil fileUtil;
    private Boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileUtil = new FileUtil(getContentResolver());
        rotateImageSelection();
    }

    int requestcode = 1;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestcode, resultCode, data);
        if (requestcode == requestCode && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            handleFileSelected(uri);
        }
    }

    public void onClearAllImages(View view) {
        fileUtil.deleteAll(getApplication().getCacheDir().listFiles());
        rotateImageSelection();
    }

    public void openFileChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, requestcode);
    }

    private void handleFileSelected(Uri uri) {
        File[] list = getApplication().getCacheDir().listFiles();
        File destination;

        if(getApplication().getCacheDir().listFiles().length == 0) {
            destination = new File(getApplication().getCacheDir() + "/1.png");
        } else {
            if (list.length == 2) {
                fileUtil.promote2to1(getApplication().getCacheDir());
            }

            destination = new File(getApplication().getCacheDir() + "/2.png");
        }

        fileUtil.saveFile(uri, destination);
        Context context = getApplicationContext();
        Toast.makeText(context, "Added image", Toast.LENGTH_SHORT).show();
        rotateImageSelection();
    }

    private void rotateImageSelection() {
        File[] list = getApplication().getCacheDir().listFiles();
        ImageView imageView = (ImageView) findViewById(R.id.vcimage);
        TextView textView = (TextView) findViewById(R.id.noimgtext);

        if (list.length == 0) {
            imageView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
            return;
        }

        textView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);

        if (list.length > 0) {
            imageView.setImageURI(null);
            imageView.setImageURI(Uri.parse(list[0].getAbsolutePath()));
            flag = true;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //No-op
                }
            });
        }

        if (list.length == 2) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flag) {
                        imageView.setImageURI(Uri.parse(getApplication().getCacheDir().listFiles()[1].getAbsolutePath()));
                    } else {
                        imageView.setImageURI(Uri.parse(getApplication().getCacheDir().listFiles()[0].getAbsolutePath()));
                    }
                    flag = !flag;
                }
            });
        }
    }
}