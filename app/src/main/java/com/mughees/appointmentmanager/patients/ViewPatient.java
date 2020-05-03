package com.mughees.appointmentmanager.patients;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.appointments.AddAppointment;
import com.mughees.appointmentmanager.profile.DentistProfile;
import com.mughees.appointmentmanager.signing.SignIn;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class ViewPatient extends AppCompatActivity {

    String PatID ;
    ImageView img ;
    EditText name , age , phone , city , mail , tdues , pdues , rdues;
    String OldImage ;
    Button update , app ;
    PatientModel patientModel;

    DatabaseReference myDb, dbRef ;

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
        setContentView(R.layout.activity_view_patient);

        Initialize();
        GetPatient();
        ImagePicker();
        UpdateButton();
        ProceedAppointment();
        Toolbar();
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
        update = findViewById(R.id.update);
        app = findViewById(R.id.appointment);
        rdues = findViewById(R.id.rdues);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }

    private void GetPatient()
    {
        PatID = getIntent().getStringExtra("PatID");
        myDb = FirebaseDatabase.getInstance().getReference("Patients").child(PatID);

        myDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                patientModel = dataSnapshot.getValue(PatientModel.class);
                assert  patientModel != null;
                name.setText(patientModel.getName());
                age.setText(String.valueOf(CheckEmptyNum(String.valueOf(patientModel.getAge()))));
                phone.setText(patientModel.getPhone());
                city.setText(patientModel.getAddress());
                mail.setText(patientModel.getEmail());
                tdues.setText(String.valueOf(CheckEmptyNum(String.valueOf(patientModel.getTDues()))));
                pdues.setText(String.valueOf(CheckEmptyNum(String.valueOf(patientModel.getPDues()))));
                rdues.setText(String.valueOf(patientModel.getRDues()));
                OldImage = patientModel.getImg();


                if (patientModel.getImg().matches("N/A"))
                {
                 img.setImageURI(Uri.parse("android.resource://com.mughees.appointmentmanager/drawable/patient"));
                }

                else
                {
                    Picasso.get().load(patientModel.getImg()).into(img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void UpdatePatient()
    {

        if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(phone.getText().toString()))
        {
            Toast.makeText(ViewPatient.this , "Name and Phone# must be filled" , Toast.LENGTH_LONG).show();
        }

        else
        {
            patientModel.set_ID(PatID);
            patientModel.setName(name.getText().toString());
            patientModel.setAge(CheckEmptyNum(age.getText().toString()));
            patientModel.setPhone(EmptyCheck(phone));
            patientModel.setAddress(EmptyCheck(city));
            patientModel.setEmail(EmptyCheck(mail));
            patientModel.setTDues(CheckEmptyNum(tdues.getText().toString()));
            patientModel.setPDues(CheckEmptyNum(pdues.getText().toString()));
            patientModel.setRDues(CheckEmptyNum(tdues.getText().toString()) - CheckEmptyNum(pdues.getText().toString()));

            if (flag == 1)
            {
                ImageUpload(patientModel);
            }

            else
            {
                patientModel.setImg(OldImage);
                DataUpdate(patientModel);
            }
        }
    }

    private void DataUpdate(PatientModel patientModel)
    {
        myDb = FirebaseDatabase.getInstance().getReference("Patients").child(patientModel.get_ID());
        myDb.setValue(patientModel);
        Toast.makeText(ViewPatient.this , "Patient Updated Successfully" , Toast.LENGTH_LONG).show();
        finish();
    }

    private void UpdateButton()
    {
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdatePatient();
            }
        });
    }

    private void ProceedAppointment()
    {
        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ViewPatient.this, AddAppointment.class);
                intent.putExtra("PatID", PatID);
                intent.putExtra("img" , patientModel.getImg());
                intent.putExtra("PatName", patientModel.getName());
                startActivity(intent);
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
            return "-";
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

        if (pat.getImg().equals("no"))
        {

        }

        else
        {
            DeleteOldPic(pat.getImg());
        }

        Toast.makeText(ViewPatient.this, "Sending Picture", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ViewPatient.this, "Successfully sent", Toast.LENGTH_SHORT).show();

                    if (downloadUri != null) {

                        photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        // Get a URL to the uploaded content
                        //String g = taskSnapshot.getUploadSessionUri().toString();
                        System.out.println("Sent " + photoStringLink);

                        pat.setImg(photoStringLink);
                        dbRef = FirebaseDatabase.getInstance().getReference("Patients").child(pat.get_ID());
                        dbRef.setValue(pat);
                        Toast.makeText(ViewPatient.this , "Patient Updated Successfully" , Toast.LENGTH_LONG).show();
                        finish();

                    }

                } else {
                    Toast.makeText(ViewPatient.this , "Update Failed" , Toast.LENGTH_SHORT).show();
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private  void DeleteOldPic(String mImageUrl)
    {
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(mImageUrl);

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d("Success", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d("Fail", "onFailure: did not delete file");
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
                Intent intent = new Intent(ViewPatient.this , SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        dentProfile = findViewById(R.id.DentProfile);
        dentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ViewPatient.this , DentistProfile.class);
                startActivity(intent);
            }
        });
    }

}
