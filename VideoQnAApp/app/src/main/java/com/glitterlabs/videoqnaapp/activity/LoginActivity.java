package com.glitterlabs.videoqnaapp.activity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.glitterlabs.videoqnaapp.R;
import com.glitterlabs.videoqnaapp.model.EditProfile;
import com.glitterlabs.videoqnaapp.model.SingaltonUser;
import com.glitterlabs.videoqnaapp.model.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,View.OnClickListener{

    private static final String TAG = "MainActivity";

    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_READ_EXTERNAL = 124;
    private static final int RC_WRITE_EXTERNAL = 125;
    private static final int RC_SETTINGS_SCREEN = 126;

    private EditText etLogEmail, etLogPassword;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private ProgressDialog progressDialog;
    private FirebaseUser currentUser;

    private Button btnLogRegister, btnLogLogin, btnLogReset;
    TextInputLayout inputLogEmail,inputLogPass;
    String strUserType;
    final boolean[] check = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        permissions();

        progressDialog = new ProgressDialog(this);

        findId();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnLogRegister.setOnClickListener(this);
        btnLogReset.setOnClickListener(this);
        btnLogLogin.setOnClickListener(this);

        if(currentUser!=null){
            //checkProfileData();
            /*if (strUserType == null){
                startActivity(new Intent(LoginActivity.this, FirstTimeNormalActivity.class));
                finish();
            }else {*/
           // setSingleTone();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
         //   }
        }
    }

    private void findId(){
        etLogEmail = (EditText) findViewById(R.id.et_log_email);
        etLogPassword = (EditText) findViewById(R.id.et_log_pass);
        btnLogRegister = (Button) findViewById(R.id.btn_log_register);
        btnLogLogin = (Button) findViewById(R.id.btn_log_Login);
        btnLogReset = (Button) findViewById(R.id.btn_reset_password);
        inputLogEmail = (TextInputLayout) findViewById(R.id.inputLoginEmail);
        inputLogPass = (TextInputLayout) findViewById(R.id.inputLoginPass);

        etLogEmail.addTextChangedListener(new MyTextWatcher(etLogEmail));
        etLogPassword.addTextChangedListener(new MyTextWatcher(etLogPassword));

    }

    @Override
    public void onClick(View view) {
        if(btnLogRegister == view) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        }

        if (btnLogLogin == view){

            String email = etLogEmail.getText().toString();
            final String password = etLogPassword.getText().toString();

            if (!validateEmail()) {
                return;
            }

            if (!validatePassword()) {
                return;
            }

            // show progress dailog
            progressDialog.setMessage("Logging User...");
            progressDialog.show();

            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            // show progress dailog
                            progressDialog.setMessage("Logging User...");
                            progressDialog.show();
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 6) {
                                    etLogPassword.setError(getString(R.string.minimum_password));
                                    progressDialog.dismiss();
                                } else {
                                   // Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                  //  showDialogMessage("Authentication Error",getString(R.string.auth_failed));
                                    Util.showDialogMessage(LoginActivity.this,"Authentication Error",getString(R.string.auth_failed));
                                    progressDialog.dismiss();
                                }
                            } else {
                              //  Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                              //  Util.showLocationDialog(LoginActivity.this,"Login Message","Login Successful.");
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
        }

        if(btnLogReset == view){
            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            finish();
        }
    }

    private boolean validateEmail() {
        String email = etLogEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLogEmail.setError("Enter correct email id");
            requestFocus(etLogEmail);
            return false;
        } else {
            inputLogEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (etLogPassword.getText().toString().trim().isEmpty()) {
            inputLogPass.setError("Enter password");
            requestFocus(etLogPassword);
            return false;
        } else {
            inputLogPass.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), null /* click listener */)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.inputLoginEmail:
                    validateEmail();
                    break;
                case R.id.inputLoginPass:
                    validatePassword();
                    break;
            }
        }
    }

    public void permissions(){
        cameraPer();
        storageReadPer();
        storageWritePer();
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraPer() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
         //   Toast.makeText(this, "TODO: Camera things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @AfterPermissionGranted(RC_READ_EXTERNAL)
    public void storageReadPer(){
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!
        //    Toast.makeText(this, "TODO: Read External Storage things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "Allow to read content in storage",
                    RC_READ_EXTERNAL, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @AfterPermissionGranted(RC_WRITE_EXTERNAL)
    public void storageWritePer(){
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!
         //   Toast.makeText(this, "TODO: Write External Storage things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "Allow to write content in storage",
                    RC_WRITE_EXTERNAL, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    /*private void checkProfileData(){
        if(mDatabase != null) {
            mDatabase.child("users").child(currentUser.getUid()).child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    EditProfile editProfile = dataSnapshot.getValue(EditProfile.class);
                    strUserType = editProfile.getUserType();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(MainActivity.class.getSimpleName(), "Failed to read value.", error.toException());
                }
            });
        }else{
            strUserType = null;
        }
    }*/

    private void setSingleTone(){
        mDatabase.child("users").child(currentUser.getUid()).child("profile").addValueEventListener(new ValueEventListener() {
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
}
