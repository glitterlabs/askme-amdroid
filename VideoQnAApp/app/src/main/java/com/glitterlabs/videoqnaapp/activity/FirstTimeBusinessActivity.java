package com.glitterlabs.videoqnaapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.glitterlabs.videoqnaapp.R;
import com.glitterlabs.videoqnaapp.model.BisunessProfile;
import com.glitterlabs.videoqnaapp.model.EditProfile;
import com.glitterlabs.videoqnaapp.model.PickImage;
import com.glitterlabs.videoqnaapp.model.SingaltonUser;
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

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FirstTimeBusinessActivity extends AppCompatActivity implements LocationListener{

    private LocationManager locationManager;
    private String latitude,langitude;

    public int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Uri uriImg;

    private ImageView ivBusinessImg;
    private EditText etBusinessName,etBusinessAdd,etBussinessDec,etBusinessPhone,etBusinessZip;
    private String strBusinessName,strBusinessAdd,strBusinessDec,strPhoneNumber,strBusinessZip;

    private ProgressDialog progressDialog;
    private TextInputLayout inputBusinessName,inputBusinessPhone,inputBusinessDec,inputBusinessZip,inputBusinessAdd;

    private Button btnNext;
    Bitmap bitmapUrl;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseUser firebaseUser;

    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    String strUserType = "Business";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busines_profile);

        progressDialog = new ProgressDialog(this);
        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();

        storageRef = storage.getReferenceFromUrl("gs://video-q-and-a.appspot.com/business_images/");

        database = FirebaseDatabase.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        findId();

        ivBusinessImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImage.pickImage(FirstTimeBusinessActivity.this);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send Data to firebase Server
                sendBusinessInfo();
            }
        });
    }

    private void sendBusinessInfo() {
        strBusinessName = etBusinessName.getText().toString().trim();
        strBusinessAdd = etBusinessAdd.getText().toString().trim();
        strBusinessZip = etBusinessZip.getText().toString().trim();
        strBusinessDec = etBussinessDec.getText().toString().trim();
        strPhoneNumber = etBusinessPhone.getText().toString().trim();

        if (!validBissName()) {
            return;
        }

        if (!validBissPhone()) {
            return;
        }

        if (!validBissAdd()) {
            return;
        }
        if (!validBissZip()) {
            return;
        }
        if (!validBissDec()) {
            return;
        }

        // show progress dailog
        progressDialog.setMessage("Submitting Information...");
        progressDialog.show();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ivBusinessImg.setDrawingCacheEnabled(true);

      /*  StorageReference mountainsRef = storageRef.child(firebaseUser.getUid());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapUrl.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                writeBusinessUser(latitude,langitude,strBusinessName,strBusinessAdd,strBusinessZip,strBusinessDec,strPhoneNumber,firebaseUser.getUid(),String.valueOf(downloadUrl),strUserType);

                progressDialog.dismiss();
                startActivity(new Intent(FirstTimeBusinessActivity.this,MainActivity.class));
                finish();
            }
        });*/

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
                    writeBusinessUser(latitude,langitude,strBusinessName,strBusinessAdd,strBusinessZip,strBusinessDec,strPhoneNumber,firebaseUser.getUid(),String.valueOf(downloadUrl),strUserType);
                    progressDialog.dismiss();
                    startActivity(new Intent(FirstTimeBusinessActivity.this,MainActivity.class));
                    finish();
                }
            });
        }else{
            // Get the data from an ImageView as bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapUrl.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageRef.child("business_images").putBytes(data);
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
                    writeBusinessUser(latitude,langitude,strBusinessName,strBusinessAdd,strBusinessZip,strBusinessDec,strPhoneNumber,firebaseUser.getUid(),String.valueOf(downloadUrl),strUserType);
                    progressDialog.dismiss();
                    startActivity(new Intent(FirstTimeBusinessActivity.this,MainActivity.class));
                    finish();
                }
            });
        }

    }

    private void writeBusinessUser(String latitude,String langitude,String strBusinessName,String strBusinessAdd,String strBusinessZip,String strBusinessDec,String strPhoneNumber,String userUId,String strImgUrl,String strUserType){
        EditProfile editProfile = new EditProfile(latitude, langitude, strBusinessName,strPhoneNumber,strBusinessAdd,strBusinessZip,strBusinessDec,strImgUrl,strUserType,userUId);
        mDatabase.child("users").child(userUId).child("profile").setValue(editProfile);
      //  Toast.makeText(FirstTimeBusinessActivity.this,"User Data Submitted",Toast.LENGTH_SHORT).show();
    }

    private void findId(){

        ivBusinessImg = (ImageView) findViewById(R.id.iv_business_pic);

        etBusinessName = (EditText) findViewById(R.id.et_business_name);
        etBusinessAdd = (EditText) findViewById(R.id.et_business_add);
        etBusinessZip = (EditText) findViewById(R.id.et_business_zip);
        etBusinessPhone = (EditText) findViewById(R.id.et_business_phone);
        etBussinessDec = (EditText) findViewById(R.id.et_business_dec);

        inputBusinessName = (TextInputLayout) findViewById(R.id.inputBusinessName);
        inputBusinessDec = (TextInputLayout) findViewById(R.id.inputBusinessDec);
        inputBusinessPhone = (TextInputLayout) findViewById(R.id.inputBusinessPhone);
        inputBusinessZip = (TextInputLayout) findViewById(R.id.inputBusinessZip);
        inputBusinessAdd = (TextInputLayout) findViewById(R.id.inputBusinessAdd);

        etBusinessName.addTextChangedListener(new MyTextWatcher(etBusinessName));
        etBusinessAdd.addTextChangedListener(new MyTextWatcher(etBusinessAdd));
        etBusinessPhone.addTextChangedListener(new MyTextWatcher(etBusinessPhone));
        etBusinessZip.addTextChangedListener(new MyTextWatcher(etBusinessZip));
        etBussinessDec.addTextChangedListener(new MyTextWatcher(etBussinessDec));

        btnNext = (Button) findViewById(R.id.btn_business_next);
    }

    private void getMyLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onLocationChanged(Location location) {

       latitude = Double.toString(location.getLatitude());
        langitude =Double.toString(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
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
                case R.id.inputBusinessName:
                    validBissName();
                    break;
                case R.id.inputBusinessAdd:
                    validBissAdd();
                    break;
                case R.id.inputBusinessDec:
                    validBissDec();
                    break;
                case R.id.inputBusinessZip:
                    validBissZip();
                    break;
                case R.id.inputBusinessPhone:
                    validBissPhone();
                    break;
            }
        }
    }

    private boolean validBissName(){
        if (etBusinessName.getText().toString().trim().isEmpty()) {
            inputBusinessName.setError("Enter business name");
            requestFocus(etBusinessName);
            return false;
        } else {
            inputBusinessName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validBissPhone(){
        if (etBusinessPhone.getText().toString().trim().isEmpty()) {
            inputBusinessPhone.setError("Enter business phone number");
            requestFocus(etBusinessPhone);
            return false;
        } else {
            inputBusinessPhone.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validBissAdd(){
        if (etBusinessAdd.getText().toString().trim().isEmpty()) {
            inputBusinessAdd.setError("Enter business Address");
            requestFocus(etBusinessAdd);
            return false;
        } else {
            inputBusinessAdd.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validBissZip(){
        if (etBusinessZip.getText().toString().trim().isEmpty()) {
            inputBusinessZip.setError("Enter business zip code");
            requestFocus(etBusinessZip);
            return false;
        } else {
            inputBusinessZip.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validBissDec(){
        if (etBussinessDec.getText().toString().trim().isEmpty()) {
            inputBusinessDec.setError("Enter business description");
            requestFocus(etBussinessDec);
            return false;
        } else {
            inputBusinessDec.setErrorEnabled(false);
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
                            .bitmapTransform(new CropCircleTransformation(FirstTimeBusinessActivity.this))
                            .into(ivBusinessImg);
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
                            .bitmapTransform(new CropCircleTransformation(FirstTimeBusinessActivity.this))
                            .into(ivBusinessImg);
                }
            } else {
                Util.showDialogMessage(FirstTimeBusinessActivity.this,"Image Not Set","Image Not Set, Please try again");
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FirstTimeBusinessActivity.this,FirstTimeNormalActivity.class));
    }
}

