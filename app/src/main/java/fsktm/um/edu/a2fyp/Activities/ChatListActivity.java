package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.RecentConversationsAdapter;
import fsktm.um.edu.a2fyp.Models.ChatMessage;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityChatListBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class ChatListActivity extends AppCompatActivity {

    ActivityChatListBinding binding;
    List<ChatMessage> conversations;
    RecentConversationsAdapter recentConversationsAdapter;
    FirebaseFirestore firestore;
    PreferenceManager preferenceManager;
    String conversationId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initi();
        listenConversations();
        bottomNavSelectItem();
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

    private void initi() {
        conversations = new ArrayList<>();
        preferenceManager = new PreferenceManager(getApplicationContext());
        recentConversationsAdapter = new RecentConversationsAdapter(conversations, getApplicationContext());
        binding.conversationRecyclerview.setAdapter(recentConversationsAdapter);
        firestore = FirebaseFirestore.getInstance();
    }

    private void listenConversations() {
        firestore.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(querySnapshotEventListener);
        firestore.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(querySnapshotEventListener);
    }

    private final EventListener<QuerySnapshot> querySnapshotEventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversationImg = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMG);
                        chatMessage.conversationName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversationId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    }else {
                        chatMessage.conversationImg = documentChange.getDocument().getString(Constants.KEY_SENDER_IMG);
                        chatMessage.conversationName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversationId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                }else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversations.get(i).senderId.equals(senderID) && conversations.get(i).receiverId.equals(receiverID)) {
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            recentConversationsAdapter.notifyDataSetChanged();
            binding.conversationRecyclerview.smoothScrollToPosition(0);
            binding.conversationRecyclerview.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    private void bottomNavSelectItem() {

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_home_meu:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        return true;

                    case R.id.bottom_navigation_cart_menu:
                        startActivity(new Intent(getApplicationContext(),CartActivity.class));
                        return true;

                    case R.id.bottom_navigation_post_menu:
                        startActivity(new Intent(getApplicationContext(),PostProductActivity.class));
                        return true;

                    case R.id.bottom_navigation_chat_menu:
                        return true;

                    case R.id.bottom_navigation_profile_menu:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }
}