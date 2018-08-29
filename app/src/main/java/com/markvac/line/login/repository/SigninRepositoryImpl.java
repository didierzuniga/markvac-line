package com.markvac.line.login.repository;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.markvac.line.login.interactor.SigninInteractor;
import com.markvac.line.login.presenter.SigninPresenter;
import com.markvac.line.models.User;

/**
 * Created by unicorn on 8/28/2018.
 */

public class SigninRepositoryImpl implements SigninRepository {

    boolean thereUsername = false; // Si no existe el número de cedula, enviar mensaje

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceUsers = database.getReference("users");
    private SigninPresenter presenter;
    private SigninInteractor interactor;

    public SigninRepositoryImpl(SigninPresenter presenter, SigninInteractor interactor) {
        this.presenter = presenter;
        this.interactor = interactor;
    }

    @Override
    public void signin(final String username, final String password, final Activity activity, final FirebaseAuth firebaseAuth) {
        referenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if ((snapshot.getKey()).equals(username)){
                        thereUsername = true;
                        User user = snapshot.getValue(User.class);
                        final String email = user.getEmail();
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if (user != null) {
                                                presenter.signinSuccess(username, email, user.getUid());
                                            }
                                        } else {
                                            presenter.signinError(task.getException().getMessage());
                                        }
                                    }
                                });
                        break;
                    }

                    if (!thereUsername){
                        presenter.dniNotExist();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
