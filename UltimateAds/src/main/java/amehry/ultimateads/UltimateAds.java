package amehry.ultimateads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.chartboost.sdk.LoggingLevel;
import com.chartboost.sdk.callbacks.BannerCallback;
import com.chartboost.sdk.callbacks.InterstitialCallback;
import com.chartboost.sdk.callbacks.RewardedCallback;
import com.chartboost.sdk.events.CacheError;
import com.chartboost.sdk.events.CacheEvent;
import com.chartboost.sdk.events.ClickError;
import com.chartboost.sdk.events.ClickEvent;
import com.chartboost.sdk.events.DismissEvent;
import com.chartboost.sdk.events.ImpressionEvent;
import com.chartboost.sdk.events.RewardEvent;
import com.chartboost.sdk.events.ShowError;
import com.chartboost.sdk.events.ShowEvent;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ads.Interstitial;
import com.chartboost.sdk.ads.Rewarded;
import com.chartboost.sdk.ads.Banner;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.mbridge.msdk.MBridgeSDK;
import com.mbridge.msdk.newinterstitial.out.MBNewInterstitialHandler;
import com.mbridge.msdk.newinterstitial.out.NewInterstitialListener;
import com.mbridge.msdk.out.MBRewardVideoHandler;
import com.mbridge.msdk.out.MBridgeIds;
import com.mbridge.msdk.out.MBridgeSDKFactory;
import com.mbridge.msdk.out.RewardInfo;
import com.mbridge.msdk.out.RewardVideoListener;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import amehry.ultimateads.ads.BannerPromote;
import amehry.ultimateads.ads.GoogleMobileAdsConsentManager;
import amehry.ultimateads.ads.Interfaces.OnBannerListener;
import amehry.ultimateads.ads.Interfaces.OnInterstitialAdListener;
import amehry.ultimateads.ads.Interfaces.OnNativeListener;
import amehry.ultimateads.ads.Interfaces.OnRewardAdListener;
import amehry.ultimateads.ads.InterstitialPromote;
import amehry.ultimateads.ads.NativePromote;
import amehry.ultimateads.appAds.AdsManager;
import amehry.ultimateads.appAds.AppAdsResponse;
import amehry.ultimateads.appAds.AppControl;
import amehry.ultimateads.databinding.LoadingAdsBinding;
import amehry.ultimateads.interfaces.CountryListener;
import amehry.ultimateads.interfaces.DataFetchListener;
import amehry.ultimateads.models.UniversaladsResponse;

public class UltimateAds {

    // :  Error Codes :
    // 1 : user country is blocked 400
    // 2 : country code is null 300;
    // 3 : error in fetching data 100;
    // 3 : internet error 50;


    public static final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    public static GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    public static String countryCode = "NULL";
    public static UniversaladsResponse response;
    public static boolean isCustomAdsInitialized = false;
    public static AppAdsResponse responseAds;
    public static AdsManager adsManager;
    public static AppControl controlApp;
    public static boolean isAdmobWillBeUsed = false;
    public static int AdmobInterstitialLimit = 1;
    public static int AdmobBannerLimit = 1;
    public static int AdmobRewardLimit = 1;
    public static int AdmobNativeLimit = 1;


    public static void getUserLocation(Context activity, CountryListener listener) {
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        countryCode = tm.getNetworkCountryIso();
        if (countryCode != null && !countryCode.isEmpty()) {
            countryCode = countryCode.toUpperCase();
            listener.onCountryReceived(response.getBlockedCountries().contains(countryCode));
        } else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    URL url = new URL(getCountryApi());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        JSONObject json = new JSONObject(stringBuilder.toString());
                        countryCode = json.getString("countryCode").toUpperCase();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> listener.onCountryError(e.getMessage()));
                    countryCode = "NULL";
                }
                String finalCountryCode = countryCode;
                new Handler(Looper.getMainLooper()).post(() -> listener.onCountryReceived(response.getBlockedCountries().contains(finalCountryCode)));
            });
        }
    }

    public static String getCountryApi() {
        Random random = new Random();
        int randomInt = random.nextInt(2);
        if (randomInt == 1) {
            return "http://ip-api.com/json/";
        }
        return "https://freeipapi.com/api/json";
    }


    public static boolean isTestMode = false;


    public static void fetchData(Activity context, String UniversalLink, DataFetchListener fetchListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, UniversalLink, s -> {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<UniversaladsResponse> jsonAdapter = moshi.adapter(UniversaladsResponse.class);
            try {
                response = jsonAdapter.fromJson(s);
                getUserLocation(context, new CountryListener() {
                    @Override
                    public void onCountryReceived(boolean blocked) {
                        if (UltimateAds.isTestMode) {
                            InitMyAds(context);
                            new Handler().postDelayed(fetchListener::onDataFetched, 4000);
                        } else {
                            if (blocked) {
                                fetchListener.onDataFetchError("400");
                            } else {
                                fetchListener.onDataFetched();
                                isCustomAdsInitialized = true;
                                InitMyAds(context);
                                new Handler().postDelayed(fetchListener::onDataFetched, 4000);
                            }
                        }
                    }

                    @Override
                    public void onCountryError(String error) {
                        fetchListener.onDataFetchError("300");
                    }
                });

            } catch (Exception e) {
                fetchListener.onDataFetchError("100");

            }
        }, volleyError -> fetchListener.onDataFetchError("50")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String clientId = BuildConfig.CF_ACCESS_CLIENT_ID;
                String clientSecret = BuildConfig.CF_ACCESS_CLIENT_SECRET;
                headers.put("CF-Access-Client-Id", clientId);  // Replace with your actual headers
                headers.put("CF-Access-Client-Secret", clientSecret);
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(10000, com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }

    public static void getAllData(Activity activity, String AppAdsLink, String UniversalLink, DataFetchListener listener) {
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, AppAdsLink, s -> {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<AppAdsResponse> jsonAdapter = moshi.adapter(AppAdsResponse.class);
            try {
                responseAds = jsonAdapter.fromJson(s);
                assert responseAds != null;
                adsManager = responseAds.getAdsManager();
                controlApp = responseAds.getAppControl();
                fetchData(activity, UniversalLink, listener);
            } catch (Exception e) {
                listener.onDataFetchError("100");

            }
        }, volleyError -> listener.onDataFetchError("50")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String clientId = BuildConfig.CF_ACCESS_CLIENT_ID;
                String clientSecret = BuildConfig.CF_ACCESS_CLIENT_SECRET;
                headers.put("CF-Access-Client-Id", clientId);  // Replace with your actual headers
                headers.put("CF-Access-Client-Secret", clientSecret);
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,  // 10 seconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }

    public static void InitMyAds(Activity context) {
        if (adsManager.getBannerAds().contains("admob") || adsManager.getNativeAds().contains("admob") || adsManager.getInterstitialAds().contains("admob") || adsManager.getRewardAds().contains("admob")) {
            isAdmobWillBeUsed = true;
            googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(context, responseAds.getAdsManager().getAdmobAppID());
            googleMobileAdsConsentManager.gatherConsent(
                    context,
                    consentError -> {
                        if (googleMobileAdsConsentManager.canRequestAds()) {
                            initializeMobileAdsSdk(context);
                        }
                    });
            if (googleMobileAdsConsentManager.canRequestAds()) {
                initializeMobileAdsSdk(context);
            }
        } else {
            isAdmobWillBeUsed = false;
        }
        Chartboost.setLoggingLevel(LoggingLevel.ALL);
        Chartboost.startWithAppId(context, adsManager.getChartBoostAppID(), adsManager.getChartBoostAppSignature(), startError -> {
            //load inters here
        });
        MBridgeSDK sdk = MBridgeSDKFactory.getMBridgeSDK();
        Map<String, String> map = sdk.getMBConfigurationMap(adsManager.getMintegralAppID(), adsManager.getMintegralAppKey());
        sdk.init(map, context);


    }

    private static void initializeMobileAdsSdk(Activity activity) {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder()
                        .setTestDeviceIds(List.of("81012F7DC2B83E97B9854112EA57A3B3"))
                        .build());
                    MobileAds.initialize(activity, initializationStatus -> {
                        // Initialization done
                    });
    }


    public static void ShowAdmobBanner(Activity activity, LinearLayout bannerContainer) {
        if (isAdmobWillBeUsed) {
            if (adsManager.isAdmobLimit()){
                if (!(AdmobBannerLimit>adsManager.getAdmobBannerLimit())){
                    AdRequest adRequest = new AdRequest.Builder().build();
                    AdView adView = new AdView(activity);
                    adView.setAdUnitId(adsManager.getAdmobBanner());
                    adView.setAdSize(AdSize.BANNER);
                    bannerContainer.addView(adView);
                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdClicked() {
                            // Code to be executed when the user clicks on an ad.
                        }

                        @Override
                        public void onAdClosed() {
                            // Code to be executed when the user is about to return
                            // to the app after tapping on an ad.
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                            // Code to be executed when an ad request fails.
                            ShowChartBoostBanner(activity, bannerContainer);
                        }

                        @Override
                        public void onAdImpression() {
                            // Code to be executed when an impression is recorded
                            // for an ad.
                        }

                        @Override
                        public void onAdLoaded() {
                            // Code to be executed when an ad finishes loading.
                            AdmobBannerLimit++;
                        }

                        @Override
                        public void onAdOpened() {
                            // Code to be executed when an ad opens an overlay that
                            // covers the screen.
                        }
                    });
                    adView.loadAd(adRequest);
                }else {
                    ShowChartBoostBanner(activity, bannerContainer);
                }
            }else {
                AdRequest adRequest = new AdRequest.Builder().build();
                AdView adView = new AdView(activity);
                adView.setAdUnitId(adsManager.getAdmobBanner());
                adView.setAdSize(AdSize.BANNER);
                bannerContainer.addView(adView);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                    }

                    @Override
                    public void onAdClosed() {
                        // Code to be executed when the user is about to return
                        // to the app after tapping on an ad.
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Code to be executed when an ad request fails.
                        ShowChartBoostBanner(activity, bannerContainer);
                    }

                    @Override
                    public void onAdImpression() {
                        // Code to be executed when an impression is recorded
                        // for an ad.
                    }

                    @Override
                    public void onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                    }

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                    }
                });
                adView.loadAd(adRequest);
            }
        }else {
            ShowChartBoostBanner(activity, bannerContainer);
        }
    }

    public static void ShowAdMobNative(Activity activity, FrameLayout nativeContainer) {
        if (isAdmobWillBeUsed) {
            if (adsManager.isAdmobLimit()){
                if (!(AdmobNativeLimit>adsManager.getAdmobNativeLimit())){
                    AdLoader adLoader = new AdLoader.Builder(activity, adsManager.getAdmobNative())
                            .forNativeAd(nativeAd -> {
                                try {
                                    LayoutInflater inflater = (LayoutInflater) nativeContainer.getContext()
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    @SuppressLint("InflateParams") NativeAdView adView = (NativeAdView) inflater
                                            .inflate(R.layout.admob_native, null, false);
                                    TextView headlineView = adView.findViewById(R.id.ad_headline);
                                    headlineView.setText(nativeAd.getHeadline());
                                    adView.setHeadlineView(headlineView);
                                    TextView ad_body = adView.findViewById(R.id.ad_body);
                                    ad_body.setText(nativeAd.getBody());
                                    MediaView mediaView = adView.findViewById(R.id.ad_media);
                                    ImageView ad_app_icon = adView.findViewById(R.id.ad_app_icon);
                                    ad_app_icon.setImageDrawable(Objects.requireNonNull(nativeAd.getIcon()).getDrawable());
                                    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                    adView.setMediaView(mediaView);
                                    adView.setNativeAd(nativeAd);
                                    nativeContainer.addView(adView);
                                    AdmobNativeLimit++;
                                } catch (Exception ignored) {

                                }

                            })
                            .withAdListener(new AdListener() {
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                    // Handle the failure by logging, altering the UI, and so on.
                                    ShowCustomNative(activity, nativeContainer);

                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();
                                }

                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();

                                }

                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                }
                            })
                            .withNativeAdOptions(new NativeAdOptions.Builder()
                                    // Methods in the NativeAdOptions.Builder class can be
                                    // used here to specify individual options settings.
                                    .build())
                            .build();
                    adLoader.loadAd(new AdRequest.Builder().build());
                }else {
                    ShowCustomNative(activity, nativeContainer);
                }
            }else {
                AdLoader adLoader = new AdLoader.Builder(activity, adsManager.getAdmobNative())
                        .forNativeAd(nativeAd -> {
                            try {
                                LayoutInflater inflater = (LayoutInflater) nativeContainer.getContext()
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                @SuppressLint("InflateParams") NativeAdView adView = (NativeAdView) inflater
                                        .inflate(R.layout.admob_native, null, false);
                                TextView headlineView = adView.findViewById(R.id.ad_headline);
                                headlineView.setText(nativeAd.getHeadline());
                                adView.setHeadlineView(headlineView);
                                TextView ad_body = adView.findViewById(R.id.ad_body);
                                ad_body.setText(nativeAd.getBody());
                                MediaView mediaView = adView.findViewById(R.id.ad_media);
                                ImageView ad_app_icon = adView.findViewById(R.id.ad_app_icon);
                                ad_app_icon.setImageDrawable(Objects.requireNonNull(nativeAd.getIcon()).getDrawable());
                                mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                adView.setMediaView(mediaView);
                                adView.setNativeAd(nativeAd);
                                nativeContainer.addView(adView);
                            } catch (Exception ignored) {

                            }

                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                // Handle the failure by logging, altering the UI, and so on.
                                ShowCustomNative(activity, nativeContainer);
                            }

                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();

                            }

                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();
                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder()
                                // Methods in the NativeAdOptions.Builder class can be
                                // used here to specify individual options settings.
                                .build())
                        .build();
                adLoader.loadAd(new AdRequest.Builder().build());
            }
        }else {
            ShowCustomNative(activity, nativeContainer);
        }
    }

    public static InterstitialAd mInterstitialAd;


    public static void ShowAdmobInterstitial(Activity activity, OnInterstitialAdListener listener) {
        if (isAdmobWillBeUsed){
            if (adsManager.isAdmobLimit()){
                if (AdmobInterstitialLimit>adsManager.getAdmobInterstitialLimit()){
                    LoadAndShowMintegralInterstitial(activity, listener);
                }else {
                    Dialog dialog = new Dialog(activity);
                    LoadingAdsBinding binding = LoadingAdsBinding.inflate(activity.getLayoutInflater());
                    dialog.setContentView(binding.getRoot());
                    dialog.setCancelable(false);
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    }
                    dialog.show();
                    AdRequest adRequest = new AdRequest.Builder().build();
                    InterstitialAd.load(activity, adsManager.getAdmobInterstitial(),
                            adRequest, new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    // Handle the error.
                                    mInterstitialAd = null;
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    LoadAndShowMintegralInterstitial(activity, listener);
                                }

                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd ad) {
                                    mInterstitialAd = ad;
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            listener.onInterstitialAdClosed();
                                            AdmobInterstitialLimit++;
                                        }

                                        @Override
                                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                           LoadAndShowMintegralInterstitial(activity, listener);
                                        }
                                    });
                                    mInterstitialAd.show(activity);
                                }
                            });
                }
            }else {
                Dialog dialog = new Dialog(activity);
                LoadingAdsBinding binding = LoadingAdsBinding.inflate(activity.getLayoutInflater());
                dialog.setContentView(binding.getRoot());
                dialog.setCancelable(false);
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
                dialog.show();
                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(activity, adsManager.getAdmobInterstitial(),
                        adRequest, new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error.
                                mInterstitialAd = null;
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                LoadAndShowMintegralInterstitial(activity, listener);
                            }

                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd ad) {
                                mInterstitialAd = ad;
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        listener.onInterstitialAdClosed();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        LoadAndShowMintegralInterstitial(activity, listener);                                    }
                                });
                                mInterstitialAd.show(activity);
                            }
                        });

            }
        }else {
            LoadAndShowMintegralInterstitial(activity, listener);
        }
    }


    public static void OpenAmazon(Context context, String packageName) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("amzn://apps/android?p=" + packageName));
            context.startActivity(intent);
        } catch (Exception var3) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + packageName)));
        }

    }


    public static Banner chartboostBanner;

    public static void ShowChartBoostBanner(Activity activity, LinearLayout Banner) {
        chartboostBanner = new Banner(activity, adsManager.getChartBoostBanner(), com.chartboost.sdk.ads.Banner.BannerSize.STANDARD, new BannerCallback() {
            @Override
            public void onAdLoaded(@NonNull CacheEvent cacheEvent, @Nullable CacheError cacheError) {
                if (cacheError != null) {
                    /* Handle error */
                    chartboostBanner.detach();
                    ShowCustomBanner(activity, Banner);
                } else {
                    chartboostBanner.show();
                }
            }

            @Override
            public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {

            }

            @Override
            public void onAdShown(@NonNull ShowEvent showEvent, @Nullable ShowError showError) {

            }

            @Override
            public void onAdClicked(@NonNull ClickEvent clickEvent, @Nullable ClickError clickError) {

            }

            @Override
            public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {

            }
        }, null);
        chartboostBanner.cache();
        Banner.removeAllViews();
        Banner.addView(chartboostBanner);
    }


    public static Interstitial chartboostInterstitial;


    public static void ShowChartBoostInterstitial(Activity activity, OnInterstitialAdListener listener) {
        Dialog dialog = new Dialog(activity);
        LoadingAdsBinding binding = LoadingAdsBinding.inflate(activity.getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        dialog.show();
        chartboostInterstitial = new Interstitial(adsManager.getChartBoostInterstitial(), new InterstitialCallback() {
            @Override
            public void onAdDismiss(@NonNull DismissEvent dismissEvent) {
                listener.onInterstitialAdClosed();
            }

            @Override
            public void onAdLoaded(@NonNull CacheEvent cacheEvent, @Nullable CacheError cacheError) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (cacheError == null) {
                    chartboostInterstitial.show();
                } else {
                    LoadAndShowMintegralInterstitial(activity, listener);
                }
            }

            @Override
            public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {
                // You could add additional logic here if needed
            }

            @Override
            public void onAdShown(@NonNull ShowEvent showEvent, @Nullable ShowError showError) {

            }

            @Override
            public void onAdClicked(@NonNull ClickEvent clickEvent, @Nullable ClickError clickError) {
                listener.onInterstitialAdClicked();
            }

            @Override
            public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {

            }
        }, null);
        chartboostInterstitial.cache();
    }


    public static MBNewInterstitialHandler mintegralInterstitial;

    public static void LoadAndShowMintegralInterstitial(Activity activity, OnInterstitialAdListener listener) {
        Dialog dialog = new Dialog(activity);
        LoadingAdsBinding binding = LoadingAdsBinding.inflate(activity.getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        dialog.show();
        mintegralInterstitial = new MBNewInterstitialHandler(activity, adsManager.getMintegralInterstitialPlacementID(), adsManager.getMintegralInterstitialUnitID());
        mintegralInterstitial.setInterstitialVideoListener(new NewInterstitialListener() {

            @Override
            public void onLoadCampaignSuccess(MBridgeIds ids) {


            }

            @Override
            public void onResourceLoadSuccess(MBridgeIds ids) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (mintegralInterstitial.isReady()) {
                    mintegralInterstitial.show();
                } else {
                    LoadAndShowCustomInterstitial(activity, listener);
                }


            }

            @Override
            public void onResourceLoadFail(MBridgeIds ids, String errorMsg) {
                LoadAndShowCustomInterstitial(activity, listener);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onShowFail(MBridgeIds ids, String errorMsg) {
                LoadAndShowCustomInterstitial(activity, listener);

            }

            @Override
            public void onAdShow(MBridgeIds ids) {

            }

            @Override
            public void onAdClose(MBridgeIds ids, RewardInfo info) {
                listener.onInterstitialAdClosed();
            }

            @Override
            public void onAdClicked(MBridgeIds ids) {
                listener.onInterstitialAdClicked();
            }

            @Override
            public void onVideoComplete(MBridgeIds ids) {

            }

            @Override
            public void onAdCloseWithNIReward(MBridgeIds ids, RewardInfo info) {

            }

            @Override
            public void onEndcardShow(MBridgeIds ids) {

            }

        });
        mintegralInterstitial.load();


    }


    //reward ads begin here

    public static RewardedAd rewardedAd;

    public static void LoadAndShowAdmobReward(Activity activity, OnRewardAdListener listener) {
        if (isAdmobWillBeUsed){
            if (adsManager.isAdmobLimit()){
                if (AdmobRewardLimit>adsManager.getAdmobRewardLimit()){
                    LoadAndShowMintegralReward(activity, listener);
                }else {
                    Dialog dialog = new Dialog(activity);
                    LoadingAdsBinding binding = LoadingAdsBinding.inflate(activity.getLayoutInflater());
                    dialog.setContentView(binding.getRoot());
                    dialog.setCancelable(false);
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    }
                    dialog.show();
                    AdRequest adRequest = new AdRequest.Builder().build();
                    RewardedAd.load(activity, adsManager.getAdmobReward(),
                            adRequest, new RewardedAdLoadCallback() {
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    // Handle the error.
                                    rewardedAd = null;
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    LoadAndShowMintegralReward(activity, listener);
                                }

                                @Override
                                public void onAdLoaded(@NonNull RewardedAd ad) {
                                    rewardedAd = ad;
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            super.onAdDismissedFullScreenContent();
                                            listener.onRewardClosed();
                                            AdmobRewardLimit++;
                                        }

                                        @Override
                                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                            super.onAdFailedToShowFullScreenContent(adError);
                                            LoadAndShowMintegralReward(activity, listener);
                                        }
                                    });

                                    rewardedAd.show(activity, rewardItem -> {

                                    });
                                }
                            });
                }
            }else {
                Dialog dialog = new Dialog(activity);
                LoadingAdsBinding binding = LoadingAdsBinding.inflate(activity.getLayoutInflater());
                dialog.setContentView(binding.getRoot());
                dialog.setCancelable(false);
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
                dialog.show();
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(activity, adsManager.getAdmobReward(),
                        adRequest, new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error.
                                rewardedAd = null;
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                LoadAndShowMintegralReward(activity, listener);
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd ad) {
                                rewardedAd = ad;
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        listener.onRewardClosed();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                        LoadAndShowMintegralReward(activity, listener);
                                    }
                                });

                                rewardedAd.show(activity, rewardItem -> {

                                });
                            }
                        });
            }
        }else {
            LoadAndShowMintegralReward(activity, listener);
        }
    }


    public static Rewarded chartboostRewarded;

    public static void LoadAndShowChartBoostReward(Activity activity, OnRewardAdListener listener) {
        Dialog dialog = new Dialog(activity);
        LoadingAdsBinding binding = LoadingAdsBinding.inflate(activity.getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        dialog.show();
        chartboostRewarded = new Rewarded(adsManager.getChartBoostReward(), new RewardedCallback() {
            @Override
            public void onRewardEarned(@NonNull RewardEvent rewardEvent) {

            }

            @Override
            public void onAdDismiss(@NonNull DismissEvent dismissEvent) {
                listener.onRewardClosed();
            }

            @Override
            public void onAdLoaded(@NonNull CacheEvent cacheEvent, @Nullable CacheError cacheError) {
                // after this is successful ad can be shown
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (chartboostRewarded.isCached()) {
                    chartboostRewarded.show();
                } else {
                    LoadAndShowMintegralReward(activity, listener);
                }
            }

            @Override
            public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {

            }

            @Override
            public void onAdShown(@NonNull ShowEvent showEvent, @Nullable ShowError showError) {

            }

            @Override
            public void onAdClicked(@NonNull ClickEvent clickEvent, @Nullable ClickError clickError) {

            }

            @Override
            public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {

            }
        }, null);
        chartboostRewarded.cache();
    }


    public static MBRewardVideoHandler mMBRewardVideoHandler;

    public static void LoadAndShowMintegralReward(Activity activity, OnRewardAdListener listener) {
        Dialog dialog = new Dialog(activity);
        LoadingAdsBinding binding = LoadingAdsBinding.inflate(activity.getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        dialog.show();
        mMBRewardVideoHandler = new MBRewardVideoHandler(activity, adsManager.getMintegralRewardPlacementID(), adsManager.getMintegralRewardUnitID());
        mMBRewardVideoHandler.setRewardVideoListener(new RewardVideoListener() {

            @Override
            public void onLoadSuccess(MBridgeIds ids) {
                // Called when an ad is filled


            }

            @Override
            public void onVideoLoadSuccess(MBridgeIds ids) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (mMBRewardVideoHandler.isReady()) {
                    mMBRewardVideoHandler.show();
                } else {
                    listener.onRewardFailedToLoad("Ad not loaded");
                }
            }

            @Override
            public void onVideoLoadFail(MBridgeIds ids, String errorMsg) {
                // Called when the ad fails to load
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                listener.onRewardFailedToLoad(errorMsg);
            }

            @Override
            public void onShowFail(MBridgeIds ids, String errorMsg) {
                // Called when the ad fails to display
                listener.onRewardFailedToShow(errorMsg);
            }

            @Override
            public void onAdShow(MBridgeIds ids) {
                // Called when the ad is displayed


            }

            @Override
            public void onAdClose(MBridgeIds ids, RewardInfo rewardInfo) {

                listener.onRewardClosed();
            }

            @Override
            public void onVideoAdClicked(MBridgeIds ids) {
                // Called when the ad is clicked

            }

            @Override
            public void onVideoComplete(MBridgeIds ids) {
                // Called when the ad playback is complete

            }

            @Override
            public void onEndcardShow(MBridgeIds ids) {
                // Called when the ad's landing page is displayed
            }

        });
        mMBRewardVideoHandler.load();
    }

    public static void LoadAndShowCustomInterstitial(Activity activity, OnInterstitialAdListener listener) {
        Dialog dialog = new Dialog(activity);
        LoadingAdsBinding binding = LoadingAdsBinding.inflate(activity.getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        dialog.show();
        InterstitialPromote interstitialPromote = new InterstitialPromote(activity);
        interstitialPromote.setOnInterstitialAdListener(new OnInterstitialAdListener() {
            @Override
            public void onInterstitialAdLoaded() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                interstitialPromote.show();
            }

            @Override
            public void onInterstitialAdClosed() {
                listener.onInterstitialAdClosed();

            }

            @Override
            public void onInterstitialAdClicked() {
                listener.onInterstitialAdClicked();

            }

            @Override
            public void onInterstitialAdFailedToLoad(String var1) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                listener.onInterstitialAdFailedToLoad(var1);

            }
        });
    }

    public static void ShowCustomBanner(Activity activity, LinearLayout Banner) {
        BannerPromote bannerPromote = new BannerPromote(activity);
        bannerPromote.setOnBannerListener(new OnBannerListener() {
            @Override
            public void onBannerAdLoaded() {
                assert Banner != null;
                Banner.addView(bannerPromote);
            }

            @Override
            public void onBannerAdClicked() {

            }

            @Override
            public void onBannerAdFailedToLoad(String var1) {

            }
        });
        bannerPromote.showPromoteBanner();

    }

    public static void ShowCustomNative(Activity activity, FrameLayout nativeContainer) {
        NativePromote nativePromote = new NativePromote(activity);
        nativePromote.setOnNativeListener(new OnNativeListener() {
            @Override
            public void onNativeAdLoaded() {
                assert nativeContainer != null;
                nativeContainer.addView(nativePromote);

            }

            @Override
            public void onNativeAdClicked() {

            }

            @Override
            public void onNativeAdFailedToLoad(String var1) {

            }
        });
        nativePromote.showPromoteNative();


    }

    public static void ShowUniversalBanner(Activity activity, LinearLayout Banner){
        try {
            if (adsManager.isShowAds()){
                switch (adsManager.getBannerAds()){
                    case "admob":
                        ShowAdmobBanner(activity,Banner);
                        break;
                    case "chart":
                        ShowChartBoostBanner(activity,Banner);
                        break;
                    case "custom":
                        ShowCustomBanner(activity,Banner);
                        break;
                    default:
                        Banner.setVisibility(View.INVISIBLE);
                        break;

                }
            }
        }catch (Exception ignored){

        }
    }
    public static void ShowUniversalNative(Activity activity,FrameLayout nativeConatiner){
        try {
            if (adsManager.isShowAds()){
                switch (adsManager.getNativeAds()){
                    case "admob":
                        ShowAdMobNative(activity,nativeConatiner);
                        break;
                    case "custom":
                        ShowCustomNative(activity,nativeConatiner);
                        break;
                    default:
                        nativeConatiner.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        }catch (Exception ignored){

        }
    }

    public static void ShowUniversalInterstitial(Activity activity, OnInterstitialAdListener listener){
        try {
            if (adsManager.isShowAds()){
                switch (adsManager.getInterstitialAds()){
                    case "admob":
                        ShowAdmobInterstitial(activity,listener);
                        break;
                    case "chart":
                        ShowChartBoostInterstitial(activity,listener);
                        break;
                    case "min":
                        LoadAndShowMintegralInterstitial(activity,listener);
                        break;
                    case "custom":
                        LoadAndShowCustomInterstitial(activity,listener);
                        break;
                    default:
                        listener.onInterstitialAdFailedToLoad("No ads to show");
                        break;
                }
            }else {
                listener.onInterstitialAdFailedToLoad("No ads to show");
            }
        }catch (Exception e){
            listener.onInterstitialAdFailedToLoad(e.getMessage());
        }
    }

    public static void ShowUniversalReward(Activity activity,OnRewardAdListener listener){
        try {
            if (adsManager.isShowAds()){
                switch (adsManager.getRewardAds()){
                    case "admob":
                        LoadAndShowAdmobReward(activity,listener);
                        break;
                    case "chart":
                        LoadAndShowChartBoostReward(activity,listener);
                        break;
                    case "min":
                        LoadAndShowMintegralReward(activity,listener);
                        break;
                    default:
                        listener.onRewardFailedToLoad("No ads to show");
                        break;
                }
            }else {
                listener.onRewardFailedToLoad("No ads to show");
            }
        }catch (Exception e){
            listener.onRewardFailedToLoad(e.getMessage());
        }
    }

}
