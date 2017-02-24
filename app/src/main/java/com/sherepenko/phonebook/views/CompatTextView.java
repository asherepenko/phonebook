package com.sherepenko.phonebook.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.sherepenko.phonebook.R;

public class CompatTextView extends AppCompatTextView {

    @DrawableRes
    private static final int NO_ID = -1;

    public CompatTextView(Context context) {
        this(context, null);
    }

    public CompatTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CompatTextView);

            Drawable drawableStart = null;
            Drawable drawableEnd = null;
            Drawable drawableTop = null;
            Drawable drawableBottom = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawableStart = attributes.getDrawable(R.styleable.CompatTextView_drawableStartCompat);
                drawableEnd = attributes.getDrawable(R.styleable.CompatTextView_drawableEndCompat);
                drawableTop = attributes.getDrawable(R.styleable.CompatTextView_drawableTopCompat);
                drawableBottom = attributes.getDrawable(R.styleable.CompatTextView_drawableBottomCompat);
            } else {
                @DrawableRes int drawableStartResId = attributes.getResourceId(R.styleable.CompatTextView_drawableStartCompat, NO_ID);
                @DrawableRes int drawableEndResId = attributes.getResourceId(R.styleable.CompatTextView_drawableEndCompat, NO_ID);
                @DrawableRes int drawableTopResId = attributes.getResourceId(R.styleable.CompatTextView_drawableTopCompat, NO_ID);
                @DrawableRes int drawableBottomResId = attributes.getResourceId(R.styleable.CompatTextView_drawableBottomCompat, NO_ID);

                if (drawableStartResId != -1) {
                    drawableStart = AppCompatResources.getDrawable(context, drawableStartResId);
                }

                if (drawableEndResId != -1) {
                    drawableEnd = AppCompatResources.getDrawable(context, drawableEndResId);
                }

                if (drawableBottomResId != -1) {
                    drawableBottom = AppCompatResources.getDrawable(context, drawableBottomResId);
                }

                if (drawableTopResId != -1) {
                    drawableTop = AppCompatResources.getDrawable(context, drawableTopResId);
                }
            }

            setCompoundDrawablesWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom);
            attributes.recycle();
        }
    }
}
