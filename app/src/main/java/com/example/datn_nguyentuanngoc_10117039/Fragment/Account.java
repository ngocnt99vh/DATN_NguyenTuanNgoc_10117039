package com.example.datn_nguyentuanngoc_10117039.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datn_nguyentuanngoc_10117039.Activity.LoginActivity;
import com.example.datn_nguyentuanngoc_10117039.Activity.MainActivity;
import com.example.datn_nguyentuanngoc_10117039.Activity.RegisterActivity;
import com.example.datn_nguyentuanngoc_10117039.Adapter.ProductAdapter;
import com.example.datn_nguyentuanngoc_10117039.Model.Posts;
import com.example.datn_nguyentuanngoc_10117039.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

public class Account extends Fragment {
    public TextView Login, tv_userName, tv_phone, acc_store;
    Button btn_logout;
    private static SharedPreferences saveInfoAccount;
    private SharedPreferences.Editor editor;
    public String userName = "";
    private DatabaseReference mDatabaseRef;
    private ProductAdapter mAdapter;
    private ArrayList<Posts> mUploads;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(R.layout.fragment_account, container, false);

        //Ánh xạ
        Login = view.findViewById(R.id.tv_Login);
        tv_userName = view.findViewById(R.id.tv_userName_Account);
        tv_phone = view.findViewById(R.id.tv_phone_Account);
        btn_logout = view.findViewById(R.id.btn_logout);
        acc_store = view.findViewById(R.id.acc_store);

        //Sự kiện
        saveInfoAccount = getContext().getSharedPreferences("saveInfo", Context.MODE_PRIVATE);
        init();
        loginSuccess(saveInfoAccount.getString("userName", null) != null);


        return view;
    }

    @Subscriber(tag = "loginSuccess")
    public void loginSuccess(Boolean b) {
        if (b = true) {
            checkData();
        }

    }


    public void init() {
        //Onclick
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = saveInfoAccount.edit();
                editor.clear();
                editor.apply();
                checkData();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

    }

    public void checkData() {
        userName = saveInfoAccount.getString("userName", null);
        if (!TextUtils.isEmpty(userName)) {
            Login.setVisibility(View.GONE);
            tv_userName.setText(userName);
            tv_phone.setText(saveInfoAccount.getString("phone", ""));
        } else {
            Login.setVisibility(View.VISIBLE);
            tv_userName.setText("");
            tv_phone.setText("");
        }
    }
    public void getStore(){
        mUploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Posts");
        mDatabaseRef.orderByChild("pStatus").equalTo("Đã xác nhận").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Posts upload = postSnapshot.getValue(Posts.class);
                    mUploads.add(upload);
                }
                mAdapter = new ProductAdapter(mUploads, getContext());
//                rcl.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}