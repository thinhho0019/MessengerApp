package com.example.messengert.Adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengert.databinding.ItemReceivedMessageBinding;
import com.example.messengert.databinding.ItemSentMessageBinding;
import com.example.messengert.models.ChatMessage;
import com.example.messengert.util.Constants;
import com.example.messengert.util.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Bitmap bitmapReceiveProfile;
    private List<ChatMessage> chatMessageList;
    private String senderId;

    public void setBitmapReceiverProfile(Bitmap bitmap){
        bitmapReceiveProfile =bitmap;
    }
    public ChatAdapter(Bitmap bitmap,List<ChatMessage>chatMessageList,String senderId){
        bitmapReceiveProfile=bitmap;
        this.chatMessageList=chatMessageList;
        this.senderId=senderId;

    }
    private static  final int KEY_TYPE_SENT=1;
    private static final int KEY_TYPE_RECEIVE=2;

    @Override
    public int getItemViewType(int position) {
        if(chatMessageList.get(position).senderId.equals(senderId)){
            return KEY_TYPE_SENT;
        }else{
            return KEY_TYPE_RECEIVE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==KEY_TYPE_SENT){
            return new SendMessageViewHolder(ItemSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }else{
            return new ReceiveMessageViewHolder(ItemReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==KEY_TYPE_SENT){
            ((SendMessageViewHolder) holder).setData(chatMessageList.get(position));
        }else{
            ((ReceiveMessageViewHolder)holder).setData(chatMessageList.get(position),bitmapReceiveProfile);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    static class  SendMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemSentMessageBinding binding;
        SendMessageViewHolder(ItemSentMessageBinding itemSentMessageBinding){
            super(itemSentMessageBinding.getRoot());
            binding=itemSentMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDatetime.setText(chatMessage.datetime);
        }
    }
    static class ReceiveMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemReceivedMessageBinding binding;
        ReceiveMessageViewHolder(ItemReceivedMessageBinding itemReceivedMessageBinding){
            super(itemReceivedMessageBinding.getRoot());
            binding=itemReceivedMessageBinding;
        }
        void setData(ChatMessage chatMessage,Bitmap bitmap){
            binding.textMessage.setText(chatMessage.message);
            binding.textDatetime.setText(chatMessage.datetime);
            if(bitmap!=null){
                binding.imageProfile.setImageBitmap(bitmap);
            }


        }

    }

}
