package com.mughees.appointmentmanager.patients;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mughees.appointmentmanager.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


public class PatientsListAdapter extends RecyclerView.Adapter<PatientsListAdapter.ListViewHolder> {

    private ArrayList<PatientModel> PatientsList ;
    private Context ctx ;

    PatientsListAdapter(ArrayList<PatientModel> PatientsList, Context ctx)
    {
        this.PatientsList = PatientsList ;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.patient_row , parent, false);

        ListViewHolder vh = new ListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, final int position) {

        holder.name.setText(PatientsList.get(position).Name);
        holder.phone.setText(PatientsList.get(position).Phone);

        if (PatientsList.get(position).getImg().equals("N/A"))
        {
            holder.PatImg.setImageURI(
                    Uri.parse("android.resource://com.mughees.appointmentmanager/drawable/patient"));
        }

        else
        {
            Picasso.get().load(PatientsList.get(position).Img).
                    networkPolicy(NetworkPolicy.OFFLINE).into(holder.PatImg, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                    Picasso.get().load(PatientsList.get(position).Img).into(holder.PatImg);

                }
            });
        }


        holder.PatientRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ctx , ViewPatient.class);
                intent.putExtra("PatID", PatientsList.get(position)._ID);
                ctx.startActivity(intent);
            }
        });

        holder.PatientRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                //builder.setMessage("Are you sure you want to delete?").setTitle("Delete?");

                AlertDialog dialog;

                builder.setMessage("Delete patient '" + PatientsList.get(position).getName() + "' ?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Appointments");
                                ref.child(PatientsList.get(position).get_ID()).removeValue();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Patients");
                                reference.child(PatientsList.get(position).get_ID()).removeValue();
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
        return PatientsList.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout PatientRow ;
        public TextView name , phone ;
        public ImageView PatImg;


        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            PatientRow = itemView.findViewById(R.id.PatientRow);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            PatImg = itemView.findViewById(R.id.PatImg);

        }
    }

}


