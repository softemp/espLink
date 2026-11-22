package fatima.tech.espnetsmart;

import android.support.v4.view.ViewPager;

/* JADX INFO: loaded from: classes.dex */
public class CircularViewPagerHandler implements ViewPager.OnPageChangeListener {
    public static final int SET_ITEM_DELAY = 300;
    private ViewPager.OnPageChangeListener mListener;
    private ViewPager mViewPager;

    public CircularViewPagerHandler(ViewPager viewPager) {
        this.mViewPager = viewPager;
        this.mViewPager.setCurrentItem(1, false);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mListener = listener;
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageSelected(int position) {
        handleSetCurrentItemWithDelay(position);
        invokeOnPageSelected(position);
    }

    private void handleSetCurrentItemWithDelay(final int position) {
        this.mViewPager.postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.CircularViewPagerHandler.1
            @Override // java.lang.Runnable
            public void run() {
                CircularViewPagerHandler.this.handleSetCurrentItem(position);
            }
        }, 300L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetCurrentItem(int position) {
        int lastPosition = this.mViewPager.getAdapter().getCount() - 1;
        if (position == 0) {
            this.mViewPager.setCurrentItem(lastPosition - 1, false);
        } else if (position == lastPosition) {
            this.mViewPager.setCurrentItem(1, false);
        }
    }

    private void invokeOnPageSelected(int position) {
        if (this.mListener != null) {
            this.mListener.onPageSelected(position - 1);
        }
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int state) {
        invokeOnPageScrollStateChanged(state);
    }

    private void invokeOnPageScrollStateChanged(int state) {
        if (this.mListener != null) {
            this.mListener.onPageScrollStateChanged(state);
        }
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        invokeOnPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    private void invokeOnPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (this.mListener != null) {
            this.mListener.onPageScrolled(position - 1, positionOffset, positionOffsetPixels);
        }
    }
}
