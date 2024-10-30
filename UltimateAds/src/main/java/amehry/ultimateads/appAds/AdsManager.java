package amehry.ultimateads.appAds;

import com.squareup.moshi.Json;

public class AdsManager{

	@Json(name = "MintegralAppID")
	private String mintegralAppID;

	@Json(name = "ChartBoostBanner")
	private String chartBoostBanner;

	@Json(name = "AdmobNative")
	private String admobNative;

	@Json(name = "AdmobAppID")
	private String admobAppID;

	@Json(name = "AdmobRewardLimit")
	private int admobRewardLimit;

	@Json(name = "InterstitialAds")
	private String interstitialAds;

	@Json(name = "MintegralInterstitialPlacementID")
	private String mintegralInterstitialPlacementID;

	@Json(name = "MintegralRewardPlacementID")
	private String mintegralRewardPlacementID;

	@Json(name = "ChartBoostReward")
	private String chartBoostReward;

	@Json(name = "AdmobInterstitial")
	private String admobInterstitial;

	@Json(name = "BannerAds")
	private String bannerAds;

	@Json(name = "AdmobInterstitialLimit")
	private int admobInterstitialLimit;

	@Json(name = "NativeAds")
	private String nativeAds;

	@Json(name = "ShowAds")
	private boolean showAds;

	@Json(name = "RewardAds")
	private String rewardAds;

	@Json(name = "ChartBoostAppSignature")
	private String chartBoostAppSignature;

	@Json(name = "ChartBoostInterstitial")
	private String chartBoostInterstitial;

	@Json(name = "MintegralRewardUnitID")
	private String mintegralRewardUnitID;

	@Json(name = "MintegralAppKey")
	private String mintegralAppKey;

	@Json(name = "MintegralInterstitialUnitID")
	private String mintegralInterstitialUnitID;

	@Json(name = "AdmobLimit")
	private boolean admobLimit;

	@Json(name = "AdmobNativeLimit")
	private int admobNativeLimit;

	@Json(name = "AdmobReward")
	private String admobReward;

	@Json(name = "ChartBoostAppID")
	private String chartBoostAppID;

	@Json(name = "AdmobBanner")
	private String admobBanner;

	@Json(name = "AdmobBannerLimit")
	private int admobBannerLimit;

	public String getMintegralAppID(){
		return mintegralAppID;
	}

	public String getChartBoostBanner(){
		return chartBoostBanner;
	}

	public String getAdmobNative(){
		return admobNative;
	}

	public String getAdmobAppID(){
		return admobAppID;
	}

	public int getAdmobRewardLimit(){
		return admobRewardLimit;
	}

	public String getInterstitialAds(){
		return interstitialAds;
	}

	public String getMintegralInterstitialPlacementID(){
		return mintegralInterstitialPlacementID;
	}

	public String getMintegralRewardPlacementID(){
		return mintegralRewardPlacementID;
	}

	public String getChartBoostReward(){
		return chartBoostReward;
	}

	public String getAdmobInterstitial(){
		return admobInterstitial;
	}

	public String getBannerAds(){
		return bannerAds;
	}

	public int getAdmobInterstitialLimit(){
		return admobInterstitialLimit;
	}

	public String getNativeAds(){
		return nativeAds;
	}

	public boolean isShowAds(){
		return showAds;
	}

	public String getRewardAds(){
		return rewardAds;
	}

	public String getChartBoostAppSignature(){
		return chartBoostAppSignature;
	}

	public String getChartBoostInterstitial(){
		return chartBoostInterstitial;
	}

	public String getMintegralRewardUnitID(){
		return mintegralRewardUnitID;
	}

	public String getMintegralAppKey(){
		return mintegralAppKey;
	}

	public String getMintegralInterstitialUnitID(){
		return mintegralInterstitialUnitID;
	}

	public boolean isAdmobLimit(){
		return admobLimit;
	}

	public int getAdmobNativeLimit(){
		return admobNativeLimit;
	}

	public String getAdmobReward(){
		return admobReward;
	}

	public String getChartBoostAppID(){
		return chartBoostAppID;
	}

	public String getAdmobBanner(){
		return admobBanner;
	}

	public int getAdmobBannerLimit(){
		return admobBannerLimit;
	}
}