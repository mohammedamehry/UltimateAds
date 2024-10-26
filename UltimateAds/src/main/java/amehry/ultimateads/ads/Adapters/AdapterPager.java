package amehry.ultimateads.ads.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import amehry.ultimateads.ads.Interfaces.OnPagerScreenListener;
import amehry.ultimateads.databinding.ItemsScreenBinding;


import java.util.ArrayList;

public class AdapterPager extends PagerAdapter {
   private final Context context;
   private final ArrayList<String> screenList;
   private OnPagerScreenListener onPagerScreenListener;

   public AdapterPager(Context context, ArrayList<String> screenList) {
      this.context = context;
      this.screenList = screenList;
   }

   public int getCount() {
      return this.screenList.size();
   }

   public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
      return view == object;
   }

   @NonNull
   public Object instantiateItem(@NonNull ViewGroup container, final int position) {
      LayoutInflater layoutInflater = LayoutInflater.from(context);
      ItemsScreenBinding binding = ItemsScreenBinding.inflate(layoutInflater, container, false);
      ImageView screen = binding.screen;
      final RelativeLayout loading = binding.interScreenProgress;
      loading.setVisibility(View.VISIBLE);
      ((RequestBuilder)Glide.with(this.context).load((String)this.screenList.get(position)).listener(new RequestListener<Drawable>() {
         public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            if (AdapterPager.this.onPagerScreenListener != null) {
               if (e != null) {
                  AdapterPager.this.onPagerScreenListener.onPagerScreenListenerFailed(e.getMessage());
               } else {
                  AdapterPager.this.onPagerScreenListener.onPagerScreenListenerFailed("Failed to get and load screen : " + position);
               }
            }

            Log.e("AdapterPagerAmehry000", (String)AdapterPager.this.screenList.get(position) + " -- onLoadFailed: " + e.getMessage());
            return false;
         }

         public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            loading.setVisibility(View.GONE);
            if (AdapterPager.this.onPagerScreenListener != null) {
               AdapterPager.this.onPagerScreenListener.onPagerScreenListenerLoaded();
            }

            return false;
         }
      }).diskCacheStrategy(DiskCacheStrategy.ALL)).into(screen);
      container.addView(binding.getRoot());
      return binding.getRoot();
   }

   public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
      ViewPager vp = (ViewPager)container;
      View view = (View)object;
      vp.removeView(view);
   }

   public void setOnPagerScreenListener(OnPagerScreenListener onPagerScreenListener) {
      this.onPagerScreenListener = onPagerScreenListener;
   }
}
