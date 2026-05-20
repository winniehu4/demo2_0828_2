package com.tp.demo2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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

    private static final String TRADPLUS_APP_ID = "0513C4B3D2C5B3F8EB5CF572B79DF811";

    private TPSplash tpSplash;
    private FrameLayout adContainer;
    private View splashTitleView;
    private View splashButtonsLayout;
    private boolean isHotStartSplash = true;      // 热启动开关
    private boolean hasShownHotStartAd = false;   // 本次从 Splash 退后台再回到前台周期内是否已自动展示过热启动广告
    private boolean isLoading = false;            // 是否正在加载广告
    private boolean isAdShowing = false;          // 当前是否有热启动位广告正在展示
    private long lastShowTime = 0;                // 防止短时间内多次展示

    private static final String LOG = "myLog";

    // ---------- 冷启动（进程被杀死后 static 重置） ----------
    private TPSplash tpSplashCold;
    private boolean isColdSplashLoading;
    private boolean isColdSplashShowing;
    private long lastColdShowTime;
    private static boolean sColdSplashFlowFinishedForProcess;

    /** 本次 Activity 是否为「桌面冷启动」流程（MAIN + LAUNCHER 且尚未完成冷开屏） */
    private boolean mColdAppEntry;

    private boolean mSplashAdsInited;
    private boolean mScheduledInitFallback;

    /** 供内部判断本进程是否还需要走冷启动开屏 */
    public static boolean shouldShowAppColdSplash() {
        return !sColdSplashFlowFinishedForProcess;
    }

    private static boolean isLauncherIntent(@Nullable Intent intent) {
        return intent != null
                && Intent.ACTION_MAIN.equals(intent.getAction())
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG, "========== SplashAdActivity 已启动 ==========");
        mColdAppEntry = shouldShowAppColdSplash() && isLauncherIntent(getIntent());

        setContentView(R.layout.activity_splash_ad);

        adContainer = findViewById(R.id.ad_container);
        splashTitleView = findViewById(R.id.tv_splash_title);
        splashButtonsLayout = findViewById(R.id.layout_splash_buttons);
        Button btnLoad = findViewById(R.id.btn_load);
        Button btnCheck = findViewById(R.id.btn_check);
        Button btnShow = findViewById(R.id.btn_show);

        if (mColdAppEntry) {
            applySplashDemoChromeVisible(false);
        }

        initTradPlusInSplash();

        if (mColdAppEntry) {
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (!sColdSplashFlowFinishedForProcess) {
                        skipColdSplashAndGoMain();
                    } else {
                        setEnabled(false);
                        finish();
                    }
                }
            });
        }

        btnLoad.setOnClickListener(v -> {
            loadSplashAd();
            toast("开始加载开屏广告");
        });
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showSplash());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mSplashAdsInited && !mScheduledInitFallback) {
            mScheduledInitFallback = true;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!isFinishing() && !isDestroyed() && !mSplashAdsInited) {
                    Log.w(LOG, "Splash 广告初始化兜底（例如 TradPlus 已在 Main 初始化且未再次回调 onInitSuccess）");
                    ensureSplashAdsInitedAfterTradPlusReady();
                }
            }, 250);
        }
    }

    private void initTradPlusInSplash() {
        try {
            TradPlusSdk.setTradPlusInitListener(new TradPlusSdk.TradPlusInitListener() {
                @Override
                public void onInitSuccess() {
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        ensureSplashAdsInitedAfterTradPlusReady();
                    });
                }
            });
            //TradPlusSdk.initSdk(this, TRADPLUS_APP_ID);
        } catch (Throwable t) {
            Log.e(LOG, "TradPlus init failed in Splash", t);
            toast("TradPlus init failed: " + t.getClass().getSimpleName());
        }
    }

    /**
     * 在 TradPlus 初始化完成后再创建广告对象（冷启动时避免先出现 Demo 再出广告）。
     */
    private void ensureSplashAdsInitedAfterTradPlusReady() {
        if (mSplashAdsInited) {
            return;
        }
        mSplashAdsInited = true;

        initSplash();

        if (mColdAppEntry && shouldShowAppColdSplash()) {
            initSplashCold();
            loadColdSplashAd();
        }
    }

    private void applySplashDemoChromeVisible(boolean visible) {
        int v = visible ? View.VISIBLE : View.GONE;
        if (splashTitleView != null) {
            splashTitleView.setVisibility(v);
        }
        if (splashButtonsLayout != null) {
            splashButtonsLayout.setVisibility(v);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!sColdSplashFlowFinishedForProcess && mColdAppEntry) {
            tryShowColdSplashWhenReady();
            return;
        }

        // 热启动展示逻辑（原有逻辑，保持不变）
        if (isHotStartSplash
                && !hasShownHotStartAd
                && tpSplash != null
                && tpSplash.isReady()
                && !isAdShowing) {
            Log.v(LOG, "热启动：广告已就绪，展示 1 次");
            hasShownHotStartAd = true;
            applySplashDemoChromeVisible(false);
            showSplash();
        } else {
            Log.v(LOG, "热启动：不展示广告（已展示过 / 无广告 / 正在展示 / 开关关闭）");
        }
    }

    /**
     * 仅在用户从本页将应用送入后台（Home / 多任务）时预加载热启动广告；
     * 跳转到 Main 等其它 Activity 不会触发（不经过 onUserLeaveHint）。
     */
    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (!sColdSplashFlowFinishedForProcess) {
            return;
        }
        hasShownHotStartAd = false;
        if (isHotStartSplash
                && tpSplash != null
                && !tpSplash.isReady()
                && !isLoading
                && !isAdShowing) {
            Log.v(LOG, "从 Splash 退后台：开始预加载热启动开屏");
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
                Log.v(LOG, "onAdLoaded【广告源：" + tpAdInfo.adSourceName +
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
                        "，广告源ID：" + tpAdInfo.adSourceId + "，广告类型：" + tpAdInfo.format +
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
                applySplashDemoChromeVisible(true);
                if (adContainer != null) {
                    adContainer.removeAllViews();
                }

                if (tpSplash != null) {
                    tpSplash.onDestroy();
                }
                initSplash();
            }

            @Override
            public void onAdShowFailed(TPAdInfo tpAdInfo, TPAdError error) {
                Log.e(LOG, "onAdShowFailed: " + error.getErrorMsg());
                toast("开屏广告展示失败：" + error.getErrorMsg());
                isAdShowing = false;
                applySplashDemoChromeVisible(true);
            }
        });
        tpSplash.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/SplashEveryLayer", "开屏"));
    }

    private void initSplashCold() {
        if (sColdSplashFlowFinishedForProcess || tpSplashCold != null) {
            return;
        }
        tpSplashCold = new TPSplash(SplashAdActivity.this, AdIds.SPLASH_COLD_AD_UNIT_ID);
        Log.v(LOG, "========== 冷启动开屏广告对象已创建 ==========");
        tpSplashCold.setAdListener(new SplashAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                isColdSplashLoading = false;
                Log.v(LOG, "冷启动 onAdLoaded【广告源：" + tpAdInfo.adSourceName + "，ecpm：" + tpAdInfo.ecpm +
                        "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId +
                        "，中介组id：" + tpAdInfo.segmentId + "】");
                toast("冷启动开屏加载成功");
                showColdSplash();
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                isColdSplashLoading = false;
                Log.e(LOG, "冷启动 onAdLoadFailed: " + error.getErrorMsg());
                toast("冷启动开屏加载失败：" + error.getErrorMsg());
                finishColdSplashFlowAfterFailure();
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.v(LOG, "冷启动 onAdImpression【广告源：" + tpAdInfo.adSourceName +
                        "，广告源Id" + tpAdInfo.adSourceId + "，广告类型：" + tpAdInfo.format +
                        "，tpAdUnitId：" + tpAdInfo.tpAdUnitId + "，中介组id：" + tpAdInfo.segmentId  + "，true_adunit_id:"+tpAdInfo.true_adunit_id+"】");
                toast("冷启动开屏展示");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.v(LOG, "冷启动 onAdClicked【广告源：" + tpAdInfo.adSourceName +
                        "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                toast("冷启动开屏点击");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.v(LOG, "冷启动 onAdClosed【广告源：" + tpAdInfo.adSourceName +
                        "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                toast("冷启动开屏关闭");
                isColdSplashShowing = false;
                sColdSplashFlowFinishedForProcess = true;
                if (adContainer != null) {
                    adContainer.removeAllViews();
                }
                if (tpSplashCold != null) {
                    tpSplashCold.onDestroy();
                    tpSplashCold = null;
                }
                openMainAndFinishIfColdAppEntry();
            }

            @Override
            public void onAdShowFailed(TPAdInfo tpAdInfo, TPAdError error) {
                Log.e(LOG, "冷启动 onAdShowFailed: " + error.getErrorMsg());
                toast("冷启动开屏展示失败：" + error.getErrorMsg());
                isColdSplashShowing = false;
                finishColdSplashFlowAfterFailure();
            }
        });
        tpSplashCold.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/SplashColdEveryLayer", "冷启动开屏"));
    }

    private void loadColdSplashAd() {
        if (tpSplashCold == null || isColdSplashLoading || sColdSplashFlowFinishedForProcess) {
            Log.v(LOG, "冷启动加载被跳过（对象为空 / 正在加载 / 流程已结束）");
            return;
        }
        isColdSplashLoading = true;
        tpSplashCold.loadAd(null);
        Log.v(LOG, "冷启动：开始请求广告");
    }

    private void tryShowColdSplashWhenReady() {
        if (sColdSplashFlowFinishedForProcess || tpSplashCold == null) {
            return;
        }
        if (tpSplashCold.isReady() && !isColdSplashShowing && !isColdSplashLoading) {
            Log.v(LOG, "冷启动：onStart 时广告已就绪，展示");
            showColdSplash();
        } else {
            Log.v(LOG, "冷启动：等待加载或正在加载（就绪=" + tpSplashCold.isReady() + "）");
        }
    }

    private void showColdSplash() {
        if (sColdSplashFlowFinishedForProcess || tpSplashCold == null) {
            return;
        }
        if (!tpSplashCold.isReady()) {
            Log.v(LOG, "冷启动 showColdSplash：未就绪");
            return;
        }
        if (adContainer == null) {
            Log.e(LOG, "冷启动 adContainer 为 null");
            return;
        }
        if (isColdSplashShowing) {
            Log.v(LOG, "冷启动：已有广告展示中，忽略");
            return;
        }
        if (System.currentTimeMillis() - lastColdShowTime < 2000) {
            Log.v(LOG, "冷启动：展示间隔过短，忽略");
            return;
        }
        Log.v(LOG, "冷启动：准备展示");
        isColdSplashShowing = true;
        lastColdShowTime = System.currentTimeMillis();
        adContainer.removeAllViews();
        tpSplashCold.showAd(adContainer);
    }

    private void finishColdSplashFlowAfterFailure() {
        sColdSplashFlowFinishedForProcess = true;
        if (adContainer != null) {
            adContainer.removeAllViews();
        }
        if (tpSplashCold != null) {
            tpSplashCold.onDestroy();
            tpSplashCold = null;
        }
        openMainAndFinishIfColdAppEntry();
    }

    private void openMainAndFinishIfColdAppEntry() {
        if (!mColdAppEntry) {
            return;
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void skipColdSplashAndGoMain() {
        Log.v(LOG, "用户返回：跳过冷启动开屏");
        isColdSplashLoading = false;
        isColdSplashShowing = false;
        sColdSplashFlowFinishedForProcess = true;
        applySplashDemoChromeVisible(true);
        if (adContainer != null) {
            adContainer.removeAllViews();
        }
        if (tpSplashCold != null) {
            tpSplashCold.onDestroy();
            tpSplashCold = null;
        }
        openMainAndFinishIfColdAppEntry();
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
        if (System.currentTimeMillis() - lastShowTime < 2000) {
            Log.v(LOG, "展示间隔过短，忽略重复请求");
            return;
        }

        Log.v(LOG, "准备展示开屏广告，isReady=true");
        applySplashDemoChromeVisible(false);
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
        if (tpSplashCold != null) {
            tpSplashCold.onDestroy();
            tpSplashCold = null;
        }
    }
}
