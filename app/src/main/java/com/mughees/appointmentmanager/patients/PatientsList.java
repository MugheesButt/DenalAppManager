package com.mughees.appointmentmanager.patients;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

public class PatientsList extends AppCompatActivity {

    private ArrayList<PatientModel> PatList ;
    private RecyclerView recyclerView ;
    private PatientsListAdapter adapter ;
    RecyclerView.LayoutManager layoutManager ;

    FirebaseUser firebaseUser ;
    DatabaseReference myDb ;

    ImageButton logout, dentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list);
        Intialization();
        Toolbar();

    }

    private void Intialization()
    {
        PatList = new ArrayList<>();
        recyclerView = findViewById(R.id.PatList);
        layoutManager = new LinearLayoutManager(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        getPatients();

    }

    private void getPatients()
    {
        myDb = FirebaseDatabase.getInstance().getReference("Patients");

        myDb.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                PatList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    PatientModel pat = snapshot.getValue(PatientModel.class);
                    if (pat.getAdmin().matches(firebaseUser.getUid()))
                    {
                        PatList.add(pat);
                    }

                }

                adapter = new PatientsListAdapter(PatList, PatientsList.this);
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
                Intent intent = new Intent(PatientsList.this , SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        dentProfile = findViewById(R.id.DentProfile);
        dentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PatientsList.this , DentistProfile.class);
                startActivity(intent);
            }
        });
    }
}
