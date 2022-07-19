package com.example.messengert.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.messengert.MainActivity;
import com.example.messengert.R;
import com.example.messengert.databinding.ActivitySignInActivitiesBinding;
import com.example.messengert.sqllite.databaselite;
import com.example.messengert.util.Constants;
import com.example.messengert.util.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

public class SignInActivities extends AppCompatActivity {
    private ActivitySignInActivitiesBinding binding;
    private PreferenceManager preferenceManager;
    private  databaselite db =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivitySignInActivitiesBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());

        setListeners();
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message , Toast.LENGTH_SHORT).show();
    }
    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),SignUpActivities.class)));
//        binding.buttonSignIn.setOnClickListener(v -> {addDataFirebase();});
        binding.buttonSignIn.setOnClickListener(v->{
            if(isValidSignIn()){
                login();
            }
                });
    }

    private void login(){
        loadding(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USER).whereEqualTo(Constants.KEY_EMAIL,binding.InputEmail.getText().toString()).whereEqualTo(Constants.KEY_PASSWORD,binding.InputPassword.getText().toString())
                .get().addOnCompleteListener(task -> {
                    if(task.isComplete()&&task.getResult()!=null&&task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolen(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USERID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                        databaselite db= new databaselite(getApplicationContext(),"autodangnhap.sqlite",null,1);
                        db.QueryDb("CREATE TABLE IF NOT EXISTS LOGIN (ID CHAR(100))");
                        db.addToken(preferenceManager.getString(Constants.KEY_USERID));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
    }
    private void loadding(Boolean a){
        if(a){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private Boolean isValidSignIn(){
        if(binding.InputEmail.getText().toString().isEmpty()){
            showToast("Nhập email");
            return false;
        }else if(binding.InputPassword.getText().toString().isEmpty()){
            showToast("Nhập email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.InputEmail.getText().toString()).matches()){
            showToast("Email sai");
            return false;
        }
            else

        {
            return true;
        }
    }
//    private void addDataFirebase(){
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        HashMap<String,Object> data = new HashMap<>();
//        data.put("first_name","nobi");
//        data.put("last_name","ta");
//        database.collection("users").add(data).addOnSuccessListener(documentReference -> {
//            Toast.makeText(getApplicationContext(),"insert complete",Toast.LENGTH_SHORT).show();
//        }).addOnFailureListener(exception->{
//            Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();});
//    }
}