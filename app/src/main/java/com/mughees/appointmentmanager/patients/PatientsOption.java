package com.mughees.appointmentmanager.patients;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.profile.DentistProfile;
import com.mughees.appointmentmanager.signing.SignIn;

public class PatientsOption extends AppCompatActivity {

    ImageView Add , View ;

    ImageButton logout, dentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_option);

        AddPatients();
        ViewPatients();
        Toolbar();
    }

    private void AddPatients()
    {
        Add = findViewById(R.id.add_pat);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

                Intent intent = new Intent(PatientsOption.this, AddPatient.class);
                startActivity(intent);

            }
        });

    }

    private void ViewPatients()
    {
        View = findViewById(R.id.view_pat);
        View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

                Intent intent = new Intent(PatientsOption.this, PatientsList.class);
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
                Intent intent = new Intent(PatientsOption.this , SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        dentProfile = findViewById(R.id.DentProfile);
        dentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PatientsOption.this , DentistProfile.class);
                startActivity(intent);
            }
        });
    }


}
