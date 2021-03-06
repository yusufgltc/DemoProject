package com.example.demoproject.Activity;
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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.demoproject.R;
import com.example.demoproject.databinding.ActivityUploadProductBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
public class UploadProductActivity extends AppCompatActivity {
    //binding
    private ActivityUploadProductBinding binding;
    //req and per codes
    public static final int CAMERA_PERMISSION_CODE = 123;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 103;
    String currentPhotoPath;
    //firebase
    FirebaseFirestore firebaseFirestore; //1 VALUE
    DatabaseReference databaseReference;
    StorageReference storageRef ; //2 IMAGE
    FirebaseStorage firebaseStorage;
    FirebaseAuth firebaseAuth;
    //spinner and image
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
        //bar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        setTitle("Add Product");
        //binding
        binding = ActivityUploadProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //firebase
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("categories");
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        //spinner
        spinnerDataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(UploadProductActivity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerDataList);
        binding.spinnerProductCategory.setAdapter(arrayAdapter);
        retrieveData();
        //camera
        binding.imageViewProductUpload.setOnClickListener(v -> { cameraDialog(); });
        binding.btnSignUp.setOnClickListener(v -> shareInfo());
    }
    private void cameraDialog() {
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
    }
    private void shareInfo() {
        binding.progressBarSign.setVisibility(View.VISIBLE);
        if (contentUri != null){
            UUID uuid = UUID.randomUUID();
            String imageName = "Images/" + uuid + ".jpg";
            storageRef.child(imageName).putFile(contentUri).addOnSuccessListener(taskSnapshot -> {
                StorageReference newReferenceForDownload = FirebaseStorage.getInstance().getReference(imageName);
                newReferenceForDownload.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    String titleProduct = String.valueOf(binding.edtTextProductTitleUpload.getText());
                    float ratingProduct = binding.ratingBarProductQualityUpload.getRating();
                    //getUserEmail
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String userEmail = firebaseUser.getEmail();
                    String descriptionProduct = String.valueOf(binding.edtTextProductDescriptionUpload.getText());
                    String spinnerProduct = binding.spinnerProductCategory.getSelectedItem().toString();
                    //time
                    int month = Calendar.getInstance().get(Calendar.MONTH);
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    String total = day + "/" + (month+1) + "/" + year;

                    HashMap<String,Object> postData = new HashMap<>();
                    postData.put("date", total);
                    postData.put("imageUrl",downloadUrl);
                    postData.put("title",titleProduct);
                    postData.put("rating",ratingProduct);
                    postData.put("description", descriptionProduct);
                    postData.put("category", spinnerProduct);
                    postData.put("email", userEmail);
                    firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(documentReference ->
                            Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId())).addOnFailureListener(e ->
                            Log.w("TAG", "Error adding document", e));
                }).addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
            }).addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
        }
    }
    private void retrieveData() {
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item :snapshot.getChildren()){
                  spinnerDataList.add(item.child("name").getValue().toString());
                }
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
               binding.imageViewProductUpload.setImageURI(Uri.fromFile(f));
               Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
               contentUri = Uri.fromFile(f);
               mediaScanIntent.setData(contentUri);
               this.sendBroadcast(mediaScanIntent);
           }
        }
        if (requestCode == GALLERY_REQUEST_CODE ) {
            if (resultCode == Activity.RESULT_OK ){
                contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
                Log.d("tag","onActivityResult: Gallery Image Uri: " + imageFileName );
                binding.imageViewProductUpload.setImageURI(contentUri);
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
