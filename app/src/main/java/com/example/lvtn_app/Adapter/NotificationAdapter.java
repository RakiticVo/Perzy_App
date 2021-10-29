package com.example.lvtn_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lvtn_app.Model.GroupChat;
import com.example.lvtn_app.Model.Joining_Group_Chat;
import com.example.lvtn_app.Model.Joining_Request;
import com.example.lvtn_app.Model.Project;
import com.example.lvtn_app.R;
import com.example.lvtn_app.View.Activity.ChatActivity;
import com.example.lvtn_app.View.Activity.MainActivity;
import com.example.lvtn_app.View.Fragment.GroupChatFragment;
import com.example.lvtn_app.View.Fragment.MemberChatFragment;
import com.example.lvtn_app.View.Fragment.NotificationFragment;
import com.example.lvtn_app.View.Fragment.ProjectDetailFragment;
import com.example.lvtn_app.View.Fragment.ProjectsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    //Khai báo
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<Joining_Request> joiningRequests;
    SharedPreferences sharedPreferences1, sharedPreferences2;
    SharedPreferences.Editor editor1, editor2;


    public NotificationAdapter(Context context, ArrayList<Joining_Request> joiningRequests) {
        this.context = context;
        this.joiningRequests = joiningRequests;
        this.mInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_accept_notification, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
//        Toast.makeText(context, "" + joiningRequests.size(), Toast.LENGTH_SHORT).show();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (joiningRequests != null){
            String objectID = joiningRequests.get(position).getObject_ID();
            String type = joiningRequests.get(position).getType();
            String role = joiningRequests.get(position).getPosition();
            String userID = firebaseUser.getUid();
            if (type != null){
                if (type.equals("group_request_joining")){
                    holder.cardView_background_notification.setVisibility(View.INVISIBLE);
                    holder.avatar_object_notification.setVisibility(View.VISIBLE);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("GroupChats").child(objectID);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            GroupChat groupChat = snapshot.getValue(GroupChat.class);
                            holder.notification_name_object.setText(groupChat.getGroup_Name());
                            Glide.with(context).load(groupChat.getGroup_Image()).into(holder.avatar_object_notification);
                            holder.notification_message.setText("You are invited to the "+ groupChat.getGroup_Name() +" group. Do you accept it?");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    holder.btn_accept_join_notification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User_List_By_Group_Chat").child(objectID);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("user_ID", userID);
                            hashMap.put("group_ID", objectID);
                            hashMap.put("position", role);
                            reference1.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        deleteNotification("Joining_Group_Chat", objectID, userID);
                                    }
                                }
                            });
                        }
                    });

                    holder.btn_denied_join_notification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child("Joining_Group_Chat").child(objectID);
                            reference.child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        AppCompatActivity activity = (AppCompatActivity) context;
                                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new NotificationFragment()).commit();
                                    }
                                }
                            });
                        }
                    });

                }else {
                    holder.cardView_background_notification.setVisibility(View.VISIBLE);
                    holder.avatar_object_notification.setVisibility(View.INVISIBLE);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Projects").child(objectID);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Project project = snapshot.getValue(Project.class);
                            String s = project.getProject_Background();
                            holder.notification_name_object.setText(project.getProject_Name());
                            holder.cardView_background_notification.setCardBackgroundColor(Integer.parseInt(s));
                            switch (project.getProject_Type()) {
                                case "Normal":
                                    holder.img_notification.setImageResource(R.drawable.project_1);
                                    break;
                                case "Kanban":
                                    holder.img_notification.setImageResource(R.drawable.project_kanban);
                                    break;
                                case "Scrum":
                                    holder.img_notification.setImageResource(R.drawable.project_scrum);
                                    break;
                                case "Personal":
                                    holder.img_notification.setImageResource(R.drawable.project_personal);
                                    break;
                                case "Bussiness":
                                    holder.img_notification.setImageResource(R.drawable.project_bussiness);
                                    break;
                            }
                            holder.notification_message.setText("You are invited to the "+ project.getProject_Name() +" project. Do you accept it?");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    holder.btn_accept_join_notification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("User_List_By_Project").child(objectID);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("user_ID", userID);
                            hashMap.put("project_ID", objectID);
                            hashMap.put("position", role);
                            reference1.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        deleteNotification("Joining_Project", objectID, userID);
                                    }
                                }
                            });
                        }
                    });

                    holder.btn_denied_join_notification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child("Joining_Project").child(objectID);
                            reference.child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        AppCompatActivity activity = (AppCompatActivity) context;
                                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new NotificationFragment()).commit();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (joiningRequests != null){
            return joiningRequests.size();
        }else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Khai báo
        public CircleImageView avatar_object_notification;
        public CardView cardView_background_notification;
        public ImageView img_notification;
        public TextView notification_name_object, notification_message;
        public Button btn_accept_join_notification, btn_denied_join_notification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            avatar_object_notification = itemView.findViewById(R.id.avatar_object_notification);
            cardView_background_notification = itemView.findViewById(R.id.cardView_background_notification);
            img_notification = itemView.findViewById(R.id.img_notification);
            notification_name_object = itemView.findViewById(R.id.notification_name_object);
            notification_message = itemView.findViewById(R.id.notification_message);
            btn_accept_join_notification = itemView.findViewById(R.id.btn_accept_join_notification);
            btn_denied_join_notification = itemView.findViewById(R.id.btn_denied_join_notification);
        }
    }

    public void deleteNotification(String notification_joining, String objectID, String userID){
//        Toast.makeText(context, notification_joining + "\n" + objectID + "\n" + userID, Toast.LENGTH_SHORT).show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(notification_joining).child(objectID);
        reference.child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    AppCompatActivity activity = (AppCompatActivity) context;
                    if (notification_joining.equals("Joining_Project")){
                        sharedPreferences2 = Objects.requireNonNull(context).getSharedPreferences("ProjectDetail", Context.MODE_PRIVATE);
                        editor2 = sharedPreferences2.edit();
                        editor2.putString("project_ID", objectID);
                        editor2.commit();
                        activity.getSupportFragmentManager().beginTransaction().addToBackStack("ProjectDetail return Projects").replace(R.id.frame_main, new ProjectDetailFragment()).commit();
                    }else {
//                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new NotificationFragment()).commit();
                        sharedPreferences1 = Objects.requireNonNull(context).getSharedPreferences("Chat", Context.MODE_PRIVATE);
                        editor1 = sharedPreferences1.edit();
                        editor1.putString("group_ID", objectID);
                        editor1.commit();
                        Intent intent = new Intent(activity, ChatActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }
        });
    }
}