package com.example.bookshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity
{
    private EditText userEmail,userPassword,userConfirmPassword,userName,userFullname;
    private Button createAccountButton;
    private ProgressDialog LoadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private CircleImageView ProfileImage;
    private static final int Gallery_Pick=1;

    private StorageReference UserProfileImageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = (EditText) findViewById(R.id.register_email_id);
        userPassword = (EditText) findViewById(R.id.register_password_id);
        userConfirmPassword = (EditText) findViewById(R.id.register_confirmpass_id);
        userName = (EditText) findViewById(R.id.setup_username_id);
        userFullname = (EditText) findViewById(R.id.setup_fullname_id);
        ProfileImage = (CircleImageView)  findViewById(R.id.setup_profile_image_id);
        createAccountButton = (Button) findViewById(R.id.register_create_account_id);
        LoadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();

            }
        });
    }

    private void CreateNewAccount()
    {

        String user_email=userEmail.getText().toString();
        String user_password=userPassword.getText().toString();
        String confirm_password=userConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(user_email))
        {
            Toast.makeText(RegisterActivity.this,"Please Write Your Email...",Toast.LENGTH_SHORT).show();
        }

       else  if(TextUtils.isEmpty(user_password))
        {
            Toast.makeText(RegisterActivity.this,"Please Write Your Password...",Toast.LENGTH_SHORT).show();
        }

       else if(TextUtils.isEmpty(confirm_password))
        {
            Toast.makeText(RegisterActivity.this,"Please Confirm Your Password...",Toast.LENGTH_SHORT).show();
        }
        else if(!user_password.equals(confirm_password))
        {
            Toast.makeText(RegisterActivity.this,"Your Password Do Not Match with Your Confirm Password...",Toast.LENGTH_SHORT).show();
        }

        else
        {
            LoadingBar.setTitle("Saving Information");
            LoadingBar.setMessage("Please wait, while we are creating your new Account... ");
            LoadingBar.show();
            LoadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        SendUserToSetupActivity();
                        Toast.makeText(RegisterActivity.this,"You are authenticated successfully ..",Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this,"Error Occured.. "+message,Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();

                    }


                }
            });
        }
    }

    private void SendUserToSetupActivity()
    {
        Intent mainIntent=new Intent(RegisterActivity.this,SetupActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}
