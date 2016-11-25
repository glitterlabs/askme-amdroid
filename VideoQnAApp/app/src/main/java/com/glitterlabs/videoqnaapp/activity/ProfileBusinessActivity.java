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

public class ProfileBusinessActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;

    TextView tvBusinessAdd,tvBusinessZip,tvBusinessPhone,tvBusinessDec,tvBusinessEmail;
    private String strBName,strBAdd,strBZip,strBPhone,strBDec,strBEmail,strBImg;

    ImageView ivbusinessPro;
    CollapsingToolbarLayout collapsingToolbar;
    FloatingActionButton fabEdit;

    public static final String B_NAME = "b_name";
    public static final String B_ADD = "b_add";
    public static final String B_ZIP = "b_zip";
    public static final String B_DEC = "b_dec";
    public static final String B_PHONE = "b_phone";
    public static final String B_IMG = "b_img";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_business);

        toolBar();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("profile");

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        findIdd();

        getBusinessProInfo();

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileBusinessActivity.this, EditProfileBusiness.class);
                intent.putExtra(B_NAME, strBName);
                intent.putExtra(B_ADD, strBAdd);
                intent.putExtra(B_PHONE, strBPhone);
                intent.putExtra(B_IMG, strBImg);
                intent.putExtra(B_ZIP,strBZip);
                intent.putExtra(B_DEC,strBDec);
                startActivity(intent);
            }
        });
    }

    private void findIdd(){
        tvBusinessAdd = (TextView) findViewById(R.id.strBAdd);
        tvBusinessDec = (TextView) findViewById(R.id.strBDec);
        tvBusinessEmail = (TextView) findViewById(R.id.strBEmail);
        tvBusinessZip = (TextView) findViewById(R.id.strBZip);
        tvBusinessPhone = (TextView) findViewById(R.id.strBPhone);

        ivbusinessPro = (ImageView) findViewById(R.id.iv_business_pic);

        fabEdit = (FloatingActionButton) findViewById(R.id.btnBFloat);
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


    private void getBusinessProInfo() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                EditProfile editProfile = dataSnapshot.getValue(EditProfile.class);

                strBName = editProfile.getStrBusinessName();
                strBAdd = editProfile.getStrBusinessAdd();
                strBDec = editProfile.getStrBusinessDec();
                strBPhone = editProfile.getStrPhoneNumber();
                strBZip = editProfile.getStrBusinessZip();
                strBEmail = firebaseUser.getEmail();
                strBImg = editProfile.getStrImgUrl();

                setBusinessData(strBName,strBAdd,strBPhone,strBZip,strBDec,strBEmail,strBImg);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(MainActivity.class.getSimpleName(), "Failed to read value.", error.toException());
            }
        });

    }

    private void setBusinessData(String strBName,String strBAdd,String strBPhone,String strBZip,String strBDec,String strBEmail,String strBImg){
        tvBusinessEmail.setText(strBEmail);
        tvBusinessPhone.setText(strBPhone);
        tvBusinessZip.setText(strBZip);
        tvBusinessDec.setText(strBDec);
        tvBusinessAdd.setText(strBAdd);

        collapsingToolbar.setTitle(strBName);

       /* Glide.with(this).load(strBImg)
                .bitmapTransform(new CenterCrop(ProfileBusinessActivity.this))
                .into(ivbusinessPro);*/
        Picasso.with(ProfileBusinessActivity.this).load(strBImg).into(ivbusinessPro);
    }
}
