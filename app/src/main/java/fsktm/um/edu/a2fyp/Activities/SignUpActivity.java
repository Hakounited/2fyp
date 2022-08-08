package fsktm.um.edu.a2fyp.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivitySignUpBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImg;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    EditText signUpName, SignUpEmail, SignUpPassword, SignUpConfirmPw;
    MaterialButton SignUpBtn;
    TextView signInTv;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        mAuth = FirebaseAuth.getInstance();

        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){

        binding.signInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
                startActivity(intent);
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidSignUpDetails()){
                    signUp();
                }
            }
        });

        binding.signUpLayoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void signUp(){

        String allowedDomain = "siswa.um.edu.my";
        String signUpEmail = binding.signUpEmail.getText().toString();
        String[] email = signUpEmail.split("@");

        if (!email[1].equals(allowedDomain)) {
            showToast("Only siswamail is allowed to register");
            return;
        }

        mAuth.createUserWithEmailAndPassword(binding.signUpEmail.getText().toString().trim(), binding.signUpPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = mAuth.getCurrentUser();
                            userId = mAuth.getCurrentUser().getUid();
//                            showToast(userId);
                            loading(true);
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            HashMap<String, Object> userDetails = new HashMap<>();
                            userDetails.put(Constants.KEY_NAME, binding.signUpName.getText().toString());
                            userDetails.put(Constants.KEY_EMAIL, binding.signUpEmail.getText().toString());
                            userDetails.put(Constants.KEY_IMAGE, encodedImg);
                            userDetails.put(Constants.KEY_USER_ID, mAuth.getCurrentUser().getUid());
                            userDetails.put("isUser","1");

                            firestore.collection(Constants.KEY_COLLECTION_USERS).document(userId).set(userDetails)
                                    .addOnSuccessListener(documentReference -> {
                                        loading(false);
                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.KEY_USER_ID, mAuth.getCurrentUser().getUid());
                                        preferenceManager.putString(Constants.KEY_NAME,binding.signUpName.getText().toString());
                                        preferenceManager.putString(Constants.KEY_IMAGE, encodedImg);
                                        preferenceManager.putString(Constants.KEY_EMAIL, binding.signUpEmail.getText().toString());

                                        showToast("Welcome");
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(execption -> {
                                        loading(false);
                                        showToast(execption.getMessage());
                                    });

                        } else {
                            showToast("Failed to register");
                            showToast(task.getException().getMessage());
                        }
                    }
                });

    }

    private String encodedImg(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getHeight();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imgUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imgUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.signUpImage.setImageBitmap(bitmap);
                            binding.signUpAddImgTv.setVisibility(View.INVISIBLE);
                            encodedImg = encodedImg(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private boolean isValidSignUpDetails(){

        if (encodedImg == null){
            showToast("select image");
            return false;
        }else if (binding.signUpName.getText().toString().trim().isEmpty()){
            showToast("Enter name");
            return false;
        }else if (binding.signUpEmail.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.signUpEmail.getText().toString().trim()).matches()){
            showToast("enter valid email");
            return false;
        }else if (binding.signUpPassword.getText().toString().trim().isEmpty()){
            showToast("enter your password");
            return false;
        }else if (binding.signUpConfirmPw.getText().toString().trim().isEmpty()){
            showToast("confirm password");
            return false;
        }else if (!binding.signUpPassword.getText().toString().trim().equals(binding.signUpConfirmPw.getText().toString().trim())){
            showToast("passwords must match");
            return false;
        }
            return true;
        }

        private void loading(boolean isLoading){
            if (isLoading){
                binding.signUpBtn.setVisibility(View.INVISIBLE);
                binding.signUpProgressBar.setVisibility(View.VISIBLE);
            }else{
                binding.signUpProgressBar.setVisibility(View.INVISIBLE);
                binding.signUpBtn.setVisibility(View.VISIBLE);
            }
        }
}
