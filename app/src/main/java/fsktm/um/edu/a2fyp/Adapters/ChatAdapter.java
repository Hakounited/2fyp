package fsktm.um.edu.a2fyp.Adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fsktm.um.edu.a2fyp.Models.ChatMessage;
import fsktm.um.edu.a2fyp.databinding.ItemConstainerReceivedMessageBinding;
import fsktm.um.edu.a2fyp.databinding.ItemContainerSentMsgBinding;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final Bitmap receiverProfileImg;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImg, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImg = receiverProfileImg;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMsgBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
            );
        } else {
            return new ReceiveMessageViewHolder(
                    ItemConstainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((ReceiveMessageViewHolder) holder).setData(chatMessages.get(position),receiverProfileImg);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }

    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMsgBinding binding;

        SentMessageViewHolder(ItemContainerSentMsgBinding itemContainerSentMsgBinding) {
            super(itemContainerSentMsgBinding.getRoot());
            binding = itemContainerSentMsgBinding;

        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
        }

    }

    static class ReceiveMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemConstainerReceivedMessageBinding binding;

        ReceiveMessageViewHolder(ItemConstainerReceivedMessageBinding itemConstainerReceivedMessageBinding){
            super(itemConstainerReceivedMessageBinding.getRoot());
            binding = itemConstainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImg) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
            binding.chatImgProfile.setImageBitmap(receiverProfileImg);

        }

    }
}
