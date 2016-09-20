package com.bookwormng.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.bookwormng.android.Data.FireBaseUser;
import com.bookwormng.android.Data.UserDBHelper;
import com.bookwormng.android.Operations.FireBaseOperations;

import twitter4j.User;


public class LogOutActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);
        new LogOut().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_out, menu);
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
    private class LogOut extends AsyncTask<User, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(LogOutActivity.this, "", "Logging out...", false, false);
            progressDialog.show();
        }
        @Override
        protected  Void doInBackground(twitter4j.User... params)
        {
            try
            {
                new FireBaseOperations(LogOutActivity.this).ReturnAdsToOnlineDatabase();

                FireBaseOperations fireBaseOperations = new FireBaseOperations(LogOutActivity.this);
                com.bookwormng.android.Data.User user = new UserDBHelper(getApplicationContext())
                        .CreateUserFromDatabase();
                if (user != null)
                {
                fireBaseOperations.updateUserOnDatabase(new FireBaseUser(user));
                }

                new UserDBHelper(getApplicationContext()).DeleteUser(user);
                Thread.sleep(2005);
            }
            catch (Exception e)
            {
                Log.e("Exception", e.toString());
            }
            progressDialog.dismiss();
            startActivity(new Intent(getApplicationContext(), FirstActivity.class));
            return null;
        }
    }
}
