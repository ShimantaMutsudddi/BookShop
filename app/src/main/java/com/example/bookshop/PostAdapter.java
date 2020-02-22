package com.example.bookshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.squareup.picasso.Picasso.get;

public class PostAdapter extends FirebaseRecyclerAdapter<Posts, PostAdapter.PostViewHolder>
{

    public PostAdapter(@NonNull FirebaseRecyclerOptions<Posts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Posts model)
    {
        holder.username.setText(model.getFullname());
        holder.description.setText(model.getDescription());
        holder.date.setText("   "+model.getDate());
        holder.time.setText("   "+model.getTime());
        get().load(model.getPostimage()).placeholder(R.drawable.booksbackground).into(holder.postimage);
        get().load(model.getProfileimgurl()).placeholder(R.drawable.booksbackground).into(holder.user_post_image);


    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_post_layout, parent, false);

        return new PostViewHolder(view);
    }

    class PostViewHolder extends RecyclerView.ViewHolder
   {
       TextView username,date,time,description;
        CircleImageView user_post_image;
       ImageView postimage;

       public PostViewHolder(@NonNull View itemView)
       {
           super(itemView);
           username=itemView.findViewById(R.id.post_username_id);
           date=itemView.findViewById(R.id.post_date_id);
           time=itemView.findViewById(R.id.post_time_id);
           description=itemView.findViewById(R.id.post_description_id);
           postimage= itemView.findViewById(R.id.post_image_id);
           user_post_image=itemView.findViewById(R.id.post_profile_image_id);
       }
   }
}
