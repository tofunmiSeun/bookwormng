package com.bookwormng.android;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bookwormng.android.Data.TwitterConstants;
import com.bookwormng.android.Data.User;
import com.bookwormng.android.Data.UserDBHelper;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterProfileActivity extends AppCompatActivity {

    ProgressDialog progressDialog2;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserDBHelper userDBHelper = new UserDBHelper(getApplicationContext());
        user = userDBHelper.CreateUserFromDatabase();
        setContentView(R.layout.activity_twitter_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.twitter_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        TextView nameView = (TextView)findViewById(R.id.textView11);
        nameView.setText(user.getName());
        TextView handleView = (TextView)findViewById(R.id.textView12);
        handleView.setText("@" + user.getHandle());
        TextView locationView = (TextView)findViewById(R.id.textView13);
        locationView.setText(user.getLocation());
        TextView followerView = (TextView)findViewById(R.id.textView18);
        followerView.setText(String.valueOf(user.getFollowers()));
        TextView friendView = (TextView)findViewById(R.id.textView19);
        friendView.setText(String.valueOf(user.getFriends()));
        TextView bioView = (TextView)findViewById(R.id.textView20);
        bioView.setText(user.getBio());
        final TextView charCountView = (TextView)findViewById(R.id.textView21);
        ImageView aviView = (ImageView)findViewById(R.id.imageView3);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TwitterProfileActivity.this);
        String c = preferences.getString(getString(R.string.avatar_string), "");
        byte [] b = Base64.decode(c.getBytes(), 0);
        Bitmap myBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        HomeActivity.SetImageInView(myBitmap, aviView);
        final EditText tweetEditText = (EditText)findViewById(R.id.editText);
        final Button tweetButton = (Button)findViewById(R.id.button8);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostTweet().execute(tweetEditText.getText().toString());
                tweetEditText.setText("");
                InputMethodManager inputManager = (InputMethodManager)TwitterProfileActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        tweetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int count2 = tweetEditText.getText().length();
                charCountView.setText(String.valueOf(140 - count2));
                if (count2 > 140) {
                    tweetButton.setEnabled(false);
                } else {
                    tweetButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class PostTweet extends AsyncTask<String, Void, String>
    {
        UserDBHelper userDBHelper = new UserDBHelper(getApplicationContext());
        User user = userDBHelper.CreateUserFromDatabase();
        @Override
        protected void onPreExecute()
        {
           progressDialog2 = ProgressDialog.show(TwitterProfileActivity.this, "tweeting", "posting tweet...", true, false);
            progressDialog2.show();
        }
        @Override
        protected  String doInBackground(String...tweet)
        {
            ConfigurationBuilder cbuilder = new ConfigurationBuilder();
            cbuilder.setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY);
            cbuilder.setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET);
            AccessToken accessToken = new AccessToken(user.getAccessToken(),user.getAccessTokenSecret());
            Twitter twitter = new TwitterFactory(cbuilder.build()).getInstance(accessToken);
            try
            {
                twitter4j.Status response = twitter.updateStatus(tweet[0]);
                return  response.toString();
            }
            catch (Exception e)
            {
                Log.e("Exception",e.toString());
            }
            return null;
        }
        @Override
        protected  void onPostExecute(String myString)
        {
           progressDialog2.cancel();
            if (myString != null)
            {
                Toast t = Toast.makeText(getBaseContext(),"tweet posted successfully!",Toast.LENGTH_SHORT);
                t.show();
            }
            else
            {
                Toast t = Toast.makeText(getBaseContext(),"error, try again later",Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_twitter_profile, menu);
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
            startActivity(new Intent(getApplicationContext(),MySettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        if (progressDialog2 != null)
        {
            progressDialog2.dismiss();
        }
        super.onPause();
    }
    @Override
    protected void onStop()
    {
        if (progressDialog2 != null)
        {
            progressDialog2.dismiss();
        }
        super.onStop();
    }
    @Override
    protected void onDestroy()
    {
        if (progressDialog2 != null)
        {
            progressDialog2.dismiss();
        }
        super.onDestroy();
    }
}
