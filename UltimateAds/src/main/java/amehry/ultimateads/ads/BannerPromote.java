package amehry.ultimateads.ads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import amehry.ultimateads.R;
import amehry.ultimateads.UltimateAds;
import amehry.ultimateads.ads.Interfaces.OnBannerListener;
import amehry.ultimateads.databinding.PromoteBannerBinding;
import amehry.ultimateads.models.MyAppsBannersItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BannerPromote extends FrameLayout {
    private final int ANIMATION_SPEED = 3;
    private final Context context;
    private String installTitle = "Install";
    private int installColor = Color.parseColor("#2196F3");
    private int contentColor = Color.parseColor("#2196F3");
    private int attrBodyColor = -1;
    private TextView installText;
    private TextView adColor;
    private RelativeLayout installButton;
    private RelativeLayout bannerBody;
    private RelativeLayout bannerProgress;
    private ImageView icon;
    private TextView name;
    private TextView downloads;
    private TextView description;
    private LinearLayout content1;
    private LinearLayout content2;
    private AppCompatRatingBar ratingBar;
    private TextView ratingCount;
    RelativeLayout banner_background;
    private List<MyAppsBannersItem> adList = new ArrayList();
    private OnBannerListener onBannerListener;
    private final Handler handler = new Handler();
    TextView ad;
    PromoteBannerBinding binding;

    public BannerPromote(Context context) {
        super(context);
        this.initView((AttributeSet) null);
        this.context = context;
        this.initializeData(context);
    }

    public BannerPromote(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView(attrs);
        this.context = context;
        this.initializeData(context);
    }

    public BannerPromote(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(attrs);
        this.context = context;
        this.initializeData(context);
    }

    public BannerPromote(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(attrs);
        this.context = context;
        this.initializeData(context);
    }

    private void initView(AttributeSet attrs) {
        TypedArray typedArray = this.getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.BannerView, 0, 0);
        this.installTitle = typedArray.getString(R.styleable.BannerView_banner_installTitle);
        this.installColor = typedArray.getColor(R.styleable.BannerView_banner_installColor, this.installColor);
        this.contentColor = typedArray.getColor(R.styleable.BannerView_banner_contentColor, this.contentColor);
        this.attrBodyColor = typedArray.getColor(R.styleable.BannerView_banner_bodyColor, this.attrBodyColor);
        this.inflateBanner();
        typedArray.recycle();
    }

    private void inflateBanner() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = PromoteBannerBinding.inflate(inflater, this, true);
    }

    private void initializeData(Context activity) {
        this.adList = UltimateAds.response.getMoreApps().getMyAppsBanners();
        String packageName = activity.getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
           adList.removeIf(banner -> banner.getBannerPackage().equals(packageName));
        } else {
            Iterator<MyAppsBannersItem> iterator = adList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getBannerPackage().equals(packageName)) {
                    iterator.remove();
                }
            }
        }

    }




    private void applyUI() {
        if (this.installText != null && this.installTitle != null) {
            this.installText.setText(this.installTitle);
        }

        if (this.installButton != null) {
            this.installButton.setBackgroundColor(this.installColor);
            this.adColor.setBackgroundColor(this.installColor);
        }

        if (this.name != null && this.description != null) {
            this.name.setTextColor(this.contentColor);
            this.description.setTextColor(this.contentColor);
        }

        if (this.bannerBody != null) {
            this.bannerBody.setBackgroundColor(this.attrBodyColor);
        }

    }

    public void onFinishInflate() {
        super.onFinishInflate();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.showPromoteBanner();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.handler.removeCallbacksAndMessages((Object) null);
    }

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void initializeInflateUI() {
        installButton = binding.install;
        installText = binding.installTxt;
        adColor = binding.ad;
        bannerBody = binding.bannerBody;
        bannerProgress = binding.bannerProgress;
        ratingBar = binding.ratingbar;
        ratingCount = binding.ratingCount;
        banner_background = binding.bannerBody;
        ad = binding.ad;
        icon = binding.icons;
        name = binding.appName;
        downloads = binding.appDownload;
        description = binding.appDescription;
        content1 = binding.content1;
        content2 = binding.content2;

        this.applyUI();
    }

    public void showPromoteBanner() {
        try {
            if (this.adList != null && !this.adList.isEmpty()) {
                int size = this.adList.size();
                Random random = new Random();
                int bannerIndex = random.nextInt(size);
                this.buildBanner(bannerIndex);
            } else {
                if (this.onBannerListener != null) {
                    this.onBannerListener.onBannerAdFailedToLoad("The Ad list is empty ! please check your json file.");
                }

                this.setVisibility(View.GONE);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    @SuppressLint({"SetTextI18n"})
    private void buildBanner(final int index) {
        this.initializeInflateUI();
        String data_name = ((MyAppsBannersItem) this.adList.get(index)).getBannertitle();
        String data_icons = ((MyAppsBannersItem) this.adList.get(index)).getBannerIcon();
        String short_description = ((MyAppsBannersItem) this.adList.get(index)).getBannerDescription();
        final String data_packageName = ((MyAppsBannersItem) this.adList.get(index)).getBannerPackage();
        final String data_link = ((MyAppsBannersItem) this.adList.get(index)).getBannerLink();
        String button_text = ((MyAppsBannersItem) this.adList.get(index)).getBannerButtonText();
        boolean isDarkMode = ((MyAppsBannersItem) this.adList.get(index)).isIsDark();
        int  ButtonTintColor = Color.parseColor((this.adList.get(index)).getButtonTintColor());
        int  ButtonTextColor = Color.parseColor((this.adList.get(index)).getButtonTextColor());
        if (isDarkMode){
            this.banner_background.setBackgroundColor(getResources().getColor(R.color.darkColor));
            this.name.setTextColor(getResources().getColor(R.color.white));
            this.description.setTextColor(getResources().getColor(R.color.white));
        }else {
            this.banner_background.setBackgroundColor(getResources().getColor(R.color.white));
            this.name.setTextColor(getResources().getColor(R.color.darkColor));
            this.description.setTextColor(getResources().getColor(R.color.darkColor));
        }
        this.installButton.setBackgroundColor(ButtonTintColor);
        this.installText.setTextColor(ButtonTextColor);
        this.adColor.setBackgroundColor(ButtonTextColor);
        this.ad.setTextColor(ButtonTintColor);
        if (this.onBannerListener != null) {
            this.onBannerListener.onBannerAdLoaded();
        }

        this.loadIcon(data_icons);
        this.name.setText(data_name);
        this.description.setText(short_description);
        this.installText.setText(button_text);
        this.animation();
        this.installButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (BannerPromote.this.onBannerListener != null) {
                    BannerPromote.this.onBannerListener.onBannerAdClicked();
                }

                switch (( UltimateAds.response.getMoreApps().getMyAppsBanners().get(index)).getType()) {
                    case 0:
                        UltimateAds.OpenAmazon(BannerPromote.this.context, data_packageName);
                        break;
                    case 1:
                        BannerPromote.this.context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(data_link)));
                }

            }
        });
    }

    private void loadIcon(String icons) {
        this.bannerProgress.setVisibility(View.VISIBLE);
        Glide.with(this.context).load(icons).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                BannerPromote.this.bannerProgress.setVisibility(View.GONE);
                return false;
            }
        }).into(this.icon);
    }

    private void animation() {

        for (int i = 1; i < 3; i++) {

            int finalI = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animationIndexX(finalI);
                    if (finalI == 3 - 1) {
                        animation();
                    }
                }
            }, 1000L * i * ANIMATION_SPEED);
        }

    }

    private void animationIndexX(int index) {
        switch (index) {
            case 1:
                this.setAnimationGone(this.content2);
                this.setAnimationVisible(this.content1);
                break;
            case 2:
                this.setAnimationGone(this.content1);
                this.setAnimationVisible(this.content2);
                break;
            case 3:
                this.setAnimationGone(this.content2);
                this.setAnimationVisible(this.content1);
        }

    }

    private void setAnimationVisible(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(1.0F);
        view.setAnimation(AnimationUtils.loadAnimation(this.context, R.anim.bottom_up));
    }

    private void setAnimationGone(View view) {
        view.setVisibility(View.GONE);
        view.setAlpha(0.3F);
        view.setAnimation(AnimationUtils.loadAnimation(this.context, R.anim.bottom_down));
    }

    public void setOnBannerListener(OnBannerListener onBannerListener) {
        this.onBannerListener = onBannerListener;
    }


}
