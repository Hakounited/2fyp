package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.UsersAdapter;
import fsktm.um.edu.a2fyp.Listeners.UserListener;
import fsktm.um.edu.a2fyp.Models.Users;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityAdminPageBinding;
import fsktm.um.edu.a2fyp.databinding.ActivityProfileBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class AdminPageActivity extends AppCompatActivity   {

    ActivityAdminPageBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();

    }

    private void setListeners(){
        binding.adminViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsers();
            }
        });
    }

    private void deleteUser() {

    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loading(false);
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Users> users = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                                if (currentUserId.equals(queryDocumentSnapshot.getId())){
//                                    continue;
//                                }
                                Users users1 = new Users();
                                users1.username = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                users1.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                users1.img = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                users1.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                users1.id = queryDocumentSnapshot.getId();

                                users.add(users1);
                            }
                            if (users.size() > 0) {
                                UsersAdapter usersAdapter = new UsersAdapter(users,getApplicationContext());
                                binding.adminRecyclerview.setAdapter(usersAdapter);
                                binding.adminRecyclerview.setVisibility(View.VISIBLE);
                            } else {
                                showErrorMessage();
                            }
                        } else {
                            showErrorMessage();
                        }
                    }
                });

    }


    private void showErrorMessage() {
        binding.adminErroMsg.setText(String.format("%s", "No users available"));
        binding.adminErroMsg.setVisibility(View.VISIBLE);

    }

    private void loading(Boolean isLoading){
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }


}