package fsktm.um.edu.a2fyp.Listeners;

import com.google.firebase.firestore.auth.User;

import fsktm.um.edu.a2fyp.Models.Users;

public interface UserListener {

    void onUserCllicked(Users user);

}
