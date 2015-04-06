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
    private final Context context;

    private final Path apple;
    private final Path leaf;
    private final Path stick;

    private float leafAnchorX = 0.0f;
    private float leafAnchorY = 0.0f;

    private final Paint applePaint;
    private final Paint leafPaint;
    private final Paint stickPaint;


    // transformations (paths are kept in their initial state until onDraw start drawing)
    private Matrix appleTransform;
    private Matrix leafTransform;
    private Matrix stickTransform;

    private int width = 0;
    private int height = 0;

    private boolean arePathsPrepared = false;

    public AppleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        Matrix noTransform = new Matrix();

        // parsing paths from xml (they stored in svg-path style)

        appleTransform = new Matrix();
        apple = PathParser.parsePath(context.getString(R.string.apple_curve), noTransform);

        leafTransform = new Matrix();
        leaf = PathParser.parsePath(context.getString(R.string.apple_leaf_curve), noTransform);
        float[] dv = PathParser.readPoint(context.getString(R.string.leaf_translation));
        leafTransform.postTranslate(dv[0], dv[1]);

        stickTransform = new Matrix();
        stick = PathParser.parsePath(context.getString(R.string.apple_stick_curve), noTransform);
        dv = PathParser.readPoint(context.getString(R.string.stick_translation));
        stickTransform.postTranslate(dv[0], dv[1]);

        // creating paint settings for apple
        applePaint = new Paint();
        leafPaint = new Paint();
        stickPaint = new Paint();

        applePaint.setDither(true);
        applePaint.setStyle(Paint.Style.FILL);
        applePaint.setColor(context.getResources().getColor(R.color.apple_main_color));
        applePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        leafPaint.setDither(true);
        leafPaint.setStyle(Paint.Style.FILL);
        leafPaint.setColor(context.getResources().getColor(R.color.apple_leaf_color));
        leafPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        stickPaint.setDither(true);
        stickPaint.setStyle(Paint.Style.FILL);
        stickPaint.setColor(context.getResources().getColor(R.color.apple_stick_color));
        stickPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        // Listening for layout to track changes in width and height (it's actually not necessary to always listen,
        // because layout is pretty static, but whatever!)
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //now we can retrieve the width and height
                width = getMeasuredWidth();
                height = getMeasuredHeight();
                prepareApple();

            }
        });

    }

    /**
     * Returns bounding rect of an apple. (The very top - is the highest point of the leaf,
     * and the bottom - is the lowest point of the main part of apple)
     * @return bounding rect of an apple
     */
    private RectF getAppleRect() {
        RectF appleRect = new RectF();
        apple.computeBounds(appleRect, true);
        appleTransform.mapRect(appleRect); // need to apply transform to get a correct bounding rect...
        RectF leafRect = new RectF();
        leaf.computeBounds(leafRect, true);
        leafTransform.mapRect(leafRect);
        appleRect.union(leafRect);
        return appleRect;
    }

    private void scaleApple() {
        RectF rect = getAppleRect();
        float scaleFactor = Math.min(width / rect.width(), height / rect.height());
        scaleFactor /= 1.2;

        appleTransform.postScale(scaleFactor, scaleFactor);
        leafTransform.postScale(scaleFactor, scaleFactor);
        stickTransform.postScale(scaleFactor, scaleFactor);
    }

    private void translateAppleToCenter() {
        RectF rect = getAppleRect();

        float xShift = ((width - rect.width()) / 2f) - rect.left;
        float yShift = ((height - rect.height()) / 2f) - rect.top;

        appleTransform.postTranslate(xShift, yShift);
        leafTransform.postTranslate(xShift, yShift);
        stickTransform.postTranslate(xShift, yShift);
    }

    private void prepareApple() {
        if (!arePathsPrepared) {
            scaleApple();
            translateAppleToCenter();

            apple.transform(appleTransform);
            leaf.transform(leafTransform);
            stick.transform(stickTransform);

            RectF r = new RectF();
            leafTransform.mapRect(r);
            leaf.computeBounds(r, true);
            leafAnchorX = r.right;
            leafAnchorY = r.bottom;

            arePathsPrepared = true;
        }
    }

    private Matrix newTransform = new Matrix();
    private Matrix invTransform = new Matrix();
    private float curScale = 0.025f;
    private float curAngle = 90f;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(apple, applePaint);
        canvas.drawPath(stick, stickPaint);

        float GOAL_SCALE = 1.0f;
        if (curScale < GOAL_SCALE)
            newTransform.setScale(curScale, curScale, leafAnchorX, leafAnchorY);
        float GOAL_ANGLE = 0.0f;
        if (curAngle > GOAL_ANGLE)
            newTransform.postRotate(curAngle, leafAnchorX, leafAnchorY);

        leaf.transform(newTransform);
        canvas.drawPath(leaf, leafPaint);

        newTransform.invert(invTransform);
        leaf.transform(invTransform);


        if (curScale < GOAL_SCALE || curAngle > GOAL_ANGLE) {
            float SCALE_STEP = 0.025f;
            curScale += SCALE_STEP;
            float ANGLE_STEP = -2.25f;
            curAngle += ANGLE_STEP;
            this.invalidate();
        }
    }
}