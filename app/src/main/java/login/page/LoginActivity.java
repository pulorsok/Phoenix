package login.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;


import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import http.AuthenticationJSONAsyncTask;
import http.mJsonHttpResponseHandler;
import main.phoenix.R;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private String UserResponse;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

       // _loginButton.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
//                R.style.AppTheme_Dark);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        RequestParams params = new RequestParams();
        params.put("username", email);
        params.put("password", password);

        AuthenticationJSONAsyncTask.post("/passport/authenticate", params, new mJsonHttpResponseHandler(this) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    if (response.getInt(getBaseContext().getString(R.string.server_response)) == 1) {



//                        SharedPreferencesHandler.writeString(getBaseContext(), "username", username.getText().toString());
//                        SharedPreferencesHandler.writeString(getBaseContext(), "password", password.getText().toString());
//                        SharedPreferencesHandler.writeBoolean(getBaseContext(), "rememberMe", true);



                        Toast.makeText(getBaseContext(), response.getString(getBaseContext().getString(R.string.server_message)), Toast.LENGTH_SHORT).show();
                        //Intent i = new Intent(getBaseContext(), BaseActivity.class);
                        //startActivity(i);
                        UserResponse = response.getString("userName");
                        Log.v("userName",UserResponse);

                        onLoginSuccess();
                    } else if(response.getInt(getBaseContext().getString(R.string.server_response)) == 0){
                        Toast.makeText(getBaseContext(), response.getString(getBaseContext().getString(R.string.server_message)), Toast.LENGTH_SHORT).show();
                        UserResponse = response.getString("userName");
                        Log.v("userName",UserResponse);
                        onLoginFailed();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onLoginSuccess or onLoginFailed
//                        onLoginSuccess();
//                       onLoginFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 1000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        ListDataController logindata = new ListDataController(getBaseContext());
        logindata.LoginInitial(UserResponse);
        logindata.GetSensorTagRelation();

        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() ) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 2 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
