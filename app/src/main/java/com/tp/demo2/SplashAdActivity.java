package com.tp.demo2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.splash.SplashAdListener;
import com.tradplus.ads.open.splash.TPSplash;

public class SplashAdActivity extends AppCompatActivity {
    private TPSplash tpSplash;
    private FrameLayout adContainer;
    private boolean isHotStartSplash = true;      // 热启动开关
    private boolean hasShownHotStartAd = false;   // 本次热启动是否已展示过广告
    private boolean isLoading = false;            // 是否正在加载广告
    private boolean isAdShowing = false;           // 当前是否有广告正在展示
    private long lastShowTime = 0;                // 防止短时间内多次展示（SDK内部也可能连续触发）
    private static final String LOG = "myLog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG, "========== SplashAdActivity 已启动 ==========");
        setContentView(R.layout.activity_splash_ad);

        adContainer = findViewById(R.id.ad_container);
        Button btnLoad = findViewById(R.id.btn_load);
        Button btnCheck = findViewById(R.id.btn_check);
        Button btnShow = findViewById(R.id.btn_show);

        initSplash();

        btnLoad.setOnClickListener(v -> {
            loadSplashAd();
            toast("开始加载开屏广告");
        });
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showSplash());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isHotStartSplash
                && !hasShownHotStartAd
                && tpSplash != null
                && tpSplash.isReady()
                && !isAdShowing) {
            Log.v(LOG, "热启动：广告已就绪，展示 1 次");
            hasShownHotStartAd = true;
            showSplash();
        } else {
            Log.v(LOG, "热启动：不展示广告（已展示过 / 无广告 / 正在展示 / 开关关闭）");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hasShownHotStartAd = false;
        if (isHotStartSplash
                && tpSplash != null
                && !tpSplash.isReady()
                && !isLoading
                && !isAdShowing) {
            Log.v(LOG, "用户离开 APP，开始预加载开屏广告");
            loadSplashAd();
        }
    }

    private void initSplash() {
        tpSplash = new TPSplash(SplashAdActivity.this, AdIds.SPLASH_AD_UNIT_ID);
        Log.v(LOG, "========== 广告对象已创建 ==========");
        tpSplash.setAdListener(new SplashAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                isLoading = false;
                Log.v(LOG, "onAdLoaded【广告源：" + tpAdInfo.adSourceName + "，ecpm：" + tpAdInfo.ecpm +
                        "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId +
                        "，中介组id：" + tpAdInfo.segmentId + "】");
                toast("开屏广告加载成功");
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                isLoading = false;
                Log.e(LOG, "onAdLoadFailed: " + error.getErrorMsg());
                toast("开屏广告加载失败：" + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdImpression【广告源：" + tpAdInfo.adSourceName +
                        "，ecpm：" + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format +
                        "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                toast("开屏广告展示");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClicked【广告源：" + tpAdInfo.adSourceName +
                        "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                toast("开屏广告点击");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClosed【广告源：" + tpAdInfo.adSourceName +
                        "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                toast("开屏广告关闭");
                isAdShowing = false;
                adContainer.removeAllViews();

                // 🔥 关键修改：广告关闭后立刻销毁对象，丢弃队列中剩余广告
                // 然后重建并预加载下一个，保证下次展示只有 1 个广告
                if (tpSplash != null) {
                    tpSplash.onDestroy();
                }
                initSplash();
                // 只预加载，不自动展示，等待热启动或手动点击
                loadSplashAd();
            }

            @Override
            public void onAdShowFailed(TPAdInfo tpAdInfo, TPAdError error) {
                Log.e(LOG, "onAdShowFailed: " + error.getErrorMsg());
                toast("开屏广告展示失败：" + error.getErrorMsg());
                isAdShowing = false;
            }
        });
        tpSplash.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/SplashEveryLayer", "开屏"));
    }

    private void loadSplashAd() {
        if (tpSplash == null || isLoading) {
            Log.v(LOG, "加载被跳过（对象为空或正在加载中）");
            return;
        }
        isLoading = true;
        tpSplash.loadAd(null);
        Log.v(LOG, "开始请求广告");
    }

    private void checkAdFill() {
        if (tpSplash != null && tpSplash.isReady()) {
            toast("开屏广告有填充，可以展示");
            Log.v(LOG, "广告已就绪，isReady=true");
        } else {
            toast("开屏广告无填充/未加载完成");
            Log.v(LOG, "广告未就绪");
        }
    }

    private void showSplash() {
        if (tpSplash == null) {
            toast("开屏对象为空");
            return;
        }
        if (!tpSplash.isReady()) {
            toast("开屏广告未加载完成，请稍后");
            Log.v(LOG, "showSplash失败：isReady=false");
            return;
        }
        if (adContainer == null) {
            toast("广告容器不存在");
            Log.e(LOG, "adContainer 为 null");
            return;
        }
        if (isAdShowing) {
            Log.v(LOG, "已有广告正在展示，忽略本次展示请求");
            return;
        }
        // 防抖：短时间内的重复调用忽略（针对 SDK 内部可能的连续回调）
        if (System.currentTimeMillis() - lastShowTime < 2000) {
            Log.v(LOG, "展示间隔过短，忽略重复请求");
            return;
        }

        Log.v(LOG, "准备展示开屏广告，isReady=true");
        isAdShowing = true;
        lastShowTime = System.currentTimeMillis();
        adContainer.removeAllViews();
        tpSplash.showAd(adContainer);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tpSplash != null) {
            tpSplash.onDestroy();
        }
    }
}