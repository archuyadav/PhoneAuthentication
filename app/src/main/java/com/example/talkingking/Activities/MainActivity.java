package com.example.talkingking.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.talkingking.Model.User;
import com.example.talkingking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private Button btnSave;
    private EditText editName;
    private ImageView profileImage;
    private FirebaseAuth auth;
   private FirebaseDatabase database;
    private FirebaseStorage storage;
   private Uri selectedImage;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        editName=findViewById(R.id.editName);
        profileImage=findViewById(R.id.profilePic);
        btnSave=findViewById(R.id.btnSave);


        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();


        loadingDialog=new Dialog(MainActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


         if (auth.getCurrentUser()!=null){
        Intent i=new Intent(MainActivity.this,HomeActivity.class);
        startActivity(i);
         finish();
         }


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,45);

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();


                if (name.isEmpty()) {

                    editName.setError("Please Enter name");
                    return;
                }

                loadingDialog.show();
                if (selectedImage != null) {

                    StorageReference reference = storage.getReference().child("profiles").child(auth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl=uri.toString();

                                        String uid=auth.getUid();
                                        String phone=auth.getCurrentUser().getPhoneNumber();
                                        String name=editName.getText().toString();




                                        User user=new User(uid,name,phone,imageUrl);

                                        database.getReference().child("users").child(uid).setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        loadingDialog.dismiss();

                                                        Intent i=new Intent(MainActivity.this,HomeActivity.class);
                                                        startActivity(i);
                                                        finish();

                                                    }
                                                });

                                    }
                                });
                            }
                        }


                    });
                }else{
                    String uid=auth.getUid();
                    String phone=auth.getCurrentUser().getPhoneNumber();

                    User user=new User(uid,name,phone,"No Image");

                    database.getReference().child("users").child(uid).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    loadingDialog.dismiss();

                                    Intent i=new Intent(MainActivity.this,HomeActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            });

                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null){
            if (data.getData()!=null){
                profileImage.setImageURI(data.getData());
               selectedImage=data.getData();
            }
        }


    }

    }
