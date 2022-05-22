package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityAdminLoginBinding;

public class AdminLoginActivity extends AppCompatActivity {

    ActivityAdminLoginBinding binding;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        setListeners();
    }

    private void setListeners(){

        binding.admniLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.adminLoginEmail.getText().toString().equals("hakodzgaming@gmail.com")&&binding.adminLoginPassword.getText().toString().equals("123123123")){
                   String txt_email, txt_password;
                   txt_email = binding.adminLoginEmail.toString();
                   txt_password = binding.adminLoginPassword.toString();
                   mAuth.signInWithEmailAndPassword(txt_email,txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           Intent intent = new Intent(getApplicationContext(), AdminPageActivity.class);
                           startActivity(intent);
                       }
                   });

                } else {
                    showToast("Only admin can login");
                }
            }
        });

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

}