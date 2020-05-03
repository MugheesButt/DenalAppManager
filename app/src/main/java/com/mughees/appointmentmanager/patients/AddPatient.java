package com.mughees.appointmentmanager.patients;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mughees.appointmentmanager.ImagePicker;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.appointments.AddAppointment;
import com.mughees.appointmentmanager.profile.DentistProfile;
import com.mughees.appointmentmanager.signing.SignIn;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.UUID;

public class AddPatient extends AppCompatActivity {

    ImageView img ;
    EditText name , age , phone , city , mail , tdues , pdues;
    Button save ;
    PatientModel pat ;
    FirebaseDatabase myDb ;
    DatabaseReference dbRef ;
    FirebaseStorage storage;
    StorageReference storageReference;

    String photoStringLink = "null";

    private static final int IMAGE_REQUEST = 1000 ;
    private static final int PERMISSION_CODE=1001;
    private Uri ImageURI ;
    int flag = 0;

    ImageButton logout, dentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        Initialize();
        ImagePicker();
        Toolbar();

        myDb = FirebaseDatabase.getInstance();
        Save();
    }

    private void Initialize()
    {
        img = findViewById(R.id.img);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        phone = findViewById(R.id.phone);
        city = findViewById(R.id.city);
        mail = findViewById(R.id.mail);
        tdues = findViewById(R.id.tdues);
        pdues = findViewById(R.id.pdues);


        ImageURI = Uri.parse("android.resource://com.mughees.appointmentmanager/drawable/patient");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        pat = new PatientModel();

    }

    private void CreatePatient()
    {

        if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(phone.getText().toString()))
        {
            Toast.makeText(AddPatient.this , "Name and Phone# must be filled" , Toast.LENGTH_LONG).show();
        }

        else
        {

            String uniqueId = UUID.randomUUID().toString();

            pat.setAdmin(FirebaseAuth.getInstance().getCurrentUser().getUid());
            pat.set_ID(uniqueId);
            pat.setName(name.getText().toString());
            pat.setAge(CheckEmptyNum(age.getText().toString()));
            pat.setPhone(EmptyCheck(phone));
            pat.setAddress(EmptyCheck(city));
            pat.setEmail(EmptyCheck(mail));
            pat.setTDues(CheckEmptyNum(tdues.getText().toString()));
            pat.setPDues(CheckEmptyNum(pdues.getText().toString()));
            pat.setRDues(CheckEmptyNum(tdues.getText().toString()) - CheckEmptyNum(pdues.getText().toString()));

            if (flag == 1)
            {
                ImageUpload(pat);
            }

            else
            {
                pat.setImg("N/A");
                DataUpdate(pat);
            }
        }
    }

    private void DataUpdate(PatientModel patientModel)
    {
        dbRef = FirebaseDatabase.getInstance().getReference("Patients").child(patientModel.get_ID());
        dbRef.setValue(patientModel);
        Toast.makeText(AddPatient.this , "Patient Updated Successfully" , Toast.LENGTH_LONG).show();

        Intent intent = new Intent(AddPatient.this, PatientsList.class);
        startActivity(intent);
        finish();
    }

    private void Save()
    {
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CreatePatient();
            }
        });

    }

    private int CheckEmptyNum(String num)
    {
        if (num.matches(""))
            return 0 ;
        else
            return Integer.parseInt(num) ;
     }

    private String EmptyCheck(EditText et)
    {

        if (TextUtils.isEmpty(et.getText().toString()))
        {
            return "N/A";
        }

        else
        {
            return et.getText().toString();
        }

    }


    /*                                                                  IMAGE PART                                  */




    private void pickimagefromgallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    pickimagefromgallery();
                }
                else {
                    Toast.makeText(this, "Permissions denied...!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK && requestCode==IMAGE_REQUEST){
            ImageURI = data.getData();
            flag = 1 ;
            img.setImageURI(ImageURI);
        }

    }

    private void ImagePicker()
    {
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {

                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }

                    else {

                        pickimagefromgallery();

                    }
                }

                else {

                    pickimagefromgallery();
                }
            }
        });

    }

    private void ImageUpload(final PatientModel pat) {

        Toast.makeText(AddPatient.this, "Sending Picture", Toast.LENGTH_SHORT).show();
        final StorageReference ur_firebase_reference = storageReference.child("images/" + pat.get_ID());

        Uri file = ImageURI;
        UploadTask uploadTask = ur_firebase_reference.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }


                // Continue with the task to get the download URL
                return ur_firebase_reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    System.out.println("Send " + downloadUri);
                    Toast.makeText(AddPatient.this, "Successfully sent", Toast.LENGTH_SHORT).show();

                    if (downloadUri != null) {

                        photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        // Get a URL to the uploaded content
                        //String g = taskSnapshot.getUploadSessionUri().toString();
                        System.out.println("Sent " + photoStringLink);

                        pat.setImg(photoStringLink);
                        dbRef = myDb.getReference("Patients").child(pat.get_ID());
                        dbRef.setValue(pat);
                        Toast.makeText(AddPatient.this , "Patient Added Successfully" , Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddPatient.this , PatientsList.class);
                        startActivity(intent);
                        finish();

                    }

                } else {
                    Toast.makeText(AddPatient.this , "Failed" , Toast.LENGTH_SHORT).show();
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void Toolbar()
    {
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AddPatient.this , SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        dentProfile = findViewById(R.id.DentProfile);
        dentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddPatient.this , DentistProfile.class);
                startActivity(intent);
            }
        });
    }

}
