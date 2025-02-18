package com.example.datn_nguyentuanngoc_10117039.Fragment;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.datn_nguyentuanngoc_10117039.Activity.LoginActivity;
import com.example.datn_nguyentuanngoc_10117039.Activity.MainActivity;
import com.example.datn_nguyentuanngoc_10117039.Adapter.Location_Adapter;
import com.example.datn_nguyentuanngoc_10117039.Model.Images;
import com.example.datn_nguyentuanngoc_10117039.Model.Location_model;
import com.example.datn_nguyentuanngoc_10117039.Model.Posts;
import com.example.datn_nguyentuanngoc_10117039.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Post extends Fragment {
    private static final String TAG = "ngocnt";
    long uuid ;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btn_upload;
    EditText edt_Name, edt_Dongxe, edt_MadeinDate, edt_Khuvuc, edt_color, edt_pirce, edt_km, edt_TT, edt_dongco;
    ImageView imgPost, imgPost2;
    Uri imgUri1;
    Uri imgUri2;
    CheckBox cbUse, cb_Nouse;
    int check = 0;
    private String condition = "";

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private static SharedPreferences saveInfoAccount;
    private SharedPreferences.Editor editor;
    String userName = "";
    Images images;
    ArrayList<String> listImages = new ArrayList<>();

    private Dialog mDialog;
    private int mPosition;

    private ArrayList<Location_model> listLocations;
    private Location_Adapter location_adapter;
    String id_loacation="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // ánh xạ
        btn_upload = view.findViewById(R.id.btn_upload_file);
        edt_Name = view.findViewById(R.id.edt_nameXe);
        edt_Dongxe = view.findViewById(R.id.edt_dongxe);
        edt_dongco = view.findViewById(R.id.edt_dongco);
        edt_color = view.findViewById(R.id.edt_color);
        edt_MadeinDate = view.findViewById(R.id.edt_madeinDate);
        edt_Khuvuc = view.findViewById(R.id.edt_khuvuc);
        edt_pirce = view.findViewById(R.id.edt_pirce);
        edt_km = view.findViewById(R.id.edt_km);
        edt_TT = view.findViewById(R.id.edt_themTT);
        edt_pirce = view.findViewById(R.id.edt_pirce);
        cbUse = view.findViewById(R.id.check_use_P);
        cb_Nouse = view.findViewById(R.id.check_nouse_P);
        imgPost = view.findViewById(R.id.imagePost);
        imgPost2 = view.findViewById(R.id.imagePost2);

        if (cbUse.isChecked()) {
            condition = cbUse.getText().toString();
            cb_Nouse.setChecked(false);
        } else {
            cbUse.setChecked(false);
            condition = cb_Nouse.getText().toString();
        }


        createDialog(getContext());
        edt_Khuvuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.show();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference("Posts");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Posts");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    uuid = (snapshot.getChildrenCount());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        images = new Images();
        // click
        saveInfoAccount = getContext().getSharedPreferences("saveInfo", Context.MODE_PRIVATE);
        userName = saveInfoAccount.getString("userName", null);
        if (!TextUtils.isEmpty(userName)) {
            imgPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check = 1;
                    openFileChooser();
                }
            });

            imgPost2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check = 2;
                    openFileChooser();
                }
            });
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });


        return view;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d(TAG, "handleMessage: " + msg.what);
            switch (msg.what) {
                case 1:
                    postFile();
                    break;
                default:
                    break;
            }
        }
    };

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {

            if (check == 1) {
                imgUri1 = data.getData();
                Picasso.get().load(imgUri1).resize(300, 300).centerCrop().into(imgPost);
            } else {
                imgUri2 = data.getData();
                Picasso.get().load(imgUri2).resize(300, 300).centerCrop().into(imgPost2);
            }

        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (imgUri1 != null) {
            StorageReference reference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imgUri1));
            reference.putFile(imgUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: 1");
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "onSuccess: 2");
                            listImages.add(uri.toString());
                            if (listImages.size() == 2) {
                                mHandler.sendEmptyMessage(1);
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }

        if (imgUri2 != null) {
            StorageReference reference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imgUri2));
            reference.putFile(imgUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: 3");
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "onSuccess: 4");
                            listImages.add(uri.toString());
                            if (listImages.size() == 2) {
                                mHandler.sendEmptyMessage(1);
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void postFile() {
        images.setImage1(listImages.get(0));
        images.setImage2(listImages.get(1));
        String tenXe = edt_Name.getText().toString();
        String dongco = edt_dongco.getText().toString();
        String dongXe = edt_Dongxe.getText().toString();
        String namSX = edt_MadeinDate.getText().toString();
        String color = edt_color.getText().toString();
        String khuvuc = id_loacation;
        String thongtin = edt_TT.getText().toString();
        String status = "Chưa xác nhận";
        Float gia = Float.valueOf(edt_pirce.getText().toString());
        Float km = Float.valueOf(edt_km.getText().toString());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String uploadId = mDatabaseRef.push().getKey();
        Posts post = new Posts(uploadId, tenXe, namSX, dongXe, color, khuvuc, thongtin, userName, status, condition, dongco, images, gia, km,currentDate, currentDate);
        if (uploadId != null) {
            mDatabaseRef.child(uploadId).setValue(post);
        }
        Toast.makeText(getActivity(), "Upload thành công", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }
    public void createDialog(Context context) {
        mDialog = new Dialog(context, R.style.FullScreenDialog);
        mDialog.setContentView(R.layout.dialog_location);
        RecyclerView rcl_dialogLocation = mDialog.findViewById(R.id.rcl_location);
        rcl_dialogLocation.setHasFixedSize(true);
        rcl_dialogLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
        listLocations = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("local");
        mDatabaseRef.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Location_model upload = postSnapshot.getValue(Location_model.class);
                    listLocations.add(upload);
                }
                listLocations.add(0, new Location_model("-1", "Việt Nam", null, "-1"));
                location_adapter = new Location_Adapter(listLocations, getContext(), mPosition);
                location_adapter.setTest(new Location_Adapter.Test() {
                    @Override
                    public void onClick(View v, int possition) {
                        mPosition = possition;
                        Location_model location_model = listLocations.get(possition);
                        edt_Khuvuc.setText(location_model.getName());
                        id_loacation= location_model.getId();
//                        Log.d(TAG, "mUploads: " + location_model.getName());
                        mDialog.dismiss();
                    }
                });
                rcl_dialogLocation.setAdapter(location_adapter);
                location_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Lỗi 1", Toast.LENGTH_SHORT).show();
            }
        });
        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setAttributes(layoutParams);
    }
}