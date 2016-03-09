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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import amai.org.conventions.R;

/**
 * A layout that arranges its children in relation to a background image. The
 * layout of each child is specified in image coordinates (pixels), and the
 * conversion to screen coordinates is performed automatically.
 * <p>The background image is adjusted so that it fills the available space. The exact
 * details of this adjustment are controlled by the custom:fit and android:gravity 
 * attributes
 * <p>For some applications this might be a useful replacement for the now
 * deprecated AbsoluteLayout.
 */
public class ImageLayout extends ViewGroup {
    
    /**
     * The image is made to fill the available vertical space, and may be cropped 
     * horizontally if there is not enough space. 
     * <p>If there is too much horizontal space it is left blank. 
     * <p>The vertical position of the image is controlled by the android:gravity attribute
     */
    public static final int FIT_VERTICAL = 0;
    /**
     * The image is made to fill the available horizontal space, and may be cropped 
     * vertically if there is not enough space. 
     * <p>If there is too much vertical space it is left blank. 
     * <p>The vertical position of the image is controlled by the android:gravity attribute
     */
    public static final int FIT_HORIZONTAL = 1;

    /**
     * The image fills the available space both vertically and horizontally. 
     * <p>If the aspect ratio of the image does not match exactly the aspect ratio
     * of the available space, the image is cropped either vertically or horizontally,
     * depending on which provides the best fit
     */
    public static final int FIT_BOTH = 2;
 
    /**
     * The image is made to fill the available space vertically in portrait mode
     * and horizontally in landscape. 
     * <p>This is the default mode.
     * <p>Note that the library does not determine the orientation based on the 
     * actual device orientation, but on the relative aspect ratios of the image
     * and the view.
     */
    public static final int FIT_AUTO = 3;

    /**
     * The fit mode that will be used in case the user does not specify one
     */
    public static final int DEFAULT_FIT_MODE = FIT_AUTO;

	private ImageResource image;
    private Rect destRect;
    private float imageWidth;
    private float imageHeight;
    private Rect srcRect;
    private ImageFitter fitter;
    private int fitMode = DEFAULT_FIT_MODE;
    private int gravity = -1;

    public ImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        parseAttributes(attrs);
    }

    private void parseAttributes(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ImageLayout);
        Drawable drawable = a.getDrawable(R.styleable.ImageLayout_image);
        if (drawable != null) {
	        if (!(drawable instanceof BitmapDrawable) && !(drawable instanceof PictureDrawable)) {
	            throw new RuntimeException("Drawable resource in layout description file must be of type \"BitmapDrawable\" or \"PictureDrawable\"");
	        }

	        image = getImageResourceFromDrawable(drawable);
	        srcRect = imageRect(image);
        }

        imageWidth = a.getInteger(R.styleable.ImageLayout_imageWidth, -1);
        imageHeight = a.getInteger(R.styleable.ImageLayout_imageHeight, -1);

        int fitMode = a.getInt(R.styleable.ImageLayout_fit, this.fitMode);
        setFitMode(fitMode);

        int gravity = a.getInt(R.styleable.ImageLayout_android_gravity, this.gravity);
        setGravity(gravity);
        a.recycle();
    }

    /**
     * Determines how the background image is drawn
     * @param newValue Accepted values are {@link ImageLayout#FIT_BOTH}, {@link ImageLayout#FIT_AUTO},
     *        {@link ImageLayout#FIT_VERTICAL} and {@link ImageLayout#FIT_HORIZONTAL} 
     */
    public void setFitMode(int newValue) {
        if (fitter != null && fitMode == newValue) {
            return;
        }
        fitMode = newValue;
        rebuildFitter();
    }
    
    public void setGravity(int newValue) {
        if (fitter != null && gravity == newValue) {
            return;
        }
        if ((newValue & Gravity.HORIZONTAL_GRAVITY_MASK) == 0) {
            newValue |= Gravity.CENTER_HORIZONTAL;
        }
        if ((newValue & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
            newValue |= Gravity.CENTER_VERTICAL;
        }
        gravity = newValue;
        rebuildFitter();
    }
    
    public int getFitMode() {
        return fitMode;
    }

    /**
     * Changes the background image and its layout dimensions.
     */
    public void setImageResource(int imageResource, float imageWidth, float imageHeight) {
	    setImageResourceFromDrawable(getResources().getDrawable(imageResource), imageWidth, imageHeight);
    }

	public void setImageResourceFromDrawable(Drawable image, float imageWidth, float imageHeight) {
		setImageResource(getImageResourceFromDrawable(image), imageWidth, imageHeight);
	}

	private ImageResource getImageResourceFromDrawable(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return new BitmapImageResource(((BitmapDrawable) drawable).getBitmap());
		} else if (drawable instanceof PictureDrawable) {
			return new PictureImageResource(((PictureDrawable) drawable).getPicture());
		}
		throw new RuntimeException("Drawable must be of type \"BitmapDrawable\" or \"PictureDrawable\"");
    }

	public void setImageResource(ImageResource image, float imageWidth, float imageHeight) {
		this.image = image;
		srcRect = imageRect(image);

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;

		rebuildFitter();
	}

    private static Rect imageRect(ImageResource image) {
        return new Rect(0, 0, image.getWidth(), image.getHeight());
    }

    private void rebuildFitter() {
        fitter = new ImageFitter(fitMode, gravity);
        requestLayout();
        invalidate();
    }

    private int transformWidthFromImageToView(float w) {
        float widthRatio = destRect.width() / imageWidth;
        return Math.round(w * widthRatio);
    }

    private int transformHeightFromImageToView(float h) {
        float heightRatio = destRect.height() / imageHeight;
        return Math.round(h * heightRatio);
    }

    private int transformXFromImageToView(float x) {
        float widthRatio = destRect.width() / imageWidth;
        return destRect.left + Math.round(x * widthRatio);
    }

    private int transformYFromImageToView(float y) {
        float heightRatio = destRect.height() / imageHeight;
        return destRect.top + Math.round(y * heightRatio);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	    image.draw(canvas, srcRect, destRect);
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int N = getChildCount();
        for (int i = 0; i < N; ++i) {
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            child.layout(layoutParams.transformedRect.left, layoutParams.transformedRect.top, layoutParams.transformedRect.right, layoutParams.transformedRect.bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int width = widthSpec;
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);
        int height = heightSpec;
        boolean isExactWidth = widthMode == MeasureSpec.EXACTLY;
        boolean isExactHeight = heightMode == MeasureSpec.EXACTLY;
        float imageAspectRatio = (image.getWidth() + getPaddingLeft() + getPaddingRight())
                                    / ((float)image.getHeight() + getPaddingTop() + getPaddingBottom());
        if (isExactWidth && !isExactHeight) {
            height = (int)(width / imageAspectRatio);
            if (heightMode == MeasureSpec.AT_MOST && height > heightSpec) {
                height = heightSpec;
            }
        }
        else if (isExactHeight && !isExactWidth) {
	        ViewGroup parent = (ViewGroup) getParent();
	        int parentWiidth = parent.getMeasuredWidth();
	        switch (getLayoutParams().width) {
		        case ViewGroup.LayoutParams.MATCH_PARENT:
			        width = parentWiidth;
			        break;
		        default:
			        // If we have WRAP_CONTENT we want to make the width at least the available
			        // space in the parent so it will appear centered (it could be more to keep the aspect ratio).
			        // This is a workaround for this view being inside HorizontalScrollView (because it cannot be
			        // centered there).
		            width = Math.max(parentWiidth, (int) (height * imageAspectRatio));
			        break;
	        }

	        if (widthMode == MeasureSpec.AT_MOST && width > widthSpec) {
		        width = widthSpec;
	        }
        }

        setMeasuredDimension(width, height);

        int effectiveWidth = width - getPaddingLeft() - getPaddingRight();
        int effectiveHeight = height - getPaddingTop() - getPaddingBottom();
        destRect = fitter.fit(image, effectiveWidth, effectiveHeight);
        adjustDestRectForPadding();

        int N = getChildCount();
        for (int i = 0; i < N; ++i) {
            View child = getChildAt(i);
            measureChild(child);
        }
    }

    private void measureChild(View child) {
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        checkChildLayoutParams(layoutParams);
        int wspec = makeWidthSpec(layoutParams);
        int hspec = makeHeightSpec(layoutParams);
        child.measure(wspec, hspec);
        int left = 0, width = child.getMeasuredWidth();
        if (layoutParams.left != -1) {
            left = transformXFromImageToView(layoutParams.left);
            if (layoutParams.right != -1) {
                int right = transformXFromImageToView(layoutParams.right);
                width = right - left;
            }
        } else if (layoutParams.right != -1) {
            int right = transformXFromImageToView(layoutParams.right);
            left = right - width;
        } else if (layoutParams.centerX != -1) {
            int cx = transformXFromImageToView(layoutParams.centerX);
            left = cx - width / 2;
        }

        int top = 0, height = child.getMeasuredHeight();
        if (layoutParams.top != -1) {
            top = transformYFromImageToView(layoutParams.top);
            if (layoutParams.bottom != -1) {
                int bottom = transformYFromImageToView(layoutParams.bottom);
                height = bottom - top;
            }
        } else if (layoutParams.bottom != -1) {
            int bottom = transformYFromImageToView(layoutParams.bottom);
            top = bottom - height;
        } else if (layoutParams.centerY != -1) {
            int cy = transformYFromImageToView(layoutParams.centerY);
            top = cy - height / 2;
        }
        layoutParams.transformedRect.set(left, top, left + width, top + height);
    }

    private void adjustDestRectForPadding() {
        destRect.left += getPaddingLeft();
        destRect.right += getPaddingLeft();
        destRect.top += getPaddingTop();
        destRect.bottom += getPaddingTop();
    }

    private int makeHeightSpec(LayoutParams layoutParams) {
        int hspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        if (layoutParams.maxHeight != -1) {
            int height = transformHeightFromImageToView(layoutParams.maxHeight);
            hspec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        } else if (layoutParams.height != LayoutParams.WRAP_CONTENT) {
            int height = transformHeightFromImageToView(layoutParams.height);
	        ((ViewGroup.LayoutParams) layoutParams).height = height;
            hspec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        return hspec;
    }

    private int makeWidthSpec(LayoutParams layoutParams) {
        int wspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        if (layoutParams.maxWidth != -1) {
            int maxWidth = transformWidthFromImageToView(layoutParams.maxWidth);
            wspec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
        } else if (layoutParams.width != LayoutParams.WRAP_CONTENT) {
            int width = transformWidthFromImageToView(layoutParams.width);
	        ((ViewGroup.LayoutParams) layoutParams).width = width;
            wspec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }
        return wspec;
    }

    private void checkChildLayoutParams(LayoutParams layoutParams) {
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        // In image coords
        public float maxWidth = -1, maxHeight = -1;
        public float left = -1, top = -1, right = -1, bottom = -1;
        public float centerX = -1, centerY = -1;
	    public float width = -1, height = -1;
        // In view coords
        Rect transformedRect = new Rect();

        public LayoutParams() {
            this(null, null);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            // We don't call super(c, attrs) to prevent the layout_width and
            // layout_height
            // attributes from being mandatory
            super(WRAP_CONTENT, WRAP_CONTENT);

            if (c == null || attrs == null) {
                return;
            }

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ImageLayout_Layout);

            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                case R.styleable.ImageLayout_Layout_layout_left:
                    left = a.getInt(attr, -1);
                    break;
                case R.styleable.ImageLayout_Layout_layout_top:
                    top = a.getInt(attr, -1);
                    break;
                case R.styleable.ImageLayout_Layout_layout_right:
                    right = a.getInt(attr, -1);
                    break;
                case R.styleable.ImageLayout_Layout_layout_bottom:
                    bottom = a.getInt(attr, -1);
                    break;
                case R.styleable.ImageLayout_Layout_layout_centerX:
                    centerX = a.getInt(attr, -1);
                    break;
                case R.styleable.ImageLayout_Layout_layout_centerY:
                    centerY = a.getInt(attr, -1);
                    break;
                case R.styleable.ImageLayout_Layout_layout_width:
                    width = a.getInt(attr, -1);
                    break;
                case R.styleable.ImageLayout_Layout_layout_maxWidth:
                    maxWidth = a.getInt(attr, -1);
                    break;
                case R.styleable.ImageLayout_Layout_layout_height:
                    height = a.getInt(attr, -1);
                    break;
                case R.styleable.ImageLayout_Layout_layout_maxHeight:
                    maxHeight = a.getInt(attr, -1);
                    break;
                }
            }
            a.recycle();
        }

        LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

	public interface ImageResource {
		int getHeight();
		int getWidth();
		void draw(Canvas canvas, Rect srcRect, Rect destRect);
	}

	public class BitmapImageResource implements ImageResource {
		private Bitmap bitmap;
		public BitmapImageResource(Bitmap bitmap) {
			this.bitmap = bitmap;
		}
		@Override
		public int getHeight() {
			return bitmap.getHeight();
		}

		@Override
		public int getWidth() {
			return bitmap.getWidth();
		}

		@Override
		public void draw(Canvas canvas, Rect srcRect, Rect destRect) {
			canvas.drawBitmap(bitmap, srcRect, destRect, null);
		}
	}

	public class PictureImageResource implements ImageResource {
		private Picture picture;
		public PictureImageResource(Picture picture) {
			this.picture = picture;
		}
		@Override
		public int getHeight() {
			return picture.getHeight();
		}

		@Override
		public int getWidth() {
			return picture.getWidth();
		}

		@Override
		public void draw(Canvas canvas, Rect srcRect, Rect destRect) {
			canvas.drawPicture(picture, destRect);
		}
	}
}