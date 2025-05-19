package com.example.hallmate.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hallmate.R;
import com.example.hallmate.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notif = notificationList.get(position);
        holder.tvTitle.setText(notif.getTitle());
        holder.tvMessage.setText(notif.getMessage());
        holder.tvTimestamp.setText(notif.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTimestamp;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            //tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}

