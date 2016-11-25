package com.glitterlabs.videoqnaapp.activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.glitterlabs.videoqnaapp.R;
import com.glitterlabs.videoqnaapp.model.EditProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

public class ProfileNormalActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    TextView tvFName,tvLName,tvPhoneNo,tvEmailId;
    ImageView ivProfile;
    CollapsingToolbarLayout collapsingToolbar;
    FloatingActionButton fabEdit;
    private String strFName,strLName,strPhone,strEmail,strUrl,userUId,userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_normal);
        toolBar();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("profile");

        tvFName = (TextView) findViewById(R.id.strFName);
        tvLName = (TextView) findViewById(R.id.strLName);
        tvPhoneNo = (TextView) findViewById(R.id.strPhoneNo);
        tvEmailId = (TextView) findViewById(R.id.strEmail);
        ivProfile = (ImageView) findViewById(R.id.iv_profile_pic);
        fabEdit = (FloatingActionButton) findViewById(R.id.btnFloat);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);

        getprofileInfo();

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileNormalActivity.this, EditProfileNormal.class);
                intent.putExtra("fname", strFName);
                intent.putExtra("lname", strLName);
                intent.putExtra("phone", strPhone);
                intent.putExtra("url", strUrl);
                intent.putExtra("user_type",userType);
                intent.putExtra("user_id",userUId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void toolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getprofileInfo(){

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                EditProfile editProfile = dataSnapshot.getValue(EditProfile.class);

                 strFName = editProfile.getfName();
                strLName = editProfile.getlName();
                strPhone = editProfile.getPhoneNumber();
                strUrl = editProfile.getProfileUrl();
                strEmail = firebaseUser.getEmail();
                userType = editProfile.getUserType();
                userUId = editProfile.getUserUId();

                setProfileData(strFName,strLName,strPhone,strUrl,strEmail);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(ProfileNormalActivity.class.getSimpleName(), "Failed to read value.", error.toException());
            }
        });

    }

    private void setProfileData(String fName,String lName,String phone,String url,String email){

        tvFName.setText(fName);
        tvLName.setText(lName);
        tvPhoneNo.setText(phone);
        tvEmailId.setText(email);

        collapsingToolbar.setTitle(fName);

    /* Glide.with(this).load(url)
                .bitmapTransform(new FitCenter(ProfileNormalActivity.this))
                .into(ivProfile);*/
        Picasso.with(ProfileNormalActivity.this).load(url).into(ivProfile);
    }

}
