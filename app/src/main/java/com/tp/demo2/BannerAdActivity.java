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
import com.tradplus.ads.base.util.SegmentUtils;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.banner.TPBanner;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class BannerAdActivity extends AppCompatActivity {
    private TPBanner tpBanner;
    private FrameLayout adContainer;
    private static final String LOG= "myLog";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG, "========== BannerAdActivity 已启动 ==========");

        setContentView(R.layout.activity_banner_ad);

        adContainer = findViewById(R.id.ad_container);
        Button btnLoad = findViewById(R.id.btn_load);
        Button btnCheck = findViewById(R.id.btn_check);
        Button btnShow = findViewById(R.id.btn_show);


        initBanner();
        //点击按钮，执行对应的方法
        btnLoad.setOnClickListener(v -> loadBanner());
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showBanner());
    }
    //广告实例初始化（页面创建时执行）
    private void initBanner() {
        tpBanner = new TPBanner(BannerAdActivity.this);//初始化广告对象
        Log.v(LOG, " ========== 广告对象已创建 ==========");


      //设置Banner自定义宽高
        Map<String, Object> mLocalExtras = new HashMap<>();
        mLocalExtras.put("width", 320);  // 宽度 dp
        mLocalExtras.put("height", 100); // 高度 dp
        tpBanner.setCustomParams(mLocalExtras);

        //预置中介组
//        String customConfig = "eyJhZFBvc2l0aW9uIjpbXSwicmVxIjpbXSwiYWR2YW5jZWROYXRpdmUiOmZhbHNlLCJhZG1vYkFkdmFuY2VkTmF0aXZlIjpmYWxzZSwiYnVja2V0X2lkIjowLCJjYWNoZU51bSI6Miwic2VnbWVudF9pZCI6OTg4NDgsInNjZW5lX3R5cGUiOjAsImFkVHlwZSI6ImJhbm5lciIsInNlY1R5cGUiOjAsInNjZW5jZXMiOltdLCJyZWZyZXNoVGltZSI6NjU1MzU2MCwicmV3YXJkZWRJbmZvIjp7InJld2FyZGVkIjpmYWxzZSwidHlwZSI6ZmFsc2V9LCJpc19zZXJ2ZXJfY2FsbGJhY2siOjAsImlzX3NlcnZlcl9pbXBfY2FsbGJhY2siOjAsImlzX3NraXAiOjAsImNvdW50ZG93bl90aW1lIjowLCJza2lwX3RpbWUiOjAsInJlbG9hZF9jb25maWciOnsiYWRzY2VuZV9yZWxvYWQiOjAsImF1dG9fcmVsb2FkIjowLCJtYW51YWxfcmVsb2FkIjowLCJhdXRvX2NoZWNrX2ludGVydmFsIjowLCJsYXN0X3JlbG9hZF9pbnRlcnZhbCI6MCwicmVxdWVzdF9sYXllciI6MH0sInBhcmFsbGVsX251bSI6MCwiYmlkZGluZ3dhdGVyZmFsbCI6W10sImMyc2JpZGRpbmd3YXRlcmZhbGwiOltdLCJ3YXRlcmZhbGwiOlt7ImNsayI6W10sImltcCI6W10sInJlcSI6W10sInZpZGVvRmluIjoiIiwiY29uZmlnIjp7ImFwcElkIjoiODgxOTQ2MCIsInBsYWNlbWVudElkIjoiOTgzMDgyNjE0In0sImZyZXF1ZW5jeSI6eyJjYXBwaW5nX2hvdXIiOjAsImNhcHBpbmdfZGF5IjowLCJwYWNpbmdfbWluIjowfSwicmVxdWVzdF9pbnRlcnZhbF9jb25maWciOnsicmVxdWVzdF9pbnRlcnZhbF9zdGF0dXMiOjAsInJlcXVlc3Rfbm9fZmlsbF9udW0iOjAsInJlcXVlc3RfaW50ZXJ2YWwiOjB9LCJuYW1lIjoiUGFuZ2xlIEFkcyIsIm1kbiI6InRyYWRwbHVzIiwicmVxdWVzdF9hZ2VudCI6IiIsImlkIjoxOSwiZHJhd190eXBlIjowLCJpc190ZW1wbGF0ZV9yZW5kZXJpbmciOjIsInZpZGVvX211dGUiOjEsImF1dG9fcGxheV92aWRlbyI6MCwidmlkZW9fbWF4X3RpbWUiOjAsInZpZGVvX29yaWVudGF0aW9uIjowLCJ2aWRlb19wcm90b2NvbCI6MCwiYWRzb3VyY2VfdHlwZSI6MCwiZnVsbF9zY3JlZW5fdmlkZW8iOjAsImVjcG0iOjAuMDAxNDY4LCJlY3BtX2NueSI6MC4wMSwiZWNwbV9wcmVjaXNpb24iOiJwdWJsaXNoZXJfZGVmaW5lZCIsImVjcG1fbGV2ZWwiOjEsInJhdGUiOjAuMDAxNDY4LCJhZHNvdXJjZV9wbGFjZW1lbnRfaWQiOjExMDkwNDYsIm5ld19zb3J0X3R5cGUiOjEsImRpcmVjdGlvbiI6MCwiYWRfZm9ybWF0IjowLCJzaWdtb2JfdHlwZSI6MCwic3R5bGVfdHlwZSI6MCwic3R5bGVfbmFtZSI6W10sImNvdW50ZG93bl90aW1lIjowLCJpc19za2lwYWJsZSI6MCwic2tpcF90aW1lIjowLCJ6b29tX291dCI6MCwiaXNfY2xvc2FibGUiOjAsInBvcGNvbmZpcm0iOjAsImluaXRfbm9fY2FsbGJhY2siOiIiLCJiaWdvX2hvc3RfcnVsZXMiOiIiLCJpc19hZHgiOjAsImlzX25hdGl2ZSI6MCwicGxhY2VtZW50X2FkX3R5cGUiOjAsImNsaWNrX2FyZWFzIjowLCJhZF9zaXplIjoxLCJhZF9zaXplX2luZm8iOnsiWCI6NjQwLCJZIjoxMDB9LCJhZF9zaXplX3JhdGlvIjowLCJhZF9zaXplX3JhdGlvX2luZm8iOnsiWCI6MCwiWSI6MH0sImlzX21haW5fdGhyZWFkIjoxLCJhbHdheXNfcmV3YXJkIjowLCJjdXN0b21DbGFzc05hbWUiOiJjb20udHJhZHBsdXMuYWRzLmZwYW5nb2xpbi5Ub3VUaWFvQmFubmVyIiwibG9hZFRpbWVvdXQiOjEwLCJhZFZhbGlkVGltZSI6MTA4MDAsInBheWxvYWRUaW1lb3V0IjowLCJidXllcnVpZFRpbWVvdXQiOjAsImJpZGRpbmdfbW9kZSI6MCwiY29sbGFwc2libGUiOjAsImltcHJlc3Npb25fdHlwZSI6MCwidXBsb2FkX2V2ZW50X2RhdGEiOjEsImlzX25ld190ZW1wbGF0ZSI6MCwiYmFubmVyX21yZWMiOjEsImF1dG9fb3B0aW1pemF0aW9uIjoxLCJhZF9jdXJyZW5jeSI6IlVTRCIsImFkX3ByaWNlX3VuaXQiOjEsImFkX3NvcnQiOjF9LHsiY2xrIjpbXSwiaW1wIjpbXSwicmVxIjpbXSwidmlkZW9GaW4iOiIiLCJjb25maWciOnsiYXBwSWQiOiI4ODE5NDYwIiwicGxhY2VtZW50SWQiOiI5ODMwODI2MTYifSwiZnJlcXVlbmN5Ijp7ImNhcHBpbmdfaG91ciI6MCwiY2FwcGluZ19kYXkiOjAsInBhY2luZ19taW4iOjB9LCJyZXF1ZXN0X2ludGVydmFsX2NvbmZpZyI6eyJyZXF1ZXN0X2ludGVydmFsX3N0YXR1cyI6MCwicmVxdWVzdF9ub19maWxsX251bSI6MCwicmVxdWVzdF9pbnRlcnZhbCI6MH0sIm5hbWUiOiJQYW5nbGUgQWRzIiwibWRuIjoidHJhZHBsdXMiLCJyZXF1ZXN0X2FnZW50IjoiIiwiaWQiOjE5LCJkcmF3X3R5cGUiOjAsImlzX3RlbXBsYXRlX3JlbmRlcmluZyI6MiwidmlkZW9fbXV0ZSI6MSwiYXV0b19wbGF5X3ZpZGVvIjowLCJ2aWRlb19tYXhfdGltZSI6MCwidmlkZW9fb3JpZW50YXRpb24iOjAsInZpZGVvX3Byb3RvY29sIjowLCJhZHNvdXJjZV90eXBlIjowLCJmdWxsX3NjcmVlbl92aWRlbyI6MCwiZWNwbSI6MC4wMDE0NjgsImVjcG1fY255IjowLjAxLCJlY3BtX3ByZWNpc2lvbiI6InB1Ymxpc2hlcl9kZWZpbmVkIiwiZWNwbV9sZXZlbCI6MSwicmF0ZSI6MC4wMDE0NjgsImFkc291cmNlX3BsYWNlbWVudF9pZCI6MTEwODY5NywibmV3X3NvcnRfdHlwZSI6MSwiZGlyZWN0aW9uIjowLCJhZF9mb3JtYXQiOjAsInNpZ21vYl90eXBlIjowLCJzdHlsZV90eXBlIjowLCJzdHlsZV9uYW1lIjpbXSwiY291bnRkb3duX3RpbWUiOjAsImlzX3NraXBhYmxlIjowLCJza2lwX3RpbWUiOjAsInpvb21fb3V0IjowLCJpc19jbG9zYWJsZSI6MCwicG9wY29uZmlybSI6MCwiaW5pdF9ub19jYWxsYmFjayI6IiIsImJpZ29faG9zdF9ydWxlcyI6IiIsImlzX2FkeCI6MCwiaXNfbmF0aXZlIjoxLCJwbGFjZW1lbnRfYWRfdHlwZSI6MSwiY2xpY2tfYXJlYXMiOjAsImFkX3NpemUiOjAsImFkX3NpemVfaW5mbyI6eyJYIjowLCJZIjowfSwiYWRfc2l6ZV9yYXRpbyI6MCwiYWRfc2l6ZV9yYXRpb19pbmZvIjp7IlgiOjAsIlkiOjB9LCJpc19tYWluX3RocmVhZCI6MCwiYWx3YXlzX3Jld2FyZCI6MCwiY3VzdG9tQ2xhc3NOYW1lIjoiY29tLnRyYWRwbHVzLmFkcy5mcGFuZ29saW4uVG91VGlhb1JlbmRlck5hdGl2ZVZpZGVvIiwibG9hZFRpbWVvdXQiOjEwLCJhZFZhbGlkVGltZSI6MTA4MDAsInBheWxvYWRUaW1lb3V0IjowLCJidXllcnVpZFRpbWVvdXQiOjAsImJpZGRpbmdfbW9kZSI6MCwiY29sbGFwc2libGUiOjAsImltcHJlc3Npb25fdHlwZSI6MCwidXBsb2FkX2V2ZW50X2RhdGEiOjEsImlzX25ld190ZW1wbGF0ZSI6MCwiYmFubmVyX21yZWMiOjEsImF1dG9fb3B0aW1pemF0aW9uIjoxLCJhZF9jdXJyZW5jeSI6IlVTRCIsImFkX3ByaWNlX3VuaXQiOjEsImFkX3NvcnQiOjF9XSwiYm90dG9td2F0ZXJmYWxsIjpbXSwiYmlkZGluZ1RpbWVvdXQiOjUsImMyc2JpZGRpbmdUaW1lb3V0Ijo1LCJib3R0b21XYWl0VGltZSI6MCwibG9hZE1heFdhaXRUaW1lIjowLCJtaW5DYWNoZSI6MiwiYWRjb2xvbnlaIjoiIiwic3RhdHVzIjowLCJjb2RlIjowLCJjbiI6MCwibG9hZEZhaWxlZEludGVydmFsIjoxMCwiYWRfZmlsbF9jYWxsYmFjayI6MiwiaXNfbm90aGluZyI6MCwiaXNfdGVzdF9tb2RlIjowLCJyZXNwX3VpZCI6ImJlZWQ5NTViNWE4MTUzNmI2YjZmOTdkOWU0ZWIyYmE2Iiwibm9iaWQiOjEsImlzX2h5YnJpZF9zZXR1cCI6MCwidXZhX2NvbmZpZyI6eyJzdGF0dXMiOjAsImltcF90aW1lcyI6MCwiZWNwbV90eXBlIjowLCJlY3BtX21pbiI6MCwiZWNwbV9tYXgiOjAsImVjcG1fbGlzdCI6W119LCJvcGVuX2F1dG9fbG9hZCI6MSwicmVzdHJhaW5fdGltZSI6MCwicmVzdHJhaW5fbGltaXQiOjAsInNoYXJlX2FkdW5pdF9pZCI6IiIsImlzX3NoYXJlX2FkdW5pdCI6MCwic2hhcmVfYWR1bml0X3VuaXF1ZV9iaW5kIjowLCJhZHVuaXRfcmVxdWVzdF9pbnRlcnZhbF9jb25maWciOnsicmVxdWVzdF9pbnRlcnZhbF9zdGF0dXMiOjAsInJlcXVlc3RfaW50ZXJ2YWwiOjB9LCJhZHVuaXRfYXV0b2xvYWRfcmV0cnlfY29uZmlnIjpbXSwic2hhcmVfYWR1bml0X3JlcXVlc3Rfc2VjIjowLCJhZF9hdXRvX2Nsb3NlX3NlY29uZCI6MCwiaW50ZXJncm91cF9hdXRvbG9hZF9yZXRyeV9jb25maWciOltdLCJjdXN0b21fY2FjaGVfc2Vjb25kIjowLCJwbGF5bG9hZF9saWZlc3BhbiI6ODY0MDAsInBsYXlsb2FkX21heF9jb3VudCI6MywicHJpY2Vfc3RyYXRlZ3lfdGltZXMiOjMsImJpZGRpbmdBZENhY2hlTnVtIjoyLCJleGNoYW5nZV9yYXRlIjo2LjgwOTkwMjd9";
//                tpBanner.setDefaultConfig(AdIds.BANNER_AD_UNIT_ID, customConfig);


       tpBanner.closeAutoShow();//关闭自动展示，广告加载后不会自己弹出

        //流量分组
        HashMap<String, String> map = new HashMap<>();
        map.put("channel", "abc");

        SegmentUtils.initPlacementCustomMap(
                AdIds.BANNER_AD_UNIT_ID,
                map
        );

        //一定要先设置监听再load
        tpBanner.setAdListener(new BannerAdListener() {

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdLoaded【广告源："+ tpAdInfo.adSourceName + "，广告源id：" + tpAdInfo.adSourceId + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + ",中介组id："+ tpAdInfo.segmentId + ",ecpm:"+tpAdInfo.ecpm+"】");



                toast("Banner loaded");
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                Log.v(LOG, "onAdFailed【code : "+ error.getErrorCode() + ", msg :" + error.getErrorMsg() + "】");

                toast("Banner load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdImpression【广告源："+ tpAdInfo.adSourceName +  "，广告源ID："+ tpAdInfo.adSourceId+  "，广告类型：" + tpAdInfo.format +  "，tpAdUnitId：" + tpAdInfo.tpAdUnitId + "，中介组id：" + tpAdInfo.segmentId  + "，true_adunit_id：" + tpAdInfo.true_adunit_id + ",ecpm:"+tpAdInfo.ecpm+"】");
                Log.v(LOG,"bannerH:"+tpAdInfo.bannerH+",bannerW:"+tpAdInfo.bannerW);
                toast("Banner impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClicked【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");

                toast("Banner clicked");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClosed【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");

                toast("Banner closed");
            }
        });

        tpBanner.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/BannerEveryLayer", "横幅"));
    }
    //点击load按钮执行


    private void loadBanner() {
        if (tpBanner.getParent() == null) {

//内嵌自适应
            FrameLayout.LayoutParams params =
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);


            adContainer.addView(tpBanner,params);//将广告添加到FrameLayout里
        }
        tpBanner.loadAd(AdIds.BANNER_AD_UNIT_ID);//传入广告位id,SDK根据后台配置的规则，向广告源发起请求
    }


    //给用户提示，状态查询
    private void checkAdFill() {
        if (tpBanner != null && tpBanner.isReady()) {
            toast("Banner is ready");
        } else {
            toast("Banner not ready");
        }
    }

    //广告是否能展示
    private void showBanner() {
            tpBanner.showAd();
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tpBanner != null) {
            tpBanner.onDestroy();
        }
    }
}
