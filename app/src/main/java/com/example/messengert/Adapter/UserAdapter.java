package com.example.messengert.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengert.databinding.ItemContanerUserBinding;
import com.example.messengert.models.User;

import  com.example.messengert.listener.userListenner;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<User> dsUser;
    private final userListenner UserListener;
    public UserAdapter(List<User> dsUser,userListenner UserListener){
        this.UserListener=UserListener;
        this.dsUser=dsUser;
    };


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContanerUserBinding itemContanerUserBinding= ItemContanerUserBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new UserViewHolder(itemContanerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setDataUser(dsUser.get(position));
    }

    @Override
    public int getItemCount() {
        return dsUser.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContanerUserBinding binding;
        UserViewHolder(ItemContanerUserBinding itemContanerUserBinding){
            super(itemContanerUserBinding.getRoot());
            binding= itemContanerUserBinding;

        };
       void  setDataUser(User user){
            binding.textEmail.setText(user.email);
            binding.textName.setText(user.name);
            binding.imageProfile.setImageBitmap(getImageUser(user.image));
            binding.getRoot().setOnClickListener(v->UserListener.userClicked(user));
        }
        void setMessageUser(User user,String Message){
            binding.textEmail.setText(Message);
            binding.textName.setText(user.name);
            binding.imageProfile.setImageBitmap(getImageUser(user.image));
            binding.getRoot().setOnClickListener(v->UserListener.userClicked(user));
        }
    }
    public Bitmap getImageUser(String image){
        byte[] bytes= Base64.decode(image,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return bitmap;
    }
}
