package amehry.ultimateads.ads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import amehry.ultimateads.UltimateAds;
import amehry.ultimateads.ads.Interfaces.OnNativeListener;
import amehry.ultimateads.R.id;
import amehry.ultimateads.R.layout;
import amehry.ultimateads.R.styleable;
import amehry.ultimateads.models.MyAppsBannersItem;
import amehry.ultimateads.models.MyAppsNativesItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class NativePromote extends FrameLayout {
   private final Context context;
   private String attrInstallTitle = "Install";
   private int attrInstallColor = Color.parseColor("#2196F3");
   private int attrContentColor = Color.parseColor("#2E2E2E");
   private int attrBodyColor = -1;
   private int attrRadiusButton = 20;
   CardView native_banner;
   private TextView adColor;
   private TextView installNativeTitle;
   private LinearLayout nativeBody;
   private RelativeLayout previewProgress;
   private RelativeLayout iconProgress;
   private ImageView icon;
   private ImageView native_preview;
   private TextView name;
   private TextView nativeDescription;
   private List<MyAppsNativesItem> adList = new ArrayList();
   private OnNativeListener onNativeListener;
   public NativePromote(Context context) {
      super(context);
      this.initView((AttributeSet)null);
      this.context = context;
      this.initializeData(context);
   }

   public NativePromote(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      this.initView(attrs);
      this.context = context;
      this.initializeData(context);
   }

   public NativePromote(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      this.initView(attrs);
      this.context = context;
      this.initializeData(context);
   }

   public NativePromote(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      this.initView(attrs);
      this.context = context;
      this.initializeData(context);
   }

   private void initView(AttributeSet attrs) {
      TypedArray typedArray = this.getContext().getTheme().obtainStyledAttributes(attrs, styleable.NativeView, 0, 0);
      this.attrInstallTitle = typedArray.getString(styleable.NativeView_native_installTitle);
      this.attrContentColor = typedArray.getColor(styleable.NativeView_native_contentColor, this.attrContentColor);
      this.attrBodyColor = typedArray.getColor(styleable.NativeView_native_bodyColor, this.attrBodyColor);
      this.attrRadiusButton = typedArray.getInteger(styleable.NativeView_native_installRadius, this.attrRadiusButton);
      this.native_banner = (CardView)this.findViewById(id.native_banner);
      this.attrInstallColor = typedArray.getColor(styleable.NativeView_native_installColor, this.attrInstallColor);
      this.inflateBanner();
      typedArray.recycle();
   }

   private void inflateBanner() {
      LayoutInflater inflater = LayoutInflater.from(this.getContext());
      inflater.inflate(layout.promote_native_banner, this);
   }


   private void initializeData(Context activity) {
      this.adList = UltimateAds.response.getMoreApps().getMyAppsNatives();
      String packageName = activity.getPackageName();

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         adList.removeIf(Mynative -> Mynative.getNativePackage().equals(packageName));
      } else {
         Iterator<MyAppsNativesItem> iterator = adList.iterator();
         while (iterator.hasNext()) {
            if (iterator.next().getNativePackage().equals(packageName)) {
               iterator.remove();
            }
         }
      }
   }
   private void applyUI() {
      if (this.installNativeTitle != null && this.attrInstallTitle != null) {
         this.installNativeTitle.setText(this.attrInstallTitle);
      }

      if (this.adColor != null) {
         this.adColor.setBackgroundColor(this.attrInstallColor);
      }

      if (this.name != null) {
         this.name.setTextColor(this.attrContentColor);
      }

      if (this.nativeBody != null) {
         this.nativeBody.setBackgroundColor(this.attrBodyColor);
      }

   }

   public void onFinishInflate() {
      super.onFinishInflate();
   }

   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      this.showPromoteNative();
   }

   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
   }

   @SuppressLint({"SetTextI18n"})
   private void initializeInflateUI() {
      this.nativeBody = (LinearLayout)this.findViewById(id.native_body);
      this.adColor = (TextView)this.findViewById(id.native_ad_text);
      this.previewProgress = (RelativeLayout)this.findViewById(id.native_preview_progress);
      this.iconProgress = (RelativeLayout)this.findViewById(id.native_icon_progress);
      this.nativeDescription = (TextView)this.findViewById(id.native_description);
      this.icon = (ImageView)this.findViewById(id.native_icons);
      this.native_preview = (ImageView)this.findViewById(id.native_preview);
      this.name = (TextView)this.findViewById(id.native_app_name);
      this.applyUI();
   }

   public void showPromoteNative() {
      try {
         if (this.adList != null && !this.adList.isEmpty()) {
            int size = this.adList.size();
            Random random = new Random();
            int nativeIndex = random.nextInt(size);
            this.buildNative(nativeIndex);
         } else {
            if (this.onNativeListener != null) {
               this.onNativeListener.onNativeAdFailedToLoad("The Ad list is empty ! please check your json file.");
            }

            this.setVisibility(View.GONE);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   @SuppressLint({"SetTextI18n"})
   private void buildNative(final int index) {
      this.initializeInflateUI();
      String data_name = ((MyAppsNativesItem)this.adList.get(index)).getNativetitle();
      String data_icons = ((MyAppsNativesItem)this.adList.get(index)).getNativeIcon();
      String preview = ((MyAppsNativesItem)this.adList.get(index)).getNatievpreview();
      final String data_packageName = ((MyAppsNativesItem)this.adList.get(index)).getNativePackage();
      final String data_link = ((MyAppsNativesItem)this.adList.get(index)).getNativeLink();
      String buttonDescription = ((MyAppsNativesItem)this.adList.get(index)).getNativeDescription();
      if (this.onNativeListener != null) {
         this.onNativeListener.onNativeAdLoaded();
      }

      this.loadIcon(data_icons);
      this.loadPreview(preview);
      this.name.setText(data_name);
      this.nativeDescription.setText(buttonDescription);
      this.native_banner.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
            if (NativePromote.this.onNativeListener != null) {
               NativePromote.this.onNativeListener.onNativeAdClicked();
            }

            switch((NativePromote.this.adList.get(index)).getType()) {
            case 0:
               UltimateAds.OpenAmazon(NativePromote.this.context, data_packageName);
               break;
            case 1:
               NativePromote.this.context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(data_link)));
            }

         }
      });
   }

   private void loadIcon(String icons) {
      this.iconProgress.setVisibility(View.VISIBLE);
      Glide.with(this.context).load(icons).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
         public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
         }

         public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            NativePromote.this.iconProgress.setVisibility(View.GONE);
            return false;
         }
      }).into(this.icon);
   }

   private void loadPreview(String preview) {
      this.previewProgress.setVisibility(View.VISIBLE);
      Glide.with(this.context).load(preview).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
         public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
         }

         public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            NativePromote.this.previewProgress.setVisibility(View.GONE);
            return false;
         }
      }).into(this.native_preview);
   }

   public void setOnNativeListener(OnNativeListener onNativeListener) {
      this.onNativeListener = onNativeListener;
   }

   public void setThemeColors(int titlesColors, int Background, int adBackgroundColor, int adtextColor) {
      this.nativeDescription.setTextColor(titlesColors);
      this.nativeBody.setBackgroundColor(Background);
      this.name.setTextColor(titlesColors);
      this.adColor.setBackgroundColor(adBackgroundColor);
      this.adColor.setTextColor(adtextColor);
   }
}
