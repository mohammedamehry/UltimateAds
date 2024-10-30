package amehry.ultimateads.appAds;

import com.squareup.moshi.Json;

public class AppAdsResponse{

	@Json(name = "AdsManager")
	private AdsManager adsManager;

	@Json(name = "AppControl")
	private AppControl appControl;

	public AdsManager getAdsManager(){
		return adsManager;
	}

	public AppControl getAppControl(){
		return appControl;
	}
}