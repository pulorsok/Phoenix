package listPage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import main.phoenix.R;

/**
 * Created by student.cce on 2016/10/19.
 */
public class MessageBox extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_list_item);

        //Button btn = (Button) findViewById(R.id.Ok);
//        btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                finish();
//            }
//        });
    }
}
