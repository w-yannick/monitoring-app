package oronos.oronosmobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import oronos.oronosmobileapp.widgets.PdfWidget;
import oronos.oronosmobileapp.login.LoginActivity;
import oronos.oronosmobileapp.logout.LogoutInteractorImpl;
import oronos.oronosmobileapp.logout.LogoutPresenter;
import oronos.oronosmobileapp.logout.LogoutPresenterImpl;
import oronos.oronosmobileapp.logout.LogoutView;
import oronos.oronosmobileapp.settings.Configuration;

/**
 * BaseActivity.java
 * Activity qui implémente ce qui est commun à toutes les activités
 */
public abstract class BaseActivity extends AppCompatActivity implements LogoutView, MenuItem.OnMenuItemClickListener {

    private static final int PDF_MENU_GROUP_ID = 200;

    public Context context;
    private LogoutPresenter presenter;
    private List<PdfWidget> pdfWidgets = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        presenter = new LogoutPresenterImpl(this, new LogoutInteractorImpl());

        SharedPreferences sharedPref = getSharedPreferences(Configuration.PREFS_NAME, Context.MODE_PRIVATE);
        Configuration.darkTheme = sharedPref.getBoolean(Configuration.PREF_DARK_THEME, false);



        if (Configuration.darkTheme) {
            setTheme(R.style.oronosDark);
        } else {
            setTheme(R.style.oronosLight);
        }

        if (Configuration.pdfFileNames != null && pdfWidgets.isEmpty()) {
            for (String fileName : Configuration.pdfFileNames) {
                PdfWidget pdfWidget = new PdfWidget();
                pdfWidget.setPdfFileName(fileName);
                pdfWidgets.add(pdfWidget);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        addPdfToHambergerMenu(menu.getItem(0).getSubMenu());
        if (Configuration.darkTheme) {
            menu.findItem(R.id.logout).setIcon(R.drawable.ic_power_settings_new_white);
            menu.findItem(R.id.hamburger_menu).setIcon(R.drawable.ic_menu_white);
        } else {
            menu.findItem(R.id.logout).setIcon(R.drawable.ic_power_settings_new_black);
            menu.findItem(R.id.hamburger_menu).setIcon(R.drawable.ic_menu_black);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                MainApplication.getInstance().stopService();
                presenter.logout();
                return true;
            case R.id.changeTheme:
                refreshTheme(!Configuration.darkTheme);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshTheme(boolean useDarkTheme) {
        SharedPreferences.Editor editor = getSharedPreferences(Configuration.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Configuration.PREF_DARK_THEME, useDarkTheme);
        editor.apply();
        Configuration.darkTheme = useDarkTheme;
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getGroupId() == PDF_MENU_GROUP_ID) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activity_content, pdfWidgets.get(item.getItemId())).commit();
            return true;
        }
        return false;
    }

    @Override
    public void setContentView(int layoutID) {
        ViewGroup fullView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_base, null);
        ConstraintLayout activityContainer = (ConstraintLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutID, activityContainer, true);
        super.setContentView(fullView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.navigationView);
        if(useAppBar())
            setSupportActionBar(toolbar);
        else {
            toolbar.setVisibility(View.GONE);
            bottomNav.setVisibility(View.GONE);
        }
    }

    @Override
    public void navigateToLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        // set the new task and clear flags
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void addPdfToHambergerMenu(SubMenu subMenu) {
        for (int i = 0; i < pdfWidgets.size(); i++)
            subMenu.add(PDF_MENU_GROUP_ID, i, i, pdfWidgets.get(i).getPdfFileName()).setOnMenuItemClickListener(this);
    }

    protected boolean useAppBar() {
        return true;
    }

}
