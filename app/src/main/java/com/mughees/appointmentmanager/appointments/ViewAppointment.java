package com.mughees.appointmentmanager.appointments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mughees.appointmentmanager.MainActivity;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.patients.PatientModel;
import com.mughees.appointmentmanager.profile.DentistProfile;
import com.mughees.appointmentmanager.signing.SignIn;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.UUID;

public class ViewAppointment extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    ImageView img ;
    EditText name , date , time , tooth , treat , desc ;
    Button update ;
    String PatID , AptID;
    String ImageURI ;

    AppointmentModel apt =  new AppointmentModel();
    PatientModel pat = new PatientModel();
    DatePickerDialog datePickerDialog;
    int day , month , year , hours , minutes ;

    String[] ToothList;
    boolean[] CheckedTeeth;
    ArrayList<Integer> SelectedTeeth = new ArrayList<>();

    String[] TreatmentsList;
    boolean[] CheckedTreatments;
    ArrayList<Integer> SelectedTreatments = new ArrayList<>();

    DatabaseReference dbRef ;

    ImageButton logout, dentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);

        Toolbar();
        GetPatientDetails();

    }

    private void GetPatientDetails()
    {
        img = findViewById(R.id.img);
        name = findViewById(R.id.name);
        desc = findViewById(R.id.desc);

        PatID = getIntent().getStringExtra("PatID");
        AptID = getIntent().getStringExtra("AptID");

        dbRef = FirebaseDatabase.getInstance().getReference("Patients").child(PatID);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                pat = dataSnapshot.getValue(PatientModel.class);
                name.setText(pat.getName());

                if (pat.getImg().equals("N/A"))
                {
                    img.setImageURI(Uri.parse("android.resource://com.mughees.appointmentmanager/drawable/patient"));
                }

                else
                {
                    Picasso.get().load(pat.getImg()).into(img);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        GetAppointmentDetails();
        UpdateAppointment();
    }

    private void GetAppointmentDetails()
    {
        DatabaseReference AptDb = FirebaseDatabase.getInstance().getReference("Appointments").child(PatID).child(AptID);
        AptDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                apt = dataSnapshot.getValue(AppointmentModel.class);
                desc.setText(apt.getDescription());
                DatePick(apt.getDay(), apt.getMonth(), apt.getYear());
                TimePick(apt.getHrs(), apt.getMins());
                ToothPick(apt.getTooth());
                TreatPick(apt.getTreatment());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DatePick(final int tDay , final int tMonth , final int tYear)
    {
        date = findViewById(R.id.date);
        date.setText(tDay + "-" + tMonth + "-" + tYear);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance() ;
                year = tYear ;
                month = tMonth - 1;
                day = tDay ;

                datePickerDialog = new DatePickerDialog(ViewAppointment.this, ViewAppointment.this, year, month ,day);
                datePickerDialog.show();

            }
        });

    }


    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {

        year = y ;
        month = m + 1;
        day = d ;

        apt.setYear(year);
        apt.setMonth(month);
        apt.setDay(day);

        date.setText(day + "-" + month + "-" + year);
    }


    private void TimePick(final int tHrs , final int tMins)
    {

        time = findViewById(R.id.time);
        time.setText(TimeCheck(tHrs, tMins));
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                hours = tHrs;
                minutes = tMins;

                TimePickerDialog timePickerDialog = new TimePickerDialog(ViewAppointment.this , ViewAppointment.this, hours , minutes ,false);
                timePickerDialog.show();
            }
        });

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hr, int min) {

        hours = hr ;
        minutes = min ;

        apt.setHrs(hours);
        apt.setMins(minutes);

        time.setText(TimeCheck(hours, minutes));
    }

    String TimeCheck (int h , int m)
    {
        String min ;
        String time ;
        if (m < 10)
            min = "0" + m;
        else
            min = String.valueOf(m);

        if (h > 12) {
            h = h % 12;
            time = h + ":" + min + " PM";
        }
        else
            time = h + ":" + min + " AM" ;

        return time ;

    }


    private void ToothPick(String tTooth)
    {
        tooth = findViewById(R.id.tooth);
        tooth.setText(tTooth);
        ToothList = getResources().getStringArray(R.array.teeth);
        CheckedTeeth = new boolean[ToothList.length];


        StringTokenizer st1 = new StringTokenizer(tTooth, ",");
        int tooth_position ;
        String prev_tooth = "" ;
        while (st1.hasMoreTokens())
        {
            prev_tooth = st1.nextToken() ;
            tooth_position = getIndex(ToothList , prev_tooth) ;
            SelectedTeeth.add(Integer.valueOf(tooth_position)) ;
            CheckedTeeth[tooth_position] = true;
        }

        tooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ViewAppointment.this);
                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(ToothList, CheckedTeeth, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {

                        if(isChecked){
                            SelectedTeeth.add(position);
                        }

                        else{
                            SelectedTeeth.remove((Integer.valueOf(position)));
                        }

                    }
                }) ;

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String item = "";
                        for (int i = 0; i < SelectedTeeth.size(); i++) {
                            item = item + ToothList[SelectedTeeth.get(i)];
                            if (i != SelectedTeeth.size() - 1) {
                                item = item + ",";
                            }
                        }
                        tooth.setText(item) ;

                    }
                }) ;


                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i = 0; i < CheckedTeeth.length; i++) {
                            CheckedTeeth[i] = false;
                            SelectedTeeth.clear();
                            tooth.setText("");
                        }

                    }
                }) ;

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });

    }

    private void TreatPick(String tTreat)
    {
        treat = findViewById(R.id.treatment);
        treat.setText(tTreat);
        TreatmentsList = getResources().getStringArray(R.array.treatments);
        CheckedTreatments = new boolean[TreatmentsList.length];


        StringTokenizer st2 = new StringTokenizer(tTreat, ",");
        int treatment_position ;
        String prev_treatment = "" ;
        while (st2.hasMoreTokens())
        {
            prev_treatment = st2.nextToken() ;
            treatment_position = getIndex(TreatmentsList , prev_treatment) ;
            SelectedTreatments.add(Integer.valueOf(treatment_position)) ;
            CheckedTreatments[treatment_position] = true;
        }


        treat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder Builder = new AlertDialog.Builder(ViewAppointment.this);
                Builder.setTitle(R.string.dialog_title);
                Builder.setMultiChoiceItems(TreatmentsList, CheckedTreatments, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {

                        if(isChecked){
                            SelectedTreatments.add(position);
                        }

                        else{
                            SelectedTreatments.remove((Integer.valueOf(position)));
                        }

                    }
                }) ;

                Builder.setCancelable(false);
                Builder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String item = "";
                        for (int i = 0; i < SelectedTreatments.size(); i++) {
                            item = item + TreatmentsList[SelectedTreatments.get(i)];
                            if (i != SelectedTreatments.size() - 1) {
                                item = item + ",";
                            }
                        }
                        treat.setText(item) ;

                    }
                }) ;


                Builder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                Builder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i = 0; i < CheckedTreatments.length; i++) {
                            CheckedTreatments[i] = false;
                            SelectedTreatments.clear();
                            treat.setText("");
                        }

                    }
                }) ;

                AlertDialog Dialog = Builder.create();
                Dialog.show();

            }
        });
    }

    private int getIndex (String [] TYPES, String Search)
    {
        int index = 0;
        for (int i = 0 ; i < TYPES.length ; i++) {
            if (TYPES[i].matches(Search)) {
                index = i;
                return  index;
            }
        }
        return 0;
    }

    private void UpdateAppointment()
    {
        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(date.getText().toString()) || TextUtils.isEmpty(time.getText().toString())
                        || TextUtils.isEmpty(tooth.getText().toString()) || TextUtils.isEmpty(treat.getText().toString()))
                {
                    Toast.makeText(ViewAppointment.this, "All boxes except Description must be filled", Toast.LENGTH_LONG).show();
                }

                else
                {
                    dbRef = FirebaseDatabase.getInstance().getReference("Appointments").child(PatID).child(AptID);

                    apt.setPat_Id(PatID);
                    apt.setApp_Id(AptID);
                    apt.setTooth(tooth.getText().toString());
                    apt.setTreatment(treat.getText().toString());
                    apt.setDescription(desc.getText().toString());

                    dbRef.setValue(apt);

                    Toast.makeText(ViewAppointment.this , "Appointment updated successfully" , Toast.LENGTH_LONG).show();
                    finish();
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
                Intent intent = new Intent(ViewAppointment.this , SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        dentProfile = findViewById(R.id.DentProfile);
        dentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ViewAppointment.this , DentistProfile.class);
                startActivity(intent);
            }
        });
    }


}
