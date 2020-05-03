package com.mughees.appointmentmanager.appointments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mughees.appointmentmanager.MainActivity;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.profile.DentistProfile;
import com.mughees.appointmentmanager.signing.SignIn;

import java.util.ArrayList;

public class AppointmentsList extends AppCompatActivity {

    RecyclerView recyclerView ;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<AppointmentModel> appointmentList;
    AppointmentsAdapter adapter;
    ImageButton logout, dentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_list);

        Toolbar();
        Initialize();

    }

    private void Initialize()
    {
        appointmentList = new ArrayList<>();
        recyclerView = findViewById(R.id.AptList);
        layoutManager = new LinearLayoutManager(AppointmentsList.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        getAppointments();

    }

    private void getAppointments()
    {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Appointments");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                appointmentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    for (DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        AppointmentModel appointmentModel = snapshot1.getValue(AppointmentModel.class);
                        if (appointmentModel.getAdmin().matches(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            appointmentList.add(appointmentModel);
                        }
                    }
                }

                adapter = new AppointmentsAdapter(AppointmentsList.this, appointmentList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                Intent intent = new Intent(AppointmentsList.this , SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        dentProfile = findViewById(R.id.DentProfile);
        dentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AppointmentsList.this , DentistProfile.class);
                startActivity(intent);
            }
        });
    }
}
