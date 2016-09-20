package com.bookwormng.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.bookwormng.android.Data.User;
import com.bookwormng.android.Data.UserDBHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;


public class SelectPictureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(android.R.drawable.ic_menu_gallery);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        Button selectPictureButton = (Button)findViewById(R.id.button6);
        selectPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
        Button noPicButton = (Button)findViewById(R.id.button7);
        noPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SelectPictureActivity.this);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(getString(R.string.pic_string),"empty" );
                String c = preferences.getString(getString(R.string.pic_string), "");
                boolean a = edit.commit();
                Intent i = new Intent(getApplication(), HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        Button avatarPicButton = (Button)findViewById(R.id.button4);
        avatarPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SelectPictureActivity.this);
                String c = preferences.getString(getString(R.string.avatar_string), "");
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(getString(R.string.pic_string),c );
                c = preferences.getString(getString(R.string.pic_string), "");
                boolean a = edit.commit();
                Intent i = new Intent(getApplication(), HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void SavePictureToDataBase(String picData)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SelectPictureActivity.this);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.pic_string),picData);
        String c = preferences.getString(getString(R.string.pic_string), "");
        boolean a = edit.commit();
        UserDBHelper userDB = new UserDBHelper(getApplicationContext());
        User newUser = userDB.CreateUserFromDatabase();
    //    newUser.setPicString(picData);
      //  userDB.UpdateUserDatabase(newUser);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try
            {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                new StorePictureAsync().execute(bitmap);
                Intent i = new Intent(getApplication(), HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                //can issue a dialog or toast here.
            }
            catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private class StorePictureAsync extends AsyncTask<Bitmap,Void,Void>
    {
        @Override
        protected Void doInBackground(Bitmap... bitmap)
        {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try
            {
                bitmap[0].compress(Bitmap.CompressFormat.JPEG, 100, bao);
            }
            catch (Exception e)
            {
                bitmap[0].compress(Bitmap.CompressFormat.PNG, 100, bao);
            }
            byte [] b = bao.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            SavePictureToDataBase(temp);
            Intent i = new Intent(getApplication(), HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return null;
        }
    }
}
