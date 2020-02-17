package com.video.fb.facebookvideodownloaderpaid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.preference.DialogPreference;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.kobakei.ratethisapp.RateThisApp;
import com.video.fb.facebookvideodownloaderpaid.R;
import com.video.fb.facebookvideodownloaderpaid.adapter.FileViewerFragment;
import com.video.fb.facebookvideodownloaderpaid.adapter.HomeFragment;
import com.video.fb.facebookvideodownloaderpaid.adapter.UrlDownloadFragment;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.home_white_24x24,
            R.drawable.video_collection_white_24x24,
            R.drawable.content_paste_white_24x24
    };
    private final String TAGS="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        // Monitor launch times and interval from installation
        // Custom condition: 3 days and 5 launches
        RateThisApp.Config config = new RateThisApp.Config(3, 5);
        RateThisApp.init(config);
        RateThisApp.onCreate(this);
        // If the condition is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(MainActivity.this, R.style.MyAlertDialogStyle2);

        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {
                RateThisApp.stopRateDialog(getApplicationContext());
            }

            @Override
            public void onNoClicked() {
                RateThisApp.stopRateDialog(getApplicationContext());
            }

            @Override
            public void onCancelClicked() {
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_album, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (item.getItemId()) {
            case R.id.menu_rate:
                intent.setData(Uri.parse("market://details?id=com.video.fb.facebookvideodownloaderpaid"));
                startActivity(intent);
                return true;
            case R.id.MoreApps:
                intent.setData(Uri.parse("market://search?q=pub:Precode apps"));
                startActivity(intent);
                return true;
            case R.id.Share_This_App:shareApp();
                return true;
            case R.id.About:showAbout();
                return true;
            case R.id.clear_app_data:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Clear App Data?");
                alertDialog.setMessage("Do you Really want to clear Application Data ?This will clear App Data but will not delete Databases!");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        clearApplicationData(getApplicationContext());
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showAbout() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        TextView textView = (TextView) messageView.findViewById(R.id.title);
        TextView textView1 = (TextView) messageView.findViewById(R.id.about_credits);
        TextView textView2 = (TextView) messageView.findViewById(R.id.textView2);
        textView.setTextColor(Color.DKGRAY);
        textView1.setTextColor(Color.DKGRAY);
        textView2.setTextColor(Color.DKGRAY);
        String tex="Facebook Video Downloader enables you to download videos from facebook while browsing directly to your device while taking your minimal storage.\nPlay videos at a later time , share the videos through whatsapp , gmail with your friends .\n\nIf you like it, share with your friends .\n\nThis App is Not Associated to Facebook Organization in any form .Any Unauthorized downloading or re-uploading of contents and/or violations of intellectual property rights is the sole responsibility of the user .\n \nHappy Browsing :) ";
        textView2.setText(tex);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }
    public  void clearApplicationData(Context context) {
        File cache =  context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                File f = new File(appDir, s);
                if(deleteDir(f))
                    Log.i(TAGS, String.format("**************** DELETED -> (%s) *******************", f.getAbsolutePath()));
            }
        }
    }
    private  boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if(!(dir.toString().contains("shared_prefs") || dir.toString().contains("databases"))) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }
    public void shareApp()
    {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Facebook Video Downloader");
            String sAux = "\nDo you want to Download Facebook Videos ? .Install this App , Its Amazing :). \n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.video.fb.facebookvideodownloaderpaid";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Share this App"));
        } catch(Exception e) {
            //e.toString();
        }
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new FileViewerFragment(), "Videos List");
        adapter.addFragment(new UrlDownloadFragment(), "Paste Link");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private String[] titles = { "Home","Videos List","Paste Link" };

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:{
                    return HomeFragment.newInstance(position);
                }
                case 1:{
                    return FileViewerFragment.newInstance(position);
                }
                case 2:{
                    return UrlDownloadFragment.newInstance(position);
                }
            }
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}