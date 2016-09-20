package com.bookwormng.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.bookwormng.android.Data.FireBaseUser;
import com.bookwormng.android.Data.UserDBHelper;
import com.bookwormng.android.Operations.FireBaseOperations;

import twitter4j.User;


public class SyncDataActivity extends MySettingsActivity {
FireBaseOperations fireBaseOperations;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new SyncData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sync_data, menu);
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

    private class SyncData extends AsyncTask<User, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(SyncDataActivity.this, "", "Syncing user details to server...", false, false);
            progressDialog.show();
        }
        @Override
        protected  Void doInBackground(twitter4j.User... params)
        {
            try
            {
                SyncDataActivity.this.fireBaseOperations = new FireBaseOperations(SyncDataActivity.this);
                com.bookwormng.android.Data.User user = new UserDBHelper(getApplicationContext())
                        .CreateUserFromDatabase();
                if (user != null)
                {
                    SyncDataActivity.this.fireBaseOperations.updateUserOnDatabase(new FireBaseUser(user));
                }
                Thread.sleep(2005);
            }catch (Exception e)
            {
                Log.e("Exception", e.toString());
            }
           progressDialog.dismiss();
            startActivity(new Intent(getApplicationContext(), MySettingsActivity.class));
            return null;
        }
    }
}
