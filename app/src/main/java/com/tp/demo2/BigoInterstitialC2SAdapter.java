package com.tp.demo2;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tradplus.ads.base.GlobalTradPlus;
import com.tradplus.ads.base.adapter.TPBaseAdapter;
import com.tradplus.ads.base.adapter.interstitial.TPInterstitialAdapter;
import com.tradplus.ads.base.common.TPError;

import java.util.HashMap;
import java.util.Map;

import sg.bigo.ads.api.AdBid;
import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdInteractionListener;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.InterstitialAd;
import sg.bigo.ads.api.InterstitialAdLoader;
import sg.bigo.ads.api.InterstitialAdRequest;

import android.app.Activity;
/**
 * Bigo 插屏 C2SBidding 自定义Adapter
 */
public class BigoInterstitialC2SAdapter extends TPInterstitialAdapter {

    private static final String TAG = "BigoC2S";
    private TPBaseAdapter.OnC2STokenListener mOnC2STokenListener;
    private InterstitialAd mInterstitialAd;
    private String mPlacementId;
    // 标记：是否已完成C2S竞价
    private boolean isBiddingSuccess = false;


    @Override
    public void getC2SBidding(Context context,
                              Map<String, Object> localParams,
                              Map<String, String> tpParams,
                              TPBaseAdapter.OnC2STokenListener onC2STokenListener) {
        Log.v(TAG,"进入了getC2SBidding()");
        this.mOnC2STokenListener = onC2STokenListener;



        loadCustomAd(context, localParams, tpParams);
    }


    @Override
    public void loadCustomAd(Context context, Map<String, Object> localParams, Map<String, String> tpParams) {
        Log.v(TAG,"进入了BIGO C2SloadCustomAd()");

        // ============== 1. 读取本地参数 ==============
        boolean isTestMode = false;
        String localPlacementId = "";
        if (localParams != null) {
            // 判断key是否存在再取值
            if (localParams.containsKey("is_test_mode")) {
                isTestMode = (Boolean) localParams.get("is_test_mode");
            }
            if (localParams.containsKey("local_placement_id")) {
                localPlacementId = (String) localParams.get("local_placement_id");
            }
            Log.v(TAG,"本地参数：isTestMode="+isTestMode+"，广告位ID="+localPlacementId);
        }

        // ============== 2. 获取广告位ID ==============
        if (!TextUtils.isEmpty(localPlacementId)) {
            mPlacementId = localPlacementId;
            Log.v(TAG,"使用本地参数广告位ID");
        } else if (tpParams != null && tpParams.containsKey("placementId")) {
            mPlacementId = tpParams.get("placementId");
            Log.v(TAG,"使用TP后台广告位ID");
        }

        // ============== 3. 广告位为空直接失败 ==============
        if (TextUtils.isEmpty(mPlacementId)) {
            Log.v(TAG, "placementId 缺失");
            if (mOnC2STokenListener != null) {
                mOnC2STokenListener.onC2SBiddingFailed("", "placementId 缺失");
            }
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoadFailed(new TPError(TPError.ADAPTER_CONFIGURATION_ERROR));
            }
            return;
        }

        // ============== 4. 竞价成功 ==============
        if (isBiddingSuccess && mInterstitialAd != null) {
            setAdInteractionListener();
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoaded(null);
            }
            return;
        }

        // ============== 5. 发起广告请求==============
        requestBigoAdForBidding(context);
    }

    // ======================== 请求广告+竞价 ========================
    private void requestBigoAdForBidding(Context context) {
        Log.v(TAG,"进入了requestBigoAdForBidding(Context context)，发起Bigo广告请求");

        InterstitialAdRequest request = new InterstitialAdRequest.Builder()
                .withSlotId(mPlacementId)
                .build();

        InterstitialAdLoader loader = new InterstitialAdLoader.Builder()
                .withAdLoadListener(new AdLoadListener<InterstitialAd>() {
                    @Override
                    public void onError(@NonNull AdError error) {
                        Log.v(TAG, "Bigo请求失败："+error.getMessage());
                        if (mOnC2STokenListener != null) {
                            mOnC2STokenListener.onC2SBiddingFailed("9999", "广告加载失败");
                        }
                        if (mLoadAdapterListener != null) {
                            TPError tpError = new TPError(TPError.NO_FILL);
                            tpError.setErrorCode(String.valueOf(error.getCode()));
                            tpError.setErrorMessage(error.getMessage());
                            mLoadAdapterListener.loadAdapterLoadFailed(tpError);
                        }
                    }
                    //onAdLoaded() 回调里一起返回广告和 ECPM
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        mInterstitialAd = ad;//拿到广告
                        AdBid adBid = ad.getBid();//直接从这个广告对象里面取出竞价信息，包括ecpm

                        if (adBid == null || adBid.getPrice() <= 0) {
                            Log.v(TAG, "ECPM无效");
                            if (mOnC2STokenListener != null) {
                                mOnC2STokenListener.onC2SBiddingFailed("", "ECPM 无效");
                            }
                            return;
                        }

                        // 竞价成功，上报价格
                        Log.v(TAG, "竞价成功 ECPM："+adBid.getPrice());
                        Map<String, Object> ecpmMap = new HashMap<>();
                        ecpmMap.put("ecpm", 100);//如果三方广告平台返回0价，则在adBid.getPrice()这里修改为一个固定的价格

                        Log.v(TAG,
                                "上报给TP的ECPM="
                                        + adBid.getPrice());


                        if (mOnC2STokenListener != null) {
                            mOnC2STokenListener.onC2SBiddingResult(ecpmMap);//传给TP
                        }

                        // 标记竞价成功
                        isBiddingSuccess = true;

                        // 竞价成功后，重新调用loadCustomAd回调加载成功
                        loadCustomAd(context, null, null);
                    }
                })
                .build();

        loader.loadAd(request);
    }

    // ======================== 广告交互监听 ========================
    private void setAdInteractionListener() {
        Log.v(TAG,"setAdInteractionListener()");
        if (mInterstitialAd == null) return;

        mInterstitialAd.setAdInteractionListener(new AdInteractionListener() {
            @Override
            public void onAdError(@NonNull AdError error) {
                if (mShowListener != null) {
                    TPError tpError = new TPError(TPError.SHOW_FAILED);
                    tpError.setErrorCode(String.valueOf(error.getCode()));
                    tpError.setErrorMessage(error.getMessage());
                    mShowListener.onAdVideoError(tpError);
                }
            }

            @Override
            public void onAdImpression() {
                if (mShowListener != null) {
                    mShowListener.onAdShown();
                }
            }

            @Override
            public void onAdClicked() {
                if (mShowListener != null) {
                    mShowListener.onAdClicked();
                }
            }

            @Override
            public void onAdOpened() {}

            @Override
            public void onAdClosed() {
                if (mShowListener != null) {
                    mShowListener.onAdClosed();
                }
            }
        });
    }

    // ======================== 广告展示与状态检查 ========================
    // ======================== 广告展示与状态检查 ========================
    @Override
    public void showAd() {
        Log.v(TAG,"进入了showAd()");
        if (mShowListener != null) {
            Activity activity = GlobalTradPlus.getInstance().getActivity();
            if (activity == null || mInterstitialAd == null) {
                mShowListener.onAdVideoError(new TPError("Didn't find valid adv.Show Failed"));
                return;
            }

            mInterstitialAd.show(activity);
        }
    }

    @Override
    public boolean isReady() {
        Log.v(TAG,"进入了isReady()");
        return mInterstitialAd != null && !mInterstitialAd.isExpired();
    }

    // ======================== 竞价失败通知 ========================
    @Override
    public void setLossNotifications(String auctionPrice, String auctionPriceCny, String lossReason) {
        Log.v(TAG,"进入了setLossNotifications()");
        super.setLossNotifications(auctionPrice, auctionPriceCny, lossReason);
        Log.v(TAG, "C2S竞价失败，胜出价格：" + auctionPrice + "，原因：" + lossReason);
    }

    // ======================== 资源释放========================
    @Override
    public void clean() {
        Log.v(TAG,"进入了clean()");
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }
        isBiddingSuccess = false;
        super.clean();
    }

    // ======================== 网络信息 ========================
    @Override
    public String getNetworkName() {
        return "BigoC2S";
    }

    @Override
    public String getNetworkVersion() {
        return "5.8.0";
    }
}