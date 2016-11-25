package com.glitterlabs.videoqnaapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.glitterlabs.videoqnaapp.R;
import com.glitterlabs.videoqnaapp.fragment.BusinessUserFragment;
import com.glitterlabs.videoqnaapp.fragment.ChatHistoryFragment;
import com.glitterlabs.videoqnaapp.model.EditProfile;
import com.glitterlabs.videoqnaapp.model.FeedBack;
import com.glitterlabs.videoqnaapp.model.SingaltonUser;
import com.glitterlabs.videoqnaapp.model.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private DatabaseReference mDatabase,mFeedDatabase;
    private FirebaseUser firebaseUser;

    private String strUserType,strUID;
    private ProgressDialog progressDialog;
    String userType;
    SingaltonUser singaltonUser;

    ArrayList<EditProfile> arrayListEdit = new ArrayList<EditProfile>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        progressDialog = new ProgressDialog(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        strUID = firebaseUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

    //    mDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("profile");
     //   mDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("profile");
      //  mFeedDatabase = FirebaseDatabase.getInstance().getReference("feedback").child(firebaseUser.getUid());

       setSingleTone();


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BusinessUserFragment(), "Business User");
        adapter.addFragment(new ChatHistoryFragment(), "Chat History");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuProfile:
                singaltonUser = SingaltonUser.getInstance();
                 userType = singaltonUser.getUserType();
                if(userType == null){
                    startActivity(new Intent(MainActivity.this, FirstTimeNormalActivity.class));
                }else {
                    if (userType.equalsIgnoreCase("normal")) {
                        startActivity(new Intent(MainActivity.this, ProfileNormalActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, ProfileBusinessActivity.class));
                    }
                }
                return true;
            case R.id.menuShare:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, "Really Amazing app use tease app");
                share.putExtra(Intent.EXTRA_TEXT, "http://www.glitterlabs.com");
                startActivity(Intent.createChooser(share, "Share link!"));
                return true;
            case R.id.menuAbout:
                startActivity(new Intent(MainActivity.this,AboutUsActivity.class));
                return true;

            case R.id.menuFeedback:
                feedBack();
                return true;
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void feedBack(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.report_bug_layout, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                       String strFeed = userInputDialogEditText.getText().toString().trim();
                        FeedBack feedBack = new FeedBack(strFeed);
                        mDatabase.child("feedback").child(strUID).setValue(feedBack);
                        Toast.makeText(MainActivity.this,"Report bugs Submitted",Toast.LENGTH_SHORT).show();
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void setSingleTone(){
        mDatabase.child("users").child(strUID).child("profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                EditProfile editProfile = dataSnapshot.getValue(EditProfile.class);
                strUserType = editProfile.getUserType();
                SingaltonUser object = SingaltonUser.getInstance();
                object.setUserType(strUserType);
               // object.setStrUID(strUID);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(MainActivity.class.getSimpleName(), "Failed to read value.", error.toException());
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
