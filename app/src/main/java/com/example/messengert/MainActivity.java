package com.example.messengert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messengert.activities.ChatActivity;
import com.example.messengert.activities.SignInActivities;
import com.example.messengert.activities.UserActivity;
import com.example.messengert.activities.basicActivity;

import com.example.messengert.listener.chatListener;
import com.example.messengert.listener.userListenner;
import com.example.messengert.models.ChatMessage;
import com.example.messengert.models.User;
import com.example.messengert.models.listConversion;
import com.example.messengert.sqllite.databaselite;
import com.example.messengert.util.Constants;
import com.example.messengert.util.PreferenceManager;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.example.messengert.Adapter.RecentConversionAdapter;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends basicActivity implements chatListener {

    private PreferenceManager preferenceManager;
    private List<ChatMessage> Conversions;
    private RecentConversionAdapter resentConversionAdapter;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigation;
    private ProgressBar progressBar;
    private AppCompatImageView imageSignOut;
    private AppCompatImageView imageAddFriend;
    private TextView textName;
    private RoundedImageView imageProfile;
    private ViewPager viewpager;
    private TextView textUpdate;
    private RoundedImageView imageViewClick;
    private FrameLayout layoutimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager =new PreferenceManager(getApplicationContext()) ;
        setContentView(R.layout.activity_main);
        //viewpager = findViewById(R.id.viewpager);
        recyclerView = findViewById(R.id.showrc);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        progressBar = findViewById(R.id.progressBar);
        imageSignOut = findViewById(R.id.imageSignOut);
        imageAddFriend= findViewById(R.id.imageAddFriend);
        textName = findViewById(R.id.textName);
        imageProfile =findViewById(R.id.imageProfile);
        textUpdate = findViewById(R.id.textWait);
        imageViewClick = findViewById(R.id.imageClickSeen);
        layoutimage = findViewById((R.id.layoutImage));
        init();
        loadInfo();
        getToken();
        listener();
        showRecrycle();;
//        setViewPager();
         bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.actionHistory:
                         //viewpager.setCurrentItem(1);
                        textUpdate.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case R.id.actionHome:
                        //viewpager.setCurrentItem(0);
                        recyclerView.setVisibility(View.VISIBLE);
                        textUpdate.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });

    }

    private void setViewPager() {
        viewPagerAdapter vpa = new viewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewpager.setAdapter(vpa);
    }

    void init(){
        Conversions = new ArrayList<>();
       resentConversionAdapter = new RecentConversionAdapter(Conversions,this);
        recyclerView.setAdapter(resentConversionAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    private void showRecrycle(){
        String a=preferenceManager.getString(Constants.KEY_USERID);
        String b=preferenceManager.getString(Constants.KEY_USERID);
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSION)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USERID))
                .addSnapshotListener(eventListener);
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSION)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USERID))
                .addSnapshotListener(eventListener);
    }
    private EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        if(error!=null){
            return;
        }
        if(value!=null){
            for(DocumentChange dc :value.getDocumentChanges()){
                if(dc.getType()== DocumentChange.Type.ADDED){
                    ChatMessage chatMessage= new ChatMessage();
                    chatMessage.message = dc.getDocument().getString(Constants.KEY_MESSAGE_LAST);
                    chatMessage.senderId = dc.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = dc.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.oldSent=dc.getDocument().getString(Constants.KEY_OLD_SENT);
                    chatMessage.getID =dc.getDocument().getId();
                    chatMessage.userId=preferenceManager.getString(Constants.KEY_USERID);
                    if(preferenceManager.getString(Constants.KEY_USERID).equals(dc.getDocument().getString(Constants.KEY_SENDER_ID))){
                        chatMessage.conversionImage = dc.getDocument().getString(Constants.KEY_IMAGE_RECEIVER);
                        chatMessage.conversionName =dc.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = dc.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        chatMessage.seen=dc.getDocument().getString(Constants.KEY_ID_SEEN);
                    }else{
                        chatMessage.conversionImage = dc.getDocument().getString(Constants.KEY_IMAGE);
                        chatMessage.conversionName =dc.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = dc.getDocument().getString(Constants.KEY_SENDER_ID);
                        chatMessage.conversionSeen=dc.getDocument().getString(Constants.KEY_RECEIVER_ID_SEEN);
                    }
                    chatMessage.OjectDate =dc.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    Conversions.add(chatMessage);
                }else{

                    if(dc.getType()== DocumentChange.Type.MODIFIED){
                        for(int i=0;i<Conversions.size();i++){
                        String senderId = dc.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = dc.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(Conversions.get(i).senderId.equals(senderId) &&Conversions.get(i).receiverId.equals(receiverId)){
                            Conversions.get(i).message = dc.getDocument().getString(Constants.KEY_MESSAGE_LAST);
                            Conversions.get(i).OjectDate =dc.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            Conversions.get(i).oldSent=dc.getDocument().getString(Constants.KEY_OLD_SENT);
                            if(preferenceManager.getString(Constants.KEY_USERID).equals(dc.getDocument().getString(Constants.KEY_ID_SEEN))){
                                Conversions.get(i).seen=dc.getDocument().getString(Constants.KEY_ID_SEEN);
                            }else{
                                Conversions.get(i).conversionSeen=dc.getDocument().getString(Constants.KEY_RECEIVER_ID_SEEN);
                            }
                            break;
                        }
                    }

                    }
                }
            }

            Collections.sort(Conversions,(obj1,obj2)->obj1.OjectDate.compareTo(obj2.OjectDate));
                resentConversionAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

        }}
    };
    private void listener(){

         imageSignOut.setOnClickListener(v->{
            SignOut();
        });
         imageAddFriend.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
        });


    }
    private void loadInfo(){
        textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        imageProfile.setImageBitmap(bitmap);
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message , Toast.LENGTH_SHORT).show();
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);

    }
    private void updateToken(String token){
        preferenceManager.putString(Constants.KEY_FCM_TOKEN,token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USER).document(
                preferenceManager.getString(Constants.KEY_USERID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN,token).addOnSuccessListener(
                unused-> {
                }

        ).addOnFailureListener(e->showToast("Unable to update token"));
    }
    private void SignOut(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USER).document(
                preferenceManager.getString(Constants.KEY_USERID)
        );
         try{
             databaselite db= new databaselite(getApplicationContext(),"autodangnhap.sqlite",null,1);
             db.deleteToken();
         }catch (Exception e){
             System.out.printf(e.toString());
         }

        HashMap<String,Object> data = new HashMap<>();
        data.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(data).addOnSuccessListener(unused -> {
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(), SignInActivities.class));
            finish();
        }).addOnFailureListener(e->showToast("Unable SignOut"));
    }


    @Override
    public void chatLinstener(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,  user);
        startActivity(intent);
        finish();
    }
}