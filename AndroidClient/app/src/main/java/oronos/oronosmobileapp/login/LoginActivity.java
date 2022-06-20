package oronos.oronosmobileapp.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import java.util.regex.Pattern;

import oronos.oronosmobileapp.BaseActivity;
import oronos.oronosmobileapp.MainActivity;
import oronos.oronosmobileapp.MainApplication;
import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.httpClientServices.httpRequests;

public class LoginActivity extends BaseActivity implements LoginView, View.OnClickListener {

    private ProgressBar progressBar;
    private EditText username;
    private EditText password;
    private LoginPresenter presenter;
    private String IP_ADDR;
    private Context mContext;
    private PopupWindow mPopupWindow;
    private ConstraintLayout mConstraintLayout;
    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validateIp(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isStoragePermissionGranted();

        SharedPreferences sharedPref2 = getPreferences(Context.MODE_PRIVATE);

        SharedPreferences sharedPref = getSharedPreferences(Configuration.PREFS_NAME, Context.MODE_PRIVATE);
        Configuration.darkTheme = sharedPref.getBoolean(Configuration.PREF_DARK_THEME, false);

        Configuration.ip_addr = sharedPref2.getString(getString(R.string.ip_key), null);

        if (savedInstanceState != null) {
            IP_ADDR = savedInstanceState.getString("IP_ADDR");
        }
        setContentView(R.layout.activity_login);

        if (!Configuration.darkTheme) {
            findViewById(R.id.button).setBackgroundColor(Color.argb(200, 224, 37, 52));
        }

        progressBar = (ProgressBar) findViewById(R.id.progress);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        Button changeIpButton = (Button) findViewById(R.id.changeIpButton);
        Button changeTheme = (Button) findViewById(R.id.changeThemeButton);
        findViewById(R.id.button).setOnClickListener(this);
        presenter = new LoginPresenterImpl(this, new LoginInteractorImpl());
        mContext = (Context) this;
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.cl);
        changeIpButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.change_ip_layout, null);

                mPopupWindow = new PopupWindow(
                        customView, 600, 400, true
                );
                mPopupWindow.showAtLocation(mConstraintLayout, Gravity.CENTER, 0, -400);
                Button confirmIp = (Button) customView.findViewById(R.id.ipAddrButton);
                if (Configuration.ip_addr != null) {
                    EditText editIp = (EditText) customView.findViewById(R.id.ipAddrTxt);
                    editIp.setHint(Configuration.ip_addr);
                }

                confirmIp.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText editIp = (EditText) view.getRootView().findViewById(R.id.ipAddrTxt);
                        if (!editIp.getText().toString().isEmpty())
                            setIPAddr(editIp.getText().toString());
                        mPopupWindow.dismiss();
                    }
                });

            }

        });

        changeTheme.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(Configuration.darkTheme) {
                    refreshTheme(false);
                } else {
                    refreshTheme(true);
                }
            }
        });
    }

    private void refreshTheme(boolean useDarkTheme) {
        SharedPreferences.Editor editor = getSharedPreferences(Configuration.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Configuration.PREF_DARK_THEME, useDarkTheme);
        editor.apply();
        Configuration.darkTheme = useDarkTheme;
        finish();
        startActivity(getIntent());
    }

    private void setIPAddr(String s) {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (validateIp(s)) {
            IP_ADDR = s;
            Configuration.ip_addr = IP_ADDR;
            editor.putString(getString(R.string.ip_key), IP_ADDR);
            editor.commit();
        } else {
            hideProgress();
            Context context = (Context) this;
            AlertDialog ad = new AlertDialog.Builder(context)
                    .create();
            ad.setCancelable(false);
            ad.setTitle("Invalid input Error");
            ad.setMessage("Invalid IP ERROR, try again");
            ad.setButton("ok", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ad.show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Configuration.ip_addr != null) {
            IP_ADDR = Configuration.ip_addr;
        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {

                    findViewById(R.id.changeIpButton).callOnClick();
                }
            }, 100);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setUsernameError() {
        username.setError(getString(R.string.username_error));
    }

    @Override
    public void setPasswordError() {
        password.setError(getString(R.string.password_error));
    }

    @Override
    public void setBadCredentialsError() {
        hideProgress();
        Context context = (Context) this;
        AlertDialog ad = new AlertDialog.Builder(context)
                .create();
        ad.setCancelable(false);
        ad.setTitle("Server Error");
        ad.setMessage(getString(R.string.bad_credentials_error));
        ad.setButton("ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }


    @Override
    public void navigateToHome() {
        MainApplication.getInstance().startService();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void startHttp() {
        new httpRequests((Context) (this));
        new httpRequests.getConfigBasicRequest().execute(Configuration.getIpAddr() + Configuration.urlConfigBasic);
        new httpRequests.getConfigMiscFilesRequest().execute(Configuration.getIpAddr() + Configuration.urlConfigMiscFiles);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        presenter.validateCredentials(username.getText().toString(), password.getText().toString());
    }

    @Override
    protected boolean useAppBar() {
        return false;
    }

    public boolean isStoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("PERMISSION", "Permission is granted");
            return true;
        } else {
            Log.v("PERMISSION", "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("PERMISSION", "Permission: " + permissions[0] + " was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

}
