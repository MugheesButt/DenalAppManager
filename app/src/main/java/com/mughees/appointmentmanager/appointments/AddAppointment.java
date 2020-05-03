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
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mughees.appointmentmanager.MainActivity;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.patients.PatientModel;
import com.mughees.appointmentmanager.profile.DentistProfile;
import com.mughees.appointmentmanager.signing.SignIn;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class AddAppointment extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    ImageView img ;
    EditText name , date , time , tooth , treat , desc ;
    Button save ;
    String PatID;
    String ImageURI ;

    AppointmentModel apt =  new AppointmentModel();
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
        setContentView(R.layout.activity_add_appointment);

        Toolbar();
        GetPatientDetails();
        DatePick();
        TimePick();
        ToothPick();
        TreatPick();
        SaveAppointment();
    }


    private void GetPatientDetails()
    {
        img = findViewById(R.id.img);
        name = findViewById(R.id.name);
        desc = findViewById(R.id.desc);

        PatID = getIntent().getStringExtra("PatID");
        ImageURI = getIntent().getStringExtra("img");
        name.setText(getIntent().getStringExtra("PatName"));

        if (ImageURI.equals("N/A"))
        {
            img.setImageURI(Uri.parse("android.resource://com.mughees.appointmentmanager/drawable/patient"));
        }

        else
        {
            Picasso.get().load(ImageURI).into(img);
        }

    }

    private void DatePick()
    {
        date = findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance() ;
                year = c.get(Calendar.YEAR) ;
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH) ;

                datePickerDialog = new DatePickerDialog(AddAppointment.this, AddAppointment.this, year, month ,day);
                datePickerDialog.show();

            }
        });

    }


    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {

        year = y ;
        month = m + 1;
        day = d ;
        date.setText(day + "-" + month + "-" + year);
    }


    private void TimePick()
    {

        time = findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                hours = c.get(Calendar.HOUR);
                minutes = c.get(Calendar.MINUTE) ;

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddAppointment.this , AddAppointment.this, hours , minutes ,false);
                timePickerDialog.show();
            }
        });

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hr, int min) {

        hours = hr ;
        minutes = min ;
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


    private void ToothPick()
    {
        tooth = findViewById(R.id.tooth);
        ToothList = getResources().getStringArray(R.array.teeth);
        CheckedTeeth = new boolean[ToothList.length];

        tooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddAppointment.this);
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

    private void TreatPick()
    {
        treat = findViewById(R.id.treatment);
        TreatmentsList = getResources().getStringArray(R.array.treatments);
        CheckedTreatments = new boolean[TreatmentsList.length];

        treat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder Builder = new AlertDialog.Builder(AddAppointment.this);
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

    private void SaveAppointment()
    {
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(date.getText().toString()) || TextUtils.isEmpty(time.getText().toString())
                || TextUtils.isEmpty(tooth.getText().toString()) || TextUtils.isEmpty(treat.getText().toString()))
                {
                    Toast.makeText(AddAppointment.this, "All boxes except Description must be filled", Toast.LENGTH_LONG).show();
                }

                else
                {
                    String uniqueId = UUID.randomUUID().toString();
                    dbRef = FirebaseDatabase.getInstance().getReference("Appointments").child(PatID).child(uniqueId);

                    apt.setAdmin(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    apt.setPat_Id(PatID);
                    apt.setApp_Id(uniqueId);
                    apt.setDay(day);
                    apt.setMonth(month);
                    apt.setYear(year);
                    apt.setHrs(hours);
                    apt.setMins(minutes);
                    apt.setTooth(tooth.getText().toString());
                    apt.setTreatment(treat.getText().toString());
                    apt.setDescription(desc.getText().toString());

                    dbRef.setValue(apt);

                    Toast.makeText(AddAppointment.this , "Appointment added successfully" , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddAppointment.this, AppointmentsList.class);
                    startActivity(intent);
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
                Intent intent = new Intent(AddAppointment.this , SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        dentProfile = findViewById(R.id.DentProfile);
        dentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddAppointment.this , DentistProfile.class);
                startActivity(intent);
            }
        });
    }


}
