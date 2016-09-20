package com.bookwormng.android;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import java.io.*;
import java.net.URL;
import java.util.List;

import android.widget.TextView;
import android.widget.Toast;

import com.bookwormng.android.Data.Question;
import com.bookwormng.android.Data.QuestionDBHelper;
import com.bookwormng.android.Operations.DatabaseLayer;
import com.bookwormng.android.Data.FireBaseUser;
import com.bookwormng.android.Data.TwitterConstants;
import com.bookwormng.android.Data.User;
import com.bookwormng.android.Data.UserDBHelper;
import com.bookwormng.android.Operations.FireBaseOperations;

import com.firebase.client.Firebase;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class MainActivity extends AppCompatActivity {

    //Members
    Twitter twitter;
    RequestToken requestToken;
    AccessToken accessToken;
    String OAuthUrl, OAuthVerifier, aviString;
    Dialog AuthDialog;
    WebView web;
    twitter4j.User user;
    ProgressDialog progressDialog;
    User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.twitter_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        //Firebase set context
        Firebase.setAndroidContext(this);
        //Twitter to app integration code
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(TwitterConstants.CONSUMER_KEY, TwitterConstants.CONSUMER_SECRET);

        Button signInButton = (Button)this.findViewById(R.id.button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new GetTokenAsync().execute();
                }
                catch (Exception e){
                    Log.e("Exception", e.getMessage());
                }
            }
        });

        TextView signUpView = (TextView)findViewById(R.id.textView15);
        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://twitter.com/signup"));
                startActivity(i);
            }
        });
    }

    void CreateUser(twitter4j.User tUser)
    {
        //Set and save user details
            TwitterConstants.avatarUrl =  tUser.getOriginalProfileImageURL();
            newUser = new User();
            newUser.setHandle(tUser.getScreenName());
            newUser.setName(tUser.getName());
            newUser.setLocation(tUser.getLocation());
            newUser.setBio(tUser.getDescription());
            new FinishUpUserCreation().execute(tUser);

    }
    private class RefreshDataBaseInBackground extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params)
        {
            List<Question> e = new QuestionDBHelper(getApplicationContext()).readUnansweredQuestion();
            if (e != null)
            {
                for(Question i: e)
                {
                    new QuestionDBHelper(getApplicationContext()).DeleteQuestion(i);
                }
            }
            DatabaseLayer.InitialDatabasePopulation(getApplicationContext());
            return null;
        }
        @Override
            protected  void onPostExecute(Void params)
        {
            Intent pictureIntent = new Intent(getApplication(), SelectPictureActivity.class);
            pictureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(pictureIntent);
        }
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    private class GetTokenAsync extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(MainActivity.this, "Logging in","Redirecting...", true, false);
            progressDialog.show();

        }
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                requestToken = twitter.getOAuthRequestToken(TwitterConstants.CALLBACK_URL);
                OAuthUrl = requestToken.getAuthenticationURL();
            }
            catch (Exception e)
            {
                Log.e("Exception", e.getMessage());
            }
            return OAuthUrl;
        }
        @Override
        protected  void onPostExecute(String url)
        {
            if ( url != null)
            {
                AuthDialog = new Dialog(MainActivity.this);
                AuthDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                AuthDialog.setContentView(R.layout.auth_layout);
                web = (WebView)AuthDialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(false);
                web.getSettings().setSaveFormData(false);
                web.getSettings().setAllowFileAccess(false);
                web.loadUrl(OAuthUrl);
                web.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        progressDialog.cancel();
                    }
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && authComplete == false) {
                            authComplete = true;
                            Uri uri = Uri.parse(url);
                            OAuthVerifier = uri.getQueryParameter("oauth_verifier");
                            AuthDialog.dismiss();
                            new GetAccessToken().execute();
                        } else if (url.contains("denied")) {
                            AuthDialog.dismiss();
                            Toast a = Toast.makeText(getBaseContext(), "Sorry !, Permission Denied", Toast.LENGTH_SHORT);
                            a.setGravity(Gravity.CENTER,0,0);
                            a.show();
                        }
                    }
                });
                AuthDialog.show();
                AuthDialog.setCancelable(true);
            }
            else
            {
                if (progressDialog != null)
                {
                    progressDialog.cancel();
                }
                Toast errorToast = Toast.makeText(getBaseContext(), "Sorry !, Network Error or Invalid Credentials", Toast.LENGTH_SHORT);
                errorToast.setGravity(Gravity.CENTER,0,0);
                errorToast.show();
            }
        }
    }
    private class GetAccessToken extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
           progressDialog = ProgressDialog.show(MainActivity.this, "Logging in","Retrieving details...", true, false);
            progressDialog.show();
        }
        @Override
        protected  Void doInBackground(Void... params)
        {
            try
            {
                accessToken = twitter.getOAuthAccessToken(requestToken, OAuthVerifier);
            }
            catch (Exception e)
            {
                Log.e("Exception", e.getMessage());
            }
            TwitterConstants.UserId = accessToken.getUserId();
            try
            {
                user = twitter.showUser(accessToken.getUserId());
            }
            catch (Exception e)
            {
                Log.e("Exception", e.getMessage());
            }
            progressDialog.cancel();
            return null;
        }
        @Override
        protected  void onPostExecute(Void params)
        {
            try
            {
                CreateUser(user);
            }
            catch (Exception e)
            {
                Log.e("Exception", e.toString());
            }
        }
    }
    private class FinishUpUserCreation extends AsyncTask<twitter4j.User, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(MainActivity.this, "Logging in","Still Retrieving details...", true, false);
            progressDialog.show();
        }
        @Override
        protected  Boolean doInBackground(twitter4j.User... params)
        {
            Bitmap bitmap1 = null;
            try
            {
                bitmap1 = BitmapFactory.decodeStream((InputStream) new URL(params[0].getOriginalProfileImageURL()).getContent());
            }
            catch (Exception e)
            {
                Log.e("Exception", e.toString());
            }
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try
            {
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            }
            catch (Exception e)
            {
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, bao);
            }
            byte [] b = bao.toByteArray();
            aviString = Base64.encodeToString(b, Base64.DEFAULT);
            //newUser.setAvatarString(aviString);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString(getString(R.string.avatar_string), aviString);
            boolean a = edit.commit();
            String c = preferences.getString(getString(R.string.avatar_string), "");
            newUser.setAccessToken(accessToken.getToken());
            newUser.setAccessTokenSecret(accessToken.getTokenSecret());
            newUser.setFollowers(params[0].getFollowersCount());
            newUser.setFriends(params[0].getFriendsCount());
            newUser.setUserId(params[0].getId());
            newUser.setSwipeLimit(3);
            //Saving user to the database
            SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = prefs2.edit();
            editor.putBoolean("stuck", false);
            edit.commit();
            SharedPreferences.Editor editor1 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
            editor1.putInt("consequtive_questions_missed", 0);
            edit.commit();

            //Reload user database if the user is a new user

            UserDBHelper userDB = new UserDBHelper(getApplicationContext());
            userDB.InsertUser(newUser);
            progressDialog.dismiss();
          return false;
        }
        @Override
        protected  void onPostExecute(Boolean params)
        {
            progressDialog = ProgressDialog.show(MainActivity.this, "","loading...", true, false);
            progressDialog.show();
            FireBaseOperations fBaseOperations = new FireBaseOperations(MainActivity.this);
            FireBaseUser fireBaseUser = new FireBaseUser(newUser);
            boolean isNewUser = fBaseOperations.saveUserToFBaseDb(fireBaseUser);
        }
    }
}
