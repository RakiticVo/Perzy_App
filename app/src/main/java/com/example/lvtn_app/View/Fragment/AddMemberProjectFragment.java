package com.example.lvtn_app.View.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lvtn_app.Model.Group_Chat_Users;
import com.example.lvtn_app.Model.Project_Users;
import com.example.lvtn_app.Model.User;
import com.example.lvtn_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddMemberProjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMemberProjectFragment extends DialogFragment {
    //Khai báo
    TextInputLayout create_email_add_member_text_input_layout, create_position_add_member_text_input_layout;
    Button btn_invite_member, btn_cancel_invite_member;

    SharedPreferences sharedPreferences, sharedPreferences_project;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference1, reference2, reference3;
    String id_user, id_project;
    ProgressDialog progressDialog;
    AppCompatActivity activity;

    ArrayList<String> user_ID_list = new ArrayList<>();
    ArrayList<String> id = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddMemberProjectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateMemberFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddMemberProjectFragment newInstance(String param1, String param2) {
        AddMemberProjectFragment fragment = new AddMemberProjectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Set up
        View view = inflater.inflate(R.layout.fragment_add_member_project, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        create_email_add_member_text_input_layout = view.findViewById(R.id.create_email_add_member_text_input_layout);
        create_position_add_member_text_input_layout = view.findViewById(R.id.create_position_add_member_text_input_layout);
        btn_invite_member = view.findViewById(R.id.btn_invite_member);
        btn_cancel_invite_member = view.findViewById(R.id.btn_cancel_invite_member);

        sharedPreferences_project = requireContext().getSharedPreferences("ProjectDetail", Context.MODE_PRIVATE);
        sharedPreferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        id_project = sharedPreferences_project.getString("project_ID", "token");
        id_user = sharedPreferences.getString("user_ID", "token");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        activity = (AppCompatActivity) getContext();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(activity.getString(R.string.adding_member));

        //Bắt sự kiện
        //Todo: Xử lý sự kiện rời khỏi fragment
        btn_cancel_invite_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //Todo: Xử lý sự kiện nhập và kiểm tra rỗng cho Member Email
        create_email_add_member_text_input_layout.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    checkRightEmail(create_email_add_member_text_input_layout.getEditText().getText().toString());
                }else{
                    create_email_add_member_text_input_layout.getEditText().addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() == 0){
                                create_email_add_member_text_input_layout.setError(activity.getString(R.string.enterEmailMember));
                                create_email_add_member_text_input_layout.setErrorEnabled(true);
                            }else {
                                create_email_add_member_text_input_layout.setErrorEnabled(false);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }
        });

        //Todo: Xử lý sự kiện nhập và kiểm tra rỗng cho Member Position
        create_position_add_member_text_input_layout.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if (create_position_add_member_text_input_layout.getEditText().getText().length() == 0){
                        create_position_add_member_text_input_layout.setError(activity.getString(R.string.enterPosition));
                        create_position_add_member_text_input_layout.setErrorEnabled(true);
                    }else create_position_add_member_text_input_layout.setErrorEnabled(false);
                }else{
                    create_email_add_member_text_input_layout.getEditText().addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() == 0){
                                create_position_add_member_text_input_layout.setError(activity.getString(R.string.enterPosition));
                                create_position_add_member_text_input_layout.setErrorEnabled(true);
                            }else {
                                create_position_add_member_text_input_layout.setErrorEnabled(false);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }
        });

        //ToDo: Xử lý sự kiện xác nhận mới tham gia dự án:
        // - Kiểm tra định dạng và khác rỗng cho email -------- (Done)
        // - Kiểm tra User có trong danh sách User list của Project theo email hay không? -------- (Incomplete)
        // - Nếu không có xử lý thông báo mời. Ngược lại báo đã có. -------- (Incomplete)
        // - Hoặc kiểm tra với User trong Database nếu không có email thông báo không có người dùng -------- (Incomplete)
        // - Gọi Api Serive để lưu thông báo mời trên database nếu có User tồn tại và chưa được thêm vào -------- (Incomplete)
        btn_invite_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: Check permission
                String email = create_email_add_member_text_input_layout.getEditText().getText().toString();
                String positon = create_position_add_member_text_input_layout.getEditText().getText().toString();
                checkRightEmail(email);
                if (positon.length() == 0){
                    create_position_add_member_text_input_layout.setError(activity.getString(R.string.enterPosition));
                    create_position_add_member_text_input_layout.setErrorEnabled(true);
                }else create_position_add_member_text_input_layout.setErrorEnabled(false);

                //Todo: Handling
                progressDialog.show();
                if (create_email_add_member_text_input_layout.isErrorEnabled()
                        || create_position_add_member_text_input_layout.isErrorEnabled()){
                    Toast.makeText(activity, activity.getString(R.string.checkError), Toast.LENGTH_SHORT).show();
                }else {
                    create_email_add_member_text_input_layout.setErrorEnabled(false);
                    user_ID_list.clear();
                    reference1 = FirebaseDatabase.getInstance().getReference("Users");
                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user_ID_list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                User user = dataSnapshot.getValue(User.class);
//                                    Toast.makeText(activity, "" + user.getUser_Email().equals(email), Toast.LENGTH_SHORT).show();
                                if (user.getUser_Email().equals(email)){
                                    user_ID_list.add(user.getUser_ID());
                                }
                            }
                            if (user_ID_list.size() == 0){
                                progressDialog.dismiss();
                                create_email_add_member_text_input_layout.setError(activity.getString(R.string.wrongEmail));
                                create_email_add_member_text_input_layout.setErrorEnabled(true);
                            }else if (user_ID_list.size() == 1){
//                                    Toast.makeText(activity, "" + user_ID_list.size(), Toast.LENGTH_SHORT).show();
                                CheckUserInGroupChat(user_ID_list.get(0), positon);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        return view;
    }

    //ToDo: Kiểm tra định dạng email
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    //ToDo: Kiểm tra có phải là 1 email hay không?
    public void checkRightEmail(String email){
        if (!isValidEmail(email)){
            create_email_add_member_text_input_layout.setError(activity.getString(R.string.wrongFormat));
            create_email_add_member_text_input_layout.setErrorEnabled(true);
        }else {
            create_email_add_member_text_input_layout.setErrorEnabled(false);
        }
    }

    public void CheckUserInGroupChat(String user_ID, String position){
        id.clear();
        final boolean[] check = {false};
        reference2 = FirebaseDatabase.getInstance().getReference("User_List_By_Project").child(id_project);
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    Project_Users users = dataSnapshot1.getValue(Project_Users.class);
                    if (user_ID.equals(users.getUser_ID())){
                        check[0] = true;
                    }else {
                        check[0] = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (check[0]){
            progressDialog.dismiss();
            create_email_add_member_text_input_layout.setError(activity.getString(R.string.user_already));
            create_email_add_member_text_input_layout.setErrorEnabled(true);
        }else {
            PushData(user_ID, position);
        }
    }

    public void PushData(String id, String position) {

        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("type", "project_request_joining");
        hashMap2.put("leader_ID", firebaseUser.getUid());
        hashMap2.put("project_ID", id_project);
        hashMap2.put("receiver_ID", id);
        hashMap2.put("position", position);
        hashMap2.put("status", "sent");
        hashMap2.put("result", "undecided");
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Notifications").child("Joining_Project");
        reference2.child(id_project).child(id).setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    dismiss();
//                    Toast.makeText(activity, "Invite: " + id, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}