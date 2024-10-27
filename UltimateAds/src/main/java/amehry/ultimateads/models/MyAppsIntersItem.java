package amehry.ultimateads.models;

import java.util.List;
import com.squareup.moshi.Json;

public class MyAppsIntersItem{

	@Json(name = "isDark")
	private boolean isDark;

	@Json(name = "InterDescription")
	private String interDescription;

	@Json(name = "InterLink")
	private String interLink;

	@Json(name = "ButtonTintColor")
	private String buttonTintColor;

	@Json(name = "InterIcon")
	private String interIcon;

	@Json(name = "Intertitle")
	private String intertitle;

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

	@Json(name = "buttonTextColor")
	private String buttonTextColor;

	@Json(name = "InterPackage")
	private String interPackage;

	public boolean isIsDark(){
		return isDark;
	}

	public String getInterDescription(){
		return interDescription;
	}

	public String getInterLink(){
		return interLink;
	}

	public String getButtonTintColor(){
		return buttonTintColor;
	}

	public String getInterIcon(){
		return interIcon;
	}

	public String getIntertitle(){
		return intertitle;
	}

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

	public String getButtonTextColor(){
		return buttonTextColor;
	}

	public String getInterPackage(){
		return interPackage;
	}
}