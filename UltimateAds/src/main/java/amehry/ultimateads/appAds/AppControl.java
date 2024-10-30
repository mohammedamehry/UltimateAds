package amehry.ultimateads.appAds;

import com.squareup.moshi.Json;

public class AppControl{

	@Json(name = "UpdateVersion")
	private int updateVersion;

	@Json(name = "UpdateFromAmazon")
	private String updateFromAmazon;

	@Json(name = "UpdateLink")
	private String updateLink;

	@Json(name = "ShowUpdate")
	private boolean showUpdate;

	public int getUpdateVersion(){
		return updateVersion;
	}

	public String getUpdateFromAmazon(){
		return updateFromAmazon;
	}

	public String getUpdateLink(){
		return updateLink;
	}

	public boolean isShowUpdate(){
		return showUpdate;
	}
}