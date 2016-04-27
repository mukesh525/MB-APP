package vmc.mcube.in.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import vmc.mcube.in.R;
import vmc.mcube.in.fragment.followUp.FollowUp;
import vmc.mcube.in.fragment.ivrs.Ivrs;
import vmc.mcube.in.fragment.lead.Lead;
import vmc.mcube.in.fragment.mtracker.Mtracker;
import vmc.mcube.in.fragment.settings.Settings;
import vmc.mcube.in.fragment.track.Track;
import vmc.mcube.in.gcm.GCMClientManager;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.RefreshCallBack;
import vmc.mcube.in.utils.SpinnerCallBack;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.UserData;
import vmc.mcube.in.utils.Utils;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        Tag, TabLayout.OnTabSelectedListener, RefreshCallBack, SpinnerCallBack {
    private static final String FIRST_TIME = "first_item";
    private static final String SELECTED_iTEM_ID = "SelectedItemId";
    public static int T = 0;
    public static String Tag = "";
    public static String BASE_URL;
    public static int CurrentFrag = 1;
    public static Boolean BACK;
    public static UserData userData;
    public static String recordLimit;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int mSelectedId;
    private boolean mUserSawDrawer = false;
    private String titles[] = {"Track", "Ivrs", "MCubeX", "Lead", "FollowUp", "Mtracker", "Settings"};
    private Toolbar mToolbar;
    private Spinner sp;
    private TabLayout mTabLayout;
    private TextView clientName, clientEmail;
    private ViewPager mViewPager;
    private ArrayList<String> filterGroup;
    private MyPagerAdapter myPagerAdapter;
    private boolean doubleBackToExitPressedOnce;
    private Vibrator v;
    private String PROJECT_NUMBER = "305656196217";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_home);
        BACK = false;
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // getSupportActionBar().setIcon(R.drawable.mcubee);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitleTextColor(Color.WHITE);
        userData = Utils.GetUserData(HomeActivity.this);
        BASE_URL = userData.getSERVER();
        filterGroup = new ArrayList<String>();
        recordLimit = Utils.getFromPrefs(HomeActivity.this, "recordLimit", "10");
        mDrawer = (NavigationView) findViewById(R.id.main_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mToolbar.setTitle(titles[Constants.position]);
        mDrawer.setItemIconTintList(null);
        View header = mDrawer.getHeaderView(0);
        clientName = (TextView) header.findViewById(R.id.tvName);
        clientEmail = (TextView) header.findViewById(R.id.tvEmail);
        clientEmail.setText(userData.EMP_EMAIL);

        clientName.setText("Hi " + userData.EMP_NAME);
        //mViewPager.setOffscreenPageLimit(0);

        if (!didUserSeeDrawer()) {
            showDrawer();
            markDrawerSeen();
        } else {
            hideDrawer();
        }


        // cancelNotification(this, 0);
        GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                Log.d("Registration id", registrationId);
                onRegisterGcm(registrationId, userData.getAUTHKEY());
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });
        onNavigationItemSelected(mDrawer.getMenu().getItem(Constants.position));
//
//        Menu menu = mDrawer.getMenu();
//        for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
//            MenuItem menuItem = menu.getItem(menuItemIndex);
//            if (menuItem.getItemId() == R.id.mtracker) {
//                menuItem.setVisible(false);
//            }
//
//
//        }
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public void onRegisterGcm(final String regid, final String authkey) {

        if (Utils.onlineStatus(HomeActivity.this)) {
            new RegisterGcm(regid, authkey).execute();
        } else {
            Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    class RegisterGcm extends AsyncTask<Void, Void, String> {
        JSONObject response = null;
        String regid, code, authkey;

        public RegisterGcm(String regid, String authkey) {
            this.regid = regid;
            this.authkey = authkey;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                response = Parser.REG_GCM(SET_OTP, regid, authkey);
                Log.d("REFER", response.toString());

                if (response.has(CODE)) {
                    code = response.getString(CODE);

                }


            } catch (Exception e) {
                Log.d("REFER", e.getMessage());
            }
            return code;
        }

        @Override
        protected void onPostExecute(String data) {

            if (data != null) {
                //Toast.makeText(HomeActivity.this, data, Toast.LENGTH_SHORT).show();
            }


        }


    }

    private void markDrawerSeen() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, mUserSawDrawer).apply();
    }

    private void navigate(int mSelectedId) {

        if (mSelectedId == R.id.followup) {
            setSelection(4);
        }
        if (mSelectedId == R.id.ivrs) {
            setSelection(1);
        }
        if (mSelectedId == R.id.lead) {
            setSelection(3);
        }
        if (mSelectedId == R.id.settings) {
            setSelection(6);
        }
        if (mSelectedId == R.id.mtracker) {
            setSelection(5);
        }
        if (mSelectedId == R.id.track) {
            setSelection(0);
        }
        if (mSelectedId == R.id.x) {
            setSelection(2);
        }
        if (mSelectedId == R.id.logout) {
            Utils.isLogout1(HomeActivity.this);
        }
        invalidateOptionsMenu();
    }

    private void setSelection(int item) {
        hideDrawer();
        displayView(item);
        // mTabLayout.setScrollPosition(item, 0f, true);
        // mViewPager.setCurrentItem(item);
        //  onNavigationItemSelected(mDrawer.getMenu().getItem(0));


    }

    public void playAudio(String url) {
        if (url != null && url.length() > 10) {
            Log.d("AUDIO", url);
            // Toast.makeText(HomeActivity.this, url, Toast.LENGTH_LONG).show();
            //Uri myUri = Uri.parse("http://mcube.vmctechnologies.com/sounds/99000220411460096169.wav");
            Uri myUri = Uri.parse(url);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(myUri, "audio/*");
            startActivity(intent);
        }

    }

    private void displayView(int position) {
        Constants.position = position;
        Fragment myFragment = null;
        setTitle(position);
        switch (position) {
            case 0:
                myFragment = Track.newInstance("", "");

                break;
            case 1:
                myFragment = Ivrs.newInstance("", "");
                break;
            case 2:
                myFragment = vmc.mcube.in.fragment.x.X.newInstance("", "");

                break;
            case 3:
                myFragment = Lead.newInstance("", "");

                break;
            case 4:
                myFragment = FollowUp.newInstance("", "");

                break;
            case 5:
                myFragment = Mtracker.newInstance("", "");
                break;
            case 6:
                myFragment = Settings.newInstance("", "");
                break;
        }
        if (myFragment != null) {
            myFragment.setUserVisibleHint(false);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout_id, myFragment).commit();
        }

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if (BACK) {
                Intent intent = getIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                this.startActivity(intent);
                finish();
                this.overridePendingTransition(0, 0);
            } else if (Constants.back) {
                super.onBackPressed();
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return false;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        menuItem.setChecked(true);
        mSelectedId = menuItem.getItemId();
        navigate(mSelectedId);
//        Constants.mSelectedId = menuItem.getItemId();
//        navigate(Constants.mSelectedId);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            //Log.d("onSaveInstanceState");
            if (outState != null) {
                outState.remove("android:support:fragments");
                outState.putInt(SELECTED_iTEM_ID, mSelectedId);
            }

        } catch (Exception ex) {
            Log.d("log", ex.toString());
        }


    }


    private boolean didUserSeeDrawer() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = sharedPreferences.getBoolean(FIRST_TIME, false);
        return mUserSawDrawer;
    }

    private void showDrawer() {
        this.mDrawerLayout.openDrawer(GravityCompat.START);

    }

    private void hideDrawer() {
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onTabSelected(final TabLayout.Tab tab) {

        Constants.position = tab.getPosition();
        // mViewPager.setCurrentItem(tab.getPosition());
        // mViewPager.setAdapter(myPagerAdapter);
        onNavigationItemSelected(mDrawer.getMenu().getItem(tab.getPosition()));


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        clearBackStack();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {


    }

    @Override
    public void onRefresh() {
        supportInvalidateOptionsMenu();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void setTitle(int position) {
        mToolbar.setTitle(titles[position]);

    }


    @Override
    public void ResetSpinner() {
    }

    private void clearBackStack() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment = null;
            switch (position) {
                case 0:
                    myFragment = Track.newInstance("", "");

                    break;
                case 1:
                    myFragment = Ivrs.newInstance("", "");
                    break;
                case 2:
                    myFragment = vmc.mcube.in.fragment.x.X.newInstance("", "");

                    break;
                case 3:
                    myFragment = Lead.newInstance("", "");

                    break;
                case 4:
                    myFragment = FollowUp.newInstance("", "");

                    break;
                case 5:
                    myFragment = Mtracker.newInstance("", "");
                    break;
                case 6:
                    myFragment = Settings.newInstance("", "");
                    break;
            }
            return myFragment;

        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {

        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }


}
