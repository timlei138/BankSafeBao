package com.zhihu.matisse.filter;

import android.content.Context;

import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;

import java.util.Set;

public class ImageFilter extends Filter{
    @Override
    protected Set<MimeType> constraintTypes() {
        return MimeType.ofImage();
    }

    @Override
    public IncapableCause filter(Context context, Item item) {
        return null;
    }
}
