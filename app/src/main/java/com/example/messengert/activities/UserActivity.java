package com.example.messengert.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.example.messengert.listener.userListenner;
import com.example.messengert.Adapter.UserAdapter;
import com.example.messengert.MainActivity;
import com.example.messengert.databinding.ActivityUserBinding;
import com.example.messengert.models.User;
import com.example.messengert.util.Constants;
import com.example.messengert.util.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends basicActivity implements userListenner {
    ActivityUserBinding binding;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        listener();
        //getUser();
        searchUser();
    }
    private void listener(){
        binding.backUser.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
    }

    private void getUser(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USER).get().addOnCompleteListener(
                task->{
                    loading(false);
                    String currentUserid= preferenceManager.getString(Constants.KEY_USERID);
                    if(task.isComplete()&&task.getResult()!=null){
                        List<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserid.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.email =queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image=queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.name=queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.token=queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size()>0){
                            UserAdapter userAdapter = new UserAdapter(users,this);
                            binding.setUserAdapter.setAdapter(userAdapter);
                            binding.setUserAdapter.setVisibility(View.VISIBLE);
                        }else{
                            showMessageError();
                        }
                    }else{
                        showMessageError();
                    }

                }
        ).addOnFailureListener(e->{
            showMessageError();
        });


    }
    private void loading(Boolean a){
        if(a){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void showMessageError(){
        binding.textError.setText(String.format("%s","No user available"));
        binding.textError.setVisibility(View.VISIBLE);
    }

    @Override
    public void userClicked(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
    private void searchUser(){
        binding.frameSearch.setOnClickListener(v -> {
            loading(true);
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                database.collection(Constants.KEY_COLLECTION_USER).get().addOnCompleteListener(
                        task->{
                        loading(false);
                        String currentUserid= preferenceManager.getString(Constants.KEY_USERID);
                        if(task.isComplete()&&task.getResult()!=null){
                            List<User> users = new ArrayList<>();
                            for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                if(currentUserid.equals(queryDocumentSnapshot.getId())){
                                    continue;
                                }
                                String a =queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                String b=binding.searchUser.getText().toString();
                                if(binding.searchUser.getText().toString().equals(queryDocumentSnapshot.getString(Constants.KEY_EMAIL))){
                                    User user = new User();
                                    user.email =queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                    user.image=queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                    user.name=queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                    user.token=queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                    user.id = queryDocumentSnapshot.getId();
                                    users.add(user);
                                }

                            }
                            if(users.size()>0){
                                UserAdapter userAdapter = new UserAdapter(users,this);
                                binding.setUserAdapter.setAdapter(userAdapter);
                                binding.setUserAdapter.setVisibility(View.VISIBLE);
                                binding.textError.setText(null);
                            }else{
                                showMessageError();
                            }
                        }else{
                            showMessageError();
                        }

                    }
            ).addOnFailureListener(e->{
                showMessageError();
            });

        });
    }
}
