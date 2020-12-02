package com.example.capstoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class VideoFromImages extends AppCompatActivity implements View.OnClickListener {
    Uri uri, imageUri;
    String[] command, imagePaths, imageDurations;
    ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9, imageView10;
    String fileName;
    File destination, imagesInput;
    FFmpeg ff;
    int currentIndex;
    Utility util;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_from_images);
        getSupportActionBar().setTitle("Video From Images");

        Intent passUri = getIntent();
        if (passUri != null) {
            String videoPath = passUri.getStringExtra("uri");
            uri = Uri.parse(videoPath);
        }
        imagePaths = new String[10];
        imageDurations = new String[10];

        imageView1 = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);
        imageView3 = findViewById(R.id.image3);
        imageView4 = findViewById(R.id.image4);
        imageView5 = findViewById(R.id.image5);
        imageView6 = findViewById(R.id.image6);
        imageView7 = findViewById(R.id.image7);
        imageView8 = findViewById(R.id.image8);
        imageView9 = findViewById(R.id.image9);
        imageView10 = findViewById(R.id.image10);

        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
        imageView5.setOnClickListener(this);
        imageView6.setOnClickListener(this);
        imageView7.setOnClickListener(this);
        imageView8.setOnClickListener(this);
        imageView9.setOnClickListener(this);
        imageView10.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                currentIndex = 0;
                selectAnImage();
                break;
            case R.id.image2:
                currentIndex = 1;
                selectAnImage();
                break;
            case R.id.image3:
                currentIndex = 2;
                selectAnImage();
                break;
            case R.id.image4:
                currentIndex = 3;
                selectAnImage();
                break;
            case R.id.image5:
                currentIndex = 4;
                selectAnImage();
                break;
            case R.id.image6:
                currentIndex = 5;
                selectAnImage();
                break;
            case R.id.image7:
                currentIndex = 6;
                selectAnImage();
                break;
            case R.id.image8:
                currentIndex = 7;
                selectAnImage();
                break;
            case R.id.image9:
                currentIndex = 8;
                selectAnImage();
                break;
            case R.id.image10:
                currentIndex = 9;
                selectAnImage();
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.VFI_save){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.capstoneapplication.VideoFromImages.this);

            LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.VideoFromImages.this);
            linLay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layPar.setMargins(50, 0, 50, 100 );
            final EditText input = new EditText(com.example.capstoneapplication.VideoFromImages.this);
            input.setLayoutParams(layPar);
            input.setGravity(Gravity.TOP|Gravity.START);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            linLay.addView(input,layPar);

            alertDialog.setMessage("Enter New Video Name");
            alertDialog.setTitle("Change Video Name");
            alertDialog.setView(linLay);
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fileName = input.getText().toString();
                    File destFolder = new File("storage/emulated/0/EditingApeOutput/VideoFromImages");
                    if (!destFolder.exists()) {
                        destFolder.mkdir();
                    }
                    String fileExtension = ".mp4";
                    destination = new File(destFolder, fileName + fileExtension);

                    try {
                        createInputTxtFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(lastIndexOf(imagePaths) ==0){
                        command = new String[] {"-loop", "1", "-y", "-i", imagePaths[0],  "-c:v", "libx264", "-t", imageDurations[0], "-pix_fmt", "yuv420p", "-vf", "scale=720:-2", "-g", "1", destination.toString()};
                        try {
                            executeCommand(command);
                        } catch (FFmpegCommandAlreadyRunningException e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        command = new String[]{"-y", "-f", "concat", "-safe", "0", "-i", imagesInput.toString(), "-vsync", "vfr", "-pix_fmt", "yuv420p", "-vf", "scale=720:-2", "-g", "1",  destination.toString()};
                        try {
                            executeCommand(command);

                        } catch (FFmpegCommandAlreadyRunningException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void setDuration(){
        final AlertDialog.Builder durationDialog = new AlertDialog.Builder(com.example.capstoneapplication.VideoFromImages.this);
        LinearLayout linLay = new LinearLayout(com.example.capstoneapplication.VideoFromImages.this);
        linLay.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layPar.setMargins(50, 0, 50, 100 );
        final EditText input = new EditText(com.example.capstoneapplication.VideoFromImages.this);
        input.setLayoutParams(layPar);
        input.setGravity(Gravity.TOP|Gravity.START);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        linLay.addView(input,layPar);

        durationDialog.setMessage("Cancelling will set to default (5 seconds)");
        durationDialog.setTitle("Enter Image Duration (seconds)");
        durationDialog.setView(linLay);
        durationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageDurations[currentIndex] = "5";
                dialog.dismiss();
            }
        });
        durationDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageDurations[currentIndex] = input.getText().toString();

            }
        });
        durationDialog.show();
    }

    public void createInputTxtFile() throws IOException {
        imagesInput = new File("storage/emulated/0/EditingApeOutput/VideoFromImages/ImagesInput.txt");
        FileWriter writer = new FileWriter(imagesInput);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        for(int i = 0; i< imagePaths.length; i++) {
            if(imagePaths[i] != null) {
                bufferedWriter.write("file " + imagePaths[i]);
                bufferedWriter.newLine();
                bufferedWriter.write("duration " + imageDurations[i]);
                bufferedWriter.newLine();
            }
        }
        if(lastIndexOf(imagePaths)!= 0) //FFmpeg needs the last file name written twice in input file
            bufferedWriter.write("file " + imagePaths[lastIndexOf(imagePaths)]);
        bufferedWriter.close();
        writer.close();

    }

    public int lastIndexOf(String [] array ){
        for(int j = imagePaths.length; j>=0; j--){
            try{if(array[j] != null) {
                return j;
            }
            }catch (java.lang.IndexOutOfBoundsException e){
                Log.i("array","OOB");
            }
        }
        return -1; //no entries
    }

    public void selectAnImage(){
        Intent galleryAccess = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryAccess.setType("image/*");
        startActivityForResult(galleryAccess,100);
    }
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data)
    {
        super.onActivityResult(reqCode,resCode,data);
        if(resCode == RESULT_OK && reqCode == 100){
            imageUri = data.getData();
            imagePaths[currentIndex] = util.getPathFromUri(getApplicationContext(),imageUri);
            switch(currentIndex) {
                case 0:
                    imageView1.setImageURI(imageUri);
                    setDuration();
                    break;
                case 1:
                    imageView2.setImageURI(imageUri);
                    setDuration();
                    break;
                case 2:
                    imageView3.setImageURI(imageUri);
                    setDuration();
                    break;
                case 3:
                    imageView4.setImageURI(imageUri);
                    setDuration();
                    break;
                case 4:
                    imageView5.setImageURI(imageUri);
                    setDuration();
                    break;
                case 5:
                    imageView6.setImageURI(imageUri);
                    setDuration();
                    break;
                case 6:
                    imageView7.setImageURI(imageUri);
                    setDuration();
                    break;
                case 7:
                    imageView8.setImageURI(imageUri);
                    setDuration();
                    break;
                case 8:
                    imageView9.setImageURI(imageUri);
                    setDuration();
                    break;
                case 9:
                    imageView10.setImageURI(imageUri);
                    setDuration();
                    break;
            }

        }

    }

    private void executeCommand(String [] ffmpegCommand) throws FFmpegCommandAlreadyRunningException {
        ff= FFmpeg.getInstance(com.example.capstoneapplication.VideoFromImages.this);
        ff.execute(ffmpegCommand, new ExecuteBinaryResponseHandler(){

            @Override
            public void onProgress(String message){
                Log.i("VideoFromImages","Progress");
            }

            @Override
            public void onSuccess(String message){
                super.onSuccess(message);
            }

            @Override
            public void onFailure(String message){
                super.onFailure(message);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.video_from_images_menu,menu);
        return true;
    }
}
