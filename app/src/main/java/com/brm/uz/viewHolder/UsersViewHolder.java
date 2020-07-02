package com.brm.uz.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brm.uz.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersViewHolder extends RecyclerView.ViewHolder {
    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setDetails(String name, String region){
        TextView textView = itemView.findViewById(R.id.item_chat_name);
        TextView textView1 = itemView.findViewById(R.id.item_chat_region);

        textView.setText(name);
        textView1.setText(region);

    }
    public void setImage(String image, Context ctx){
        CircleImageView circleImageView1 = itemView.findViewById(R.id.item_chat_circleImage);
        Picasso.with(ctx).load(image).placeholder(R.drawable.avatar).into(circleImageView1);
    }
}
