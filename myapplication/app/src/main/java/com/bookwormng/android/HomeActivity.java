package com.bookwormng.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bookwormng.android.Data.Advert;
import com.bookwormng.android.Data.AdvertDBHelper;
import com.bookwormng.android.Data.Question;
import com.bookwormng.android.Data.QuestionDBHelper;
import com.bookwormng.android.Data.User;
import com.bookwormng.android.Data.UserDBHelper;
import com.bookwormng.android.Operations.FireBaseOperations;
import com.bookwormng.android.Operations.HomeArrayAdapter;
import com.bookwormng.android.Operations.RoundImage;

import java.io.FileNotFoundException;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    User newUser = new User();
    Button playButton;
    boolean stuck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref_general,false);
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        stuck = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).getBoolean("stuck",false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.bookwormng_icon);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        ImageView imageView1 = (ImageView) findViewById(R.id.imageView);
        playButton = (Button)findViewById(R.id.button2);
        playButton.setOnClickListener(goToQuestionListener);

        //ROUNDED BACKGROUND
        android.graphics.Bitmap bm = android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.maleavatar);
        SetImageInView(bm, imageView1);

        //CODE

        //CALLING USER DATA FROM MY DATABASE
        UserDBHelper userDB = new UserDBHelper(getApplicationContext());
        newUser = userDB.CreateUserFromDatabase();


        boolean autoAdSync = myPreference.getBoolean(getString(R.string.pref_auto_advert_sync_key), false);
        boolean autoQuestionSync = myPreference.getBoolean(getString(R.string.pref_auto_question_sync_key), false);
        if (newUser != null)
        {
            try {
                Thread.sleep(1500);
            } catch (Exception e) {
            }
                if (!stuck)
                {
                    if (autoAdSync) {
                        if (autoQuestionSync) {
                            if (new AdvertDBHelper(getApplicationContext()).getUnsharedAdverts() == null) {
                                new FireBaseOperations(HomeActivity.this).getAdsFromOnlineDatabase(true);
                            } else {
                                Toast.makeText(getBaseContext(), "You still have ads remaining", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (new AdvertDBHelper(getApplicationContext()).getUnsharedAdverts() == null) {
                                new FireBaseOperations(HomeActivity.this).getAdsFromOnlineDatabase(false);
                            } else {
                                Toast.makeText(getBaseContext(), "You still have ads remaining", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        if (autoQuestionSync) {
                            if (new AdvertDBHelper(getApplicationContext()).getUnsharedAdverts() == null) {
                                if (new QuestionDBHelper(getApplicationContext()).readUnansweredQuestion() == null)
                                {
                                        new FireBaseOperations(HomeActivity.this).getQuestionsFromDatabase(75);
                                }
                            } else {
                                Toast.makeText(getBaseContext(), "You still have ads remaining", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            else
            {
                Toast.makeText(getBaseContext(),"You are stuck, you are not allowed to refresh deck...",Toast.LENGTH_SHORT).show();
            }
                }
            if (userDB.IsEmpty())
            {     //IF THERE IS NOBODY LOGGED ON, GO TO THE SIGN IN PAGE
                Log.e("empty",String.valueOf(userDB.IsEmpty()));
                Intent i = new Intent(getApplication(),FirstActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }

        else {// ELSE LOAD DATA
            if (new QuestionDBHelper(getApplicationContext()).DataBaseIsEmpty() && new AdvertDBHelper(getApplicationContext()).IsEmpty()) {
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast t = Toast.makeText(getBaseContext(), "No more questions and ads, refresh deck", Toast.LENGTH_LONG);
                        t.show();
                    }
                });
            }

            List<Question> q = new QuestionDBHelper(getApplicationContext()).readUnansweredQuestion();
            int qCount = 0, aCount = 0, fCount = 0, asCount = 0, sCount = 0;
            if (q != null) {
                qCount = q.size();
            }
            List<Advert> a = new AdvertDBHelper(getApplicationContext()).getUnsharedAdverts();
            if (a != null) {
                aCount = a.size();
            }
            if (newUser != null)
            {
                fCount = newUser.getFollowers();
                asCount = newUser.getAdshared();
                sCount = newUser.getScore();
            }
            String[] mydatakey =
                    {
                            String.valueOf(fCount),
                            String.valueOf(asCount),
                            String.valueOf(sCount),
                            String.valueOf(qCount),
                            String.valueOf(aCount)
                    };
            String[] mydatavalue =
                    {
                            "Followers",
                            "Ads Shared",
                            "Score",
                            "Questions Remaining",
                            "Ads Remaining"
                    };
            HomeArrayAdapter adapter = new HomeArrayAdapter(this, mydatakey, mydatavalue);
            ListView mylistview = (ListView) findViewById(R.id.homeListView);
            mylistview.setAdapter(adapter);
            TextView handleview = (TextView) findViewById(R.id.textView2);
            if (newUser != null)
            {
                handleview.setText(newUser.getHandle());
                handleview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), TwitterProfileActivity.class);
                        startActivity(i);
                    }
                });
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
            String profilePicString = preferences.getString(getString(R.string.pic_string), "");
            try {
                if (profilePicString.equals(String.valueOf("empty"))) {
                    Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maleavatar);
                    SetImageInView(myBitmap, imageView1);
                } else {
                    byte[] b = Base64.decode(profilePicString.getBytes(), 0);
                    Bitmap myBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    SetImageInView(myBitmap, imageView1);
                }
            } catch (Exception e){}
        }
        final Button b2 = (Button)findViewById(R.id.button3);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try {
                    Thread.sleep(1500);
                } catch (Exception e) {
                }
                if (new AdvertDBHelper(getApplicationContext()).getUnsharedAdverts() == null)
                {
                        if (!stuck)
                        {
                            Toast.makeText(getBaseContext(), "processing...", Toast.LENGTH_SHORT).show();
                            FireBaseOperations fireBaseOperations = new FireBaseOperations(HomeActivity.this);
                            fireBaseOperations.getAdsFromOnlineDatabase(true);
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),"You are stuck, you are not allowed to refresh deck...",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(),"You still have ads remaining",Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }

    View.OnClickListener goToQuestionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try{
                startActivity(new Intent(getApplicationContext(),QuestionActivity.class));}
            catch (Exception e)
            {
                Toast.makeText(getBaseContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                ImageView imageView1 = (ImageView) findViewById(R.id.imageView);
                SetImageInView(bitmap, imageView1);
            }
            catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    public static void SetImageInView(Bitmap b, ImageView imgV)
    {
        RoundImage roundImage = new RoundImage(b);
        imgV.setImageDrawable(roundImage);
    }
    public static void SetImageInView(Bitmap b, ImageButton imgV)
    {
        RoundImage roundImage = new RoundImage(b);
        imgV.setImageDrawable(roundImage);
    }
}

