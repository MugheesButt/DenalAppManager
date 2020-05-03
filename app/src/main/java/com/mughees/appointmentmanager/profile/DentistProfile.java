package com.mughees.appointmentmanager.profile;

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
import com.mughees.appointmentmanager.MainActivity;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.patients.PatientModel;
import com.mughees.appointmentmanager.patients.ViewPatient;
import com.mughees.appointmentmanager.signing.SignIn;
import com.squareup.picasso.Picasso;

public class DentistProfile extends AppCompatActivity {


    ImageView img ;
    String OldImage;
    EditText name, phone, mail ;
    ProfileModel Dentist ;
    Button update;

    StorageReference storageReference;

    String photoStringLink = "null";

    private static final int IMAGE_REQUEST = 1000 ;
    private static final int PERMISSION_CODE=1001;
    private Uri ImageURI ;
    int flag = 0;

    ImageButton logout , dentProfile ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dentist_profile);

        Initialize();
        Update();
        ImagePicker();
        Toolbar();
    }

    private void Initialize()
    {
        img = findViewById(R.id.img);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        mail = findViewById(R.id.mail);


        storageReference = FirebaseStorage.getInstance().getReference();

        GetProfile();
    }

    private void GetProfile()
    {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ProfileModel profileModel = dataSnapshot.getValue(ProfileModel.class);

                if (profileModel != null)
                {
                    name.setText(profileModel.getName());
                    if (!profileModel.getPhone().matches("N/A"))
                        phone.setText(profileModel.getPhone());

                    if (!profileModel.getEmail().matches("N/A"))
                        mail.setText(profileModel.getEmail());

                    OldImage = profileModel.getImage();

                    if (OldImage.equals("N/A")) {

                    }
                    else {
                        Picasso.get().load(OldImage).into(img);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Update()
    {
        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateProfile();
            }
        });
    }

    private void UpdateProfile()
    {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
        Dentist = new ProfileModel();
        Dentist.setName(EmptyCheck(name));
        Dentist.setPhone(EmptyCheck(phone));
        Dentist.setEmail(EmptyCheck(mail));
        Dentist.setId(id);

        if (flag == 0)
        {
            Dentist.setImage(OldImage);
            dbRef.setValue(Dentist);
            Toast.makeText(DentistProfile.this, "Profile Updated Successfully" , Toast.LENGTH_LONG).show();
            finish();
        }

        else
        {
            ImageUpload(Dentist);
        }

    }

    private String EmptyCheck(EditText et)
    {
        if (TextUtils.isEmpty(et.getText().toString()))
        {
            return "-";
        }

        else
        {
            return et.getText().toString() ;
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

    private void ImageUpload(final ProfileModel dent) {

        if (dent.getImage().equals("N/A"))
        {

        }

        else
        {
            DeleteOldPic(dent.getImage());
        }

        Toast.makeText(DentistProfile.this, "Sending Picture", Toast.LENGTH_SHORT).show();
        final StorageReference ur_firebase_reference = storageReference.child("images/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid());

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
                    Toast.makeText(DentistProfile.this, "Successfully sent", Toast.LENGTH_SHORT).show();

                    if (downloadUri != null) {

                        photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        // Get a URL to the uploaded content
                        //String g = taskSnapshot.getUploadSessionUri().toString();
                        System.out.println("Sent " + photoStringLink);

                        dent.setImage(photoStringLink);
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dbRef.setValue(dent);
                        Toast.makeText(DentistProfile.this , "Profile Updated Successfully" , Toast.LENGTH_LONG).show();
                        finish();

                    }

                } else {
                    Toast.makeText(DentistProfile.this , "Update Failed" , Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(DentistProfile.this , SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        dentProfile = findViewById(R.id.DentProfile);
        dentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
