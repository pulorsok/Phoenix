package main.phoenix;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.capricorn.ArcMenu;
import com.capricorn.RayMenu;
import com.github.clans.fab.FloatingActionButton;

import listPage.ListPage;
import login.page.LoginActivity;

public class HomePageActivity extends AppCompatActivity {
    private static final int[] ITEM_DRAWABLES = { R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * @see  LoginActivity
         * */
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);

        FloatingActionButton ItemListBtn = (FloatingActionButton) findViewById(R.id.item_list_page_button);
        assert ItemListBtn != null;
        ItemListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment listPage = new ListPage();
                ft.replace(R.id.fragment,listPage).commit();
            }
        });


        ArcMenu arcMenu2 = (ArcMenu) findViewById(R.id.arc_menu_2);
        arcMenu2.getArcLauout().setChildSize(240);

        initArcMenu(arcMenu2, ITEM_DRAWABLES);

        final int itemCount = ITEM_DRAWABLES.length;

    }
    private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(this);
            item.setImageResource(itemDrawables[i]);

            final int position = i;
            menu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(HomePageActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                    Toast.makeText(HomePageActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
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
}
