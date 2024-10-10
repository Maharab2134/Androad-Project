package com.example.chats.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.chats.Activities.MainActivity;
import com.example.chats.Models.Status;
import com.example.chats.Models.UserStatus;
import com.example.chats.R;
import com.example.chats.databinding.ItemStutasBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;

    public TopStatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stutas, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {
        UserStatus userStatus = userStatuses.get(position);

        if (userStatus != null && !userStatus.getStatuses().isEmpty()) {
            Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);

            Glide.with(context)
                    .load(lastStatus.getImageUrl())
                    .placeholder(R.drawable.avatar)
                    .into(holder.binding.image);
            holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String uploadTime = sdf.format(lastStatus.getTimeStamp());

            holder.binding.circularStatusView.setOnClickListener(v -> {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Status status : userStatus.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));
                }

                new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                        .setStoriesList(myStories)
                        .setStoryDuration(5000)
                        .setTitleText(userStatus.getName())
                        .setSubtitleText(uploadTime) // No subtitle needed
                        .setTitleLogoUrl(userStatus.getProfileImage())
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                            }
                        })
                        .build()
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class TopStatusViewHolder extends RecyclerView.ViewHolder {

        ItemStutasBinding binding;

        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStutasBinding.bind(itemView);
        }
    }
}
