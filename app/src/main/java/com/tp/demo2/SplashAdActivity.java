package com.tp.demo2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.ads.open.splash.SplashAdListener;
import com.tradplus.ads.open.splash.TPSplash;

public class SplashAdActivity extends AppCompatActivity {
    public static final String EXTRA_MANUAL_DEMO = "extra_manual_demo";

    private TPSplash tpSplash;
    private FrameLayout adContainer;
    private static final String LOG = "myLog";
    private static final String TAG = "TradPlusDemo";
    private static final String TRADPLUS_APP_ID = "0513C4B3D2C5B3F8EB5CF572B79DF811";
    private static final long COLD_START_LOAD_TIMEOUT_MS = 15_000;

    private boolean manualDemoMode;
    private boolean navigatedToMain;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable coldStartTimeoutRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG, "========== SplashAdActivity 已启动 ==========");

        manualDemoMode = getIntent().getBooleanExtra(EXTRA_MANUAL_DEMO, false);

        setContentView(R.layout.activity_splash_ad);

        adContainer = findViewById(R.id.ad_container);
        Button btnLoad = findViewById(R.id.btn_load);
        Button btnCheck = findViewById(R.id.btn_check);
        Button btnShow = findViewById(R.id.btn_show);
        View tvTitle = findViewById(R.id.tv_splash_title);
        LinearLayout buttonRow = findViewById(R.id.layout_splash_buttons);

        initSplash();

        if (manualDemoMode) {
            tvTitle.setVisibility(View.VISIBLE);
            buttonRow.setVisibility(View.VISIBLE);
            btnLoad.setOnClickListener(v -> tpSplash.loadAd(null));
            btnCheck.setOnClickListener(v -> checkAdFill());
            btnShow.setOnClickListener(v -> showSplash());
        } else {
            tvTitle.setVisibility(View.GONE);
            buttonRow.setVisibility(View.GONE);
//            startColdStartSplash();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (manualDemoMode) {
                    finish();
                } else {
                    goMainAndFinishColdStart();
                }
            }
        });
    }
   //冷启动代码：
  /*  private void startColdStartSplash() {
        coldStartTimeoutRunnable = () -> {
            if (!manualDemoMode && !navigatedToMain) {
                Log.w(LOG, "Cold start splash: load timeout, entering main");
                goMainAndFinishColdStart();
            }
        };
        mainHandler.postDelayed(coldStartTimeoutRunnable, COLD_START_LOAD_TIMEOUT_MS);

        try {
            TradPlusSdk.setTradPlusInitListener(new TradPlusSdk.TradPlusInitListener() {
                @Override
                public void onInitSuccess() {
                    Log.d(TAG, "TradPlus SDK init success (cold start)");
                    runOnUiThread(() -> {
                        if (isFinishing() || navigatedToMain || manualDemoMode || tpSplash == null) {
                            return;
                        }
                        tpSplash.loadAd(null);
                    });
                }
            });
            TradPlusSdk.initSdk(this, TRADPLUS_APP_ID);
        } catch (Throwable t) {
            Log.e(TAG, "TradPlus SDK init failed (cold start)", t);
            Toast.makeText(this, "TradPlus init failed: " + t.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
            goMainAndFinishColdStart();
        }
    }*/

    private void initSplash() {
        tpSplash = new TPSplash(SplashAdActivity.this, AdIds.SPLASH_AD_UNIT_ID);
        Log.v(LOG, " ========== 广告对象已创建 ==========");
        tpSplash.setAdListener(new SplashAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.v(LOG, "onAdLoaded【广告源：" + tpAdInfo.adSourceName + "，ecpm：" + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "'中介组id：" + tpAdInfo.segmentId + "】");

                if (manualDemoMode) {
                    toast("Splash loaded");
                } else {
                    showSplash();
                }
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                if (manualDemoMode) {
                    toast("Splash load failed: " + error.getErrorMsg());
                } else {
                    //Log.w(LOG, "Cold start splash load failed: " + error.getErrorMsg());
//                    goMainAndFinishColdStart();
                }
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdImpression【广告源：" + tpAdInfo.adSourceName + "，tp.ecpm: " + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                Log.v(LOG, "onAdImpression【广告源ID：" + tpAdInfo.adSourceId + "】");

                if (manualDemoMode) {
                    toast("Splash impression");
                }
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClicked【广告源：" + tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                if (manualDemoMode) {
                    toast("Splash clicked");
                }
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClosed【广告源：" + tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");

                if (manualDemoMode) {
                    toast("Splash closed");
                }
                adContainer.removeAllViews();
                if (!manualDemoMode) {
                    goMainAndFinishColdStart();
                }
            }

            @Override
            public void onAdShowFailed(TPAdInfo tpAdInfo, TPAdError error) {
                if (manualDemoMode) {
                    toast("Splash show failed: " + error.getErrorMsg());
                } else {
                    Log.w(LOG, "Cold start splash show failed: " + error.getErrorMsg());
                    goMainAndFinishColdStart();
                }
            }
        });
        tpSplash.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/SplashEveryLayer", "开屏"));
    }

    private void goMainAndFinishColdStart() {
        if (manualDemoMode || navigatedToMain) {
            return;
        }
        navigatedToMain = true;
        if (coldStartTimeoutRunnable != null) {
            mainHandler.removeCallbacks(coldStartTimeoutRunnable);
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void checkAdFill() {
        if (tpSplash != null && tpSplash.isReady()) {
            toast("开屏广告有填充，可以展示");
        } else {
            toast("开屏广告无填充/未加载完成");
        }
    }

    private void showSplash() {
        if (tpSplash.isReady()) {
            tpSplash.showAd(adContainer);
        } else if (manualDemoMode) {
            toast("Splash not ready");
        } else {
            goMainAndFinishColdStart();
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (coldStartTimeoutRunnable != null) {
            mainHandler.removeCallbacks(coldStartTimeoutRunnable);
        }
        super.onDestroy();
        if (tpSplash != null) {
            tpSplash.onDestroy();
        }
    }
}
