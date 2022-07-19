package com.example.messengert.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.messengert.MainActivity;
import com.example.messengert.databinding.ActivityStartLayoutBinding;
import com.example.messengert.models.User;
import com.example.messengert.sqllite.databaselite;
import com.example.messengert.util.Constants;
import com.example.messengert.util.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class StartLayout extends AppCompatActivity {
    ActivityStartLayoutBinding binding;
    private PreferenceManager preferenceManager;
    private databaselite db =null;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding =ActivityStartLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadUser();

    }
    void loadUser(){
        user =(User) getIntent().getSerializableExtra(Constants.KEY_USER);
//        databaselite db= new databaselite(getApplicationContext(),"usernotifycation.sqlite",null,1);
//        db.QueryDb("CREATE DATABASE IF NOT EXISTS USER(id varchar(50),name varchar(20),token varchar(100)");
//        db.addUser(user.token,user.id,user.name);
        if(user!=null){
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra(Constants.KEY_USER,user);
            startActivity(intent);
        }else{
            Thread wait = new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(1000);
                    }catch (Exception e){

                    }finally {
                        initDB();
                    }
                }
            };

            wait.start();
        }

    }
    void initDB(){
        String  token="";
        try{databaselite db= new databaselite(getApplicationContext(),"autodangnhap.sqlite",null,1);
            Cursor login = db.getToken("SELECT * FROM LOGIN");

            while(login.moveToNext()){
                token = login.getString(0);
            }
        }catch (Exception e){
            System.out.printf(e.toString());
        }

        if(token==""){
            Intent intent = new Intent(getApplicationContext(), SignInActivities.class);

            startActivity(intent);

        }else {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            String finalToken = token;
            database.collection(Constants.KEY_COLLECTION_USER).get().addOnCompleteListener(
                    task->{
                        if(task.isComplete()&&task.getResult()!=null){
                            for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                                if(finalToken.equals(queryDocumentSnapshot.getId())){
                                    preferenceManager.putBoolen(Constants.KEY_IS_SIGNED_IN, true);
                                    preferenceManager.putString(Constants.KEY_USERID,queryDocumentSnapshot.getId());
                                    preferenceManager.putString(Constants.KEY_NAME,queryDocumentSnapshot.getString(Constants.KEY_NAME));
                                    preferenceManager.putString(Constants.KEY_IMAGE,queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    break;
                                }
                            }
                        }

                    });
        }
    }
}