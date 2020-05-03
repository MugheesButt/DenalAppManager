package com.mughees.appointmentmanager.appointments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.patients.PatientModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.MyViewHolder> {

    ArrayList<AppointmentModel> appointmentList ;
    Context ctx ;

    public AppointmentsAdapter(Context ctx , ArrayList<AppointmentModel> appointmentList)
    {
        this.ctx = ctx;
        this.appointmentList = appointmentList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.appointment_row , parent, false);

        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        getPatientData(appointmentList.get(position).Pat_Id , holder.name , holder.img , holder.phone);
        holder.date.setText(appointmentList.get(position).day + "-" + appointmentList.get(position).month + "-" + appointmentList.get(position).year);
        holder.time.setText(TimeCheck(appointmentList.get(position).hrs, appointmentList.get(position).mins));

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ctx , ViewAppointment.class);
                intent.putExtra("PatID" , appointmentList.get(position).getPat_Id());
                intent.putExtra("AptID" , appointmentList.get(position).getApp_Id());
                ctx.startActivity(intent);
            }
        });

        holder.row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                //builder.setMessage("Are you sure you want to delete?").setTitle("Delete?");

                AlertDialog dialog;

                builder.setMessage("Delete appointment '" + TimeCheck(appointmentList.get(position).getHrs(), appointmentList.get(position).getMins()) + "' ?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Appointments").
                                        child(appointmentList.get(position).getPat_Id());
                                reference.child(appointmentList.get(position).getApp_Id()).removeValue();
                            }
                        });

                dialog = builder.create();
                dialog.show();

                return true;
            }
        });

    }


    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    private void getPatientData(String PatID , final TextView Name , final ImageView Image, final TextView Phone)
    {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Patients").child(PatID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                PatientModel patientModel = dataSnapshot.getValue(PatientModel.class);
                Name.setText(patientModel.getName());
                Phone.setText(patientModel.getPhone());


                if (patientModel.getImg().equals("N/A"))
                {
                    Image.setImageURI(Uri.parse("android.resource://com.mughees.appointmentmanager/drawable/patient"));
                }
                else
                {
                    Picasso.get().load(patientModel.getImg()).into(Image);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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




    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name , phone , time , date ;
        ImageView img ;
        LinearLayout row ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            img = itemView.findViewById(R.id.AptPatImg);
            row = itemView.findViewById(R.id.AppointmentRow);
        }
    }
}

