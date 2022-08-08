package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.UsersAdapter;
import fsktm.um.edu.a2fyp.Models.Users;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityAdminUsersPageBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;

public class AdminUsersPage extends AppCompatActivity {

    ActivityAdminUsersPageBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminUsersPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        getUsers();
        setListeners();
    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getUsers() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            List<Users> users = new ArrayList<>();
                            for (QueryDocumentSnapshot qs : task.getResult()) {
                                if (currentUserId.equals(qs.getId())){
                                    continue;
                                }
                                Users users1 = new Users();
                                users1.username = qs.getString(Constants.KEY_NAME);
                                users1.email = qs.getString(Constants.KEY_EMAIL);
                                users1.img = qs.getString(Constants.KEY_IMAGE);
                                users1.token = qs.getString(Constants.KEY_FCM_TOKEN);
                                users1.id = qs.getId();

                                users.add(users1);
                            }
                            if (users.size() > 0) {
                                UsersAdapter usersAdapter = new UsersAdapter(users,getApplicationContext());
                                binding.adminUsersRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                binding.adminUsersRecyclerview.setAdapter(usersAdapter);
                            }else{
                                showToast("users list empty");
                            }
                        } else {
                            showToast("Error");
                            showToast(task.getException().getMessage());
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }
}