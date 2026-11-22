package fatima.tech.espnetsmart;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes.dex */
public class QuickReturnFooterBehavior extends CoordinatorLayout.Behavior<View> {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private int mDySinceDirectionChange;
    private boolean mIsHiding;
    private boolean mIsShowing;

    public QuickReturnFooterBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & 2) != 0;
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if ((dy > 0 && this.mDySinceDirectionChange < 0) || (dy < 0 && this.mDySinceDirectionChange > 0)) {
            child.animate().cancel();
            this.mDySinceDirectionChange = 0;
        }
        this.mDySinceDirectionChange += dy;
        if (this.mDySinceDirectionChange > child.getHeight() && child.getVisibility() == 0 && !this.mIsHiding) {
            hide(child);
        } else if (this.mDySinceDirectionChange < 0 && child.getVisibility() == 4 && !this.mIsShowing) {
            show(child);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hide(final View view) {
        this.mIsHiding = true;
        ViewPropertyAnimator animator = view.animate().translationY(view.getHeight()).setInterpolator(INTERPOLATOR).setDuration(300L);
        animator.setListener(new Animator.AnimatorListener() { // from class: fatima.tech.espnetsmart.QuickReturnFooterBehavior.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator2) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                QuickReturnFooterBehavior.this.mIsHiding = false;
                view.setVisibility(4);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator2) {
                QuickReturnFooterBehavior.this.mIsHiding = false;
                if (!QuickReturnFooterBehavior.this.mIsShowing) {
                    QuickReturnFooterBehavior.this.show(view);
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator2) {
            }
        });
        animator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show(final View view) {
        this.mIsShowing = true;
        ViewPropertyAnimator animator = view.animate().translationY(0.0f).setInterpolator(INTERPOLATOR).setDuration(300L);
        animator.setListener(new Animator.AnimatorListener() { // from class: fatima.tech.espnetsmart.QuickReturnFooterBehavior.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator2) {
                view.setVisibility(0);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                QuickReturnFooterBehavior.this.mIsShowing = false;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator2) {
                QuickReturnFooterBehavior.this.mIsShowing = false;
                if (!QuickReturnFooterBehavior.this.mIsHiding) {
                    QuickReturnFooterBehavior.this.hide(view);
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator2) {
            }
        });
        animator.start();
    }
}
