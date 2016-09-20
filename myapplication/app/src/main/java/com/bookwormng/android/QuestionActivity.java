package com.bookwormng.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import com.bookwormng.android.Data.AdvertDBHelper;
import com.bookwormng.android.Data.Advert;
import com.bookwormng.android.Data.FireBaseConstants;
import com.bookwormng.android.Data.FireBaseUser;
import com.bookwormng.android.Data.Question;
import com.bookwormng.android.Data.QuestionDBHelper;
import com.bookwormng.android.Data.TwitterConstants;
import com.bookwormng.android.Data.User;
import com.bookwormng.android.Data.UserDBHelper;
import com.bookwormng.android.Operations.FireBaseOperations;
import com.bookwormng.android.Operations.SwipeDetector;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;


public class QuestionActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    //Activity member fields.
    User user;
    //SQLite helper classes
    QuestionDBHelper questionDb;
    AdvertDBHelper AdsDb;
    UserDBHelper userDb;
    //Lists
    ArrayList<Question> MyQuestions;
    ArrayList<Advert> MyAds;
    //Others
    int consecutiveQuestionsAnswered = 0;
    int consecutiveQuestionsMissed = 0;
    int index = 0;
    int rate;
    GestureDetector myGestureDetector;
    Random nextQuestionIndexGnerator = new Random();
    Random nextAdPopGenerator = new Random();
    int random;
    enum Mode{Question, Ad}
    Mode appMode;
    boolean adWasShared = false, cardValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        myGestureDetector = new GestureDetector(this);

        consecutiveQuestionsMissed = PreferenceManager.getDefaultSharedPreferences(QuestionActivity.this).getInt("consequtive_questions_missed",0);
        consecutiveQuestionsAnswered = PreferenceManager.getDefaultSharedPreferences(QuestionActivity.this).getInt("consequtive_questions_answer",0);
        questionDb = new QuestionDBHelper(getApplicationContext());
        AdsDb = new AdvertDBHelper(getApplicationContext());
        userDb = new UserDBHelper(getApplicationContext());

        MyQuestions = questionDb.readUnansweredQuestion();
        MyAds = AdsDb.getUnsharedAdverts();
        user =  userDb.CreateUserFromDatabase();
        //UI Elements
        final EditText answerEditText = (EditText) findViewById(R.id.editText5);
        final TextView questionTextView = (TextView) findViewById(R.id.textView6);
        final Button b = (Button) findViewById(R.id.button5);
        final LinearLayout linLayout = (LinearLayout)findViewById(R.id.linearLayout);
        final TextView hintView = (TextView)findViewById(R.id.textView8);
        hintView.setText("swipe right to skip a question");
        linLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return myGestureDetector.onTouchEvent(event);
            }
        });
        answerEditText.setText("");
        if (user == null)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(QuestionActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("database_been_loaded",true);
            editor.commit();
        }

        if (MyAds == null && MyQuestions == null)
        {
            if(user != null)
            {
                NoMoreQuestions("No more Questions to answer or Ad cards to share, Please Refresh your database.");
            }
            else
            {
                NoMoreQuestions("No more Questions to answer, log in with your twitter account to play the game and share Ads for benefit");
            }
        }
        appMode = Mode.Question;
        if(MyQuestions != null)
        {
            if (MyAds != null)
            {
                int followerCount = user.getFollowers();
                if (followerCount >= 0 && followerCount < 1500)
                {
                    if (MyQuestions.size() > 25)
                    {
                        rate = nextAdPopGenerator.nextInt(25);
                    }
                    else
                    {
                        rate = nextAdPopGenerator.nextInt(MyQuestions.size());
                    }
                }
                else if (followerCount > 1499 && followerCount < 4000)
                {
                    if (MyQuestions.size() > 20)
                    {
                        rate = nextAdPopGenerator.nextInt(20);
                    }
                    else
                    {
                        rate = nextAdPopGenerator.nextInt(MyQuestions.size());
                    }
                }
                else if (followerCount > 3999 && followerCount < 8000)
                {
                    if (MyQuestions.size() > 15)
                    {
                        rate = nextAdPopGenerator.nextInt(15);
                    }
                    else
                    {
                        rate = nextAdPopGenerator.nextInt(MyQuestions.size());
                    }
                }
                else if (followerCount >= 8000)
                {
                    if (MyQuestions.size() > 12)
                    {
                        rate = nextAdPopGenerator.nextInt(12);
                    }
                    else
                    {
                        rate = nextAdPopGenerator.nextInt(MyQuestions.size());
                    }
                }
            }
        }
        if (MyQuestions != null && MyQuestions.size() > 0)
        {
            random = 0;
            final TextView brandView = (TextView)findViewById(R.id.textView7);
            brandView.setVisibility(View.INVISIBLE);
            questionTextView.setText("   " + MyQuestions.get(random).getQuestion());
            b.setOnClickListener(questionListener);
        }
        else if (MyAds != null)
        {
            AdTime();
        }
        setSwipeCount();
    }

    View.OnClickListener questionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final EditText answerEditText = (EditText) findViewById(R.id.editText5);
            final TextView questionTextView = (TextView) findViewById(R.id.textView6);
            final Button b = (Button) findViewById(R.id.button5);
            String ansText = " "+answerEditText.getText().toString().toUpperCase().trim()+" ";
            String realAnsText = " "+MyQuestions.get(random).getAnswer().toUpperCase().trim()+" ";
            if (ansText.contains(realAnsText))
            {
                consecutiveQuestionsMissed = 0;
                index++;
                if (user != null)
                {
                    user.setScore(user.getScore() + 1);
                    consecutiveQuestionsAnswered = consecutiveQuestionsAnswered + 1;
                    if (consecutiveQuestionsAnswered == 5)
                    {
                        user.setSwipeLimit(user.getSwipeLimit() + 1);
                        new UserDBHelper(getApplicationContext()).UpdateUserDatabase(user);
                        setSwipeCount();
                        consecutiveQuestionsAnswered = 0;
                    }
                    FinishUp();
                }
                Toast msg = Toast.makeText(getBaseContext(), "You got it Right!", Toast.LENGTH_SHORT);
                msg.setGravity(Gravity.CENTER, 0, 0);
                msg.show();
                questionDb.updateQuestion(MyQuestions.get(random));
                questionDb.DeleteQuestion(MyQuestions.get(random));
                MyQuestions.remove(random);
                if(MyQuestions.size() > 0)
                {
                    random = nextQuestionIndexGnerator.nextInt(MyQuestions.size());
                    answerEditText.setText("");
                    questionTextView.setText("   " + MyQuestions.get(random).getQuestion());
                    if (index == rate)
                    {
                        if (MyAds != null)
                        {
                            if (MyAds.size() != 0)
                            {
                                b.setOnClickListener(adListener);
                                AdTime();
                                index = 0;
                            }
                        }
                    }
                }
                else
                {
                    if (MyAds != null && MyAds.size() > 0)
                    {
                        b.setOnClickListener(adListener);
                        AdTime();
                    }
                    else
                    {
                        if (user != null)
                        {
                            NoMoreQuestions("No more Questions to answer or Ad cards to share, Please Refresh your database.");
                        }
                        else
                        {
                            NoMoreQuestions("No more Questions to answer, log in with your twitter account to play the game and share Ads for benefit");
                        }
                    }
                }
            }
            else
            {
                consecutiveQuestionsMissed++;
                Toast msg = Toast.makeText(getBaseContext(), "Wrong Answer, Try again or skip the question by swiping.", Toast.LENGTH_LONG);
                msg.setGravity(Gravity.CENTER, 0, 0);
                msg.show();
                FinishUp();
            }
        }
    };
    View.OnClickListener adListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           new CheckAd().execute();
        }
    };

    void PostTweetAction()
    {
        if (adWasShared)
        {
            if (user != null)
            {
                Advert a = MyAds.get(0);
                FireBaseOperations fireBaseOperations = new FireBaseOperations(QuestionActivity.this);
                fireBaseOperations.NotifyDataBaseAd(a);
                if(user.getLastAdvertId() < a.getId())
                {
                    user.setLastAdvertId(a.getId());
                }
                user.setAdshared(user.getAdshared() + 1);
                new UserDBHelper(getApplicationContext()).UpdateUserDatabase(user);
                fireBaseOperations.updateUserOnDatabase(new FireBaseUser(user));
                AdsDb.updateAdvert(MyAds.get(0));
                AdsDb.DeleteAdvert(MyAds.get(0));
                MyAds.remove(0);
                adWasShared = false;
            }
        }
        if (MyQuestions != null && MyQuestions.size() > 0)
        {
            QuestionTime();
        }
        else
        {
            if (MyAds.size() > 0)
            {
                AdTime();
            }
            else
            {
                if (user != null)
                {
                    NoMoreQuestions("No more Questions to answer or Ad cards to share, Please Refresh your database.");
                }
                else
                {
                    NoMoreQuestions("No more Questions to answer, log in with your twitter account to play the game and share Ads for benefit");
                }
            }
        }
    }
    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return true;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
         SwipeDetector detector = new SwipeDetector();
        try{
            if (detector.userSwipedLeft(e1, e2, velocityX))
            {
                SwipeTime();
            }
           else if (detector.userSwipedRight(e1,e2,velocityX)) {
               SwipeTime();
            }
        }
        catch(Exception e){}
        return true;
    }
    @Override
    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent me) {
       return false;
    }
        public void AdTime()
    {
        setSwipeCount();
        if (MyQuestions != null)
        {
            if (MyQuestions.size() > 20)
            {
                rate = nextAdPopGenerator.nextInt(20);
            }
            else if (MyQuestions.size() == 0)
            {
                rate = 0;
            }
            else
            {
                rate = nextAdPopGenerator.nextInt(MyQuestions.size());
            }
        }
        else
        {
            rate = 0;
        }
        appMode = Mode.Ad;
        final EditText answerEditText = (EditText) findViewById(R.id.editText5);
        final TextView questionTextView = (TextView) findViewById(R.id.textView6);
        final ImageView questionView = (ImageView)findViewById(R.id.imageView2);
        final Button b = (Button) findViewById(R.id.button5);
        final TextView brandView = (TextView)findViewById(R.id.textView7);
        final TextView hintView = (TextView)findViewById(R.id.textView8);
        hintView.setText("swipe right to share the ad later");
        brandView.setVisibility(View.VISIBLE);
        b.setOnClickListener(adListener);
        questionView.setVisibility(View.INVISIBLE);
        answerEditText.setVisibility(View.INVISIBLE);
        b.setText("SHARE AD!");
        setTitle("Share Ad Card");
        questionTextView.setText(" " + MyAds.get(0).getContent());
        brandView.setText(MyAds.get(0).getBrand());
        InputMethodManager inputManager = (InputMethodManager)this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void QuestionTime()
    {
        setSwipeCount();
        appMode = Mode.Question;
        final EditText answerEditText = (EditText) findViewById(R.id.editText5);
        final TextView questionTextView = (TextView) findViewById(R.id.textView6);
        final ImageView questionView = (ImageView)findViewById(R.id.imageView2);
        final Button b = (Button) findViewById(R.id.button5);
        final TextView brandView = (TextView)findViewById(R.id.textView7);
        final TextView hintView = (TextView)findViewById(R.id.textView8);
        hintView.setText("swipe right to skip a question");
        brandView.setVisibility(View.INVISIBLE);
        b.setOnClickListener(questionListener);
        questionView.setVisibility(View.VISIBLE);
        answerEditText.setVisibility(View.VISIBLE);
        b.setText("Answer");
        setTitle(R.string.title_activity_question);
        random = nextQuestionIndexGnerator.nextInt(MyQuestions.size());
        questionTextView.setText("   " + MyQuestions.get(random).getQuestion());
    }

    public void SwipeTime()
    {
        setSwipeCount();
        if (user != null)
        {
            if (user.getSwipeLimit() > 0) {
                if (appMode == Mode.Question) {
                    user.setSwipeLimit(user.getSwipeLimit() - 1);
                    new UserDBHelper(getApplicationContext()).UpdateUserDatabase(user);
                    if (rate == index + 1) {
                        AdTime();
                    } else {
                        QuestionTime();
                    }
                } else if (appMode == Mode.Ad) {
                    user.setSwipeLimit(user.getSwipeLimit() - 1);
                    if (MyAds.size() > 1) {
                        Advert a = MyAds.get(0);
                        MyAds.remove(0);
                        MyAds.add(a);
                        if (MyQuestions.size() > 0) {
                            QuestionTime();
                        } else {
                            Toast t = Toast.makeText(getBaseContext(),
                                    "no more questions to answer, share ads or refresh your database", Toast.LENGTH_SHORT);
                            t.show();
                            AdTime();
                        }
                    }
                }
            } else
            {
                Toast msg = Toast.makeText(getBaseContext(), "You have exhausted your swipes.", Toast.LENGTH_LONG);
                msg.setGravity(Gravity.CENTER, 0, 0);
                msg.show();
                final TextView hintView = (TextView) findViewById(R.id.textView8);
                hintView.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            Toast msg = Toast.makeText(getBaseContext(), "You can only swipe in full game mode.", Toast.LENGTH_LONG);
            msg.setGravity(Gravity.CENTER, 0, 0);
            msg.show();
        }
    }
    public void NoMoreQuestions(String text)
    {
        setSwipeCount();
        final EditText answerEditText = (EditText) findViewById(R.id.editText5);
        final TextView questionTextView = (TextView) findViewById(R.id.textView6);
        final ImageView questionView = (ImageView)findViewById(R.id.imageView2);
        final Button b = (Button) findViewById(R.id.button5);
        final TextView brandView = (TextView)findViewById(R.id.textView7);
        brandView.setVisibility(View.INVISIBLE);
        answerEditText.setVisibility(View.INVISIBLE);
        questionView.setVisibility(View.INVISIBLE);
        b.setVisibility(View.INVISIBLE);
        new GoToHomeAsync().execute();
        setTitle(R.string.title_activity_question);
        questionTextView.setText("  "+text);
        InputMethodManager inputManager = (InputMethodManager)this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    void setSwipeCount()
    {
        if (user != null)
        {
            TextView t = (TextView)findViewById(R.id.textView22);
            t.setText(String.valueOf(new UserDBHelper(getApplicationContext()).CreateUserFromDatabase().getSwipeLimit()));
        }
    }
    private class GoToHomeAsync extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try { Thread.sleep(3000);}
            catch (Exception e){}
            Intent i;
            if (user != null)
            {
                i = new Intent(getApplication(), HomeActivity.class);
            }
            else
            {
                i = new Intent(getApplication(),FirstActivity.class);
            }
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return null;
        }
    }
public void FinishUp()
{
    if (user != null)
    {
        if (consecutiveQuestionsMissed > 0)
        {
            if (user.getSwipeLimit() == 0)
            {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(QuestionActivity.this).edit();
                editor.putBoolean("stuck",true);
                editor.commit();
            }
        }
        else
        {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(QuestionActivity.this).edit();
            editor.putBoolean("stuck",false);
            editor.commit();
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(QuestionActivity.this).edit();
        editor.putInt("consequtive_questions_missed",consecutiveQuestionsMissed);
        editor.commit();
        editor.putInt("consequtive_questions_answer",consecutiveQuestionsAnswered);
        editor.commit();
        UserDBHelper userdb = new UserDBHelper(getApplicationContext());
        userdb.UpdateUserDatabase(user);
    }
}
    @Override
    protected void onPause()
    {
        FinishUp();
        super.onPause();
    }
    @Override
    protected void onStop()
    {
        FinishUp();
        super.onStop();
    }
    @Override
    protected void onDestroy()
    {
        FinishUp();
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
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
        else if (id == R.id.action_share)
        {
            if (appMode == Mode.Question)
            {
            final TextView questionTextView = (TextView) findViewById(R.id.textView6);
            if (questionTextView.getText().toString() != null) {
                String s = questionTextView.getText().toString() + "  (via BookWormNG)";
                new PostQuestion().execute(s);
            }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class CheckAd extends AsyncTask<Void,Void,Boolean>
    {
        ProgressDialog progressDialog2;
        @Override
        protected void onPreExecute()
        {
            progressDialog2 = ProgressDialog.show(QuestionActivity.this, "", "connecting...", true, false);
            progressDialog2.show();
        }
        @Override
        protected Boolean doInBackground(Void...params)
        {
            if (user != null)
            {
                Firebase.setAndroidContext(QuestionActivity.this);
                Firebase firebase = new Firebase(FireBaseConstants.BookWormURL);
                Firebase adBase = firebase.child(FireBaseConstants.AdChild);
                Firebase advertRef = adBase.child(String.valueOf(MyAds.get(0).getId()));
                advertRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null)
                        {
                            progressDialog2.dismiss();
                            new AdExpired().execute();
                        }
                        else
                        {
                            Log.e("datasnapshot",String.valueOf(dataSnapshot.getValue()));
                            int token = Integer.parseInt(String.valueOf(dataSnapshot.child("token").getValue()));
                            int used = Integer.parseInt(String.valueOf(dataSnapshot.child("used").getValue()));
                            if (token > used)
                            {
                                progressDialog2.dismiss();
                                new PostTweet().execute(MyAds.get(0).getBrand() + " - " + MyAds.get(0).getContent() + " (via BookWormNG)");
                            }
                            else
                            {
                                progressDialog2.dismiss();
                              new AdExpired().execute();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
          return cardValid;
        }
        @Override
        protected  void onPostExecute( Boolean b) {

        }
    }
    private class AdExpired extends AsyncTask<Void,Void,Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Toast.makeText(getBaseContext(), "Ad card has expired", Toast.LENGTH_SHORT).show();
            new AdvertDBHelper(getApplicationContext()).DeleteAdvert(MyAds.get(0));
            MyAds.remove(MyAds.get(0));
            if (MyQuestions != null && MyQuestions.size() > 0) {
                QuestionTime();
            } else if (MyAds.size() > 0) {
                AdTime();
            } else {
                NoMoreQuestions("No more Questions to answer or Ad cards to share, Please Refresh your database.");
            }
        }
        @Override
        protected Boolean doInBackground(Void...params)
        {
            return cardValid;
        }
        @Override
        protected  void onPostExecute( Boolean b) {
        }
    }
    private class PostTweet extends AsyncTask<String, Void, String>
    {
        ProgressDialog progressDialog2;
        UserDBHelper userDBHelper = new UserDBHelper(getApplicationContext());
        User user = userDBHelper.CreateUserFromDatabase();
        @Override
        protected void onPreExecute()
        {
            progressDialog2 = ProgressDialog.show(QuestionActivity.this, "", "sharing ad...", true, false);
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
                Log.e("Exception", e.toString());
            }
            return null;
        }
        @Override
        protected  void onPostExecute(String myString)
        {
            progressDialog2.cancel();
            if (myString != null)
            {
                Toast t = Toast.makeText(getBaseContext(),"Ad was successfully shared to twitter",Toast.LENGTH_SHORT);
                adWasShared = true;
                t.show();
            }
            else
            {
                adWasShared = false;
                Toast t = Toast.makeText(getBaseContext(),"error, try again later",Toast.LENGTH_SHORT);
                t.show();
            }
            PostTweetAction();
        }
    }

    private class PostQuestion extends AsyncTask<String, Void, String>
    {
        ProgressDialog progressDialog2;
        UserDBHelper userDBHelper = new UserDBHelper(getApplicationContext());
        User user = userDBHelper.CreateUserFromDatabase();
        @Override
        protected void onPreExecute()
        {
            progressDialog2 = ProgressDialog.show(QuestionActivity.this, "", "posting question to twitter...", true, false);
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
                Log.e("Exception", e.toString());
            }
            return null;
        }
        @Override
        protected  void onPostExecute(String myString)
        {
            progressDialog2.cancel();
            if (myString != null)
            {
                Toast t = Toast.makeText(getBaseContext(),"successful",Toast.LENGTH_SHORT);
                t.show();
            }
            else
            {
                Toast t = Toast.makeText(getBaseContext(),"error, try again later",Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }
}
