package com.example.bookshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton SelectPostImage;
    private Button UpdatePostbutton;
    private EditText PostDescription;
    private StorageReference PostImagesReference;
    private String saveCurrentDate,saveCurrentTime,postRandomName,currentUserId;
    private DatabaseReference Usersref,PostRef;
    private FirebaseAuth mAuth;
    private static final int Gallery_Pick=1;
    private Uri ImageUri;
    private String Description;
    private ProgressDialog LoadingBar;
    private StorageReference Filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mToolbar =(Toolbar) findViewById(R.id.updatePostToolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

        SelectPostImage=(ImageButton) findViewById(R.id.selectbookImageId);
        UpdatePostbutton=(Button) findViewById(R.id.updatepostbuttonId);
        PostDescription=(EditText) findViewById(R.id.postdescriptionId);

        //Firebase Auth

        mAuth=FirebaseAuth.getInstance();

        currentUserId=mAuth.getCurrentUser().getUid();

        //Firebase Storageref

        PostImagesReference= FirebaseStorage.getInstance().getReference();

        //firebASE database ref

        Usersref= FirebaseDatabase.getInstance().getReference().child("Users");

        PostRef= FirebaseDatabase.getInstance().getReference().child("Posts");




        LoadingBar=new ProgressDialog(this);

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();

            }
        });

        UpdatePostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidatePostInfo();

            }
        });
    }

    private void ValidatePostInfo()
    {
        Description=PostDescription.getText().toString();

        if(ImageUri==null)
        {
            Toast.makeText(PostActivity.this,"Please Choose Your Book Image ....",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(PostActivity.this,"Please Say Something About Your Book ....",Toast.LENGTH_SHORT).show();
        }
        else
        {
            LoadingBar.setTitle("Add new post");
            LoadingBar.setMessage("Please wait, while we are updating your new post.. ");
            LoadingBar.show();
            LoadingBar.setCanceledOnTouchOutside(true);
            StoringImageToFireBaseStorage();
            // UploadImage();
        }
    }

    private void StoringImageToFireBaseStorage()
    {
        Calendar CallFordate=Calendar.getInstance();
        SimpleDateFormat CurrentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate=CurrentDate.format(CallFordate.getTime());

        Calendar CallForTime=Calendar.getInstance();
        SimpleDateFormat CurrentTime=new SimpleDateFormat("HH:mm");
        saveCurrentTime=CurrentTime.format(CallForTime.getTime());

        postRandomName=saveCurrentDate+saveCurrentTime;


        Filepath = PostImagesReference.child("postimages").child(ImageUri.getLastPathSegment()+postRandomName+".jpg");


        Filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        HashMap<String,String>hashMap=new HashMap<>();
                        hashMap.put("postimage", String.valueOf(uri));

                        PostRef.child(currentUserId + postRandomName).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Toast.makeText(PostActivity.this,"finally completed",Toast.LENGTH_SHORT).show();
                                SavingUserInformationToDatabase();

                            }
                        });

                    }
                });


            }
        });
    }

    private void SavingUserInformationToDatabase()
    {
        Usersref.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String Userfullname=dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImage=dataSnapshot.child("profileimgurl").getValue().toString();
                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", currentUserId);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", Description);
                     postsMap.put("profileimgurl",userProfileImage);
                    postsMap.put("fullname", Userfullname);

                    PostRef.child(currentUserId + postRandomName).updateChildren(postsMap).
                            addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        SendUserToMainActivity();
                                        Toast.makeText(PostActivity.this, "New post is updated successfully to storage...", Toast.LENGTH_SHORT).show();
                                        LoadingBar.dismiss();

                                    }
                                    else {

                                        Toast.makeText(PostActivity.this, "Error occured while updating...", Toast.LENGTH_SHORT).show();
                                        LoadingBar.dismiss();


                                    }

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_Pick&& resultCode==RESULT_OK && data !=null)
        {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), Gallery_Pick);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id =item.getItemId();
        if(id == android.R.id.home);
        {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);

    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(PostActivity.this,MainActivity.class);
        startActivity(mainIntent);

    }
}
