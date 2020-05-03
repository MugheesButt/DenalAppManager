package com.mughees.appointmentmanager.signing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mughees.appointmentmanager.MainActivity;
import com.mughees.appointmentmanager.R;
import com.mughees.appointmentmanager.profile.DentistProfile;
import com.mughees.appointmentmanager.profile.ProfileModel;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpPage extends AppCompatActivity {


    Button RegButton;
    MaterialEditText UsernameText , EmailText , PasswordText ;
    FirebaseAuth auth ;
    DatabaseReference myDb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        Intialize();
        Register();
    }

    private void Intialize()
    {

        RegButton = findViewById(R.id.Register);
        UsernameText = findViewById(R.id.Username);
        EmailText = findViewById(R.id.Email);
        PasswordText = findViewById(R.id.Password);

        auth = FirebaseAuth.getInstance();
    }

    private void Register()
    {

        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = UsernameText.getText().toString().trim();
                String mail = EmailText.getText().toString();
                String pass = PasswordText.getText().toString();

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass))
                {
                    Toast.makeText(SignUpPage.this, "All fields must be filled" , Toast.LENGTH_LONG).show();
                }

                else if (pass.length() < 6)
                    Toast.makeText(SignUpPage.this, "Password length must be more than 5" , Toast.LENGTH_LONG).show();
                else
                {
                    Toast.makeText(SignUpPage.this , "Registering..." , Toast.LENGTH_SHORT).show();
                    register(name , mail, pass);
                }
            }
        });
    }



    private void register(final String username , String email , String  password)
    {
        auth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;
                            String userID = user.getUid();
                            myDb = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                            ProfileModel dentist = new ProfileModel();
                            dentist.setId(userID);
                            dentist.setName(username);

                            myDb.setValue(dentist).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                    {
                                        Intent intent = new Intent(SignUpPage.this , MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    }

                                }
                            });
                        }

                        else
                        {
                            Toast.makeText(SignUpPage.this, "Registration Failed" , Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }
}
