package com.glitterlabs.videoqnaapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.glitterlabs.videoqnaapp.R;
import com.glitterlabs.videoqnaapp.model.EditProfile;
import com.glitterlabs.videoqnaapp.model.PickImage;
import com.glitterlabs.videoqnaapp.model.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FirstTimeNormalActivity extends AppCompatActivity {


    private ImageView ivProfile;

    private EditText etFirstName, etLastName, etPhoneNumber;
    private TextInputLayout inputFName, inputLName, inputPhone;
    private ProgressDialog progressDialog;

    String strUserType = "normal";

    private Button btnNext, btnCreateBusinessUser;

    String photoPath;
    Bitmap bitmapUrl;
    private Uri uriImg;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseUser firebaseUser;

    private DatabaseReference mDatabase;
    private FirebaseDatabase database;

    private String userChoosenTask;
    public int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private String email,pass,conformPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_screen_setup);

        progressDialog = new ProgressDialog(this);

        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();

        storageRef = storage.getReferenceFromUrl("gs://video-q-and-a.appspot.com/profile_pics/");

        database = FirebaseDatabase.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        findId();

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PickImageDialog.on(FirstTimeNormalActivity.this, new PickSetup());
                PickImage.pickImage(FirstTimeNormalActivity.this);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send Data to firebase Server
                sendProfileInfo();
            }
        });

        btnCreateBusinessUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstTimeNormalActivity.this, FirstTimeBusinessActivity.class));
            }
        });

    }

    private void sendProfileInfo() {

        // collecting values from user
        String fName = etFirstName.getText().toString().trim();
        String lName = etLastName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if (!validFName()) {
            return;
        }

        if (!validLName()) {
            return;
        }

        if (!validPhoneNo()) {
            return;
        }

        final String firstName = etFirstName.getText().toString().trim();
        final String lastName = etLastName.getText().toString().trim();
        final String phoneNo = etPhoneNumber.getText().toString().trim();

        // show progress dailog
        progressDialog.setMessage("Submitting Information...");
        progressDialog.show();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       // ivProfile.setDrawingCacheEnabled(true);

        if(!(uriImg == null)){
        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child(uriImg.getLastPathSegment());

        UploadTask uploadTask = mountainsRef.putFile(uriImg);
        // Register observers to listen for when the download is done or if it fails
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
                writeNewUser(firstName, lastName, phoneNo, strUserType, String.valueOf(downloadUrl), firebaseUser.getUid());

                progressDialog.dismiss();
                Intent intentMain = new Intent(FirstTimeNormalActivity.this, MainActivity.class);
                startActivity(intentMain);
                finish();
            }
        });
        }else{
            // Get the data from an ImageView as bytes

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
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    writeNewUser(firstName, lastName, phoneNo, strUserType, String.valueOf(downloadUrl), firebaseUser.getUid());

                    progressDialog.dismiss();
                    Intent intentMain = new Intent(FirstTimeNormalActivity.this, MainActivity.class);
                    startActivity(intentMain);
                    finish();
                }
            });
        }
    }

    private void findId() {
        etFirstName = (EditText) findViewById(R.id.et_fts_FName);
        etLastName = (EditText) findViewById(R.id.et_fts_LName);
        etPhoneNumber = (EditText) findViewById(R.id.et_fts_PhoneNo);

        inputFName = (TextInputLayout) findViewById(R.id.inputFName);
        inputLName = (TextInputLayout) findViewById(R.id.inputLName);
        inputPhone = (TextInputLayout) findViewById(R.id.inputPhone);


        btnNext = (Button) findViewById(R.id.btnNext);
        btnCreateBusinessUser = (Button) findViewById(R.id.btn_create_business_profile);

        ivProfile = (ImageView) findViewById(R.id.iv_profile_pic);

        etPhoneNumber.addTextChangedListener(new MyTextWatcher(etPhoneNumber));
        etLastName.addTextChangedListener(new MyTextWatcher(etLastName));
        etFirstName.addTextChangedListener(new MyTextWatcher(etFirstName));

    }

    private void writeNewUser(String fName, String lName, String phoneNumber, String userType, String profileUrl, String userUId) {
        EditProfile editProfile = new EditProfile(fName, lName, phoneNumber, userType, profileUrl, userUId);
        //   mDatabase.child("users").child("user_type").child("normal").child(userUId).child("profile").setValue(userProfile);
        mDatabase.child("users").child(userUId).child("profile").setValue(editProfile);
     //   Toast.makeText(FirstTimeNormalActivity.this, "User Data Submitted", Toast.LENGTH_SHORT).show();
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
                case R.id.inputFName:
                    validFName();
                    break;
                case R.id.inputLName:
                    validLName();
                    break;
                case R.id.inputPhone:
                    validPhoneNo();
                    break;
            }
        }
    }

    private boolean validFName() {
        if (etFirstName.getText().toString().trim().isEmpty()) {
            inputFName.setError("Enter First Name");
            requestFocus(etFirstName);
            return false;
        } else {
            inputFName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validLName() {
        if (etLastName.getText().toString().trim().isEmpty()) {
            inputLName.setError("Enter Last Name");
            requestFocus(etLastName);
            return false;
        } else {
            inputLName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validPhoneNo() {
        if (etPhoneNumber.getText().toString().trim().isEmpty()) {
            inputPhone.setError("Enter Phone Number");
            requestFocus(etPhoneNumber);
            return false;
        } else {
            inputPhone.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (!(data == null)) {

                if (requestCode == SELECT_FILE) {
                    uriImg = data.getData();
                    Glide.with(this).load(uriImg)
                            .bitmapTransform(new CropCircleTransformation(FirstTimeNormalActivity.this))
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
                            .bitmapTransform(new CropCircleTransformation(FirstTimeNormalActivity.this))
                            .into(ivProfile);
                }
            } else {
                Util.showDialogMessage(FirstTimeNormalActivity.this,"Image Not Set","Image Not Set, Please try again");
            //    Toast.makeText(this, "Image Not Set, Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Util.showDialogMessage(FirstTimeNormalActivity.this,"Empty Field Error","Filled require data");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                               // Toast.makeText(MainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(FirstTimeNormalActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Util.showDialogMessage(FirstTimeNormalActivity.this,"Error Message","Failed to delete your account");
                              //  Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                              //  progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

}
