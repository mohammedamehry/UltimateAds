package amehry.ultimateads.models;

import java.util.List;
import com.squareup.moshi.Json;

public class MyAppsIntersItem{

	@Json(name = "Type")
	private int type;

	@Json(name = "InterPager")
	private List<String> interPager;

	@Json(name = "InterstitialButtonText")
	private String interstitialButtonText;

	@Json(name = "InterPreview")
	private String interPreview;

	@Json(name = "Style")
	private int style;

	@Json(name = "InterDescription")
	private String interDescription;

	@Json(name = "InterPackage")
	private String interPackage;

	@Json(name = "InterLink")
	private String interLink;

	@Json(name = "InterIcon")
	private String interIcon;

	@Json(name = "Intertitle")
	private String intertitle;

	public int getType(){
		return type;
	}

	public List<String> getInterPager(){
		return interPager;
	}

	public String getInterstitialButtonText(){
		return interstitialButtonText;
	}

	public String getInterPreview(){
		return interPreview;
	}

	public int getStyle(){
		return style;
	}

	public String getInterDescription(){
		return interDescription;
	}

	public String getInterPackage(){
		return interPackage;
	}

	public String getInterLink(){
		return interLink;
	}

	public String getInterIcon(){
		return interIcon;
	}

	public String getIntertitle(){
		return intertitle;
	}
}