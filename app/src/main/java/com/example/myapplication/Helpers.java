package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Helpers {


    Context context;

    public Helpers(Context context) {
        this.context = context;
    }

    public boolean askForPermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                return true;
            } else {
                requestPermission(); // Code for permission
            }
        } else {
            return true;
        }
        return false;
    }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(context, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 312);
        }
    }


    public ArrayList<String> initializeUrls(){
        ArrayList<String> urlsList = new ArrayList<>();
        urlsList.add("https://gratisography.com/wp-content/uploads/2021/01/gratisogarphy-distressed-brick.jpg");
        urlsList.add("https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_2mb.mp4");
        urlsList.add("https://www.sample-videos.com/video123/mp4/360/big_buck_bunny_360p_5mb.mp4");
        urlsList.add("https://www.sample-videos.com/video123/mp4/480/big_buck_bunny_480p_1mb.mp4");
        urlsList.add("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
        urlsList.add("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-zip-file.zip");
        urlsList.add("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-large-zip-file.zip");
        urlsList.add("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample.tar");
        urlsList.add("https://www.sample-videos.com/video123/mp4/360/big_buck_bunny_360p_10mb.mp4");
        urlsList.add("https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_30mb.mp4");

        return urlsList;
    }

}
