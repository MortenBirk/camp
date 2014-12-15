package com.cac.camp.camp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by nikolaiollegaard on 15/12/14.
 */
public class ButtonFontAwesome extends Button{
    public ButtonFontAwesome(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface font = Typeface.createFromAsset( context.getAssets(), "fontawesome-webfont.ttf" );
        this.setTypeface(font);
    }

    public ButtonFontAwesome(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface font = Typeface.createFromAsset( context.getAssets(), "fontawesome-webfont.ttf" );
        this.setTypeface(font);
    }

    public ButtonFontAwesome(Context context) {
        super(context);
        Typeface font = Typeface.createFromAsset( context.getAssets(), "fontawesome-webfont.ttf" );
        this.setTypeface(font);
    }
}
