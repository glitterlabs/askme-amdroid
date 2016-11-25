package com.glitterlabs.videoqnaapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.glitterlabs.videoqnaapp.R;
import com.glitterlabs.videoqnaapp.model.EditProfile;
import com.glitterlabs.videoqnaapp.model.PickImage;
import com.glitterlabs.videoqnaapp.model.UserProfile;
import com.glitterlabs.videoqnaapp.model.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.library.IPickResult;
import com.vansuita.library.PickImageDialog;
import com.vansuita.library.PickSetup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class EditProfileNormal extends AppCompatActivity{

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private EditText etFirstName,etLastName,etPhoneNo;
    private ImageView ivProfile;
    private Button btnCancel,btnDone;

    private ProgressDialog progressDialog;

    private String strFName,strLName,strPhone,strEmail,strUrl,strUserType,strUid;
    private String upFName,upLName,upPhone,upUrl;
    private Uri uriImg;

    public int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Bitmap bitmapUrl;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_normal);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://video-q-and-a.appspot.com/profile_pics/");

        progressDialog = new ProgressDialog(this);

        findID();

        Bundle bundle = getIntent().getExtras();
        strFName = bundle.getString("fname");
        strLName = bundle.getString("lname");
        strEmail = bundle.getString("email");
        strPhone = bundle.getString("phone");
        strUrl = bundle.getString("url");
        strUserType = bundle.getString("user_type");
        strUid = firebaseUser.getUid();

        setData();

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImage.pickImage(EditProfileNormal.this);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upFName = etFirstName.getText().toString().trim();
                upLName = etLastName.getText().toString().trim();
                upPhone = etPhoneNo.getText().toString().trim();

                // show progress dailog
                progressDialog.setMessage("Updating Information...");
                progressDialog.show();

                if(uriImg == null){
                //    StorageReference mountainsRef = storageRef.child(uriImg.getLastPathSegment());

                  //  UploadTask uploadTask = mountainsRef.putFile(uriImg);

                   // String key = mDatabase.push().getKey();
                    EditProfile editProfile = new EditProfile(upFName,upLName,upPhone,strUserType,strUrl,strUid);
                    Map<String, Object> postValues = editProfile.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/profile/", postValues);
                    //   childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

                    mDatabase.updateChildren(childUpdates);
                    progressDialog.dismiss();
                    startActivity(new Intent(EditProfileNormal.this,MainActivity.class));
                    finish();
                }else {
                    if(!(uriImg == null)) {
                        StorageReference mountainsRef = storageRef.child(uriImg.getLastPathSegment());

                        UploadTask uploadTask = mountainsRef.putFile(uriImg);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                progressDialog.dismiss();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                EditProfile editProfile = new EditProfile(upFName, upLName, upPhone, strUserType, String.valueOf(downloadUrl), strUid);
                                Map<String, Object> postValues = editProfile.toMap();

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/profile/", postValues);
                                //   childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

                                mDatabase.updateChildren(childUpdates);
                                progressDialog.dismiss();
                                startActivity(new Intent(EditProfileNormal.this, ProfileNormalActivity.class));
                                finish();

                            }
                        });
                    }else{
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmapUrl.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageRef.child("profile_pics").putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                progressDialog.dismiss();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                EditProfile editProfile = new EditProfile(upFName, upLName, upPhone, strUserType, String.valueOf(downloadUrl), strUid);
                                Map<String, Object> postValues = editProfile.toMap();

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/profile/", postValues);
                                //   childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

                                mDatabase.updateChildren(childUpdates);
                                progressDialog.dismiss();
                                startActivity(new Intent(EditProfileNormal.this, ProfileNormalActivity.class));
                                finish();

                            }
                        });
                    }
                }
            }
        });

    }

    private void findID(){
        etFirstName = (EditText) findViewById(R.id.et_fts_FName);
        etLastName = (EditText) findViewById(R.id.et_fts_LName);
        etPhoneNo = (EditText) findViewById(R.id.et_fts_PhoneNo);
        ivProfile = (ImageView) findViewById(R.id.iv_profile_pic);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnDone = (Button) findViewById(R.id.btnDone);
    }

    private void setData(){
        etFirstName.setText(strFName);
        etLastName.setText(strLName);
        etPhoneNo.setText(strPhone);

        Glide.with(this).load(strUrl)
                .bitmapTransform(new CropCircleTransformation(EditProfileNormal.this))
                .into(ivProfile);
    }

    private void updateData(String fName, String lName, String phoneNumber, String userType,String profileUrl,String userUId){
        UserProfile userProfile = new UserProfile(fName, lName, phoneNumber,userType,profileUrl,userUId);
        //   mDatabase.child("users").child("user_type").child("normal").child(userUId).child("profile").setValue(userProfile);
        mDatabase.child("users").child(userUId).child("profile").setValue(userProfile);
        Toast.makeText(EditProfileNormal.this,"User Data Updated...",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (!(data == null)) {

                if (requestCode == SELECT_FILE) {
                    uriImg = data.getData();
                    Glide.with(this).load(uriImg)
                            .bitmapTransform(new CropCircleTransformation(EditProfileNormal.this))
                            .into(ivProfile);
                } else if (requestCode == REQUEST_CAMERA) {
                    bitmapUrl = (Bitmap) data.getExtras().get("data");
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Glide.with(this).load(destination)
                            .bitmapTransform(new CropCircleTransformation(EditProfileNormal.this))
                            .into(ivProfile);
                }
            } else {
                Util.showDialogMessage(EditProfileNormal.this,"Image Not Set","Image Not Set, Please try again");
                //    Toast.makeText(this, "Image Not Set, Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
