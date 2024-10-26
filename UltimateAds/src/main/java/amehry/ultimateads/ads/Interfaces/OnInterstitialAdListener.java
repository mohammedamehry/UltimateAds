package amehry.ultimateads.ads.Interfaces;

public interface OnInterstitialAdListener {
   void onInterstitialAdLoaded();

   void onInterstitialAdClosed();

   void onInterstitialAdClicked();

   void onInterstitialAdFailedToLoad(String var1);
}
