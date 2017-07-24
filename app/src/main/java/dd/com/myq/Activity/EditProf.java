package dd.com.myq.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;
import dd.com.myq.App.Config;
import dd.com.myq.R;
import dd.com.myq.Util.SessionManager;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static dd.com.myq.Util.SessionManager.KEY_ABOUTME;
import static dd.com.myq.Util.SessionManager.KEY_COLLEGE_NAME;
import static dd.com.myq.Util.SessionManager.KEY_DOB;
import static dd.com.myq.Util.SessionManager.KEY_EMAIL;
import static dd.com.myq.Util.SessionManager.KEY_GENDER;
import static dd.com.myq.Util.SessionManager.KEY_UID;
import static dd.com.myq.Util.SessionManager.KEY_USERNAME;

public class EditProf extends AppCompatActivity {
    private static final String TAG ="" ;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;
    ImageView imgview;
    Button b1, b2;
    File ft;
    TransferObserver transferObserver;

    String imageUrl;
    AmazonS3Client s3;
    String picturePath;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    EditText e1, e2, e3, e4,e5;
    final int PIC_CROP = 1;
    public int Editflag = 0;
    private static int RESULT_LOAD_IMAGE = 1;

    public static final int MEDIA_TYPE_IMAGE = 1;

    private Uri fileUri; // file url to store image/video
    private Uri imageUri;

    String profilepicture, uid, a, b, d, e, c, s, f;
    RadioGroup rg;
    private ProgressDialog progress;
    private Button save_profile;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prof);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addListenerRadioButton();

        imgview = (ImageView) findViewById(R.id.profile_picture);

        myCalendar = Calendar.getInstance();
        e4 = (EditText) findViewById(R.id.editText7);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };




        imgview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        e4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog picker = new DatePickerDialog(EditProf.this,R.style.AppTheme_Dialog , date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                long now = System.currentTimeMillis() - 1000;
                picker.getDatePicker().setMaxDate(now);
                picker.show();
            }
        });


        if((new LoginActivity()).fb_flag == 1 || Editflag==1){

            Editflag = 1;
        }

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        progress = new ProgressDialog(this);
        progress.setMessage("Updating Profile...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);


        e1 = (EditText) findViewById(R.id.editText);
        e2 = (EditText) findViewById(R.id.editText2);
        e3 = (EditText) findViewById(R.id.editText3);
        e5 = (EditText) findViewById(R.id.editText8);



        e2.setKeyListener(null);///so that email cannot be updated
        e1.setKeyListener(null);///so that email cannot be updated

        Object name = user.get(KEY_USERNAME);
        e1.setText(name.toString());

        Object email = user.get(KEY_EMAIL);
        e2.setText(email.toString());

        final Object aboutme = user.get(KEY_ABOUTME);
        e3.setText(aboutme.toString());
        final Object dob = user.get(KEY_DOB);

        String[] parts = dob.toString().split("T"); // escape .
        String part1 = parts[0];
        e4.setText(part1);


        final Object college = user.get(KEY_COLLEGE_NAME);
        e5.setText(college.toString());

       // e4.setText(dob.toString());

        final Object userid = user.get(KEY_UID);
        uid = userid.toString();
        profilepicture = null;


        Object gender = user.get(KEY_GENDER);
        String sex = gender.toString();
        if (sex.equalsIgnoreCase("Male")) {
            s = "Male";
            rg.check(R.id.radioButton);
        } else {
            s = "Female";
            rg.check(R.id.radioButton2);
        }
        Log.d("gender is===========",sex);
        save_profile = (Button) findViewById(R.id.save_profile);
        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = e1.getText().toString();
                b = e2.getText().toString();
                c = s;
                d = e3.getText().toString();
                e = e4.getText().toString();
                f=e5.getText().toString();

                login(uid, b, a, e, c, d,f);
            }
        });

    }

    public void login(final String userid, String email, String username, String dob, String gender, String aboutme,String college) {

        if (TextUtils.isEmpty(a)) {
            e1.setError("This field cannot be empty!!");
            return;
        } else if (TextUtils.isEmpty(d)) {
            e3.setError("This field cannot be empty!!");
            return;
        } else {
            progress.show();

            AsyncHttpClient client = new AsyncHttpClient();

            RequestParams requestParams = new RequestParams();
            requestParams.put("userid", userid);
            requestParams.put("emailaddress", email);
            requestParams.put("aboutme", aboutme);
            requestParams.put("username", username);
            requestParams.put("gender", gender);
            requestParams.put("dob", dob);
            requestParams.put("college", college);


            client.post(this, Config.UpdateDetailAPIUrl, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    sessionManager.UpdateLoginSession(uid, a, b, d, e, c,f);

                    progress.hide();
                    Log.e("Response Login: ", response.toString());
                    Toast.makeText(EditProf.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    progress.hide();
                    Toast.makeText(EditProf.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    progress.hide();
                    Toast.makeText(EditProf.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseString) {
                    super.onFailure(statusCode, headers, throwable, responseString);
                    progress.hide();
                    Toast.makeText(EditProf.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //////////////////////////////
    private void updateLabel() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        e4.setText(sdf.format(myCalendar.getTime()));
    }
    ////////////////////////


    private void addListenerRadioButton() {

        rg = (RadioGroup) findViewById(R.id.radioGroup);
        b1 = (Button) findViewById(R.id.radioButton);
        b2 = (Button) findViewById(R.id.radioButton2);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioButton:
                        s=b1.getText().toString();
                        break;
                    case R.id.radioButton2:
                        s=b2.getText().toString();
                        break;
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
             picturePath = cursor.getString(columnIndex);
            cursor.close();

            imgview.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Toast.makeText(this, picturePath, Toast.LENGTH_LONG).show();
            putons3server(picturePath);
        }
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
              //  launchUploadActivity(true);
               boolean isImage=true;

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }

    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    public  void putons3server(String picturePath)
    {

        // image or video path that is captured in previous activity

        s3 =   new AmazonS3Client( new BasicAWSCredentials( "AKIAJBFZZ2I5V734U4XQ", "WGBjOfIGzW2opU05TYPhEKeyDnt+9vizATMPjrK3" ) );
        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.US_WEST_2));
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

        String comImg=compressImage(picturePath);
        Log.e("comImg",comImg);
        ft=new File(comImg);

        transferObserver = transferUtility.upload(
                "ddmyish",     /* The bucket to upload to */
                ft.getName(),    /* The key for the uploaded object */
                ft       /* The file where the data to upload exists */
        );

        // boolean flag to identify the media type, image or video
        boolean isImage = true;

        if (picturePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }



        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                // do something
                Log.e("onStateChanged",""+state);
                if(state.equals(TransferState.COMPLETED)){
                    Log.e("public path", s3.getResourceUrl("ddmyish", ft.getName()));
                    imageUrl=s3.getResourceUrl("ddmyish", ft.getName());

                }
            }


            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                //Display percentage transfered to user
                Log.e("percentage",""+percentage);
            }

            @Override
            public void onError(int id, Exception ex) {
                // do something
                Log.e("onError",""+ex);
            }

        });
    }


    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);

            imgview.setImageBitmap(bitmap);
        }
    }

//    private void launchUploadActivity(boolean isImage){
//        Intent i = new Intent(Search.this, UploadActivity.class);
//        i.putExtra("filePath", fileUri.getPath());
//        i.putExtra("isImage", isImage);
//        startActivity(i);
//    }

}