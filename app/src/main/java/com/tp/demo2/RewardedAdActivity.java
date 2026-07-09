package com.tp.demo2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.ads.open.reward.RewardAdListener;
import com.tradplus.ads.open.reward.TPReward;

import java.util.HashMap;
import java.util.Map;

public class RewardedAdActivity extends AppCompatActivity implements View.OnClickListener {

    private TPReward tpReward;

    private static final String LOG = "myLog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG, "========== RewardedAdActivity 已启动 ==========");

        setContentView(R.layout.activity_rewarded_ad);

        disableAutoLoadForInterstitialAd();

        // 进入页面立即创建广告对象
        createRewardObjectIfNeed();

        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_check).setOnClickListener(this);
        findViewById(R.id.btn_change).setOnClickListener(this);
    }

    private void disableAutoLoadForInterstitialAd() {

        Map<String, Object> settingParam = new HashMap<>();

        String[] unitIds = {AdIds.REWARDED_AD_UNIT_ID};

        settingParam.put("autoload_close", unitIds);

        TradPlusSdk.setSettingDataParam(settingParam);

        Log.v(LOG, "========== 已为广告位关闭自动加载 ==========");
    }

    private void createRewardObjectIfNeed() {

        if (tpReward != null) {
            return;
        }

        tpReward = new TPReward(
                RewardedAdActivity.this,
                AdIds.REWARDED_AD_UNIT_ID);

        Log.v(LOG, "========== 广告对象已创建 ==========");

        tpReward.setAdListener(new RewardAdListener() {

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {

                Log.v(LOG,
                        "onAdLoaded【广告源："
                                + tpAdInfo.adSourceName
                                + "，广告源id："
                                + tpAdInfo.adSourceId
                                + "，广告类型："
                                + tpAdInfo.format
                                + "，广告位ID："
                                + tpAdInfo.tpAdUnitId
                                + "，中介组id："
                                + tpAdInfo.segmentId
                                + "】");

                toast("Rewarded loaded");
            }

            @Override
            public void onAdFailed(TPAdError error) {
                toast("Rewarded load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {

                Log.v(LOG,
                        "onAdImpression【广告源："
                                + tpAdInfo.adSourceName
                                + "，广告源ID："
                                + tpAdInfo.adSourceId
                                + "，广告类型："
                                + tpAdInfo.format
                                + "，tpAdUnitId："
                                + tpAdInfo.tpAdUnitId
                                + "，中介组id："
                                + tpAdInfo.segmentId
                                + "，true_adunit_id："
                                + tpAdInfo.true_adunit_id
                                + "，ecpm:"
                                + tpAdInfo.ecpm
                                +"】");

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
                Log.v(LOG,"onAdReward");
                toast("Reward callback received");
            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {

            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {

            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError error) {
                toast("Rewarded video error: " + error.getErrorMsg());
            }
        });

        tpReward.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(
                        this,
                        "TPDemo/RewardEveryLayer",
                        "激励视频"));
    }

    /**
     * Load按钮
     * Map1
     */
    private void initRewarded() {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", "123");
        params.put("custom_data", "abc");

        tpReward.setCustomParams(params);

        tpReward.loadAd();
    }

    private void change() {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", "999");
        params.put("custom_data", "xyz");

        tpReward.setCustomParams(params);

        tpReward.loadAd();

        if (tpReward.isReady()) {

            tpReward.showAd(
                    RewardedAdActivity.this,
                    null);

        } else {

            toast("广告未Ready，仅执行Load");
        }
    }

    private void checkAdFill() {

        if (tpReward != null && tpReward.isReady()) {

            toast("激励视频有填充，可以展示");

        } else {

            toast("激励视频无填充/未加载完成");
        }
    }

    private void showRewarded() {

        if (tpReward != null && tpReward.isReady()) {

            tpReward.showAd(
                    RewardedAdActivity.this,
                    null);

        } else {

            toast("Rewarded not ready");
        }
        tpReward.onDestroy();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.btn_load) {

            initRewarded();

        } else if (id == R.id.btn_check) {

            checkAdFill();

        } else if (id == R.id.btn_show) {

            showRewarded();

        } else if (id == R.id.btn_change) {

            change();
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

  /*  @Override
    protected void onDestroy() {

        super.onDestroy();

        if (tpReward != null) {
            tpReward.onDestroy();
        }
    }*/
}