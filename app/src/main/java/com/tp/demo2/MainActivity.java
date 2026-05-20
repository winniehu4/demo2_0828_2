package com.tp.demo2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.core.GlobalImpressionManager;
import com.tradplus.ads.open.TradPlusSdk;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TradPlusDemo";
    // Replace with your real TradPlus App ID from the TradPlus dashboard.
    private static final String TRADPLUS_APP_ID = "0513C4B3D2C5B3F8EB5CF572B79DF811";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initTradPlusSdk();


        setupMenu();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initTradPlusSdk() {
        try {
            TradPlusSdk.setTradPlusInitListener(new TradPlusSdk.TradPlusInitListener() {
                @Override
                public void onInitSuccess() {
                    Log.d(TAG, "TradPlus SDK init success");


                    TradPlusSdk.setGlobalImpressionListener(new GlobalImpressionManager.GlobalImpressionListener() {
                        @Override
                        public void onImpressionSuccess(TPAdInfo tpAdInfo) {
                            Log.v(TAG, "tpAdInfo: " + tpAdInfo);
                        }
                    });





                }
            });
            TradPlusSdk.initSdk(this, TRADPLUS_APP_ID);
        } catch (Throwable t) {
            Log.e(TAG, "TradPlus SDK init failed", t);
            Toast.makeText(this, "TradPlus init failed: " + t.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupMenu() {
        Button btnBanner = findViewById(R.id.btn_banner);
        Button btnNative = findViewById(R.id.btn_native);
        Button btnInterstitial = findViewById(R.id.btn_interstitial);
        Button btnReward = findViewById(R.id.btn_reward);
        Button btnSplash = findViewById(R.id.btn_splash);

        btnBanner.setOnClickListener(v -> startActivity(new Intent(this, BannerAdActivity.class)));
        btnNative.setOnClickListener(v -> startActivity(new Intent(this, NativeAdActivity.class)));
        btnInterstitial.setOnClickListener(v -> startActivity(new Intent(this, InterstitialAdActivity.class)));
        btnReward.setOnClickListener(v -> startActivity(new Intent(this, RewardedAdActivity.class)));
        btnSplash.setOnClickListener(v -> startActivity(new Intent(this, SplashAdActivity.class)));
    }
}
