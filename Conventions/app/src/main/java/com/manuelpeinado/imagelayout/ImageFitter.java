/*
 * Copyright (C) 2013 Manuel Peinado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Changed by Tal Sapan on May 2015 - allow to use any type of image that can draw itself
 * instead of only Bitmap. Specific support for Picture was added.
 * This version is based on ImageLayout (https://github.com/ManuelPeinado/ImageLayout) version 1.1.0 from May 2015.
 */
package com.manuelpeinado.imagelayout;

import android.graphics.Rect;
import android.view.Gravity;

class ImageFitter {
    private int gravity;
    private int fitMode;

    ImageFitter(int mode, int gravity) {
        this.fitMode = mode;
        this.gravity = gravity;
    }

	Rect fit(ImageLayout.ImageResource image, int viewWidth, int viewHeight) {
        switch (fitMode) {
        case ImageLayout.FIT_VERTICAL:
				return fitVertical(image, viewWidth, viewHeight);
        case ImageLayout.FIT_HORIZONTAL:
				return fitHorizontal(image, viewWidth, viewHeight);
        case ImageLayout.FIT_BOTH:
				return fitBoth(image, viewWidth, viewHeight);
        default:
				return fitAuto(image, viewWidth, viewHeight);
        }
    }

    private Rect fitHorizontal(ImageLayout.ImageResource image, int w, int h) {
        float imageAspectRatio = computeImageAspectRatio(image);
        return fitHorizontal(w, h, imageAspectRatio);
    }

    private Rect fitVertical(ImageLayout.ImageResource image, int w, int h) {
        float imageAspectRatio = computeImageAspectRatio(image);
        return fitVertical(w, h, imageAspectRatio);
    }

    private Rect fitBoth(ImageLayout.ImageResource image, int w, int h) {
        float imageAspectRatio = computeImageAspectRatio(image);
        float viewAspectRatio = w / (float) h;
        if (imageAspectRatio < viewAspectRatio) {
            return fitHorizontal(w, h, imageAspectRatio);
        }
        return fitVertical(w, h, imageAspectRatio);
    }
    
    private Rect fitAuto(ImageLayout.ImageResource image, int w, int h) {
        float imageAspectRatio = computeImageAspectRatio(image);
        float viewAspectRatio = w / (float) h;
        if (imageAspectRatio > viewAspectRatio) {
            return fitHorizontal(w, h, imageAspectRatio);
        }
        return fitVertical(w, h, imageAspectRatio);
    }

    private Rect fitHorizontal(int w, int h, float imageAspectRatio) {
        int destWidth = w;
        int destHeight = (int) (destWidth / imageAspectRatio);
        int vGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        int top = 0;
        if (vGravity == Gravity.CENTER_VERTICAL) {
            top = h / 2 - destHeight / 2;
        }
        else if (vGravity == Gravity.BOTTOM) {
            top = h - destHeight;
        }
        return new Rect(0, top, destWidth, top + destHeight);
    }

    private Rect fitVertical(int w, int h, float imageAspectRatio) {
        int destHeight = h;
        int destWidth = (int) (destHeight * imageAspectRatio);
        int hGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        int left = 0;
        if (hGravity == Gravity.CENTER_HORIZONTAL) {
            left = w / 2 - destWidth / 2;
        }
        else if (hGravity == Gravity.RIGHT) {
            left = w - destWidth;
        }
        return new Rect(left, 0, left + destWidth, destHeight);
    }
    
    private static float computeImageAspectRatio(ImageLayout.ImageResource image) {
        return image.getWidth() / (float) image.getHeight();
    }
}
