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
import com.mughees.appointmentmanager.MainActivity;
import com.mughees.appointmentmanager.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {


    MaterialEditText email , password ;
    Button signin , signup;
    FirebaseAuth auth ;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Intialize();
        SignIn();
        SignUp();
    }

    private void Intialize()
    {
        email = findViewById(R.id.EmailIn);
        password = findViewById(R.id.PasswordIn);
        signin = findViewById(R.id.SignIn);
        signup = findViewById(R.id.SignUp);
        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null)
        {
            Intent intent = new Intent(SignIn.this , MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


    private void SignIn()
    {

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String pass = password.getText().toString();

                if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass))
                {
                    Toast.makeText(SignIn.this , "All fields must be filled" , Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Toast.makeText(SignIn.this , "Signing In..." , Toast.LENGTH_SHORT).show();
                    login(mail, pass);
                }
            }
        });

    }

    private void login(String email , String password){

        auth.signInWithEmailAndPassword(email,password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(SignIn.this , MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                        else
                        {
                            Toast.makeText(SignIn.this , "Login Failed" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SignUp()
    {

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this , SignUpPage.class);
                startActivity(intent);
            }
        });
    }


}
