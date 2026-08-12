package com.example.metalconstructionsestimates.dbbackuprestore.google;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.example.metalconstructionsestimates.R;

import java.util.ArrayList;
import java.util.List;

public abstract class GoogleDriveActivity extends GoogleSignInActivity {

    protected void startGoogleDriveSignIn() {
        startGoogleSignIn();
    }

    protected abstract void onGoogleDriveSignedInSuccess(final Drive driveApi);

    protected abstract void onGoogleDriveSignedInFailed(final ApiException exception);

    @Override
    protected GoogleSignInOptions getGoogleSignInOptions() {
        Scope scopeDriveAppFolder = new Scope(Scopes.DRIVE_APPFOLDER);
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(scopeDriveAppFolder)
                .build();
    }

    @Override
    protected void onGoogleSignedInSuccess(final GoogleSignInAccount signInAccount) {
        initializeDriveClient(signInAccount);
    }

    @Override
    protected void onGoogleSignedInFailed(final ApiException exception) {
        onGoogleDriveSignedInFailed(exception);
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        List<String> scopes = new ArrayList<>();
        scopes.add(DriveScopes.DRIVE_APPDATA);

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, scopes);
        credential.setSelectedAccount(signInAccount.getAccount());
        Drive.Builder builder = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential
        );
        String appName = getString(R.string.app_name);
        Drive driveApi = builder
                .setApplicationName(appName)
                .build();
        onGoogleDriveSignedInSuccess(driveApi);
    }

    /**
     * Returns the currently signed-in account, or null if nobody is signed in.
     * Use this to restore the session silently (e.g. in onCreate/onStart)
     * instead of forcing the user through the interactive sign-in flow again.
     */
    protected GoogleSignInAccount getExistingSignedInAccount() {
        return GoogleSignIn.getLastSignedInAccount(this);
    }

    /**
     * Call this on activity start to silently rebuild the Drive client
     * if the user is already signed in, avoiding a repeated sign-in prompt.
     * Returns true if a session was restored, false if the user needs to sign in.
     */
    protected boolean tryRestoreGoogleDriveSession() {
        GoogleSignInAccount account = getExistingSignedInAccount();
        if (account != null) {
            initializeDriveClient(account);
            return true;
        }
        return false;
    }

    /**
     * Signs the user out of Google entirely (not just disconnects the Drive scope),
     * so getExistingSignedInAccount() will return null afterward.
     */
    protected void signOutOfGoogleDrive(Runnable onComplete) {
        GoogleSignInClient client = GoogleSignIn.getClient(this, getGoogleSignInOptions());
        client.signOut().addOnCompleteListener(task -> {
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }
}