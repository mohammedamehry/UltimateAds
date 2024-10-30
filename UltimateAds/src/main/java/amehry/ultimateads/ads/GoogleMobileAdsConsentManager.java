package amehry.ultimateads.ads;

import android.app.Activity;
import android.content.Context;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;



import android.app.Activity;
import android.content.Context;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm.OnConsentFormDismissedListener;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentInformation.PrivacyOptionsRequirementStatus;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

import java.util.Arrays;

public class GoogleMobileAdsConsentManager {
    private static GoogleMobileAdsConsentManager instance;
    private final ConsentInformation consentInformation;

    /** Private constructor */
    String AdmobAppID;
    private GoogleMobileAdsConsentManager(Context context, String AdmobAppID) {
        this.consentInformation = UserMessagingPlatform.getConsentInformation(context);
        this.AdmobAppID = AdmobAppID;
    }

    /** Public constructor */
    public static GoogleMobileAdsConsentManager getInstance(Context context, String AdmobAppID) {
        if (instance == null) {
            instance = new GoogleMobileAdsConsentManager(context, AdmobAppID);
        }

        return instance;
    }

    /** Interface definition for a callback to be invoked when consent gathering is complete. */
    public interface OnConsentGatheringCompleteListener {
        void consentGatheringComplete(FormError error);
    }

    /** Helper variable to determine if the app can request ads. */
    public boolean canRequestAds() {
        return consentInformation.canRequestAds();
    }

    // [START is_privacy_options_required]
    /** Helper variable to determine if the privacy options form is required. */
    public boolean isPrivacyOptionsRequired() {
        return consentInformation.getPrivacyOptionsRequirementStatus()
                == PrivacyOptionsRequirementStatus.REQUIRED;
    }

    public void gatherConsent(
            Activity activity, OnConsentGatheringCompleteListener onConsentGatheringCompleteListener) {
        // For testing purposes, you can force a DebugGeography of EEA or NOT_EEA.
        ConsentDebugSettings debugSettings =
                new ConsentDebugSettings.Builder(activity)
                        .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                        .addTestDeviceHashedId("81012F7DC2B83E97B9854112EA57A3B3")
                        .build();

        ConsentRequestParameters params =
                new ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).setAdMobAppId(AdmobAppID).build();

        // [START gather_consent]
        // Requesting an update to consent information should be called on every app launch.
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () ->
                        UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                                activity,
                                formError -> {
                                    // Consent has been gathered.
                                    onConsentGatheringCompleteListener.consentGatheringComplete(formError);
                                }),
                requestConsentError ->
                        onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError));
        // [END gather_consent]
    }

    /** Helper method to call the UMP SDK method to present the privacy options form. */
    public void showPrivacyOptionsForm(
            Activity activity, OnConsentFormDismissedListener onConsentFormDismissedListener) {
        // [START present_privacy_options_form]
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener);
        // [END present_privacy_options_form]
    }
}