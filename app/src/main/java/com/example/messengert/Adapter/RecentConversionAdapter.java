package com.example.messengert.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.messengert.R;
import com.example.messengert.listener.chatListener;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengert.databinding.ItemContanerUserRecentBinding;
import com.example.messengert.databinding.ItemReceivedMessageBinding;
import com.example.messengert.listener.chatListener;
import com.example.messengert.models.ChatMessage;
import com.example.messengert.models.User;
import com.example.messengert.sqllite.databaselite;
import com.example.messengert.util.Constants;
import com.example.messengert.util.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RecentConversionAdapter extends RecyclerView.Adapter<RecentConversionAdapter.ConversionMessage>  {
    private Bitmap bitmapImage;
    private List<ChatMessage> chatMessage;
    private chatListener ChatListener;

    public RecentConversionAdapter(List<ChatMessage> chatMessage,chatListener chatlistener) {
        this.chatMessage = chatMessage;
        ChatListener=chatlistener;
    }

    public Bitmap bitmapForEncode(String image){
        byte[] bytes = Base64.decode(image,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    @NonNull
    @Override
    public ConversionMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionMessage(ItemContanerUserRecentBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionMessage holder, int position) {
        holder.setMessage(chatMessage.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessage.size();
    }

    class   ConversionMessage extends RecyclerView.ViewHolder{
        private ItemContanerUserRecentBinding binding;
        ConversionMessage(ItemContanerUserRecentBinding itemContanerUserRecentBinding){
            super(itemContanerUserRecentBinding.getRoot());
            binding=itemContanerUserRecentBinding;
        }
        void setMessage(ChatMessage message){
            String temp="";
            binding.textName.setText(message.conversionName);
            binding.imageProfile.setImageBitmap(bitmapForEncode(message.conversionImage));
            if(message.userId.equals(message.oldSent)){
                binding.textRecent.setText("Bạn: "+message.message);
            }else{
                binding.textRecent.setText(message.message);
            }

            if(message.seen!=null ){
                temp="user";
                binding.textRecent.setTextColor(Color.rgb(200,0,0));
            }else if(message.conversionSeen!=null){
                temp="Receiver";
                binding.textRecent.setTextColor(Color.rgb(200,0,0));
            }else{
                binding.textRecent.setTextColor(Color.rgb(0,0,0));
            }

            String finalTemp = temp;
            binding.getRoot().setOnClickListener(v->{
                User user = new User();
                user.id=message.conversionId;
                user.name=message.conversionName;
                user.image = message.conversionImage;
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                if(finalTemp =="user"){
                    database.collection(Constants.KEY_COLLECTION_CONVERSION).document(message.getID).update(

                            Constants.KEY_ID_SEEN,null
                    );
                }else if(finalTemp=="receiver"){
                    database.collection(Constants.KEY_COLLECTION_CONVERSION).document(message.getID).update(
                            Constants.KEY_RECEIVER_ID_SEEN,null

                    );
                }else{
                            database.collection(Constants.KEY_COLLECTION_CONVERSION).document(message.getID).update(
                                    Constants.KEY_RECEIVER_ID_SEEN,null,
                                    Constants.KEY_ID_SEEN,null
                            );
                        }



                ChatListener.chatLinstener(user);
            }
            );
        }
    }


}
