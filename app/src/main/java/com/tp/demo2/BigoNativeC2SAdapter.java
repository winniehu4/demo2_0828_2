package com.tp.demo2;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tradplus.ads.base.adapter.TPBaseAdapter;
import com.tradplus.ads.base.adapter.nativead.TPNativeAdapter;
import com.tradplus.ads.base.common.TPError;
import com.tradplus.ads.bigo.BigoNativeAd;

import java.util.HashMap;
import java.util.Map;

import sg.bigo.ads.BigoAdSdk;
import sg.bigo.ads.api.AdBid;
import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.NativeAd;
import sg.bigo.ads.api.NativeAdLoader;
import sg.bigo.ads.api.NativeAdRequest;

/**
 * Bigo 原生 C2SBidding 自定义 Adapter
 */
public class BigoNativeC2SAdapter extends TPNativeAdapter {

    private static final String TAG = "BigoNativeC2S";

    private TPBaseAdapter.OnC2STokenListener mOnC2STokenListener;
    private NativeAd mNativeAd;
    private BigoNativeAd mBigoNativeAd;
    private String mPlacementId;
    private Context mContext;
    private boolean isBiddingLoaded = false;
    private boolean mVideoMute = true;
    private boolean mNeedDownloadImg = false;

    @Override
    public void getC2SBidding(Context context,
                              Map<String, Object> localParams,
                              Map<String, String> tpParams,
                              TPBaseAdapter.OnC2STokenListener onC2STokenListener) {
        Log.v(TAG, "getC2SBidding()");
        this.mOnC2STokenListener = onC2STokenListener;
        loadCustomAd(context, localParams, tpParams);
    }

    @Override
    public void loadCustomAd(Context context, Map<String, Object> localParams, Map<String, String> tpParams) {
        Log.v(TAG, "loadCustomAd()");
        mContext = context;

        String localPlacementId = "";
        if (localParams != null && localParams.containsKey("local_placement_id")) {
            localPlacementId = (String) localParams.get("local_placement_id");
        }

        if (!TextUtils.isEmpty(localPlacementId)) {
            mPlacementId = localPlacementId;
        } else if (tpParams != null && tpParams.containsKey("placementId")) {
            mPlacementId = tpParams.get("placementId");
        }

        if (localParams != null) {
            if (localParams.containsKey("need_down_load_img")) {
                Object needDownloadImg = localParams.get("need_down_load_img");
                mNeedDownloadImg = "true".equals(String.valueOf(needDownloadImg));
            }
            if (localParams.containsKey("video_mute")) {
                Object videoMute = localParams.get("video_mute");
                if (videoMute instanceof Number) {
                    mVideoMute = ((Number) videoMute).intValue() == 1;
                }
            }
        }

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

        requestBigoNativeForBidding(context);
    }

    private void requestBigoNativeForBidding(Context context) {
        if (mOnC2STokenListener != null && isBiddingLoaded) {
            notifyNativeLoaded();
            return;
        }

        Log.v(TAG, "requestBigoNativeForBidding()");

        NativeAdRequest request = new NativeAdRequest.Builder()
                .withSlotId(mPlacementId)
                .build();

        NativeAdLoader loader = new NativeAdLoader.Builder()
                .withAdLoadListener(new AdLoadListener<NativeAd>() {
                    @Override
                    public void onError(@NonNull AdError error) {
                        Log.v(TAG, "Bigo 原生请求失败：" + error.getMessage());
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

                    @Override
                    public void onAdLoaded(@NonNull NativeAd ad) {
                        mNativeAd = ad;
                        setBigoNetworkInfo(ad.getCreativeId());

                        AdBid adBid = ad.getBid();
                        if (adBid == null || adBid.getPrice() <= 0) {
                            Log.v(TAG, "ECPM 无效");
                            if (mOnC2STokenListener != null) {
                                mOnC2STokenListener.onC2SBiddingFailed("", "ECPM 无效");
                            }
                            if (mLoadAdapterListener != null) {
                                mLoadAdapterListener.loadAdapterLoadFailed(new TPError(TPError.NO_FILL));
                            }
                            return;
                        }

                        double ecpm = adBid.getPrice();
                        Log.v(TAG, "竞价成功 ECPM：" + ecpm);

                        Map<String, Object> ecpmMap = new HashMap<>();
                        ecpmMap.put("ecpm", ecpm);

                        if (mOnC2STokenListener != null) {
                            mOnC2STokenListener.onC2SBiddingResult(ecpmMap);
                        }
                        isBiddingLoaded = true;
                    }
                })
                .build();

        loader.loadAd(request);
    }

    private void notifyNativeLoaded() {
        if (mNativeAd == null || mLoadAdapterListener == null) {
            return;
        }

        if (mNativeAd.isExpired()) {
            TPError tpError = new TPError(TPError.NO_FILL);
            tpError.setErrorMessage("NativeAd expired");
            mLoadAdapterListener.loadAdapterLoadFailed(tpError);
            return;
        }

        if (mBigoNativeAd == null) {
            mBigoNativeAd = new BigoNativeAd(mContext, mNativeAd, mVideoMute);
        }

        downloadAndCallback(mBigoNativeAd, mNeedDownloadImg);
    }

    private void setBigoNetworkInfo(String creativeId) {
        if (TextUtils.isEmpty(creativeId)) {
            return;
        }
        Map<String, Object> networkInfo = new HashMap<>();
        networkInfo.put("network_creativeId", creativeId);
        setNetworkhashMap(networkInfo);
    }

    @Override
    public boolean isReady() {
        return mNativeAd != null && !mNativeAd.isExpired();
    }

    @Override
    public void setLossNotifications(String auctionPrice, String auctionPriceCny, String lossReason) {
        super.setLossNotifications(auctionPrice, auctionPriceCny, lossReason);
        Log.v(TAG, "C2S 竞价失败，胜出价格：" + auctionPrice + "，原因：" + lossReason);
    }

    @Override
    public void clean() {
        Log.v(TAG, "clean()");
        if (mNativeAd != null) {
            mNativeAd.destroy();
            mNativeAd = null;
        }
        mBigoNativeAd = null;
        isBiddingLoaded = false;
    }

    @Override
    public String getNetworkName() {
        return "BigoC2S";
    }

    @Override
    public String getNetworkVersion() {
        return BigoAdSdk.getSDKVersionName();
    }
}
