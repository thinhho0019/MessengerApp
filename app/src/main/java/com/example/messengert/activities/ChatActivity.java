package com.example.messengert.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.messengert.Adapter.ChatAdapter;

import com.example.messengert.MainActivity;
import com.example.messengert.databinding.ActivityChatBinding;
import com.example.messengert.models.ChatMessage;
import com.example.messengert.models.User;
import com.example.messengert.network.apiClient;
import com.example.messengert.network.apiService;
import com.example.messengert.util.Constants;
import com.example.messengert.util.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends basicActivity {
    ActivityChatBinding  binding;
    private User Receiveruser;
    private List<ChatMessage> chatMessageList;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversionId=null;
    int online=0;
    String oldSent_click;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetail();
        setListener();

        init();
        //showMessage();
        listenMessage();
        //checkOn();


    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    private void sendNotification(String messageBody){

        apiClient.getClient().create(apiService.class).sendMessage(
                Constants.getRemoteMsgHeader(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                 if(response.isSuccessful()){
                    try{
                        if(response.body() !=null){
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONArray result = jsonObject.getJSONArray("results");
                            if(jsonObject.getInt("failure")==1){
                                JSONObject error =(JSONObject) result.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                 }else{

                 }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }
    private void init(){
        try{
            preferenceManager = new PreferenceManager(getApplicationContext());
            chatMessageList = new ArrayList<>();
            chatAdapter= new ChatAdapter(bitmapFromEncodeString(Receiveruser.image),chatMessageList,preferenceManager.getString(Constants.KEY_USERID));
            binding.chatRecycler.setAdapter(chatAdapter);
            database = FirebaseFirestore.getInstance();
        }catch (Exception e){
            System.out.printf(e.toString());
        }


    }
    private void chatMessage(){

        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USERID));
        message.put(Constants.KEY_RECEIVER_ID,Receiveruser.id);
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        if(conversionId!=null){
            updateConversion(binding.inputMessage.getText().toString());
        }else{

            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USERID));
            hashMap.put(Constants.KEY_SENDER_NAME,preferenceManager.getString(Constants.KEY_NAME));
            hashMap.put(Constants.KEY_RECEIVER_NAME,Receiveruser.name);
            hashMap.put(Constants.KEY_IMAGE,preferenceManager.getString((Constants.KEY_IMAGE)));
            hashMap.put(Constants.KEY_RECEIVER_ID,Receiveruser.id);
            hashMap.put(Constants.KEY_IMAGE_RECEIVER,Receiveruser.image);
            hashMap.put(Constants.KEY_MESSAGE_LAST,binding.inputMessage.getText().toString());
            hashMap.put(Constants.KEY_TIMESTAMP,new Date());
            hashMap.put(Constants.KEY_RECEIVER_ID_SEEN,Receiveruser.id);
            hashMap.put(Constants.KEY_ID_SEEN,preferenceManager.getString(Constants.KEY_USERID));
            hashMap.put(Constants.KEY_OLD_SENT,preferenceManager.getString(Constants.KEY_USERID));

            addConversion(hashMap);
        }
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(online==0){
            try{
//                JSONArray tokens = new JSONArray();
//                tokens.put(Receiveruser.token);
                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USERID,preferenceManager.getString(Constants.KEY_USERID));
                data.put(Constants.KEY_NAME,preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
                data.put(Constants.KEY_FCM_TOKEN,preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MESSAGE_DATA,data);
                body.put("to",Receiveruser.token);
                body.put("direct_boot_ok",true);
                Log.e("String json",body.toString());
                sendNotification(body.toString());
            }catch (Exception e){
                showToast(e.getMessage());
            }
        }
        oldSent_click=preferenceManager.getString(Constants.KEY_USERID);
        binding.inputMessage.setText(null);

    }
    void addConversion(HashMap<String,Object> hashMap){
        database.collection(Constants.KEY_COLLECTION_CONVERSION).add(hashMap).addOnSuccessListener(documentReference -> {
            conversionId = documentReference.getId();
        });
    }
    void updateConversion(String message){
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSION).document(conversionId);
            documentReference.update(
                    Constants.KEY_MESSAGE_LAST ,message,
                    Constants.KEY_TIMESTAMP ,new Date(),
                    Constants.KEY_ID_SEEN,preferenceManager.getString(Constants.KEY_USERID),
                    Constants.KEY_RECEIVER_ID_SEEN,Receiveruser.id,
                    Constants.KEY_OLD_SENT,preferenceManager.getString(Constants.KEY_USERID)
        );
    }
    private void checkOn(){
        database.collection(Constants.KEY_COLLECTION_USER).document(Receiveruser.id).addSnapshotListener(
                ChatActivity.this,(v,e)->{
                    if(e !=null){
                        Log.w(TAG, "Listen failed.", e );
                        return;
                    }
                    if(v !=null){
                        if(v.getLong(Constants.KEY_AVAILABILITY) !=null){
                             online = Objects.requireNonNull(v.getLong(Constants.KEY_AVAILABILITY).intValue());
                        }

                        Receiveruser.token=v.getString(Constants.KEY_FCM_TOKEN);
                        if(Receiveruser.image==null){
                            Receiveruser.image=v.getString(Constants.KEY_IMAGE);

                        }
                    }
                    if(online==0){
                        binding.availability.setVisibility(View.GONE);
                    }else{
                        binding.availability.setVisibility(View.VISIBLE);
                        chatAdapter.setBitmapReceiverProfile(bitmapFromEncodeString(Receiveruser.image));
                        chatAdapter.notifyItemRangeChanged(0,chatMessageList.size());
                    }
                }
        );
    }
    private void listenMessage(){
        String a=preferenceManager.getString(Constants.KEY_USERID);
        String b=Receiveruser.id;
        database.collection("chat")
                .whereEqualTo("senderId",preferenceManager.getString(Constants.KEY_USERID))
                .whereEqualTo("receiverId",Receiveruser.id)
                .addSnapshotListener(eventListener);
        database.collection("chat")
                .whereEqualTo("senderId",Receiveruser.id)
                .whereEqualTo("receiverId",preferenceManager.getString(Constants.KEY_USERID))
                .addSnapshotListener(eventListener);

    }
    private final EventListener<QuerySnapshot> eventListener = new  EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(error!=null){
                Log.w(TAG, "Listen failed.", error);
                return;
            }
            int count = chatMessageList.size();
            for(DocumentChange dc :value.getDocumentChanges()){
                if(dc.getType()==DocumentChange.Type.ADDED){
                    ChatMessage chatMessage= new ChatMessage();
                    chatMessage.senderId = dc.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId =dc.getDocument().getString(Receiveruser.id);
                    chatMessage.message = dc.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.datetime = getReadableDateTime(dc.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.OjectDate = dc.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessageList.add(chatMessage);
                }

            }
            Collections.sort(chatMessageList, (obj1, obj2) -> obj1.OjectDate.compareTo(obj2.OjectDate));//sort list by datetime
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessageList.size(), chatMessageList.size());

                binding.chatRecycler.smoothScrollToPosition(chatMessageList.size() - 1);//Nếu muốn trượt mềm mại dùng smoothScrollToPosition()
            }

            binding.chatRecycler.setVisibility(View.VISIBLE);

            binding.progressBar.setVisibility(View.GONE);
            if(conversionId==null){
                CheckForConversion();
            }
        }

    };

    private Bitmap bitmapFromEncodeString(String encode){
        if(encode!=null){
            byte[] bytes = Base64.decode(encode,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }else{
            return null;
        }

    }
    public void loadReceiverDetail (){
        Receiveruser  = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textInfo.setText(Receiveruser.name);
    }
    private void setListener(){
        binding.backUser.setOnClickListener(v->{
            if(preferenceManager.getString(Constants.KEY_USERID).equals(oldSent_click)){
                database.collection(Constants.KEY_COLLECTION_CONVERSION).document(conversionId).update(
                        Constants.KEY_ID_SEEN,null
                );
            }else{
                database.collection(Constants.KEY_COLLECTION_CONVERSION).document(conversionId).update(
                        Constants.KEY_RECEIVER_ID_SEEN,null,
                        Constants.KEY_ID_SEEN,null
                );
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
        binding.layoutSent.setOnClickListener(v->chatMessage());
    }
    private void loading(Boolean a){
        if(a==false){
            binding.chatRecycler.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }else{
            binding.chatRecycler.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }
    private String getReadableDateTime(Date time){
        SimpleDateFormat sdt = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdt.format(time);
    }
    private void CheckForConversion(){
        if(chatMessageList.size()>0){
            CheckConversionDocument(
                    preferenceManager.getString(Constants.KEY_USERID),
                    Receiveruser.id
            );
            CheckConversionDocument(
                    Receiveruser.id,
                    preferenceManager.getString(Constants.KEY_USERID)

            );
        }
    }
    private void CheckConversionDocument(String senderId,String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSION).whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId).get().addOnCompleteListener(onCompleteListener);
    }
    private final OnCompleteListener<QuerySnapshot> onCompleteListener = task->{ //lưu đầu tiên
        if(task.isComplete() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
            DocumentSnapshot documentSnapshot =task.getResult().getDocuments().get(0);
            conversionId=documentSnapshot.getId();
        };
    };

    @Override
    protected void onResume() {
        super.onResume();
        checkOn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkOn();
    }
}