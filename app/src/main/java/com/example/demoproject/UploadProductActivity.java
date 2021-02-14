package com.example.demoproject;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.internal.$Gson$Preconditions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.R.layout.simple_spinner_item;

public class UploadProductActivity extends AppCompatActivity {

    public static final int CAMERA_PERMISSION_CODE = 123;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 103;

    ImageView imageView_product;
    TextView textView_product_title,textView_product_quality,textView_product_description,textView_product_category;
    RatingBar ratingBar_product;
    Button button_share_product;
    Spinner spinner_product;
    EditText editText_product_title,editText_product_description;
    String currentPhotoPath;


    FirebaseFirestore firebaseFirestore; //1 VALUE
    DatabaseReference databaseReference;
    StorageReference storageRef ; //2 IMAGE
    FirebaseStorage firebaseStorage;

    ValueEventListener valueEventListener;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> spinnerDataList;
    String imageFileName;
    File image;
    Uri photoURI;
    Uri contentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        imageView_product = findViewById(R.id.imgView_product);
        textView_product_title = findViewById(R.id.txt_product_title);
        textView_product_quality = findViewById(R.id.txt_product_quality);
        textView_product_description = findViewById(R.id.txt_product_description);
        textView_product_category = findViewById(R.id.txt_product_category);
        ratingBar_product = findViewById(R.id.ratingBar_product);
        button_share_product = findViewById(R.id.btn_share_product);
        spinner_product = findViewById(R.id.spinner_product);
        editText_product_title = findViewById(R.id.txtbox_product_title);
        editText_product_description = findViewById(R.id.txtbox_product_description);

        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("categories");
        firebaseFirestore = FirebaseFirestore.getInstance();

        spinnerDataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(UploadProductActivity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerDataList);
        spinner_product.setAdapter(arrayAdapter);

        retriveData();

        imageView_product.setOnClickListener(v -> {
            String[] options = {"Camera", "Gallery"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose..");
            builder.setItems(options, (dialog, which) -> {
                if(which == 0 ){
                    //Toast.makeText(UploadProductActivity.this, "Camera", Toast.LENGTH_SHORT).show();
                    askCameraPermission();

                }else{
                    //Toast.makeText(UploadProductActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);

                }
            });
            builder.show();
        });

        button_share_product.setOnClickListener(v -> shareInfo());

    }

    private void shareInfo() {


        if (contentUri != null){

            UUID uuid = UUID.randomUUID();
            String imageName = "Images/" + uuid + ".jpg";

            storageRef.child(imageName).putFile(contentUri).addOnSuccessListener(taskSnapshot -> {

                StorageReference newReferenceForDownload = FirebaseStorage.getInstance().getReference(imageName);
                newReferenceForDownload.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();

                    String titleProduct = String.valueOf(editText_product_title.getText());
                    float ratingProduct = ratingBar_product.getRating();
                    String descriptionProduct = String.valueOf(editText_product_description.getText());
                    String spinnerProduct = spinner_product.getSelectedItem().toString();


                    HashMap<String,Object> postData = new HashMap<>();
                    postData.put("date", FieldValue.serverTimestamp());
                    postData.put("imageUrl",downloadUrl);
                    postData.put("titlePruduct",titleProduct);
                    postData.put("ratingProduct",ratingProduct);
                    postData.put("descriptionProduct", descriptionProduct);
                    postData.put("spinnerProduct", spinnerProduct);

                    firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(documentReference ->
                            Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId())).addOnFailureListener(e ->
                            Log.w("TAG", "Error adding document", e));

                }).addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));


            }).addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));

        }
    }

    private void retriveData() {

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot item :dataSnapshot.getChildren()){
                    spinnerDataList.add(item.getValue().toString());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }else{
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(this, "Permission is required to use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
           if (resultCode == Activity.RESULT_OK){
               File f = new File(currentPhotoPath);
               imageView_product.setImageURI(Uri.fromFile(f));

               Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
               contentUri = Uri.fromFile(f);
               mediaScanIntent.setData(contentUri);
               this.sendBroadcast(mediaScanIntent);
           }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK){
                contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
                Log.d("tag","onActivityResult: Gallery Image Uri: " + imageFileName );
                imageView_product.setImageURI(contentUri);

            }
        }

    }

    private String getFileExt(Uri contentUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

}
//