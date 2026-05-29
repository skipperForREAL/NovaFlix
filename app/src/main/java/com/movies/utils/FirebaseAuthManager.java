// File: com/expense/utils/FirebaseAuthManager.java
package com.movies.utils;

import android.app.Activity;
import android.content.Intent;

import com.movies.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthManager {

    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // ============ USER STATE ============

    public static FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public static boolean isSignedIn() {
        return getCurrentUser() != null;
    }

    // ============ EMAIL/PASSWORD AUTH ============

    public static Task<AuthResult> signInWithEmail(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public static Task<AuthResult> signUpWithEmail(String email, String password, String username) {
        return auth.createUserWithEmailAndPassword(email, password)
                .continueWithTask((Task<AuthResult> task) -> {
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        return Tasks.forException(e != null ? e : new Exception("Sign-up failed"));
                    }
                    AuthResult result = task.getResult();
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        return Tasks.forException(new Exception("User creation failed"));
                    }
                    return saveUserToFirestore(user, username, "email")
                            .continueWithTask(t -> {
                                if (t.isSuccessful()) {
                                    return Tasks.forResult(result);
                                } else {
                                    Exception fe = t.getException();
                                    return Tasks.forException(fe != null ? fe : new Exception("Firestore save failed"));
                                }
                            });
                });
    }

    // ============ GOOGLE SIGN-IN ============

    public static GoogleSignInClient getGoogleSignInClient(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(activity, gso);
    }

    public static Task<AuthResult> handleGoogleSignInResult(Intent data) {
        // Step 1: Get Google account from intent
        return GoogleSignIn.getSignedInAccountFromIntent(data)
                .continueWithTask((Task<GoogleSignInAccount> task) -> {
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        return Tasks.forException(e != null ? e : new Exception("Google sign-in failed"));
                    }
                    GoogleSignInAccount account = task.getResult();
                    if (account == null || account.getIdToken() == null) {
                        return Tasks.forException(new Exception("Google account is null"));
                    }
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    return auth.signInWithCredential(credential);
                })
                // Step 2: Handle Firebase auth result + Firestore save
                .continueWithTask((Task<AuthResult> authTask) -> {
                    if (!authTask.isSuccessful()) {
                        Exception e = authTask.getException();
                        return Tasks.forException(e != null ? e : new Exception("Firebase auth failed"));
                    }
                    AuthResult result = authTask.getResult();
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        return Tasks.forException(new Exception("Firebase user is null"));
                    }

                    Task<Void> firestoreTask;
                    AdditionalUserInfo userInfo = result.getAdditionalUserInfo();
                    if (userInfo != null && userInfo.isNewUser()) {
                        String username = user.getDisplayName() != null ?
                                user.getDisplayName() : "User_" + user.getUid().substring(0, 6);
                        firestoreTask = saveUserToFirestore(user, username, "google");
                    } else {
                        firestoreTask = firestore.collection("users").document(user.getUid())
                                .update("lastLogin", System.currentTimeMillis());
                    }

                    return firestoreTask.continueWithTask(t -> {
                        if (t.isSuccessful()) {
                            return Tasks.forResult(result);
                        } else {
                            Exception fe = t.getException();
                            return Tasks.forException(fe != null ? fe : new Exception("Firestore update failed"));
                        }
                    });
                });
    }

    // ============ APPLE SIGN-IN ============

    public static Task<AuthResult> signInWithApple(Activity activity) {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("apple.com");
        provider.addCustomParameter("locale", "en");
        return auth.startActivityForSignInWithProvider(activity, provider.build())
                .continueWithTask((Task<AuthResult> authTask) -> {
                    if (!authTask.isSuccessful()) {
                        Exception e = authTask.getException();
                        return Tasks.forException(e != null ? e : new Exception("Apple auth failed"));
                    }
                    AuthResult result = authTask.getResult();
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        return Tasks.forException(new Exception("Apple auth user is null"));
                    }

                    Task<Void> firestoreTask;
                    AdditionalUserInfo userInfo = result.getAdditionalUserInfo();
                    if (userInfo != null && userInfo.isNewUser()) {
                        String username = user.getDisplayName() != null ?
                                user.getDisplayName() : "User_" + user.getUid().substring(0, 6);
                        firestoreTask = saveUserToFirestore(user, username, "apple");
                    } else {
                        firestoreTask = firestore.collection("users").document(user.getUid())
                                .update("lastLogin", System.currentTimeMillis());
                    }

                    return firestoreTask.continueWithTask(t -> {
                        if (t.isSuccessful()) {
                            return Tasks.forResult(result);
                        } else {
                            Exception fe = t.getException();
                            return Tasks.forException(fe != null ? fe : new Exception("Firestore update failed"));
                        }
                    });
                });
    }

    // ============ FIRESTORE USER STORAGE ============

    private static Task<Void> saveUserToFirestore(FirebaseUser user, String username, String provider) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("username", username);
        userData.put("email", user.getEmail());
        userData.put("photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        userData.put("createdAt", System.currentTimeMillis());
        userData.put("provider", provider);
        userData.put("lastLogin", System.currentTimeMillis());
        userData.put("isActive", true);
        return firestore.collection("users").document(user.getUid()).set(userData);
    }

    public static Task<Void> updateLastLogin(String uid) {
        return firestore.collection("users").document(uid)
                .update("lastLogin", System.currentTimeMillis());
    }

    public static Task<Map<String, Object>> getUserData(String uid) {
        return firestore.collection("users").document(uid).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().getData();
                    }
                    return new HashMap<>();
                });
    }

    // ============ SIGN OUT ============

    public static void signOut(Activity activity) {
        auth.signOut();
        if (activity != null) {
            GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
        }
    }

    // ============ PASSWORD RESET ============

    public static Task<Void> sendPasswordResetEmail(String email) {
        return auth.sendPasswordResetEmail(email);
    }
}