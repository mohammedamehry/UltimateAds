package lib.ultimateadsam;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import amehry.ultimateads.UltimateAds;
import amehry.ultimateads.ads.BannerPromote;
import amehry.ultimateads.ads.Interfaces.OnBannerListener;
import amehry.ultimateads.ads.Interfaces.OnInterstitialAdListener;
import amehry.ultimateads.ads.Interfaces.OnNativeListener;
import amehry.ultimateads.ads.InterstitialPromote;
import amehry.ultimateads.ads.NativePromote;
import amehry.ultimateads.interfaces.DataFetchListener;
import lib.ultimateadsam.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private InterstitialPromote interstitialPromote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UltimateAds.isTestMode = true;
        UltimateAds.fetchData(this, "https://api.npoint.io/d63be1cd5349548b4ab8", new DataFetchListener() {
            @Override
            public void onDataFetched() {
                binding.ShowBanner.setEnabled(true);
                binding.ShowNative.setEnabled(true);
                binding.LoadIter.setEnabled(true);

            }

            @Override
            public void onDataFetchError(String error) {
                Toast.makeText(MainActivity.this, "data failed", Toast.LENGTH_SHORT).show();
            }
        });

        binding.ShowBanner.setOnClickListener(v -> {

            BannerPromote bannerPromote = new BannerPromote(MainActivity.this);
            bannerPromote.setOnBannerListener(new OnBannerListener() {
                @Override
                public void onBannerAdLoaded() {

                    binding.bannerContainer.addView(bannerPromote);
                }

                @Override
                public void onBannerAdClicked() {

                }

                @Override
                public void onBannerAdFailedToLoad(String var1) {

                }
            });
            bannerPromote.showPromoteBanner();
        });

        binding.ShowNative.setOnClickListener(v -> {
            NativePromote nativePromote = new NativePromote(MainActivity.this);
            nativePromote.setOnNativeListener(new OnNativeListener() {
                @Override
                public void onNativeAdLoaded() {
                    binding.nativeContainer.addView(nativePromote);
                }

                @Override
                public void onNativeAdClicked() {

                }

                @Override
                public void onNativeAdFailedToLoad(String var1) {

                }
            });
            nativePromote.showPromoteNative();
        });

        binding.LoadIter.setOnClickListener(v -> {
             interstitialPromote = new InterstitialPromote(MainActivity.this);
             interstitialPromote.setOnInterstitialAdListener(new OnInterstitialAdListener() {
                 @Override
                 public void onInterstitialAdLoaded() {
                     binding.ShowInter.setEnabled(true);
                 }

                 @Override
                 public void onInterstitialAdClosed() {

                 }

                 @Override
                 public void onInterstitialAdClicked() {

                 }

                 @Override
                 public void onInterstitialAdFailedToLoad(String var1) {

                 }
             });
        });

        binding.ShowInter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialPromote.isAdLoaded()){
                    interstitialPromote.setOnInterstitialAdListener(new OnInterstitialAdListener() {
                        @Override
                        public void onInterstitialAdLoaded() {

                        }

                        @Override
                        public void onInterstitialAdClosed() {
                            binding.ShowInter.setEnabled(false);
                        }

                        @Override
                        public void onInterstitialAdClicked() {

                        }

                        @Override
                        public void onInterstitialAdFailedToLoad(String var1) {
                            binding.ShowInter.setEnabled(false);
                        }
                    });
                    interstitialPromote.show();
                }else {
                    Toast.makeText(MainActivity.this, "Ad not loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}