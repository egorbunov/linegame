package org.spbstu.linegame.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import org.spbstu.linegame.R;
import org.spbstu.svg.PathParser;

/**
 * Created by Egor Gorbunov on 05.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 *
 * That view is responsible for drawing an apple on the splash screen
 */
public class AppleView extends View {
    private final Path apple;
    private final Path leaf;
    private final Path stick;

    private float leafTranslation[] = {0.0f, 0.0f}; // Translation of the leaf to be on the right place near the apple
    private float stickTranslation[] = {0.0f, 0.0f}; // Translation of the stick to be on the right place near the apple

    private float toCenterTranslation[] = {0.0f, 0.0f};

    private final Paint applePaint;
    private final Paint leafPaint;
    private final Paint stickPaint;

    private int width = 0;
    private int height = 0;

    private float leafAnchor[] = {0.0f, 0, 0f};  // point, relatively to that leaf will be scaled
    private float appleAnchor[] = {0.0f, 0, 0f}; // point, relatively to that apple will be scaled
    private float stickAnchor[] = {0.0f, 0, 0f}; // point, relatively to that stick will be scaled

    float[] leafPoint = {0.0f, 0.0f}; // anchor point of the leaf moved to the correct position near apple
    float[] stickPoint = {0.0f, 0.0f}; // anchor point of the stick moved to the correct position near apple

    private Matrix appleTransformMat = new Matrix();
    private Matrix leafTransformMat = new Matrix();
    private Matrix stickTransformMat = new Matrix();
    private float maxScaleFactor = 1.0f;


    private void setupPaints(Context context) {
        // creating paint settings for apple
        applePaint.setDither(true);
        applePaint.setStyle(Paint.Style.FILL);
        applePaint.setColor(context.getResources().getColor(R.color.apple_main_color));
        applePaint.setAntiAlias(true);

        leafPaint.setDither(true);
        leafPaint.setStyle(Paint.Style.FILL);
        leafPaint.setColor(context.getResources().getColor(R.color.apple_leaf_color));
        leafPaint.setAntiAlias(true);


        stickPaint.setDither(true);
        stickPaint.setStyle(Paint.Style.FILL);
        stickPaint.setColor(context.getResources().getColor(R.color.apple_stick_color));
        stickPaint.setAntiAlias(true);

    }

    private void computeAnchorPoints() {
        RectF r = new RectF();

        apple.computeBounds(r, true);
        appleAnchor[0] = (r.right + r.left) / 2f;
        appleAnchor[1] = r.bottom;

        leaf.computeBounds(r, true);
        leafAnchor[0] = r.right;
        leafAnchor[1] = r.bottom;

        stick.computeBounds(r, true);
        stickAnchor[0] = r.left;
        stickAnchor[1] = r.bottom;

        leafPoint[0] = leafAnchor[0] + leafTranslation[0];
        leafPoint[1] = leafAnchor[1] + leafTranslation[1];
        stickPoint[0] = stickAnchor[0] + stickTranslation[0];
        stickPoint[1] = stickAnchor[1] + stickTranslation[1];
    }

    /**
     * Computes the maximum scale coefficient for apple to fit on the view
     */
    private void computeMaxScale() {
        RectF rect = getAppleRect();

        // 1.2f - magic number to fit apple better
        maxScaleFactor = Math.min(width / rect.width(), height / rect.height()) / 1.2f;
    }

    public AppleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Matrix noTransform = new Matrix();

        // parsing paths from xml (they stored in svg-path style)
        apple = PathParser.parsePath(context.getString(R.string.apple_curve), noTransform);

        leaf = PathParser.parsePath(context.getString(R.string.apple_leaf_curve), noTransform);
        leafTranslation = PathParser.readPoint(context.getString(R.string.leaf_translation));

        stick = PathParser.parsePath(context.getString(R.string.apple_stick_curve), noTransform);
        stickTranslation = PathParser.readPoint(context.getString(R.string.stick_translation));

        applePaint = new Paint();
        leafPaint = new Paint();
        stickPaint = new Paint();
        setupPaints(context);

        // Listening for layout to track changes in width and height (it's actually not necessary to always listen,
        // because layout is pretty static, but whatever!)
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //now we can retrieve the width and height
                width = getMeasuredWidth();
                height = getMeasuredHeight();

                // firstly transform leaf and stick matrices to represent a real looking apple
                // I need it because method getAppleRect() use that transformations to
                // compute the bounding apple rect, but if we do not do that transformations
                // leaf and stick will be not on the right place near apple
                leafTransformMat.setTranslate(leafTranslation[0], leafTranslation[1]);
                stickTransformMat.setTranslate(stickTranslation[0], stickTranslation[1]);


                computeMaxScale();

                // making apple fit into view
                Matrix m = new Matrix();
                m.setScale(maxScaleFactor, maxScaleFactor);
                apple.transform(m);
                leaf.transform(m);
                stick.transform(m);
                m.mapPoints(leafTranslation, leafTranslation);
                m.mapPoints(stickTranslation, stickTranslation);

                // computing translations
                computeToCenterTranslation();

                // resetting to the identity matrices
                leafTransformMat.reset();
                stickTransformMat.reset();


                computeAnchorPoints();
            }
        });
    }

    /**
     * Returns bounding rect of an apple. (The very top - is the highest point of the leaf,
     * and the bottom - is the lowest point of the main part of apple)
     *
     *
     * @return bounding rect of an apple, it transformed as to apple and leaf transformation matrices
     */
    private RectF getAppleRect() {
        RectF appleRect = new RectF();
        apple.computeBounds(appleRect, true);
        appleTransformMat.mapRect(appleRect); // need to apply transform to get a correct bounding rect...
        RectF leafRect = new RectF();
        leaf.computeBounds(leafRect, true);
        leafTransformMat.mapRect(leafRect);
        appleRect.union(leafRect);
        return appleRect;
    }

    RectF ttt;
    private void computeToCenterTranslation() {
        RectF rect = getAppleRect();

        ttt = getAppleRect();

        toCenterTranslation[0] = width / 2 - rect.centerX();
        toCenterTranslation[1] = height / 2 - rect.centerY();
    }

    public void startAppleAnimation() {
        isAnimationInProgress = true;
        invalidate();
    }


    boolean isAnimationInProgress = false;

    private float curScale = 0.3f;
    private float curAngle = -10f;
    private static final float GOAL_ANGLE = 0.0f;

    private void resetApple() {
        leafTransformMat.invert(leafTransformMat);
        stickTransformMat.invert(stickTransformMat);
        appleTransformMat.invert(appleTransformMat);

        leaf.transform(leafTransformMat);
        apple.transform(appleTransformMat);
        stick.transform(stickTransformMat);

        leafTransformMat.reset();
        stickTransformMat.reset();
        appleTransformMat.reset();
    }

    float[] newLeafPoint = {0.0f, 0.0f};
    float[] newStickPoint = {0.0f, 0.0f};

    private void prepareApple(float scaleFactor, float leafRotation) {
        resetApple();

        appleTransformMat.postScale(scaleFactor, scaleFactor, appleAnchor[0], appleAnchor[1]);
        leafTransformMat.postScale(scaleFactor, scaleFactor, leafAnchor[0], leafAnchor[1]);
        stickTransformMat.postScale(scaleFactor, scaleFactor, stickAnchor[0], stickAnchor[1]);
        leafTransformMat.postRotate(leafRotation, leafAnchor[0], leafAnchor[1]);

        appleTransformMat.postTranslate(toCenterTranslation[0], toCenterTranslation[1]);

        appleTransformMat.mapPoints(newLeafPoint, leafPoint);
        appleTransformMat.mapPoints(newStickPoint, stickPoint);

        leafTransformMat.postTranslate(newLeafPoint[0] - leafAnchor[0], newLeafPoint[1] - leafAnchor[1]);
        stickTransformMat.postTranslate(newStickPoint[0] - stickAnchor[0], newStickPoint[1] - stickAnchor[1]);

        leaf.transform(leafTransformMat);
        apple.transform(appleTransformMat);
        stick.transform(stickTransformMat);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float goalScale = maxScaleFactor;

        prepareApple(curScale, curAngle);

        if (curScale >= goalScale && curAngle >= GOAL_ANGLE) {
            isAnimationInProgress = false;
        }

        canvas.drawPath(stick, stickPaint);
        canvas.drawPath(apple, applePaint);
        canvas.drawPath(leaf, leafPaint);



        resetApple();

        if (isAnimationInProgress) {
            if (curScale < goalScale) {
                float SCALE_STEP = 0.03f;
                curScale += SCALE_STEP;
            }
            if (curAngle < GOAL_ANGLE) {
                float ANGLE_STEP = 0.5f;
                curAngle += ANGLE_STEP;
            }

            this.invalidate();
        }
    }
}