package com.sip;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;

public class Main extends AppCompatActivity {
    FrameLayout frame_container;
    public static NavigationView navigation_drawer;
    public static DrawerLayout drawerLayout;
    static Toolbar toolbar;
    CoordinatorLayout coordinate_layout;
    public static SipManager sipManager=null;
    public static SipProfile sipProfile;
    public SipAudioCall call=null;
    IncomingCallReceiver callReceiver;
    static Activity activity;
    public static int open_fragment=0;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        navigation_drawer=(NavigationView) findViewById(R.id.navigation_drawer);
        coordinate_layout=(CoordinatorLayout) findViewById(R.id.coordinate_layout);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawerLayout);
        fragmentManager = getSupportFragmentManager();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigation_drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        if (!menuItem.isChecked()) {
                            displayView(0);
                        }
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.profile:
                        displayView(1);
                        drawerLayout.closeDrawers();
                        menuItem.setChecked(true);
                        return true;
                    case R.id.about_us:
                        displayView(2);
                        drawerLayout.closeDrawers();
                        menuItem.setChecked(true);
                        return true;
                    default:
                        return true;
                }
            }
        });
        ((TextView) navigation_drawer.findViewById(R.id.user_name)).setText("Ankur Khandelwal");
        ((TextView) navigation_drawer.findViewById(R.id.user_email)).setText("ankurkhandelwal08027@gmail.com");
//                ((TextView) navigation_drawer.findViewById(R.id.user_name)).setText(Common.prefs.getString("user_name", ""));
//        ((TextView) navigation_drawer.findViewById(R.id.user_email)).setText(Common.prefs.getString("user_email", ""));

         /**
         * Set up IntentFilter, this will fire up when someone calls on this sip address
         */
        Log.i("OnCreate","Before IntentFilter");
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.ankurkhandelwal.sip.INCOMING_CALL");
        callReceiver=new IncomingCallReceiver();
        this.registerReceiver(callReceiver,intentFilter);
        Log.i("OnCreate", "After IntentFilter");
        //"Push to talk" can be a serious pain when the screen keeps turning off., now prevent that
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initializeManager();
    }

    private void displayView(int i) {
        Fragment fragment=null;
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        switch (i){
            case 0:
                fragment=new HomeFragment();
                open_fragment=0;
                fragmentManager.popBackStack("Home",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.addToBackStack("Home");
                break;
            case 1:
                fragment=new HomeFragment();
                open_fragment=1;
                fragmentManager.popBackStack("Profile",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.addToBackStack("Profile");
                break;
            case 2:
                fragment=new HomeFragment();
                open_fragment=2;
                fragmentManager.popBackStack("AboutUs",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.addToBackStack("AboutUs");
                break;
            default:
                break;
        }
        transaction.replace(R.id.frame_container,fragment).commit();
    }

    private void initializeManager() {
        if(sipManager==null){
            sipManager=SipManager.newInstance(this);
            Log.i("InitializeManager","Manager Initialised");
        }
        initializeProfile();
    }

    private void initializeProfile() {
//        String username= Common.prefs.getString("username","");
//        String password=Common.prefs.getString("password","");
//        String domain=Common.prefs.getString("domain","");
        String username="ank27ur";
        String password="password";
        String domain="sip.linphone.org";
        try {
            SipProfile.Builder builder=new SipProfile.Builder(username,domain);
            builder.setPassword(password);
            builder.setSendKeepAlive(true);
            builder.setProtocol("TCP");
            sipProfile=builder.build();
            System.out.println("builder"+ builder);
            Log.i("InitializeProfile",sipProfile.getDisplayName()+"");
            Intent intent=new Intent();
            intent.setAction("com.example.ankurkhandelwal.sip.INCOMING_CALL");
            PendingIntent pendingIntent= PendingIntent.getBroadcast(this,0,intent,Intent.FILL_IN_DATA);
            sipManager.open(sipProfile,pendingIntent,null);
            Log.i("InitializeProfile", "Manager open");
            //listener must be added AFTER manager.open is called,
            sipManager.setRegistrationListener(sipProfile.getUriString(), new SipRegistrationListener() {
                @Override
                public void onRegistering(String localProfileUri) {
                    updateStatus("Registering with SIP Server...");
                }

                @Override
                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    updateStatus("Registration Done");
                }

                @Override
                public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                    updateStatus("Registration Failed, Try again Later!!!");
                }
            });
            Toast.makeText(this, "Initiated.." + username, Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
            if(sipProfile!=null){
                try{
                    sipManager.close(sipProfile.getUriString());
                }catch (Exception e1){
                    Log.i("Initiatecall","Error Closing Manager",e1);
                }
            }
            if(call!=null){
             call.close();
            }
        } catch (SipException e) {
            e.printStackTrace();
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

    @Override
    public void onPostResume(){
        super.onPostResume();
        displayView(0);
        navigation_drawer.getMenu().findItem(R.id.home).setChecked(true);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        initializeManager();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(call!=null){
            call.close();
        }
        if(callReceiver!=null){
            this.unregisterReceiver(callReceiver);
        }
        try{
            if(sipProfile!=null){
                sipManager.close(sipProfile.getUriString());
            }
        }catch (Exception e){
            Log.d("Main_Destroy","Failed to close LocalProfile",e);
        }

    }

    public static void updateStatus(SipAudioCall sipAudioCall) {
        String callername=sipAudioCall.getPeerProfile().getDisplayName();
        if(callername==null){
            callername=sipAudioCall.getPeerProfile().getUserName();
        }
        updateStatus(callername + "@" + sipAudioCall.getPeerProfile().getSipDomain());
    }

    private static void updateStatus(final String s) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }
}
