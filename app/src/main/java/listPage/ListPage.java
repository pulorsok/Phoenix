package listPage;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.dd.CircularProgressButton;
import com.capricorn.RayMenu;
import com.race604.flyrefresh.FlyRefreshLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import main.phoenix.R;

public class ListPage extends AppCompatActivity implements FlyRefreshLayout.OnPullRefreshListener, ColorChooserDialog.ColorCallback {
    private static final int[] ITEM_DRAWABLES = { R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with };

    private FlyRefreshLayout mFlylayout;
    private RecyclerView mListView;

    private ItemAdapter mAdapter;

    private ArrayList<ItemData> mDataSet = new ArrayList<>();
    private Handler mHandler = new Handler();
    private LinearLayoutManager mLayoutManager;
    private int primaryPreselect;
    private int accentPreselect;
    private ShapeDrawable drawable ;

    int notifyID = 1; // 通知的識別號碼
    Uri soundUri ;
    NotificationManager notificationManager;
    Notification notification ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataSet();
        setContentView(R.layout.list_page_main);


        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_launcher).setContentTitle("內容標題").setContentText("內容文字").setSound(soundUri).build(); // 建立通知
        notification.defaults=Notification.DEFAULT_ALL;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFlylayout = (FlyRefreshLayout) findViewById(R.id.fly_layout);

        mFlylayout.setOnPullRefreshListener(this);

        mListView = (RecyclerView) findViewById(R.id.list);

        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        mAdapter = new ItemAdapter(this);


        mListView.setAdapter(mAdapter);

        mListView.setItemAnimator(new SampleItemAnimator());


        RayMenu ItemListMenu = (RayMenu)findViewById(R.id.item_list_menu);
        initRayMenu(ItemListMenu ,ITEM_DRAWABLES);
        ItemListMenu.getRayLayout().setChildSize(125);


        View actionButton = mFlylayout.getHeaderActionButton();
        if (actionButton != null) {
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFlylayout.startRefresh();
                }
            });
        }
    }

    private void initDataSet() {
        mDataSet.add(new ItemData(Color.parseColor("#76A9FC"), R.mipmap.ic_assessment_white_24dp, "Key", new Date(2014 - 1900, 2, 9)));
        mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_folder_white_24dp, "Note Book", new Date(2014 - 1900, 1, 3)));
        mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_folder_white_24dp, "wallet", new Date(2014 - 1900, 0, 9)));
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
            ImageView item = new ImageView(this);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
        addItemData();
    }
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
            if (getSupportActionBar() != null){
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
            itemViewHolder.checkImage.setIndeterminateProgressMode(true);
            itemViewHolder.checkImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(ListPage.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning")
                            .setContentText("You have missed your personal belongings")
                            .setConfirmText("OK")
                            .show();
                    notificationManager.notify(notifyID, notification); // 發送通知
                }
            });

            /**
             *  Initial information button
             * */
            itemViewHolder.infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(ListPage.this)
                            .title(R.string.item_list_edit)
                            .content(R.string.item_list_edit_content)
                            .inputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                    InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                            .inputRange(2, 16)
                            .positiveText(R.string.submit)
                            .input(R.string.item_list_edit_input_hint, 0, false, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    /**
                                     *      Warn, if string has been created
                                     * */
                                    if (input.toString().equalsIgnoreCase("hello")) {
                                        dialog.setContent("It has a same name that already created");
                                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                    } else {
                                        dialog.setContent(R.string.item_list_edit_content);
                                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                        itemViewHolder.title.setText(input.toString());
                                        Toast.makeText(ListPage.this, "Edited successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).show();
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
            checkImage = (CircularProgressButton) itemView.findViewById(R.id.btnWithText);
            infoButton = (ImageButton)itemView.findViewById(R.id.edit_btn);
        }

    }
}