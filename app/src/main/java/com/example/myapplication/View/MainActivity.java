package com.example.myapplication.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Helpers;
import com.example.myapplication.Models.FileModel;
import com.example.myapplication.R;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar, progressBar2, progressBar3, progressBar4, progressBar5;
    TextView textView, textView2, textView3, textView4, textView5;
    Helpers helpers;
    int count = 0;
    ArrayList<String> urlsList;

    FileModel[] tasks = new FileModel[11];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helpers = new Helpers(this);

        // Initializing Views
        initializeViews();
        // Initializing List of Downloads
        urlsList = helpers.initializeUrls();

        // Downloading first 5 tasks
        if (helpers.askForPermissions()) {
            startInitialDownloads();
        }

        // Broadcast receiver for checking if download is complete and adding next task if exist
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    public void initializeViews(){
        progressBar = findViewById(R.id.progress);
        progressBar2 = findViewById(R.id.progress2);
        progressBar3 = findViewById(R.id.progress3);
        progressBar4 = findViewById(R.id.progress4);
        progressBar5 = findViewById(R.id.progress5);
        textView = findViewById(R.id.text);
        textView2 = findViewById(R.id.text2);
        textView3 = findViewById(R.id.text3);
        textView4 = findViewById(R.id.text4);
        textView5 = findViewById(R.id.text5);
    }


    public void startInitialDownloads(){
        for (int i = 0; i < 5; i++) {
            String filename = FilenameUtils.getName(urlsList.get(count));
            if (i == 0) {
                textView.setText(filename);
            } else if (i == 1) {
                textView2.setText(filename);
            } else if (i == 2) {
                textView3.setText(filename);
            } else if (i == 3) {
                textView4.setText(filename);
            } else if (i == 4) {
                textView5.setText(filename);
            }
            downloadTask(urlsList.get(i), i);
            displayProgress(i, count);
            count++;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 312:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startInitialDownloads();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    DownloadManager downloadManager;

    // Function for adding task in queue
    public void downloadTask(String url, int taskId) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("Downloading");

        request.setDescription("Your file is being downloaded");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FilenameUtils.getName(url));
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        FileModel file = new FileModel();
        file.setUrl(url);
        file.setId(downloadManager.enqueue(request));

        tasks[taskId] = file;

    }

    //Function for getting progress bar through download manager
    public void displayProgress(int progressCount, int count) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean downloading = true;
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                while (downloading) {

                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(tasks[progressCount].getId()); //filter by id which you have receieved when reqesting download from download manager
                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }

                    final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //Update Progress Bar
                            updateProgress(progressCount,dl_progress);
                        }
                    });
                    cursor.close();
                }
            }
        }).start();

    }

    public void updateProgress(int progressCount, int dl_progress){
        if (progressCount == 0) {
            progressBar.setProgress(dl_progress);
        } else if (progressCount == 1) {
            progressBar2.setProgress(dl_progress);
        } else if (progressCount == 2) {
            progressBar3.setProgress(dl_progress);
        } else if (progressCount == 3) {
            progressBar4.setProgress(dl_progress);
        } else if (progressCount == 4) {
            progressBar5.setProgress(dl_progress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onComplete !=null)
            unregisterReceiver(onComplete);
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            //Checking if the received broadcast is for our enqueued download by matching download id
            for (int i = 0; i < tasks.length; i++) {

                if (tasks[i]!=null && tasks[i].getId() == id) {

                    if (count < urlsList.size()) {
                        String filename = FilenameUtils.getName(urlsList.get(count));
                        if (i == 0) {
                            textView.setText(filename);
                            progressBar.setProgress(0);
                        } else if (i == 1) {
                            textView2.setText(filename);
                            progressBar2.setProgress(0);
                        } else if (i == 2) {
                            textView3.setText(filename);
                            progressBar3.setProgress(0);
                        } else if (i == 3) {
                            textView4.setText(filename);
                            progressBar4.setProgress(0);
                        } else if (i == 4) {
                            textView5.setText(filename);
                            progressBar5.setProgress(0);
                        }
                        downloadTask(urlsList.get(count), i);
                        displayProgress(i, count);
                        count++;
                        break;
                    }
                }
            }
            if (context!=null){
                Toast.makeText(context, "Completed Download", Toast.LENGTH_SHORT).show();
            }
        }
    };
}