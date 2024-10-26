package amehry.devxcustomads.Adapters;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.PageTransformer;

public class PagerTransformer implements PageTransformer {
   private static final float MIN_SCALE = 0.85F;
   private final ViewPager viewPager;

   public PagerTransformer(ViewPager viewPager) {
      this.viewPager = viewPager;
   }

   public void transformPage(@NonNull View page, float position) {
      try {
         float percentage = 1.0F - Math.abs(position);
         float scaleAmountPercent = 5.0F;
         float amount = (100.0F - scaleAmountPercent + scaleAmountPercent * percentage) / 100.0F;
         float scaleFactor = Math.max(0.85F, percentage);
         int pageWidth = this.viewPager.getMeasuredWidth() - this.viewPager.getPaddingLeft() - this.viewPager.getPaddingRight();
         int paddingLeft = this.viewPager.getPaddingLeft();
         float transformPos = (float)(page.getLeft() - (this.viewPager.getScrollX() + paddingLeft)) / (float)pageWidth;
         if (transformPos < -1.0F) {
            this.setSize(page, scaleFactor);
         } else if (transformPos <= 1.0F) {
            this.setSize(page, amount);
         } else {
            this.setSize(page, scaleFactor);
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }

   }

   private void setSize(View page, float size) {
      page.animate().scaleY(size).scaleX(size).setInterpolator(new DecelerateInterpolator()).start();
   }

   private void setSize(View page, float position, float percentage) {
      page.setScaleX(position != 0.0F && position != 1.0F ? percentage : 1.0F);
      page.setScaleY(position != 0.0F && position != 1.0F ? percentage : 1.0F);
   }
}
