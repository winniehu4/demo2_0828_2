package com.tp.demo2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.reward.RewardAdListener;
import com.tradplus.ads.open.reward.TPReward;

public class RewardedAdActivity extends AppCompatActivity {
    private static final String EVERY_LAYER_SUBTAG = "Reward";
    private static final String AD_TYPE_LABEL = "激励视频";

    private TPReward tpReward;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded_ad);

        Button btnLoad = findViewById(R.id.btn_load);
        Button btnShow = findViewById(R.id.btn_show);
        Button btnCheck = findViewById(R.id.btn_check);


        initRewarded();
        btnLoad.setOnClickListener(v -> tpReward.loadAd());
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showRewarded());
    }

    private void initRewarded() {
        tpReward = new TPReward(RewardedAdActivity.this, AdIds.REWARDED_AD_UNIT_ID);
        tpReward.setAdListener(new RewardAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                toast("Rewarded loaded");
            }

            @Override
            public void onAdFailed(TPAdError error) {
                toast("Rewarded load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                toast("Rewarded impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                toast("Rewarded clicked");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                toast("Rewarded closed");
            }

            @Override
            public void onAdReward(TPAdInfo tpAdInfo) {
                toast("Reward callback received");
            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {
                // Optional callback in newer SDK versions.
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                // Optional callback in newer SDK versions.
            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError error) {
                toast("Rewarded video error: " + error.getErrorMsg());
            }
        });
        tpReward.setAllAdLoadListener(createEveryLayerLoadListener());
    }

    private LoadAdEveryLayerListener createEveryLayerLoadListener() {
        return new LoadAdEveryLayerListener() {
            @Override
            public void onAdAllLoaded(boolean isSuccess) {
                String msg = "[" + AD_TYPE_LABEL + "] 瀑布流结束，是否有可用广告: " + isSuccess;
                DemoAdLog.tradpluslog(EVERY_LAYER_SUBTAG, msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void oneLayerLoadFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                String err = tpAdError != null ? tpAdError.getErrorMsg() : "null";
                DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] oneLayerLoadFailed err=" + err + " adInfo=" + tpAdInfo);
            }

            @Override
            public void oneLayerLoaded(TPAdInfo tpAdInfo) {
                DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] oneLayerLoaded adInfo=" + tpAdInfo);
            }

            @Override
            public void onAdStartLoad(String adUnitId) {
                DemoAdLog.tradpluslog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] onAdStartLoad adUnitId=" + adUnitId);
            }

            @Override
            public void oneLayerLoadStart(TPAdInfo tpAdInfo) {
                DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] oneLayerLoadStart adInfo=" + tpAdInfo);
            }

            @Override
            public void onBiddingStart(TPAdInfo tpAdInfo) {
                DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] onBiddingStart adInfo=" + tpAdInfo);
            }

            @Override
            public void onBiddingEnd(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                if (tpAdError != null) {
                    DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] onBiddingEnd failed err=" + tpAdError.getErrorMsg()
                            + " adInfo=" + tpAdInfo);
                } else {
                    DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] onBiddingEnd success adInfo=" + tpAdInfo);
                }
            }

            @Override
            public void onAdIsLoading(String adUnitId) {
                DemoAdLog.tradpluslog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] onAdIsLoading adUnitId=" + adUnitId);
            }
        };
    }

    private void checkAdFill() {
        // 替换成你的激励视频对象：tpRewardedAd
        if (tpReward != null && tpReward.isReady()) {
            toast("✅ 激励视频有填充，可以展示");
        } else {
            toast("❌ 激励视频无填充/未加载完成");
        }
    }



    private void showRewarded() {
        if (tpReward.isReady()) {
            tpReward.showAd(RewardedAdActivity.this, null);
        } else {
            toast("Rewarded not ready");
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tpReward != null) {
            tpReward.onDestroy();
        }
    }
}
