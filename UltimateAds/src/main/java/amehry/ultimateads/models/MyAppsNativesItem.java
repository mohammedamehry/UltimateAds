package amehry.ultimateads.models;

import com.squareup.moshi.Json;

public class MyAppsNativesItem{

	@Json(name = "Natievpreview")
	private String natievpreview;

	@Json(name = "NativeDescription")
	private String nativeDescription;

	@Json(name = "NativeIcon")
	private String nativeIcon;

	@Json(name = "Type")
	private int type;

	@Json(name = "NativePackage")
	private String nativePackage;

	@Json(name = "NativeLink")
	private String nativeLink;

	@Json(name = "Nativetitle")
	private String nativetitle;

	public String getNatievpreview(){
		return natievpreview;
	}

	public String getNativeDescription(){
		return nativeDescription;
	}

	public String getNativeIcon(){
		return nativeIcon;
	}

	public int getType(){
		return type;
	}

	public String getNativePackage(){
		return nativePackage;
	}

	public String getNativeLink(){
		return nativeLink;
	}

	public String getNativetitle(){
		return nativetitle;
	}
}