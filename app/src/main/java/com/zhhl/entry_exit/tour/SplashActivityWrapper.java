package com.zhhl.entry_exit.tour;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.xdja.uaac.api.TokenCallback;
import com.xdja.uaac.api.UaacApi;

public abstract class SplashActivityWrapper extends AppCompatActivity {

    public abstract void login(String token);

    public abstract void uaacApiError(String error);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UaacApi.getToken(this, new AppTokenCallback());
    }


    class AppTokenCallback implements TokenCallback {

        @Override
        public void onSuccess(String s, boolean b) {
            if (b) {
                login(s);
            } else {
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UaacApi.getToken(SplashActivityWrapper.this, AppTokenCallback.this);
                    }
                }, 1000);
            }
        }

        @Override
        public void onError(String s) {
            uaacApiError(s);
            if (s.equals("权限被拒绝")) return;
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UaacApi.getToken(SplashActivityWrapper.this, AppTokenCallback.this);
                }
            }, 1000);
        }
    }
}
