package com.mughees.appointmentmanager.appointments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.mughees.appointmentmanager.MainActivity;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.profile.DentistProfile;
import com.mughees.appointmentmanager.signing.SignIn;

public class AppointmentOption extends AppCompatActivity {

    ImageView Add, View ;
    ImageButton logout, dentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_option);

        Toolbar();
        AddApt();
        ViewApts();
    }

    private void AddApt()
    {
        Add = findViewById(R.id.add_app);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

                Intent intent = new Intent(AppointmentOption.this , AptPersonChoice.class);
                startActivity(intent);
            }
        });

    }

    private void ViewApts()
    {
        View = findViewById(R.id.view_app);
        View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

                Intent intent = new Intent(AppointmentOption.this , AppointmentsList.class);
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
                Intent intent = new Intent(AppointmentOption.this , SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        dentProfile = findViewById(R.id.DentProfile);
        dentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AppointmentOption.this , DentistProfile.class);
                startActivity(intent);
            }
        });
    }
}
