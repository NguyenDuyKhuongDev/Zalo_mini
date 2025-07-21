package Helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zalo_mini.R;

import java.util.List;

public class CurrentAccountAdapter extends RecyclerView.Adapter<CurrentAccountAdapter.AccountViewHolder> {
    private List<String> phoneList;
    private OnAccountClickListener listener;

    public interface OnAccountClickListener {
        void onClick(String phoneNumber);
    }

    public CurrentAccountAdapter(List<String> phoneList, OnAccountClickListener listener) {
        this.phoneList = phoneList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        String phone = phoneList.get(position);
        holder.tvPhoneNumber.setText(phone);
        holder.itemView.setOnClickListener(v -> listener.onClick(phone));
    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView tvPhoneNumber;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
        }
    }
}

