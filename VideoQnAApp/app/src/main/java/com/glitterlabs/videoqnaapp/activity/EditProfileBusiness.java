package com.glitterlabs.videoqnaapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.glitterlabs.videoqnaapp.R;
import com.glitterlabs.videoqnaapp.model.EditProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.library.IPickResult;
import com.vansuita.library.PickImageDialog;
import com.vansuita.library.PickSetup;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class EditProfileBusiness extends AppCompatActivity implements IPickResult.IPickResultBitmap,IPickResult.IPickResultUri{

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private EditText etBussName,etBussAdd,etBussDec,etBussZip,etBussPhone;
    private Button btnDone,btnCancel;
    private ImageView ivBussinessImg;

    private String strBName,strBAdd,strBZip,strBPhone,strBDec,strBEmail,strBImg;
    private String upName,upAdd,upZip,upPhone,upDec,upUid,upUserType;

    public static final String B_NAME = "b_name";
    public static final String B_ADD = "b_add";
    public static final String B_ZIP = "b_zip";
    public static final String B_DEC = "b_dec";
    public static final String B_PHONE = "b_phone";
    public static final String B_IMG = "b_img";

    private ProgressDialog progressDialog;
    private Uri uriImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_business);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://video-q-and-a.appspot.com/business_images/");

        progressDialog = new ProgressDialog(this);

        findIdd();

        Bundle bundle = getIntent().getExtras();
        strBName = bundle.getString(B_NAME);
        strBAdd = bundle.getString(B_ADD);
        strBDec = bundle.getString(B_DEC);
        strBImg = bundle.getString(B_IMG);
        strBPhone = bundle.getString(B_PHONE);
        strBZip = bundle.getString(B_ZIP);

        setBussProData();

        ivBussinessImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageDialog.on(EditProfileBusiness.this, new PickSetup());
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileBusiness.this,ProfileBusinessActivity.class));
                finish();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upName = etBussName.getText().toString().trim();
                upAdd = etBussAdd.getText().toString().trim();
                upPhone = etBussPhone.getText().toString().trim();
                upZip = etBussZip.getText().toString().trim();
                upDec = etBussDec.getText().toString().trim();
                upUid = firebaseUser.getUid();
                upUserType = "Business";

                // show progress dailog
                progressDialog.setMessage("Updating Information...");
                progressDialog.show();

                if(uriImg == null){

                    EditProfile editProfile = new EditProfile(upName,upPhone,upAdd,upZip,upDec,strBImg,firebaseUser.getUid(),upUserType);
                    Map<String, Object> postValues = editProfile.toMapB();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/profile/", postValues);

                    mDatabase.updateChildren(childUpdates);
                    progressDialog.dismiss();
                    startActivity(new Intent(EditProfileBusiness.this,MainActivity.class));
                    finish();
                }else {
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
                            EditProfile editProfile = new EditProfile(upName,upPhone,upAdd,upZip,upDec,String.valueOf(downloadUrl),firebaseUser.getUid(),upUserType);
                            Map<String, Object> postValues = editProfile.toMapB();

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/profile/", postValues);

                            mDatabase.updateChildren(childUpdates);
                            progressDialog.dismiss();
                            startActivity(new Intent(EditProfileBusiness.this,MainActivity.class));
                            finish();
                        }
                    });

                }
            }
        });
    }

    private void setBussProData() {
        etBussZip.setText(strBZip);
        etBussPhone.setText(strBPhone);
        etBussDec.setText(strBDec);
        etBussName.setText(strBName);
        etBussAdd.setText(strBAdd);

        Glide.with(this).load(strBImg)
                .bitmapTransform(new CropCircleTransformation(EditProfileBusiness.this))
                .into(ivBussinessImg);
    }

    private void findIdd(){

       etBussAdd = (EditText) findViewById(R.id.et_edit_buss_add);
       etBussDec = (EditText) findViewById(R.id.et_edit_buss_dec);
       etBussName = (EditText) findViewById(R.id.et_edit_buss_name);
       etBussPhone = (EditText) findViewById(R.id.et_edit_buss_phone);
       etBussZip = (EditText) findViewById(R.id.et_edit_buss_zip);

       btnCancel = (Button) findViewById(R.id.btnCancel);
       btnDone = (Button) findViewById(R.id.btnDone);

       ivBussinessImg = (ImageView) findViewById(R.id.iv_business_img);

   }


    @Override
    public void onPickImageResult(Bitmap bitmap) {

    }

    @Override
    public void onPickImageResult(Uri bitmap) {
        uriImg = bitmap;
        Glide.with(this).load(bitmap)
                .bitmapTransform(new CropCircleTransformation(EditProfileBusiness.this))
                .into(ivBussinessImg);
    }
}
