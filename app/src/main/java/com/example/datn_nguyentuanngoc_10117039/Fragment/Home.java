package com.example.datn_nguyentuanngoc_10117039.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datn_nguyentuanngoc_10117039.Activity.ProducrActivity;
import com.example.datn_nguyentuanngoc_10117039.Adapter.ProductAdapter;
import com.example.datn_nguyentuanngoc_10117039.Dialog.Location;
import com.example.datn_nguyentuanngoc_10117039.Model.Posts;
import com.example.datn_nguyentuanngoc_10117039.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {
    RecyclerView rcl;
    ImageView img_location;
    TextView tv_location;
    private DatabaseReference mDatabaseRef;
    private ProductAdapter mAdapter;
    private ArrayList<Posts> mUploads;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        // ánh xạ

        rcl = view.findViewById(R.id.rcl_home);
        img_location = view.findViewById(R.id.img_location);
        tv_location = view.findViewById(R.id.tv_location);


        // Sự kiện

        /// Sự kiện load images
        rcl.setHasFixedSize(true);
        rcl.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.orderByChild("mName").equalTo("ngoc99vh").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Posts upload = postSnapshot.getValue(Posts.class);
                    mUploads.add(upload);
                }
                mAdapter = new ProductAdapter(mUploads, getContext());
                rcl.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        /// Sự kiện lấy địa chỉ

        img_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onpenDialogLoction();
            }
        });

        return view;
    }
    public void onpenDialogLoction(){
        Location exampleDialog = new Location();
        exampleDialog.show(getFragmentManager(), "example dialog");
    }
}