package com.example.sandeep.cropper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA_PICTURE = 1232;
    private File file;
    Uri uri;
    Button button;
    ImageView imageView;
    ImageCapture imageCapture;
    String mCurrentPhotoPath;
    private boolean isDenied = false;
    public static final int REQUEST_CODE_GALLERY_PICTURE = 1233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button= findViewById(R.id.button2);
        imageView= findViewById(R.id.imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pick = true;

                if (pick == true){
                    if (!checkCameraPermision()){
                        requestPermision();
                    }else{
                        pickimage();
                    }
                }else{
                    if (!checkStoragePermision()){
                        requestStoragePermision();
                    }
                }

            }
        });
    }

    private void requestStoragePermision() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

    }

    private boolean checkStoragePermision() {
        boolean r1= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return r1;
    }

    private void pickimage() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {

            try {
                file = getFilename(getApplicationContext());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (file != null) {
                uri = FileProvider.getUriForFile(getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    public File getFilename(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result!=null) {
                Uri resultUri = result.getUri();
                uri=resultUri;
                mCurrentPhotoPath = resultUri.getPath();
                imageView.setImageURI(resultUri);

                Log.e("uploadImage", "=" + mCurrentPhotoPath);

            }
            if(resultCode==-1 && data==null)
            {

                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1) //You can skip this for free form aspect ratio)
                        .start(this);

            }
        }
    }

    private void requestPermision() {

        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

    }


    private boolean checkCameraPermision() {
        boolean r= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean r1= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return r&&r1;
    }

//    private void openGallery() {
//        Intent intent;
//        intent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(Intent.createChooser(intent, "choose_from_gallery"), REQUEST_CODE_GALLERY_PICTURE);
//    }


}