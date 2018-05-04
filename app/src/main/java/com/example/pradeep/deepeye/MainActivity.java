package com.example.pradeep.deepeye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private StorageReference mStorageRef;
    private Uri file_uri,photoUri;
    private ProgressDialog mprogress;
    private ImageView imv;
    private static  final int CAMERA_REQUEST_CODE = 1;

    String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
       button=(Button)findViewById(R.id.button2);
       imv=(ImageView)findViewById(R.id.imageView);
       button.setOnClickListener(this);
       mprogress= new ProgressDialog(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!= null) {
            File photoFile =null;
            try{
                photoFile = createImageFile();

            }catch (Exception e){
                Log.d("MainActivity","Photo file creation failed");
            }
            if (photoFile != null){
                 photoUri = FileProvider.getUriForFile(this,"com.example.android.fileprovider",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }


        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     //   super.onActivityResult(requestCode, resultCode, data);
        String photoName = "Malaria";
        int i=0;


        if(requestCode==CAMERA_REQUEST_CODE && resultCode==RESULT_OK){
            mprogress.setMessage("uploading image...");
            mprogress.show();
            mprogress.setCancelable(true);
            file_uri = photoUri;
           /* if (file_uri == null ){
                Log.d("MAINACTIVITY","file is null ");
            }*/
            imv.setImageURI(file_uri);
            StorageReference filepath = mStorageRef.child("photos").child(photoName+String.valueOf(i++));
            filepath.putFile(file_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask. TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    Toast.makeText(MainActivity.this,"upload done ", Toast.LENGTH_LONG).show();
                    mprogress.dismiss();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Toast.makeText(MainActivity.this,"upload failed ", Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signout){
            FirebaseAuth.getInstance().signOut();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //hello
}
