package oronos.oronosmobileapp.logout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import oronos.oronosmobileapp.MainApplication;
import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Tag;

/**
 * LogoutPresenterImpl.java
 * Implémentation du présenter pour le logout
 */
public class LogoutPresenterImpl implements LogoutPresenter, LogoutInteractor.OnLogoutFinishedListener {

    private final LogoutView logoutView;
    private final LogoutInteractor logoutInteractor;

    public LogoutPresenterImpl(LogoutView logoutView, LogoutInteractor logoutInteractor) {
        this.logoutView = logoutView;
        this.logoutInteractor = logoutInteractor;
    }

    @Override
    public void logout() {
        logoutInteractor.logout(this);
    }

    @Override
    public void onSuccess() {
        FLog.i(Tag.LOGOUT, "Success to logout");
        // Si le logout réussi on redirige l'utilisateur vers la vue du login
        logoutView.navigateToLoginView();
    }
    public void onError() {
        Context context = (Context) logoutView;
        FLog.e(Tag.LOGOUT, "Logout with server response failed");

        //Si le lougout echoue on affiche un message d'erreur et on le redirige vers la login vue
        AlertDialog ad = new AlertDialog.Builder(context)
            .create();
        ad.setCancelable(false);
        ad.setTitle("Server Error");
        ad.setMessage("Error from server, it seems you were not logged in");
        ad.setButton("ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                logoutView.navigateToLoginView();
            }
        });
        ad.show();}


}
