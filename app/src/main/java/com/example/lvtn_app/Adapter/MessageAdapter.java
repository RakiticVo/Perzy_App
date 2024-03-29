package com.example.lvtn_app.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lvtn_app.Model.Message;
import com.example.lvtn_app.Model.Project;
import com.example.lvtn_app.Model.User;
import com.example.lvtn_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    //Khai báo
    public static final int LEFT = 0 ;
    public static final int RIGHT = 1 ;
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<Message> message_list;
    SharedPreferences sharedPreferences;
    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, ArrayList<Message> message_list) {
        this.context = context;
        this.message_list = message_list;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        holder.message_user.setText(message_list.get(position).getMessage_text());
        holder.tv_username_message.setText(message_list.get(position).getMessage_sender());
        if (holder.tv_username_message.getText().length() > holder.message_user.getText().length()){
            holder.temp1.setVisibility(View.VISIBLE);
        }else {
            holder.temp2.setVisibility(View.VISIBLE);
        }
        if (message_list.get(position).getMessage_img_sender() == null || message_list.get(position).getMessage_img_sender().equals(" ")){
            holder.avatar_user.setImageResource(R.drawable.user);
            holder.avatar_user.setScaleType(CircleImageView.ScaleType.CENTER_CROP);
        }else {
            Glide.with(context).load(message_list.get(position).getMessage_img_sender()).centerCrop().into(holder.avatar_user);
        }
        if (position == message_list.size() - 1)
        {
            holder.tv_seen.setVisibility(View.VISIBLE);
//            Toast.makeText(context, "" + userName, Toast.LENGTH_SHORT).show();
            if (message_list.get(position).getMessage_sender().equals(sharedPreferences.getString("user_Name", "abc"))){
                holder.tv_seen.setText(message_list.get(position).getMessage_send_status());
            }else {
                holder.tv_seen.setVisibility(View.GONE);
            }
        }
        else
        {
            holder.tv_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return message_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Khai báo
        CircleImageView avatar_user;
        TextView message_user, tv_username_message, tv_seen, temp1, temp2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar_user = itemView.findViewById(R.id.avatar_user);
            message_user = itemView.findViewById(R.id.message_user);
            tv_username_message = itemView.findViewById(R.id.tv_username_message);
            tv_seen = itemView.findViewById(R.id.tv_seen);
            temp1 = itemView.findViewById(R.id.temp1);
            temp2 = itemView.findViewById(R.id.temp2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        sharedPreferences = Objects.requireNonNull(context).getSharedPreferences("User", Context.MODE_PRIVATE);
        if (message_list.get(position).getMessage_sender().equals(sharedPreferences.getString("user_Name", "abc"))){
            return RIGHT;
        }else {
            return LEFT;
        }
    }
}
