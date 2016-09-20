package com.bookwormng.android.Operations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.bookwormng.android.Data.AdvertDBHelper;
import com.bookwormng.android.Data.Advert;
import com.bookwormng.android.Data.FireBaseConstants;
import com.bookwormng.android.Data.FireBaseUser;
import com.bookwormng.android.Data.Question;
import com.bookwormng.android.Data.QuestionDBHelper;
import com.bookwormng.android.Data.User;
import com.bookwormng.android.Data.UserDBHelper;
import com.bookwormng.android.HomeActivity;
import com.bookwormng.android.SelectPictureActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tofunmi Seun on 21/11/2015.
 */
public class FireBaseOperations
{
    ProgressDialog progressDialog;
    Context myContext;
    Firebase userRef;
    boolean saved = false;
    int count, adCount;

    public FireBaseOperations(Context c)
    {
        this.myContext = c;
        Firebase.setAndroidContext(this.myContext);
        userRef = new Firebase(FireBaseConstants.BookWormURL);
    }
    public boolean saveUserToFBaseDb(final FireBaseUser user)
    {
        saved = false;
        try
        {
            final Firebase childUserRef = userRef.child(FireBaseConstants.UserChild).child(String.valueOf(user.getId()));
            childUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User newUser = new UserDBHelper(myContext.getApplicationContext()).CreateUserFromDatabase();
                    if (dataSnapshot.getValue() == null)
                    {
                        childUserRef.setValue(user);

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myContext);
                        boolean questionLoaded = prefs.getBoolean("database_been_loaded", false);
                        if (!questionLoaded)
                        {
                            List<Question> e = new QuestionDBHelper(myContext.getApplicationContext()).readUnansweredQuestion();
                            if (e != null)
                            {
                                for(Question i: e)
                                {
                                    new QuestionDBHelper(myContext.getApplicationContext()).DeleteQuestion(i);
                                }
                            }
                            new RefreshDataBaseInBackground().execute();
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(myContext).edit();
                            editor.putInt("consequtive_questions_answer",0);
                        } else
                        {
                            Intent pictureIntent = new Intent(myContext, SelectPictureActivity.class);
                            pictureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            myContext.startActivity(pictureIntent);
                        }
                    } else {
                        updateUserLocally(String.valueOf(newUser.getUserId()));
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        catch (Exception e)
        {
            Log.e("Exception", e.toString());
        }
        return saved;
    }
    public void updateUserOnDatabase(final FireBaseUser FUser)
    {
        final Firebase childRef = userRef.child(FireBaseConstants.UserChild).child(String.valueOf(FUser.getId()));
        childRef.setValue(FUser);
    }
    public void updateUserLocally(String userID)
    {
        new UpdateUserLocally2().execute(userID);
    }
    public void getAdsFromOnlineDatabase(final boolean getQuestionsToo)
    {
       new getAdsFromDatabse2().execute(getQuestionsToo);
    }
    public void getQuestionsFromDatabase(final int roundTo) {
      new getQuestionsFromDatabase2().execute(roundTo);
    }
    public void ReturnAdsToOnlineDatabase()
    {
        List<Advert> adverts = new AdvertDBHelper(this.myContext.getApplicationContext()).getUnsharedAdverts();
        if (adverts != null)
        {
            for (final Advert advert: adverts)
            {
                new AdvertDBHelper(this.myContext.getApplicationContext()).DeleteAdvert(advert);
            }
        }
        updateUserOnDatabase(new FireBaseUser(new UserDBHelper(this.myContext.getApplicationContext()).CreateUserFromDatabase()));
    }

    public void NotifyDataBaseAd(Advert a)
    {
        Firebase adBase = this.userRef.child(FireBaseConstants.AdChild);
        Firebase advertRef = adBase.child(String.valueOf(a.getId()));
        advertRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null)
                {
                    Firebase updateFireBase = dataSnapshot.getRef();
                    String brand = dataSnapshot.child("brand").getValue().toString();
                    String content = dataSnapshot.child("content").getValue().toString();
                    int token = Integer.parseInt(dataSnapshot.child("token").getValue().toString());
                    int used = Integer.parseInt(dataSnapshot.child("used").getValue().toString());
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("brand", brand);
                    map1.put("content", content);
                    map1.put("token", token);
                    map1.put("used", used + 1);
                    try {
                        updateFireBase.updateChildren(map1);
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    void Notify(String update)
    {
        Toast t = Toast.makeText(this.myContext,update,Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER,0,50);
        t.show();
    }

    private class UpdateUserLocally2 extends AsyncTask<String,Void,Void>
    {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute()
        {
            try{
            progressDialog = ProgressDialog.show(myContext, "", "downloading data...", true, false);
            progressDialog.show();}
            catch (Exception e){}
        }
        @Override
        protected Void doInBackground(String...params)
        {
            try {
                final Firebase childUserRef = userRef.child(FireBaseConstants.UserChild).child(params[0]);
                childUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.getValue() != null)
                        {
                            UserDBHelper userDB = new UserDBHelper(myContext.getApplicationContext());
                            User user = userDB.CreateUserFromDatabase();
                            FireBaseUser fuser = dataSnapshot.getValue(FireBaseUser.class);
                            if (fuser != null)
                            {
                                user.setAdshared(fuser.getAdShared());
                                user.setScore(fuser.getScore());
                                user.setLastQuestionId(fuser.getLastQuestionId());
                                user.setLastAdvertId(fuser.getLastAdvertId());
                                user.setSwipeLimit(fuser.getSwipeLimit());
                                userDB.UpdateUserDatabase(user);
                            }
                            Long lastId = PreferenceManager.getDefaultSharedPreferences(myContext).getLong("lastquestionindex", 0);
                            User newUser = new UserDBHelper(myContext.getApplicationContext()).CreateUserFromDatabase();
                            if (lastId > newUser.getLastQuestionId())
                            {
                                newUser.setLastQuestionId(lastId);
                                new UserDBHelper(myContext.getApplicationContext()).UpdateUserDatabase(newUser);
                            }
                            Intent pictureIntent = new Intent(myContext,SelectPictureActivity.class);
                            pictureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            myContext.startActivity(pictureIntent);
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
            catch (Exception e)
            {
                Log.e("Excepton", e.toString());
            }
            return null;
        }
        @Override
        protected  void onPostExecute( Void params) {
            progressDialog.dismiss();
        }
    }
    private class RefreshDataBaseInBackground extends AsyncTask<Void,Void,Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute()
        {
            try{
            progressDialog = ProgressDialog.show(myContext, "", "loading deck...", true, false);
            progressDialog.show();}
            catch (Exception e){}
        }
        @Override
        protected Void doInBackground(Void... params) {
            List<Question> e = new QuestionDBHelper(myContext.getApplicationContext()).readUnansweredQuestion();
            if (e != null) {
                for (Question i : e) {
                    new QuestionDBHelper(myContext.getApplicationContext()).DeleteQuestion(i);
                }
            }
            DatabaseLayer.InitialDatabasePopulation(myContext.getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            progressDialog.dismiss();
            Intent pictureIntent = new Intent(myContext, SelectPictureActivity.class);
            pictureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            myContext.startActivity(pictureIntent);
        }
    }

    private class getAdsFromDatabse2 extends AsyncTask<Boolean,Void,Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            try {
                progressDialog = ProgressDialog.show(myContext, "getting ads...", "might take a while...", true, false);
                progressDialog.show();
            }
            catch (Exception e){}
        }

        @Override
        protected Void doInBackground(final Boolean... params)
        {
            count = 0;
            ArrayList<Advert> Ads = new AdvertDBHelper(myContext.getApplicationContext()).getUnsharedAdverts();
            final User user = new UserDBHelper(myContext.getApplicationContext()).CreateUserFromDatabase();
            final FireBaseUser FUser = new FireBaseUser(user);
            if (Ads != null) {
                adCount = Ads.size();
            }
            count = 3 - adCount;
            try {
                final AdvertDBHelper adsDb = new AdvertDBHelper(myContext.getApplicationContext());
                final Firebase adsRef = userRef.child(FireBaseConstants.AdChild);
                adsRef.goOnline();
                final Query adsQuery = adsRef.orderByKey().startAt(String.valueOf(FUser.getLastAdvertId() + 1));
                        adsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    for (DataSnapshot adsShot : dataSnapshot.getChildren()) {
                                        if (adsShot != null) {
                                            String brand = "", content = "";
                                            int token = 0, used = 0;
                                            int id = Integer.parseInt(adsShot.getKey());
                                            if (adsShot.child("brand").getValue().toString() != null) {
                                                if (adsShot.child("content").getValue().toString() != null) {
                                                    if (adsShot.child("token").getValue().toString() != null) {
                                                        if (adsShot.child("used").getValue().toString() != null) {
                                                            brand = adsShot.child("brand").getValue().toString();
                                                            content = adsShot.child("content").getValue().toString();
                                                            token = Integer.parseInt(adsShot.child("token").getValue().toString());
                                                            used = Integer.parseInt(adsShot.child("used").getValue().toString());
                                                        }
                                                    }
                                                }
                                            }
                                            if (token > used) {
                                                Advert a = new Advert(brand, content, id);
                                                adsDb.AddAdvert(a);
                                                new UserDBHelper(myContext.getApplicationContext()).UpdateUserDatabase(user);
                                            }
                                        }
                                        if (new AdvertDBHelper(myContext.getApplicationContext()).getUnsharedAdverts() != null &&
                                                new AdvertDBHelper(myContext).getUnsharedAdverts().size() == 3) {
                                            break;
                                        }
                                    }
                                    try {
                                        if (progressDialog != null) {
                                            progressDialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                    }
                                    if (new AdvertDBHelper(myContext.getApplicationContext()).getUnsharedAdverts() != null) {
                                        if ((new AdvertDBHelper(myContext.getApplicationContext()).getUnsharedAdverts().size() - adCount) > 0) {
                                            Notify("you now have new ads to share");
                                        }
                                        if (params[0]) {
                                            try {
                                                Thread.sleep(1500);
                                            } catch (Exception e) {
                                            }
                                            getQuestionsFromDatabase(75);
                                        } else {
                                            Intent i = new Intent(myContext, HomeActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            myContext.startActivity(i);
                                        }
                                    }
                                } else {
                                   // adsRef.goOffline();
                                    try {
                                        if (progressDialog != null) {
                                            progressDialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                    }
                                    Notify("No ads to share, try again later...");
                                    if (params[0]) {
                                        try {
                                            Thread.sleep(1500);
                                        } catch (Exception e) {
                                        }
                                        getQuestionsFromDatabase(75);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
            } catch (Exception e) {
                Toast.makeText(myContext, "Error connecting to database", Toast.LENGTH_SHORT);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void b) {

        }
    }

    private class getQuestionsFromDatabase2 extends AsyncTask<Integer,Void,Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            try {
                progressDialog = ProgressDialog.show(myContext, "getting questions...", "might take a while...", true, false);
                progressDialog.show();
            }
            catch (Exception e){}
        }

        @Override
        protected Void doInBackground(final Integer... params)
        {
            final User user = new UserDBHelper(myContext.getApplicationContext()).CreateUserFromDatabase();
            List<Question> questions = new QuestionDBHelper(myContext.getApplicationContext()).readUnansweredQuestion();
            List<Advert> adverts = new AdvertDBHelper(myContext.getApplicationContext()).getUnsharedAdverts();
            int q1;
            if (questions == null) {
                if (adverts == null) {
                    q1 = 0;
                } else {
                    q1 = adverts.size();
                }
            } else {
                if (adverts == null) {
                    q1 = questions.size();
                } else {
                    q1 = questions.size() + adverts.size();
                }
            }
            int remainder = 75 - q1;
            if (remainder > 0) {
                final Firebase questionRef = userRef.child(FireBaseConstants.QuestionChild);
                questionRef.goOnline();
                final Query questionQuery = questionRef.orderByKey().startAt(String.valueOf(user.getLastQuestionId() + 1)).limitToFirst(remainder);
                questionQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot questionShots : dataSnapshot.getChildren()) {
                                String question = questionShots.child("question").getValue().toString();
                                String answer = questionShots.child("answer").getValue().toString();
                                long Id = Long.parseLong(questionShots.getKey());
                                Question q = new Question(question, answer, Id);
                                new QuestionDBHelper(FireBaseOperations.this.myContext.getApplicationContext()).AddQuestion(q);
                                if (user.getLastQuestionId() < Id) {
                                    user.setLastQuestionId(Id);
                                    new UserDBHelper(FireBaseOperations.this.myContext.getApplicationContext()).UpdateUserDatabase(user);
                                }
                            }
                            try{
                                if (progressDialog != null)
                                {
                                    progressDialog.dismiss();

                                }}
                            catch (Exception e){};
                            Intent i = new Intent(myContext, HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            myContext.startActivity(i);
                        }
                        else
                        {
                            questionRef.goOffline();
                            try{
                                if (progressDialog != null)
                                {
                                    progressDialog.dismiss();
                                }}
                            catch (Exception e){};
                            Notify("No questions available at the moment, try again later...");
                        }
                    }
                        @Override
                        public void onCancelled (FirebaseError firebaseError){
                        }
                });
            }
            else
            {
                try{
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }}
                catch (Exception e){};

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void b) {

        }
    }
}
