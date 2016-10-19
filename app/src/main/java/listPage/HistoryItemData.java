package listPage;

import android.graphics.Color;

import java.util.Date;

/**
 * Created by Jing on 15/5/27.
 */
public class HistoryItemData {

    public int icon;
    public String title;
    public Date time;
    public boolean check;

    public HistoryItemData(int icon, String title, Date time, boolean check) {

        this.icon = icon;
        this.title = title;
        this.time = time;
        this.check = check;

    }

    public HistoryItemData(int icon, String title,boolean check) {
        this(icon, title, new Date(),check);
    }

}
