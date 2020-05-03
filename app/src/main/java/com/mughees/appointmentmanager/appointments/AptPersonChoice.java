package com.mughees.appointmentmanager.appointments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.mughees.appointmentmanager.MainActivity;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.patients.AddPatient;
import com.mughees.appointmentmanager.patients.PatientsList;
import com.mughees.appointmentmanager.profile.DentistProfile;
import com.mughees.appointmentmanager.signing.SignIn;

public class AptPersonChoice extends AppCompatActivity {

    ImageView choose , create ;
    ImageButton logout, dentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apt_person_choice);

        Toolbar();
        Choose();
        Create();
    }

    private void Choose()
    {
        choose = findViewById(R.id.choose_pat);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AptPersonChoice.this, PatientsList.class);
                startActivity(intent);
            }
        });
    }

    private void Create()
    {
        create = findViewById(R.id.create_pat);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AptPersonChoice.this, AddPatient.class);
                startActivity(intent);
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
                Intent intent = new Intent(AptPersonChoice.this , SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        dentProfile = findViewById(R.id.DentProfile);
        dentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AptPersonChoice.this , DentistProfile.class);
                startActivity(intent);
            }
        });
    }
}
