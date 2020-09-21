package com.sib4u.spymessenger;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EditText1 extends androidx.appcompat.widget.AppCompatEditText {

    public EditText1(@NonNull Context context, @Nullable AttributeSet attrs) {
        super ( context, attrs );
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        setCompoundDrawables ( null, null, icon, null );
    }
}
