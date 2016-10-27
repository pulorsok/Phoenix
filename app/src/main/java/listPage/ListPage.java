package listPage;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.dd.CircularProgressButton;
import com.capricorn.RayMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.race604.flyrefresh.FlyRefreshLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import cn.pedant.SweetAlert.SweetAlertDialog;
import gcm.MyGcmListenerService;
import gcm.QuickstartPreferences;
import gcm.RegistrationIntentService;
import dataController.ListDataController;
import main.phoenix.R;
import sqlite.sqliteDatabaseContract;

public class ListPage extends AppCompatActivity implements FlyRefreshLayout.OnPullRefreshListener, ColorChooserDialog.ColorCallback {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private boolean isReceiverRegistered;
    private static String sensorPage = "ALL";

    private static final int[] ITEM_DRAWABLES = { R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with };

    private BroadcastReceiver receiver;

    /**
     * View parameter
     */
    private NavigationView navigationView;
    private FlyRefreshLayout mFlylayout;
    private RecyclerView mListView;
    private FloatingActionButton hisUp; // history page up
    private FloatingActionButton hisDown; // history page down
    private FloatingActionButton PMcheck;
    private FloatingActionButton Raincheck;
    /**
     * View Controller
     */
    private ItemAdapter mAdapter;
    private ArrayList<ItemData> mDataSet = new ArrayList<>();
    private Handler mHandler = new Handler();
    private LinearLayoutManager mLayoutManager;
    private int primaryPreselect;
    private int accentPreselect;
    private ShapeDrawable drawable ;

    /**
     * DB ,notification , server
     */
    private ListDataController dbController = new ListDataController(getBaseContext());
    int notifyID = 1; // 通知的識別號碼
    Uri soundUri ;
    NotificationManager notificationManager;
    Notification notification ;

    private DrawerLayout mDrawerLayout;

    private WebView mWebview ;

    private FragmentManager fgm = getSupportFragmentManager();
    private Fragment history = new HistoryListPage();

    private String ForgotMessage;
    private boolean DialogCheck = true;
    public static boolean hasToShowDialogPM = false;
    public static boolean hasToShowDialogRain = false;
    public static boolean hasToShowDialogNoPM = false;
    public static boolean hasToShowDialogNoRain = false;
    public static boolean hasToSHowDialogForgot = false;
    public static boolean mIsPause = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_page_main);


        // the BroadCast form GCM
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(DialogCheck) {
                    String s = intent.getStringExtra(MyGcmListenerService.COPA_MESSAGE);
                    if(s != null)
                        switch (s){
                            case "pm":
                                ShowPM25SweetDialog();
                                DialogCheck = false;
                                break;
                            case "rain":
                                ShowRainSweetDialog();
                                DialogCheck = false;
                                break;
                            case "noPm":
                                ShowNoPM25SweetDialog();
                                DialogCheck = false;
                                break;
                            case "noRain":
                                ShowNoRainSweetDialog();
                                DialogCheck = false;
                                break;
                            case "forget":
                                ShowForgotSweetDialog(intent.getStringExtra("item"));
                                ForgotMessage = intent.getStringExtra("item");
                                DialogCheck = false;
                                break;
                        }

                    String c = intent.getStringExtra("remind");
                    Log.i("BroadCaster", "c = " + c);
                    Log.i("BroadCaster", "s = " + s);
                    if(c!=null)
                        if(c.equals("ok"))
                            updateDataSet();
                }
            }
        };
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();



        // Alert Notification
//        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
//        notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_launcher).setContentTitle("內容標題").setContentText("內容文字").setSound(soundUri).build(); // 建立通知
//        notification.defaults=Notification.DEFAULT_ALL;



        // Toolbar setting
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(sensorPage);

        // sensor and tag data
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //Do something after 100ms
                initDataSet();
                pDialog.dismissWithAnimation();
            }
        }, 2000);
        //initDataSet();


        // Drawer Menu Button init
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setting navigation data
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.invalidate();
        Menu menu = navigationView.getMenu();
        menu.add(R.id.group1,R.id.home,Menu.NONE,"ALL");
        setNavigationMenuItem(menu);  // Add sensor data to navigation menu
        NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                /**
                 *   Set the drawer listener
                * */
                String itemName = item.getTitle().toString();
                sensorPage = itemName;
                updateDataSet();
                Log.v("get Item Name",itemName);
                toolbar.setTitle(sensorPage);
                return true;
            }
        };
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);


        // fly
        mFlylayout = (FlyRefreshLayout) findViewById(R.id.fly_layout);
        mFlylayout.setOnPullRefreshListener(this);



        // List init
        mLayoutManager = new LinearLayoutManager(this);
        mListView = (RecyclerView) findViewById(R.id.list);
        mListView.setLayoutManager(mLayoutManager);
        mAdapter = new ItemAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setItemAnimator(new SampleItemAnimator());




        mWebview  = new WebView(this);
        PMcheck = (FloatingActionButton)findViewById(R.id.pm_btn);
        PMcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbController.CheckPM();
            }

        });
        Raincheck = (FloatingActionButton)findViewById(R.id.rain_btn);
        Raincheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbController.CheckRain();
            }

        });
        hisUp = (FloatingActionButton)findViewById(R.id.his_up_button);
        hisDown = (FloatingActionButton)findViewById(R.id.his_down_button);
        hisUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fts = fgm.beginTransaction();
                fts.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_in_down);
                fts.add(R.id.history,history).commit();
                hisUp.setVisibility(View.GONE);
                hisDown.setVisibility(View.VISIBLE);

            }
        });
        hisDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fts = fgm.beginTransaction();
                fts.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_in_down);
                fts.remove(history).commit();
                hisDown.setVisibility(View.GONE);
                hisUp.setVisibility(View.VISIBLE);
            }
        });

        View actionButton = mFlylayout.getHeaderActionButton();
        if (actionButton != null) {
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFlylayout.startRefresh();
                }
            });
        }



        // Registering BroadcastReceiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    //mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }



    }


    /**
     * GCM
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.v("ListPage Life","onResume");

        registerReceiver();
        mIsPause = false;
        if(hasToShowDialogPM){
            ShowPM25SweetDialog();
            hasToShowDialogPM = false;
        }
        if(hasToShowDialogRain){
            ShowRainSweetDialog();
            hasToShowDialogRain = false;
        }
        if(hasToSHowDialogForgot){
            ShowForgotSweetDialog(ForgotMessage);
            hasToSHowDialogForgot = false;
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.v("ListPage Life","onStart");
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(MyGcmListenerService.COPA_RESULT)
        );
    }

    @Override
    protected void onStop() {
        Log.v("ListPage Life","onStop");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.v("ListPage Life","onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        mIsPause = true;

    }
    private void ShowPM25SweetDialog(){
        new SweetAlertDialog(ListPage.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("")
                .setContentText("The Particulate Matter(PM2.5) has exceeded  the standard.\n" +
                        "Please wearing masks.")
                .setConfirmText("Remind me")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dbController.setRemind("口罩","add");
                        sweetAlertDialog.dismissWithAnimation();
                        DialogCheck = true;
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        DialogCheck = true;
                    }
                })
                .setCustomImage(R.drawable.pm)
                .show();
    }
    private void ShowNoPM25SweetDialog(){
        new SweetAlertDialog(ListPage.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("")
                .setContentText("The Pm25 is available")
                .setConfirmText("OK")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        DialogCheck = true;
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        DialogCheck = true;
                    }
                })
                .setCustomImage(R.drawable.pm)
                .show();
    }
    private void ShowForgotSweetDialog(String forgotItem){
        new SweetAlertDialog(ListPage.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("")
                .setContentText("You have missed your " + forgotItem)
                .setConfirmText("OK")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        DialogCheck = true;
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        DialogCheck = true;
                    }
                })
                .show();
    }
    private void ShowRainSweetDialog(){
        new SweetAlertDialog(ListPage.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("")
                .setContentText("The probability of precipitation \nhas exceeded the standard." +
                                    "Suggest user  bringing the umbrella.")
                .setConfirmText("Remind me")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dbController.setRemind("雨傘","add");
                        sweetAlertDialog.dismissWithAnimation();
                        DialogCheck = true;
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        DialogCheck = true;
                    }
                })
                .setCustomImage(R.drawable.rain)
                .show();
    }
    private void ShowNoRainSweetDialog(){
        new SweetAlertDialog(ListPage.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("")
                .setContentText("no rain.")
                .setConfirmText("OK")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        DialogCheck = true;
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        DialogCheck = true;
                    }
                })
                .setCustomImage(R.drawable.rain)
                .show();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    public void dialog(){





    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    /**
     *  Get sensor data from sqlite and add to navigation menu
     * @param menu
     */
    private void setNavigationMenuItem(Menu menu){

        String[] sensors = dbController.getSqliteSelect(sqliteDatabaseContract.USER_SESNSOR.TABLE,sqliteDatabaseContract.USER_SESNSOR.SENSOR);
        for(String s : sensors){
            menu.add(s);
        }
    }

    /**
     *  Init list adapter data
     */
    private void initDataSet() {
//        mDataSet.add(new ItemData(Color.parseColor("#76A9FC"), R.mipmap.ic_assessment_white_24dp, "Key", new Date(2014 - 1900, 2, 9)));
//        mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_folder_white_24dp, "Note Book", new Date(2014 - 1900, 1, 3)));
//        mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_folder_white_24dp, "wallet", new Date(2014 - 1900, 0, 9)));
//
        updateDataSet();
//        String[] alltags = dbController.getSqliteSelect(sqliteDatabaseContract.USER_TAG.TABLE,sqliteDatabaseContract.USER_TAG.TAG);
//        for(String s: alltags){
//            mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_smartphone_white_24dp, s,new Date()));
//        }
    }
    private void updateDataSet(){
        //String conditionQuery = sqliteDatabaseContract.SENSOR_TAG.SENSOR + " = " + "'" + sensorPage + "'";
        //mDataSet.clear();

        String[] sensorTags = null;

        
        if(sensorPage == "ALL") {

            sensorTags = dbController.getSqliteSelect(sqliteDatabaseContract.USER_TAG.TABLE, sqliteDatabaseContract.USER_TAG.TAG);
        }else{

            sensorTags = dbController.getSqliteSelect(
                    sqliteDatabaseContract.SENSOR_TAG.TABLE,
                    sqliteDatabaseContract.SENSOR_TAG.TAG,
                    sqliteDatabaseContract.SelectConditionQurey.tagOrderFromSensor(sqliteDatabaseContract.SENSOR_TAG.SENSOR, sensorPage)
            );
        }

        mDataSet.clear();
        String[] remind = ListDataController.array;
        for(String s: sensorTags){
            if (Arrays.asList(remind).contains(s)) {
                mDataSet.add(new ItemData(Color.YELLOW, R.mipmap.ic_smartphone_white_24dp, s,new Date()));
                // is valid
            } else {
                mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_smartphone_white_24dp, s,new Date()));
                // not valid
            }
           // mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_smartphone_white_24dp, s,new Date()));
        }
        mListView.removeAllViews();
        mAdapter = new ItemAdapter(this);

        mListView.setAdapter(mAdapter);

        mListView.setItemAnimator(new SampleItemAnimator());

    }

    private void addItemData() {
        ItemData itemData = new ItemData(Color.parseColor("#FFC970"), R.mipmap.ic_smartphone_white_24dp, "Magic Cube Show", new Date());


        mDataSet.add(0, itemData);
        mAdapter.notifyItemInserted(0);
        mLayoutManager.scrollToPosition(0);
    }

    private void initRayMenu(RayMenu menu, int[] itemDrawables) {
        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(ListPage.this);
            item.setImageResource(itemDrawables[i]);

            final int position = i;
            menu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    Toast.makeText(ListPage.this, "position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh(FlyRefreshLayout view) {
        dbController.Update();
        View child = mListView.getChildAt(0);
        if (child != null) {
            bounceAnimateView(child.findViewById(R.id.icon));
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFlylayout.onRefreshFinish();
            }
        }, 2000);
    }
    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void bounceAnimateView(View view) {
        if (view == null) {
            return;
        }

        Animator swing = ObjectAnimator.ofFloat(view, "rotationX", 0, 30, -20, 0);
        swing.setDuration(400);
        swing.setInterpolator(new AccelerateInterpolator());
        swing.start();
    }
    protected void setColorParmeter(ShapeDrawable drawable ,int accentPreselect ,int primaryPreselect){
        this.drawable=drawable;
        this.accentPreselect=accentPreselect;
        this.primaryPreselect=primaryPreselect;
    }

    @Override
    public void onRefreshAnimationEnd(FlyRefreshLayout view) {
        updateDataSet();
    }

    /**
     *  Color picker planet
     * @param dialog
     * @param color
     */
    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int color) {
        if (dialog.isAccentMode()) {
            accentPreselect = color;
            drawable.getPaint().setColor(color);
            ThemeSingleton.get().positiveColor = DialogUtils.getActionTextStateList(ListPage.this, color);
            ThemeSingleton.get().neutralColor = DialogUtils.getActionTextStateList(ListPage.this, color);
            ThemeSingleton.get().negativeColor = DialogUtils.getActionTextStateList(ListPage.this, color);
            ThemeSingleton.get().widgetColor = color;
        } else {
            primaryPreselect = color;
            drawable.getPaint().setColor(color);
            if (ListPage.this.getActionBar() != null){
                mAdapter.setcolor(color);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAdapter.setcolor(color);
            }
        }
        mAdapter.releaseItemViewHolder();
    }


    /**
     *  Item Class
     *
     * */

    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder>{



        private ShapeDrawable drawable ;
        private LayoutInflater mInflater;
        private DateFormat dateFormat;
        public ItemViewHolder itemViewHolder;


        public ItemAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.view_list_item, viewGroup, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder itemViewHolder, int i) {
            final ItemData data = mDataSet.get(i);
            drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(data.color);

            itemViewHolder.icon.setBackgroundDrawable(drawable);
            itemViewHolder.icon.setImageResource(data.icon);
            itemViewHolder.title.setText(data.title);
            itemViewHolder.subTitle.setText(dateFormat.format(data.time));
//            itemViewHolder.checkImage.setIndeterminateProgressMode(true);
//            itemViewHolder.checkImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {

////                    new SweetAlertDialog(ListPage.this, SweetAlertDialog.WARNING_TYPE)
////                            .setTitleText("Warning")
////                            .setContentText("You have missed your personal belongings")
////                            .setConfirmText("OK")
////                            .show();
////                    notificationManager.notify(notifyID, notification); // 發送通知
//                }
//            });

            /**
             *  Initial information button
             * */
            itemViewHolder.infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String action = (data.color == Color.YELLOW) ? "del" : "add";
                    new SweetAlertDialog(ListPage.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("")
                            .setContentText("You sure you want to " + action + " this item?")
                            .setConfirmText("YES")
                            .setCancelText("NO")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    Log.d("datatitle",data.title);
                                    dbController.setRemind(data.title,action);
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    DialogCheck = true;
                                }
                            })
                            .show();



//
                }
            });

            itemViewHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setColorParmeter(drawable,accentPreselect,primaryPreselect);
                    new ColorChooserDialog.Builder(ListPage.this, R.string.color_palette)
                            .titleSub(R.string.color_palette)
                            .preselect(primaryPreselect)
                            .show();
                    setItemViewHolder(itemViewHolder);

                }
            });


        }
        void setItemViewHolder(ItemViewHolder itemViewHolder){
            this.itemViewHolder = itemViewHolder;
        }
        void setcolor(@ColorInt int color){
            drawable.getPaint().setColor(color);
            itemViewHolder.icon.setBackgroundColor(color);
            itemViewHolder.icon.setBackgroundDrawable(drawable);

        }
        void releaseItemViewHolder(){
            itemViewHolder =null;
        }
        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        CircularProgressButton checkImage;
        ImageButton icon;
        TextView title;
        TextView subTitle;
        ImageButton infoButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageButton) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subtitle);
            //checkImage = (CircularProgressButton) itemView.findViewById(R.id.btnWithText);
            infoButton = (ImageButton)itemView.findViewById(R.id.edit_btn);
        }

    }
}
