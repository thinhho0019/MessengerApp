package com.example.messengert.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.messengert.MainActivity;
import com.example.messengert.R;
import com.example.messengert.databinding.ActivitySignInActivitiesBinding;
import com.example.messengert.databinding.ActivitySignUpActivitiesBinding;
import com.example.messengert.util.Constants;
import com.example.messengert.util.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

public class SignUpActivities extends AppCompatActivity {
    private ActivitySignUpActivitiesBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpActivitiesBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();
    }
    private void setListeners(){
        binding.textSignIn.setOnClickListener(v->startActivity(new Intent(getApplicationContext(),SignInActivities.class)));
        binding.buttonSignUp.setOnClickListener(v->{
            if(isValidSignUpDetails()==true){
                signUp();
            }
        });
        binding.imageProfile.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent) ;
        });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message , Toast.LENGTH_SHORT).show();
    }
    private void signUp(){
        loadding(true);
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();
        HashMap<String ,Object> users = new HashMap<>();
        users.put(Constants.KEY_NAME,binding.InputName.getText().toString());
        users.put(Constants.KEY_EMAIL,binding.InputEmail.getText().toString());
        users.put(Constants.KEY_PASSWORD,binding.InputPassword.getText().toString());
        users.put(Constants.KEY_IMAGE,encodedImage);
        firebase.collection("users").add(users).addOnSuccessListener(
                documentReference -> {
                    loadding(false);
//                    preferenceManager.putBoolen(Constants.KEY_IS_SIGNED_IN,true);
//                    preferenceManager.putString(Constants.KEY_USERID, documentReference.getId());
//                    preferenceManager.putString(Constants.KEY_NAME,binding.InputName.getText().toString());
//                    preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                    Intent intent = new Intent(getApplicationContext(), SignInActivities.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
        ).addOnFailureListener(exception->{
            showToast(exception.getMessage());
        });
    }
    private String encodeImage(Bitmap bitmap){
        int previewWidth=200;//tính toán
        int previewHeight=bitmap.getHeight()*previewWidth/bitmap.getWidth();//tính toán
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false); //thêm vào bitmap khi có rộng và cao
        ByteArrayOutputStream byteArrayOutPutStream = new ByteArrayOutputStream();//khởi tạo mảng output byte
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutPutStream);//nén lại giảm một nữa
        byte[] bytes =byteArrayOutPutStream.toByteArray();//tách thành một mảng
        return Base64.encodeToString(bytes,Base64.DEFAULT);//chuyển byte thành string theo bit 0 1 để truyền đi
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result -> {
                if(result.getResultCode()==RESULT_OK){
                    if(result.getData()!=null){
                        Uri imageUri =result.getData().getData(); //tựa như url mà đây uri của hình ảnh
                        try{
                            InputStream inputStream=getContentResolver().openInputStream(imageUri); //tạo đầu vào stream khi thêm ảnh
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream); //decode sang thành bitmap
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);//ẩn và để cách thứ khác thay thế vị trí của nó
                            encodedImage = encodeImage(bitmap);

                        }catch(Exception ex){
                            System.out.println(ex.toString());
                        }
                    }
                }
            }
    );
    private boolean isValidSignUpDetails(){
        if(encodedImage==null){
            showToast("Vui lòng chọn ảnh");
            return false;
        }else if(binding.InputName.getText().toString().isEmpty()){
            showToast("Nhập tên");
            return false;
        }else if(binding.InputEmail.getText().toString().isEmpty()){
            showToast("Nhập email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.InputEmail.getText().toString()).matches()){
            showToast("Vui lòng nhập đúng email");
            return false;
        }else if(binding.InputPassword.getText().toString().isEmpty()){
            showToast("Nhập mật khẩu");
            return false;
        }else if(binding.InputConfirmPassword.getText().toString().isEmpty()){
            showToast("Nhập lại mật khẩu");
            return false;
        }else if(!binding.InputPassword.getText().toString().equals(binding.InputConfirmPassword.getText().toString())){
            showToast("Mật khẩu không giống nhau");
            return false;
        }else{
            return true;
        }
    }
    private void loadding(Boolean a){
        if(a){
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}