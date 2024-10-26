package amehry.ultimateads.models;

import java.util.List;
import com.squareup.moshi.Json;

public class MoreApps{

	@Json(name = "MyAppsBanners")
	private List<MyAppsBannersItem> myAppsBanners;

	@Json(name = "MyAppsNatives")
	private List<MyAppsNativesItem> myAppsNatives;

	@Json(name = "MyAppsInters")
	private List<MyAppsIntersItem> myAppsInters;

	public List<MyAppsBannersItem> getMyAppsBanners(){
		return myAppsBanners;
	}

	public List<MyAppsNativesItem> getMyAppsNatives(){
		return myAppsNatives;
	}

	public List<MyAppsIntersItem> getMyAppsInters(){
		return myAppsInters;
	}
}