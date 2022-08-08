package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivitySignInBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;

    String txt_loginEmail, txt_loginPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else {

        }



        setListeners();
    }

    private void setListeners(){
        binding.creatAccTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidSignInDetails()){
                    signIn();
                }
            }
        });
    }

    private void signIn(){

        txt_loginEmail = binding.loginEmail.getText().toString().trim();
        txt_loginPassword = binding.loginPassword.getText().toString().trim();

        if (txt_loginEmail.isEmpty() || txt_loginPassword.isEmpty()){
            showToast("enter email or password");
        } else {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(txt_loginEmail,txt_loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        DocumentReference ref = fstore.collection("users").document(mAuth.getCurrentUser().getUid());
                        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (task.isSuccessful()) {
                                    if (documentSnapshot.contains("isAdmin")) {
                                        loading(true);
                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                                        preferenceManager.putString(Constants.KEY_USER_ID, mAuth.getCurrentUser().getUid());
                                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                                        Intent intent = new Intent(getApplicationContext(),AdminPageActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }else {
                                        loading(true);

                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        firestore.collection(Constants.KEY_COLLECTION_USERS)
                                                .whereEqualTo(Constants.KEY_EMAIL, binding.loginEmail.getText().toString())
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                                                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                                                    preferenceManager.putString(Constants.KEY_USER_ID, mAuth.getCurrentUser().getUid());
                                                    preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                                                    preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                                                    preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
//                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                                                }else {
                                                    loading(false);
                                                    showToast("unable to sign in");
                                                }
                                            }
                                        });
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }else{
                        showToast("Failed to login");
                        showToast(task.getException().getMessage());
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast(e.getMessage());
                }
            });
        }



    }

    private void loading(Boolean isLoading){

        if (isLoading) {
            binding.loginBtn.setVisibility(View.INVISIBLE);
            binding.loginProgressBar.setVisibility(View.VISIBLE);
        } else {
            binding.loginProgressBar.setVisibility(View.INVISIBLE);
            binding.loginBtn.setVisibility(View.VISIBLE);
        }
    }



    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails(){
        if (binding.loginEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.loginEmail.getText().toString()).matches()){
            showToast("Enter valid email");
            return false;
        } else if (binding.loginPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else {
            return true;
        }
    }
}