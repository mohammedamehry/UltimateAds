package amehry.ultimateads.models;

import java.util.List;
import com.squareup.moshi.Json;

public class UniversaladsResponse{

	@Json(name = "MoreApps")
	private MoreApps moreApps;

	@Json(name = "BlockedCountries")
	private List<String> blockedCountries;



	public MoreApps getMoreApps(){
		return moreApps;
	}

	public List<String> getBlockedCountries(){
		return blockedCountries;
	}

}