package listPage;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import android.media.Image;
import android.os.Bundle;

import android.support.annotation.ColorInt;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import dataController.ListDataController;
import main.phoenix.R;
import sqlite.sqliteDatabaseContract;

/**
 * Created by student.cce on 2016/9/10.
 */
public class HistoryListPage extends Fragment {


    private Context context;
    private RecyclerView mListView;
    private ItemAdapter mAdapter;
    private ArrayList<HistoryItemData> mDataSet = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private View View ;

    private ListDataController dbController = new ListDataController(getContext());


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View = inflater.inflate(R.layout.history_list, container, false);
        Cursor c = dbController.getHistory();


        if(c.getCount()!=0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                Log.v("History", c.getString(c.getColumnIndex(sqliteDatabaseContract.HISTORY.TAG)));
                mDataSet.add(new HistoryItemData(
                        R.mipmap.ic_folder_white_24dp, //image
                        c.getString(c.getColumnIndex(sqliteDatabaseContract.HISTORY.TAG)), // title
                        new Date(2016-1900,8,25,9,24,22), //time
                        c.getInt(c.getColumnIndex(sqliteDatabaseContract.HISTORY.LOCATION)) == 1) // location
                );

                c.moveToNext();
            }
        }

        c.close();


        mLayoutManager = new LinearLayoutManager(getContext());
        mListView = (RecyclerView) View.findViewById(R.id.history_list);
        mListView.setLayoutManager(mLayoutManager);
        mAdapter = new ItemAdapter(getContext());
        mListView.setAdapter(mAdapter);
        mListView.setItemAnimator(new SampleItemAnimator());
        return View;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataSet.clear();
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder>{



        private ShapeDrawable drawable ;
        private LayoutInflater mInflater;
        private DateFormat dateFormat;
        public ItemViewHolder itemViewHolder;
        private ShapeDrawable checkDrawable;

        public ItemAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.history_view_list_item, viewGroup, false);

            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder itemViewHolder, int i) {
            final HistoryItemData data = mDataSet.get(i);
            drawable = new ShapeDrawable(new OvalShape());
            checkDrawable = new ShapeDrawable(new OvalShape());

            itemViewHolder.icon.setBackgroundDrawable(drawable);
            itemViewHolder.icon.setImageResource(data.icon);
            itemViewHolder.title.setText(data.title);
            itemViewHolder.subTitle.setText(data.time.toLocaleString());
            if(data.check){
                checkDrawable.getPaint().setColor(0xFF27AA2D);
                itemViewHolder.check.setBackgroundDrawable(checkDrawable);
                itemViewHolder.check.setImageResource(R.drawable.sign_in);
            } else{
                checkDrawable.getPaint().setColor(0xFFAD2727);
                itemViewHolder.check.setBackgroundDrawable(checkDrawable);
                itemViewHolder.check.setImageResource(R.drawable.sign_out);
            }






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

        ImageButton icon;
        TextView title;
        TextView subTitle;
        ImageButton check;

        public ItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageButton) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subtitle);
            check = (ImageButton) itemView.findViewById(R.id.check);
        }

    }
}
