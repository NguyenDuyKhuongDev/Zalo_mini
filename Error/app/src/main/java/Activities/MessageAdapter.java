package Activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zalo_mini.R;

import java.util.List;


import Models.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public MessageAdapter() {
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage, textViewUsername, textViewTime;
        public ImageView imageAvatar;

        public MessageViewHolder(View view) {
            super(view);
            textViewMessage = view.findViewById(R.id.textViewMessage);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewTime = view.findViewById(R.id.textViewTime);
            imageAvatar = view.findViewById(R.id.imageAvatar);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
       Message msg = messageList.get(position);
       holder.textViewMessage.setText(msg.getContent());
       holder.textViewUsername.setText(msg.getSenderId()); //ĐOẠN NÀY ĐANG SAI
       holder.textViewTime.setText(msg.getSendAt());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
