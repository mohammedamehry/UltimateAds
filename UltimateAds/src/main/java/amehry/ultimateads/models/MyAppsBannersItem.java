package amehry.ultimateads.models;

import com.squareup.moshi.Json;

public class MyAppsBannersItem{

	@Json(name = "BannerDescription")
	private String bannerDescription;

	@Json(name = "isDark")
	private boolean isDark;

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

	@Json(name = "buttonTextColor")
	private String buttonTextColor;

	@Json(name = "BannerButtonText")
	private String bannerButtonText;

	@Json(name = "ButtonTintColor")
	private String buttonTintColor;

	public String getBannerDescription(){
		return bannerDescription;
	}

	public boolean isIsDark(){
		return isDark;
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

	public String getButtonTextColor(){
		return buttonTextColor;
	}

	public String getBannerButtonText(){
		return bannerButtonText;
	}

	public String getButtonTintColor(){
		return buttonTintColor;
	}
}