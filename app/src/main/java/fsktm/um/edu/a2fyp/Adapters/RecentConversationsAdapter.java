package fsktm.um.edu.a2fyp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fsktm.um.edu.a2fyp.Activities.ChatActivity;
import fsktm.um.edu.a2fyp.Models.ChatMessage;
import fsktm.um.edu.a2fyp.databinding.ChatConversationsCardBinding;
import fsktm.um.edu.a2fyp.databinding.ItemConstainerReceivedMessageBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversationViewHolder> {

    List<ChatMessage> chatMessages;
    Context context;

    public RecentConversationsAdapter(List<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
                ChatConversationsCardBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        ChatConversationsCardBinding binding;

        ConversationViewHolder(ChatConversationsCardBinding chatConversationsCardBinding) {
            super(chatConversationsCardBinding.getRoot());
            binding = chatConversationsCardBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.userImg.setImageBitmap(getConversationImg(chatMessage.getConversationImg()));
            binding.conversationName.setText(chatMessage.getConversationName());
            binding.txtRecentMsg.setText(chatMessage.getMessage());

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("conversation_name", binding.conversationName.getText().toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }

    }


    public Bitmap getConversationImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
