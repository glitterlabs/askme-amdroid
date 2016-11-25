package com.glitterlabs.videoqnaapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.glitterlabs.videoqnaapp.R;
import com.glitterlabs.videoqnaapp.model.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnRegister,btnLogin,btnCreateBusinessUser;
    private EditText etEmailId,etPasssword,etConformPass;
    private ProgressDialog progressDialog;
    private TextInputLayout inputRegEmailId,inputRegPass,inputRegConPass;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth firebaseAuth;
    private Toolbar mToolbar;
    String conformPass;
    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find the Ids
        findId();

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void findId(){
        btnRegister = (Button) findViewById(R.id.btn_reg_register);
        btnLogin = (Button) findViewById(R.id.btn_reg_login);
        btnCreateBusinessUser = (Button) findViewById(R.id.btn_create_business_profile);

        etEmailId = (EditText) findViewById(R.id.et_reg_email);
        etPasssword = (EditText) findViewById(R.id.et_reg_pass);
        etConformPass = (EditText) findViewById(R.id.et_reg_conform_pass);

        inputRegEmailId = (TextInputLayout) findViewById(R.id.inputRegEmail);
        inputRegPass = (TextInputLayout) findViewById(R.id.inputRegPass);
        inputRegConPass = (TextInputLayout) findViewById(R.id.inputRegConformPass);

        etEmailId.addTextChangedListener(new MyTextWatcher(etEmailId));
        etPasssword.addTextChangedListener(new MyTextWatcher(etPasssword));
        etConformPass.addTextChangedListener(new MyTextWatcher(etConformPass));
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
    }

    @Override
    public void onClick(View view) {

        if(view == btnRegister){
            // Register user logic
            registerUser();
        }

        if(view == btnLogin){
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            finish();
        }
    }

    private void registerUser() {

        // collecting values from user
        String email = etEmailId.getText().toString().trim();
        String pass = etPasssword.getText().toString().trim();
        conformPass = etConformPass.getText().toString().trim();

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        if(!validateConformPass()){
            return;
        }

        if(pass.equals(conformPass)){
            // show progress dailog
            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Error",e.getMessage());
                }
            })
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {
                                // Showing msg register successfully.
                              //  Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                                Intent intentMain = new Intent(RegisterActivity.this,FirstTimeNormalActivity.class);
                                startActivity(intentMain);
                                finish();
                            }else{
                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                            public void onFailure(@NonNull Exception e) {
                                    showDialogMessage("Error Message",e.getMessage());
                                    progressDialog.dismiss();
                                }
                            });

                            }
                        }
                    });
        }else{
            showDialogMessage("Password not matched","Password and conform password not matched.");
        }
    }

    private boolean validateEmail() {
        String email = etEmailId.getText().toString().trim();

        if (email.isEmpty() || !Util.isValidEmail(email)) {
            inputRegEmailId.setError("Enter correct email id");
            requestFocus(etEmailId);
            return false;
        } else {
            inputRegEmailId.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (etPasssword.getText().toString().trim().isEmpty()) {
            inputRegPass.setError("Enter password");
            requestFocus(etPasssword);
            return false;
        } else {
            inputRegPass.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConformPass() {

        if (conformPass.isEmpty()) {
            inputRegConPass.setError("Enter conform password");
            requestFocus(etConformPass);
            return false;
        } else {
            inputRegConPass.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
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
                case R.id.inputRegEmail:
                    validateEmail();
                    break;
                case R.id.inputRegPass:
                    validatePassword();
                    break;
            }
        }
    }

    private void showDialogMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        String positiveText = "Ok";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

}
