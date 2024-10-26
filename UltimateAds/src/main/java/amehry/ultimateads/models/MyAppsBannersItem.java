package amehry.ultimateads.models;

import com.squareup.moshi.Json;

public class MyAppsBannersItem{

	@Json(name = "BannerDescription")
	private String bannerDescription;

	@Json(name = "Type")
	private int type;

	@Json(name = "BannerPackage")
	private String bannerPackage;

	@Json(name = "BannerLink")
	private String bannerLink;

	@Json(name = "Bannertitle")
	private String bannertitle;

	@Json(name = "BannerIcon")
	private String bannerIcon;

	@Json(name = "BannerButtonText")
	private String bannerButtonText;

	public String getBannerDescription(){
		return bannerDescription;
	}

	public int getType(){
		return type;
	}

	public String getBannerPackage(){
		return bannerPackage;
	}

	public String getBannerLink(){
		return bannerLink;
	}

	public String getBannertitle(){
		return bannertitle;
	}

	public String getBannerIcon(){
		return bannerIcon;
	}

	public String getBannerButtonText(){
		return bannerButtonText;
	}
}