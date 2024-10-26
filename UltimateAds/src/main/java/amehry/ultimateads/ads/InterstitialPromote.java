package amehry.ultimateads.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import amehry.ultimateads.R;
import amehry.ultimateads.UltimateAds;
import amehry.ultimateads.ads.Adapters.AdapterPager;
import amehry.ultimateads.ads.Interfaces.OnAdClosed;
import amehry.ultimateads.ads.Interfaces.OnInterstitialAdListener;
import amehry.ultimateads.ads.Interfaces.OnPagerScreenListener;
import amehry.ultimateads.models.MyAppsIntersItem;
import amehry.ultimateads.models.MyAppsNativesItem;

public class InterstitialPromote extends Dialog {
   private int attrRadiusButton = 20;
   private int attrInstallColor = Color.parseColor("#2196F3");
   private String attrInstallTitle = "Install";
   private int interstitialIndex;
   private final Activity activity;
   private RelativeLayout close;
   private TextView closeCount;
   private TextView closeText;
   private TextView ad;
   private RelativeLayout install;
   private RelativeLayout Background;
   private TextView installTitle;
   private TextView downloadAd;
   private TextView rateAd;
   private TextView rateCounter;
   private RelativeLayout previewProgress;
   private RelativeLayout iconProgress;
   private ViewPager viewPager;
   private SpringDotsIndicator springDotsIndicator;
   private CountDownTimer countDownTimer;
   private boolean isTimer;
   private int timer = 0;
   private ImageView preview;
   private ImageView icon;
   private TextView name;
   private TextView description;
   private OnInterstitialAdListener onInterstitialAdListener;
   private OnAdClosed onAdClosed;
   private ConstraintLayout interstitial_content;
   private List<MyAppsIntersItem> adList = new ArrayList();
   private List<String> adScreen = new ArrayList();

   public InterstitialPromote(@NonNull Activity activity) {
      super(activity, R.style.InterstitialStyle);
      this.activity = activity;
         this.adList = UltimateAds.response.getMoreApps().getMyAppsInters();
         String packageName = activity.getPackageName();

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            adList.removeIf(Mynative -> Mynative.getInterPackage().equals(packageName));
         } else {
            Iterator<MyAppsIntersItem> iterator = adList.iterator();
            while (iterator.hasNext()) {
               if (iterator.next().getInterPackage().equals(packageName)) {
                  iterator.remove();
               }
            }
         }
      Random random = new Random();
      this.interstitialIndex = random.nextInt(this.adList.size());
      switch((this.adList.get(this.interstitialIndex)).getStyle()) {
      case 0:
         this.setContentView(R.layout.promote_interstitial_custom);
         break;
      case 1:
         this.setContentView(R.layout.promote_interstitial_advance);
      }
      this.setCancelable(false);
      this.initializeData();
      this.initializeUI();
   }

   private void initializeData() {
      this.onLoadAdListener();
   }

   private void onLoadAdListener() {
      (new Handler()).postDelayed(new Runnable() {
         public void run() {
            if (InterstitialPromote.this.adList != null && !InterstitialPromote.this.adList.isEmpty()) {
               if (InterstitialPromote.this.onInterstitialAdListener != null) {
                  InterstitialPromote.this.onInterstitialAdListener.onInterstitialAdLoaded();
               }
            } else if (InterstitialPromote.this.onInterstitialAdListener != null) {
               InterstitialPromote.this.onInterstitialAdListener.onInterstitialAdFailedToLoad("Ad Loaded, but data base of ad wrong ! please check your file.");
            }

         }
      }, 500L);
   }

   private void initializeUI() {
      this.close = (RelativeLayout)this.findViewById(R.id.interstitial_close);
      this.closeCount = (TextView)this.findViewById(R.id.closeCount);
      this.closeText = (TextView)this.findViewById(R.id.closeText);
      this.install = (RelativeLayout)this.findViewById(R.id.interstitial_install);
      this.installTitle = (TextView)this.findViewById(R.id.interstitial_install_txt);
      this.ad = (TextView)this.findViewById(R.id.ad);
      this.downloadAd = (TextView)this.findViewById(R.id.downloadAd);
      this.rateAd = (TextView)this.findViewById(R.id.rateAd);
      this.rateCounter = (TextView)this.findViewById(R.id.rate_counter);
      this.viewPager = (ViewPager)this.findViewById(R.id.viewpager);
      this.springDotsIndicator = (SpringDotsIndicator)this.findViewById(R.id.dots_indicator);
      this.previewProgress = (RelativeLayout)this.findViewById(R.id.interstitial_preview_progress);
      this.iconProgress = (RelativeLayout)this.findViewById(R.id.inter_icon_progress);
      this.preview = (ImageView)this.findViewById(R.id.interstitial_preview);
      this.icon = (ImageView)this.findViewById(R.id.inter_icons);
      this.name = (TextView)this.findViewById(R.id.interstitial_app_name);
      this.description = (TextView)this.findViewById(R.id.interstitial_short_description);
      this.Background = (RelativeLayout)this.findViewById(R.id.Background);
      this.interstitial_content = (ConstraintLayout)this.findViewById(R.id.interstitial_content);
      this.generateInstallButton();
      this.updateInstallTitle();
   }

   private void startCountDown() {
      if (this.timer != 0) {
         this.close.setClickable(false);
         this.closeText.setAlpha(0.5F);
         this.closeCount.setVisibility(View.VISIBLE);
         this.countDownTimer = (new CountDownTimer(1000L * (long)this.timer, 1000L) {
            public void onTick(long j) {
               String count = String.valueOf(j / 1000L);
               InterstitialPromote.this.closeCount.setText(count);
               InterstitialPromote.this.isTimer = true;
            }

            public void onFinish() {
               InterstitialPromote.this.isTimer = false;
               InterstitialPromote.this.closeCount.setVisibility(View.INVISIBLE);
               InterstitialPromote.this.closeText.setAlpha(1.0F);
               InterstitialPromote.this.close.setClickable(true);
               InterstitialPromote.this.timerCancel();
            }
         }).start();
      }

   }

   private void timerCancel() {
      try {
         if (this.isTimer && this.countDownTimer != null) {
            this.countDownTimer.cancel();
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public boolean isAdLoaded() {
      if (this.adList != null) {
         return !this.adList.isEmpty();
      } else {
         if (this.onInterstitialAdListener != null) {
            this.onInterstitialAdListener.onInterstitialAdFailedToLoad("Failed to show : No Ad");
         }

         return false;
      }
   }

   public void show() {
      super.show();
      this.buildInterstitial();
   }

   private void dismissAd() {
      try {
         if (this.isShowing()) {
            if (this.onAdClosed != null) {
               this.onAdClosed.onAdClosed();
            }

            this.dismiss();
         }
      } catch (Exception var2) {
         this.cancel();
      }

   }

   private void updateInstallTitle() {
      this.installTitle.setText(this.attrInstallTitle);
   }

   private void initializePager(int index) {
      try {
         DisplayMetrics displayMetrics = new DisplayMetrics();
         this.activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         int width = displayMetrics.widthPixels;
         int paddingLeftRight = width / 4;
         this.viewPager.setPadding(paddingLeftRight, 0, paddingLeftRight, 0);
         this.viewPager.setPageMargin(5);
         this.viewPager.setPageTransformer(false, new amehry.devxcustomads.Adapters.PagerTransformer(this.viewPager));
         AdapterPager adapterPager = new AdapterPager(this.activity.getApplicationContext(), this.screenShot(index));
         this.viewPager.setAdapter(adapterPager);
         int sizeOfScreen = this.screenShot(index).size();
         if (sizeOfScreen >= 3) {
         }

         this.springDotsIndicator.setViewPager(this.viewPager);
         adapterPager.setOnPagerScreenListener(new OnPagerScreenListener() {
            @Override
            public void onPagerScreenListenerLoaded() {

            }

            @Override
            public void onPagerScreenListenerFailed(String var1) {

            }
         });
      } catch (Exception var7) {
      }

   }

   private ArrayList<String> screenShot(int index) {
      this.adScreen = ((MyAppsIntersItem)this.adList.get(index)).getInterPager();
      ArrayList<String> screenList = new ArrayList();
      if (this.adScreen != null && !this.adScreen.isEmpty()) {
         screenList.addAll(((MyAppsIntersItem)this.adList.get(index)).getInterPager());
      }

      return screenList;
   }

   private void buildInterstitial() {
      try {
         if (this.adList != null && !this.adList.isEmpty()) {
            int size = this.adList.size();
            this.updateInterstitial(this.interstitialIndex);
            this.initializePager(this.interstitialIndex);
            this.startCountDown();
         } else {
            if (this.onInterstitialAdListener != null) {
               this.onInterstitialAdListener.onInterstitialAdFailedToLoad("The Ad list is empty ! please check your json file.");
            }

            this.dismissAd();
         }
      } catch (Exception var2) {
      }

   }

   @SuppressLint({"SetTextI18n"})
   private void updateInterstitial(final int index) {
      String data_name = ((MyAppsIntersItem)this.adList.get(index)).getIntertitle();
      String data_icons = ((MyAppsIntersItem)this.adList.get(index)).getInterIcon();
      String data_preview = ((MyAppsIntersItem)this.adList.get(index)).getInterPreview();
      final String data_packageName = ((MyAppsIntersItem)this.adList.get(index)).getInterPackage();
      String data_description = ((MyAppsIntersItem)this.adList.get(index)).getInterDescription();
      final String data_link = ((MyAppsIntersItem)this.adList.get(index)).getInterLink();
      String ButtonText = ((MyAppsIntersItem)this.adList.get(index)).getInterstitialButtonText();
      this.loadIcon(data_icons);
      this.loadPreview(data_preview);
      this.name.setText(data_name);
      this.description.setText(data_description);
      this.rateCounter.setText("186");
      this.installTitle.setText(ButtonText);
      this.install.setOnClickListener(v -> {
         InterstitialPromote.this.dismiss();
         if (InterstitialPromote.this.onInterstitialAdListener != null) {
            InterstitialPromote.this.onInterstitialAdListener.onInterstitialAdClicked();
         }

         switch((InterstitialPromote.this.adList.get(index)).getType()) {
            case 0:
               UltimateAds.OpenAmazon(InterstitialPromote.this.activity, data_packageName);
               break;
            case 1:
               InterstitialPromote.this.activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(data_link)));
         }

      });
      this.close.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            InterstitialPromote.this.dismissAd();
            if (InterstitialPromote.this.onInterstitialAdListener != null) {
               InterstitialPromote.this.onInterstitialAdListener.onInterstitialAdClosed();
            }
         }
      });
   }

   private void loadIcon(String icons) {
      this.iconProgress.setVisibility(View.VISIBLE);
     Glide.with(this.activity.getApplicationContext()).load(icons).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
         public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
         }

         public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            InterstitialPromote.this.iconProgress.setVisibility(View.GONE);
            return false;
         }
      }).into(this.icon);
   }

   private void loadPreview(String previewIcon) {
      this.previewProgress.setVisibility(View.VISIBLE);
      Glide.with(this.activity.getApplicationContext()).load(previewIcon).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
         public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
         }

         public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            InterstitialPromote.this.previewProgress.setVisibility(View.GONE);
            return false;
         }
      }).into(this.preview);
   }

   private void generateInstallButton() {
      GradientDrawable gradientDrawable = new GradientDrawable();
      gradientDrawable.setShape(GradientDrawable.RECTANGLE);
      gradientDrawable.setColor(this.attrInstallColor);
      this.ad.setBackgroundColor(this.attrInstallColor);
      this.springDotsIndicator.setDotIndicatorColor(this.attrInstallColor);
      this.springDotsIndicator.setStrokeDotsIndicatorColor(this.attrInstallColor);
      gradientDrawable.setCornerRadii(new float[]{(float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton});
      this.install.setBackground(gradientDrawable);
      this.generateInstallClose();
   }

   private void generateInstallClose() {
      GradientDrawable gradientDrawable = new GradientDrawable();
      gradientDrawable.setShape(GradientDrawable.RECTANGLE);
      gradientDrawable.setColor(Color.parseColor("#E4E4E4"));
      gradientDrawable.setCornerRadii(new float[]{(float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton, (float)this.attrRadiusButton});
      this.close.setBackground(gradientDrawable);
   }

   public void setTimer(int timer) {
      this.timer = timer;
   }

   public void setStyle() {
      switch(((MyAppsIntersItem)this.adList.get(this.interstitialIndex)).getStyle()) {
      case 0:
         this.setContentView(R.layout.promote_interstitial_custom);
         break;
      case 1:
         this.setContentView(R.layout.promote_interstitial_advance);
      }

      this.initializeUI();
   }

   public void setRadiusButton(int radius) {
      this.attrRadiusButton = radius;
      this.generateInstallButton();
   }

   public void setInstallTitle(String title) {
      this.attrInstallTitle = title;
      this.updateInstallTitle();
   }

   public void setInstallColor(int color) {
      try {
         this.attrInstallColor = this.activity.getResources().getColor(color);
         this.generateInstallButton();
      } catch (Exception var3) {
      }

   }

   public void setInstallColor(String color) {
      try {
         if (color.startsWith("#")) {
            this.attrInstallColor = Color.parseColor(color);
            this.generateInstallButton();
         }
      } catch (Exception var3) {
      }

   }

   public void setOnInterstitialAdListener(OnInterstitialAdListener onInterstitialAdListener) {
      this.onInterstitialAdListener = onInterstitialAdListener;
   }

   public void setOnAdClosed(OnAdClosed onAdClosed) {
      this.onAdClosed = onAdClosed;
   }

   public void setThemeColors(int dotsColor, int textcolors, int background, int positivebuttoncolor, int positivetextcolor, int closebuttoncolor, int closebuttontextcolor, int adBackground, int adtextColor) {
      this.springDotsIndicator.setDotIndicatorColor(dotsColor);
      this.springDotsIndicator.setStrokeDotsIndicatorColor(dotsColor);
      this.name.setTextColor(textcolors);
      this.description.setTextColor(textcolors);
      this.ad.setBackgroundColor(adBackground);
      this.ad.setTextColor(adtextColor);
      this.close.setBackgroundTintList(ColorStateList.valueOf(closebuttoncolor));
      this.closeText.setTextColor(closebuttontextcolor);
      this.install.setBackgroundTintList(ColorStateList.valueOf(positivebuttoncolor));
      this.installTitle.setTextColor(positivetextcolor);
      this.Background.setBackgroundTintList(ColorStateList.valueOf(background));
      this.interstitial_content.setBackgroundTintList(ColorStateList.valueOf(background));
   }
}
