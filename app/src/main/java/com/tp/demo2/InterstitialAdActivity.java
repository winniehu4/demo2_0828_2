package com.tp.demo2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;

public class InterstitialAdActivity extends AppCompatActivity {
    private TPInterstitial tpInterstitial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad);

        Button btnLoad = findViewById(R.id.btn_load);
        Button btnShow = findViewById(R.id.btn_show);
        Button btnCheck = findViewById(R.id.btn_check);


        initInterstitial();
        btnLoad.setOnClickListener(v -> tpInterstitial.loadAd());
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showInterstitial());
    }

    private void initInterstitial() {
        tpInterstitial = new TPInterstitial(InterstitialAdActivity.this, AdIds.INTERSTITIAL_AD_UNIT_ID);
        tpInterstitial.setAdListener(new InterstitialAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                toast("Interstitial loaded");
            }

            @Override
            public void onAdFailed(TPAdError error) {
                toast("Interstitial load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                toast("Interstitial impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                toast("Interstitial clicked");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                toast("Interstitial closed");
            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                toast("Interstitial video error: " + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {
                // Optional callback in newer SDK versions.
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                // Optional callback in newer SDK versions.
            }
        });
        tpInterstitial.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/InterstitialEveryLayer", "插屏"));
    }

    private void checkAdFill() {
        if (tpInterstitial != null && tpInterstitial.isReady()) {
            toast("✅ 插屏广告有填充，可以展示");
        } else {
            toast("❌ 插屏广告无填充/未加载完成");
        }
    }

    private void showInterstitial() {
        if (tpInterstitial.isReady()) {
            tpInterstitial.showAd(InterstitialAdActivity.this, null);
        } else {
            toast("Interstitial not ready");
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tpInterstitial != null) {
            tpInterstitial.onDestroy();
        }
    }
}
