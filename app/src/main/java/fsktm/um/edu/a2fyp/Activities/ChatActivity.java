package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fsktm.um.edu.a2fyp.Adapters.ChatAdapter;
import fsktm.um.edu.a2fyp.Models.ChatMessage;
import fsktm.um.edu.a2fyp.Models.Users;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityChatBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    private Users rUid;
    String receiverUser, receiverUid, receiverImg, receiverUser2;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    String conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        setListeners();

        firestore = FirebaseFirestore.getInstance();
        receiverUser = getIntent().getExtras().getString("posted_by");

        binding.textName.setText(receiverUser);
        firestore.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo("name",receiverUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        receiverUid = document.getString("userId");
                        receiverImg = document.getString("image");
                        binding.rImg.setText(receiverImg);
                        binding.rId.setText(receiverUid);
                        showToast("load details");
                        showToast(binding.rId.getText().toString());
                        init();
                        listenMessages();
                    }
                }
            }
        });

        receiverUser2 = getIntent().getExtras().getString("conversation_name");
        binding.textName.setText(receiverUser2);
        firestore.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo("name",receiverUser2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        receiverUid = document.getString("userId");
                        receiverImg = document.getString("image");
                        binding.rImg.setText(receiverImg);
                        binding.rId.setText(receiverUid);
                        showToast("load details");
                        showToast(binding.rId.getText().toString());
                        init();
                        listenMessages();
                    }
                }
            }
        });

//        loadReceiverDetails();
//        init();
//        listenMessages();


    }

    private void setListeners() {
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUid);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        firestore.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if (conversationId != null) {
            updateConversion(binding.inputMessage.getText().toString());
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMG, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receiverUid);
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser);
            conversion.put(Constants.KEY_RECEIVER_IMG, receiverImg);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        binding.inputMessage.setText(null);
    }

    private void listenMessages() {

        receiverUid = binding.rId.getText().toString();


        firestore.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUid)
                .addSnapshotListener(eventListener);


        firestore.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,receiverUid)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, mAuth.getCurrentUser().getUid())
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {

        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange dc : value.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = dc.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = dc.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = dc.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(dc.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = dc.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages,(obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyDataSetChanged();
                binding.chatRecyclerview.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecyclerview.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if (conversationId == null) {
            checkForConversion();
        }
    };

    private void loadReceiverDetails() {
        firestore = FirebaseFirestore.getInstance();
        receiverUser = getIntent().getExtras().getString("posted_by");
        binding.textName.setText(receiverUser);
        firestore.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo("name",receiverUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        receiverUid = document.getString("userId");
                        receiverImg = document.getString("image");
                        binding.rImg.setText(receiverImg);
                        binding.rId.setText(receiverUid);
                        showToast("load details");
                        showToast(binding.rId.getText().toString());

                    }
                }
            }
        });
    }

    private void init() {
        receiverImg = binding.rImg.getText().toString();

        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages, getBitmapFromEncodedString(receiverImg),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerview.setAdapter(chatAdapter);
        firestore = FirebaseFirestore.getInstance();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }


    private Bitmap getBitmapFromEncodedString(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }


    private String getReadableDateTime(Date date) {
        return  new SimpleDateFormat("MMMM dd, yyyy -hh:mm a", Locale.getDefault()).format(date);
    }


    private void addConversion(HashMap<String, Object> conversion) {
        firestore.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());

    }

    private void updateConversion(String message) {
        DocumentReference documentReference =
                firestore.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversationId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date()
        );
    }


    private void checkForConversion() {
        if (chatMessages.size() != 0) {
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUid
            );
            checkForConversionRemotely(
                    receiverUid, preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderId, String receiverID) {
        firestore.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverID)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };
}