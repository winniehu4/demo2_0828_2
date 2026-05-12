package com.tp.demo2;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.reward.RewardAdListener;
import com.tradplus.ads.open.reward.TPReward;

public class RewardedAdActivity extends AppCompatActivity {
    private TPReward tpReward;
    private static final String LOG= "myLog";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG, "========== RewardedAdActivity 已启动 ==========");
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
        Log.v(LOG, " ========== 广告对象已创建 ==========");
        tpReward.setAdListener(new RewardAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdLoaded【广告源："+ tpAdInfo.adSourceName + "，ecpm：" + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "'中介组id："+ tpAdInfo.segmentId + "】");

                toast("Rewarded loaded");
            }

            @Override
            public void onAdFailed(TPAdError error) {
                toast("Rewarded load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdImpression【广告源："+ tpAdInfo.adSourceName +  "，tp.ecpm: "  + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format +  "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                Log.v(LOG, "onAdImpression【广告源ID："+ tpAdInfo.adSourceId+ "】");
                toast("Rewarded impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdClicked【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                toast("Rewarded clicked");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {


                Log.v(LOG, "onAdClosed【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
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
        tpReward.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/RewardEveryLayer", "激励视频"));
    }

    private void checkAdFill() {
        // 替换成你的激励视频对象：tpRewardedAd
        if (tpReward != null && tpReward.isReady()) {
            toast("激励视频有填充，可以展示");
        } else {
            toast("激励视频无填充/未加载完成");
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
