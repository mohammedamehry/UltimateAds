package amehry.ultimateads.ads.Interfaces;

public interface OnRewardAdListener {
   void onRewardFailedToLoad(String error);

   void onRewardClosed();

   void onRewardFailedToShow(String error);

}
