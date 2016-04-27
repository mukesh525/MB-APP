package vmc.mcube.in.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import vmc.mcube.in.R;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.LoginData;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Tag {
    public static final String DEAFULT = "n/a";
    public static LoginData lloginData;
    private TextInputLayout mInputLayoutEmail, mInputLayoutPassword, mInputLayoutServer;
    private CoordinatorLayout mroot;
    private ProgressDialog mProgressDialog;
    private LoginData loginData;
    private CheckBox checkBox;
    private EditText inputEmail, inputPassword, inputServer;
    private View.OnClickListener mSnackBarListner;
    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_main);
        mInputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        mInputLayoutServer = (TextInputLayout) findViewById(R.id.inputLayoutServer);
        checkBox = (CheckBox) findViewById(R.id.checkBox1);
        mroot = (CoordinatorLayout) findViewById(R.id.rootLayout);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputEmail.requestFocus();
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputServer = (EditText) findViewById(R.id.inputServer);
        btnLogin = (Button) findViewById(R.id.submit);
        btnLogin.setOnClickListener(this);
        load();
        mSnackBarListner = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            View view = this.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else {
            // writeToLog("Software Keyboard was not shown");
        }
    }

    public void save() {
        if (((CheckBox) findViewById(R.id.checkBox1)).isChecked()) {
            SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name", inputEmail.getText().toString());
            editor.putString("password", inputPassword.getText().toString());
            editor.putString("server", inputServer.getText().toString());
            editor.commit();

        }
    }

    public void load() {
        SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
        String name = pref.getString("name", DEAFULT);
        String password = pref.getString("password", DEAFULT);
        String serverName = pref.getString("server", DEAFULT);
        if (!name.equals(DEAFULT) || !password.equals(DEAFULT) || !serverName.equals(DEAFULT)) {
            inputEmail.setText(name);
            inputPassword.setText(password);
            inputServer.setText(serverName);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        //  InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        hideKeyboard();
        boolean isEmptyEmail = isEmptyEmail();
        boolean isEmptyPassword = isEmptyPassword();

        if (isEmptyEmail && isEmptyPassword && isEmptyServer()) {
            Snackbar.make(mroot, "One or more fields are blank", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_dismiss), mSnackBarListner).show();
        } else if (isEmptyEmail && !isEmptyPassword && !isEmptyServer()) {
            mInputLayoutEmail.setError("Email Cannot Be Empty");
            mInputLayoutPassword.setError(null);
            mInputLayoutServer.setError(null);
        } else if (!isEmptyEmail && isEmptyPassword && !isEmptyServer()) {
            mInputLayoutPassword.setError("Password Cannot Be Empty");
            mInputLayoutEmail.setError(null);
            mInputLayoutServer.setError(null);
        } else if (!isEmptyEmail && !isEmptyPassword && isEmptyServer()) {
            mInputLayoutServer.setError("Server Cannot Be Empty");
            mInputLayoutEmail.setError(null);
            mInputLayoutPassword.setError(null);
        } else if (isEmptyEmail && isEmptyPassword && !isEmptyServer()) {
            mInputLayoutPassword.setError("Server Cannot Be Empty");
            mInputLayoutEmail.setError("Password Cannot Be Empty");
            mInputLayoutServer.setError(null);
        } else if (!isEmptyEmail && isEmptyPassword && isEmptyServer()) {
            mInputLayoutPassword.setError("Password Cannot Be Empty");
            mInputLayoutServer.setError("Server Cannot Be Empty");
            mInputLayoutEmail.setError(null);

        } else {
            mInputLayoutPassword.setError(null);
            mInputLayoutEmail.setError(null);
            mInputLayoutServer.setError(null);

            Login();

        }


    }

    protected void Login() {
        String server = inputServer.getText().toString().replace(" ", "");
        String email = inputEmail.getText().toString().replace(" ", "");
        String password = inputPassword.getText().toString();
        if (Utils.onlineStatus(MainActivity.this)) {
            Utils.saveToPrefs(MainActivity.this, Tag.SERVER, "https://" + inputServer.getText().toString().replace(" ", ""));
            new StartLogin(server, password, email).execute();
        } else {
            Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Login();

                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.accent));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }
    }

    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();
        mProgressDialog = new ProgressDialog(this, R.style.StyledDialog);
        mProgressDialog.setMessage("Login Please Wait..!!");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
        mProgressDialog.show();
    }

    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private boolean isEmptyEmail() {
        return inputEmail.getText() == null
                || inputEmail.getText().toString() == null
                || inputEmail.getText().toString().isEmpty();
    }

    private boolean isEmptyPassword() {
        return inputPassword.getText() == null
                || inputPassword.getText().toString() == null
                || inputPassword.getText().toString().isEmpty();
    }

    private boolean isEmptyServer() {
        return inputServer.getText() == null
                || inputServer.getText().toString() == null
                || inputServer.getText().toString().isEmpty();
    }


    class StartLogin extends AsyncTask<Void, Void, LoginData> {
        String message = "No Response from server";
        String code = "N";
        JSONObject response = null;
        String server, password, email;

        public StartLogin(String server, String password, String email) {
            this.server = server;
            this.password = password;
            this.email = email;
        }

        @Override
        protected void onPreExecute() {
            showProgress("Login Please Wait..");
            super.onPreExecute();
        }


        @Override
        protected LoginData doInBackground(Void... params) {
            // TODO Auto-generated method stub
            loginData = new LoginData();

            try {

                response = Parser.InsertJSONToUrl("https://" + server + AUTHENTICATION_URL, email,
                        password, server);
            } catch (Exception e) {
            }
            if (response != null) {
                System.out.println(email + "  " + password + " " + server);
                System.out.println(response);

                try {
                    code = response.getString(CODE);
                    message = response.getString(MESSAGE);
                    if (code.equalsIgnoreCase(SUCCESS)) {

                        loginData.setAuthKey(response.getString(AUTHKEY));
                        loginData.setBusinessName(response.getString(BUSINESS_NAME));
                        loginData.setEmpContact(response.getString(EMP_CONTACT));
                        loginData.setEmpEmail(response.getString(EMP_EMAIL));
                        loginData.setEmpName(response.getString(EMP_NAME));
                        loginData.setMessage(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return loginData;
        }

        @Override
        protected void onPostExecute(LoginData data) {
            dismissProgress();
            if (!code.equals("N") && code.equalsIgnoreCase(SUCCESS)) {
                save();
                Utils.saveToPrefs(MainActivity.this, Tag.AUTHKEY, data.getAuthKey());
                Utils.saveToPrefs(MainActivity.this, Tag.BUSINESS_NAME, data.getBusinessName());
                Utils.saveToPrefs(MainActivity.this, Tag.EMP_CONTACT, data.getEmpContact());
                Utils.saveToPrefs(MainActivity.this, Tag.EMP_EMAIL, data.getEmpEmail());
                Utils.saveToPrefs(MainActivity.this, Tag.EMP_NAME, data.getEmpName());
                Utils.saveToPrefs(MainActivity.this, Tag.MESSAGE, data.getMessage());

                Constants.Anim = true;
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Snackbar snack = Snackbar.make(mroot, message, Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Login();

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.accent));
                View view = snack.getView();
                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }
        }

    }


}

