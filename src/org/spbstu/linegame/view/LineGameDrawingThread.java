package org.spbstu.linegame.view;


import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;

import org.spbstu.linegame.R;
import org.spbstu.linegame.logic.LineGameLogic;
import org.spbstu.linegame.model.curve.Curve;
import org.spbstu.linegame.model.curve.CurvePoint;
import org.spbstu.linegame.utils.MyMath;

import java.util.concurrent.atomic.AtomicBoolean;

class LineGameDrawingThread extends Thread {
    private AtomicBoolean isThreadAlive;

    private final SurfaceHolder surfaceHolder;
    private Rect surfaceFrame;
    private LineGameLogic gameLogic;


    private final int tappedLineColor;
    private final int mainLineColor;
    private final int backgroundColor;


    public LineGameDrawingThread(SurfaceHolder surfaceHolder, Context context) {
        isThreadAlive = new AtomicBoolean(true);

        this.surfaceHolder = surfaceHolder;
        this.surfaceFrame = surfaceHolder.getSurfaceFrame();
        
        backgroundColor = context.getResources().getColor(R.color.main_background_color);
        tappedLineColor = context.getResources().getColor(R.color.tapped_line_color);
        mainLineColor = context.getResources().getColor(R.color.main_line_color);
    }

    public void kill() {
        isThreadAlive.set(false);
    }

    public void setGameLogic(LineGameLogic gameLogic) {
        if (gameLogic == null) {
            throw new NullPointerException();
        }

        this.gameLogic = gameLogic;
    }

    /**
     * Callback function, which is called from LineGameView.surfaceChanged(...) method.
     * @param width - new width of the view surface
     * @param height - new height of the view surface
     */
    public void resizeSurface(int width, int height) {
        surfaceFrame = surfaceHolder.getSurfaceFrame();
    }

    @Override
    public void run() {
        super.run();
        Canvas canvas = null;

        while(isThreadAlive.get()) {
            // drawing all the game stuff
            try {
                canvas = surfaceHolder.lockCanvas(null);
                if (canvas == null)
                    continue;

                canvas.drawColor(backgroundColor);
                //redrawBackground(canvas);
                drawLogic(canvas);
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            canvas = null;

        }
    }

    /**
     * Drawing line - main object of the view
     * @param canvas - just canvas
     */
    private void drawLogic(Canvas canvas) {
        if (canvas == null)
            return;

        Curve curve = gameLogic.getCurve();
        Paint paint = new Paint();

        CurvePoint prevPoint = null;
        for (CurvePoint curPoint : curve) {
        	// If width of the curve is big, line looks angular,
        	// if it's drew with segments. But if it's tiny and drawn with circles
        	// it become disconnected, so if the line is thick I draw only circles
        	// and add segments if not.
        	if (curPoint.isTapped()) {
        		paint.setColor(tappedLineColor);
        	} else {
        		paint.setColor(mainLineColor);
        	}
        	canvas.drawCircle(scaleWidth(curPoint.getX()), 
        			scaleHeight(curPoint.getY()), 
        			gameLogic.getLineThickness() / 2, 
        			paint);
        	
        	if (gameLogic.getLineThickness() < 75) {
        		paint.setStrokeWidth(gameLogic.getLineThickness());
	            if (prevPoint != null) {
	            	if (prevPoint.isTapped() == curPoint.isTapped()) {
	                    // if two points (previous and current) are both tapped or not tapped , we draw
	                    // whole segment with one color
	                    drawFullyColoredSegment(prevPoint, curPoint, canvas, paint);
	                } else {
	                    // here we draw the segment with two different colors (half with color
	                    // of the previous point and half with color of current)
	                	drawSemiColoredSegment(prevPoint, curPoint, canvas, paint);
	                }
	            }
        	}
            prevPoint = curPoint;
        }
        gameLogic.nextCurveFrame();
    }
    
    private void drawFullyColoredSegment(CurvePoint from, CurvePoint to, Canvas canvas, Paint paint) {
		if (from == null || to == null)
			throw new NullPointerException();
		
        // if two points (previous and current) are both tapped or not tapped , we draw
        // whole segment with one color
        paint.setColor(from.isTapped() ? tappedLineColor : mainLineColor);
        canvas.drawLine(scaleWidth(from.getX()), scaleHeight(from.getY()),
                scaleWidth(to.getX()), scaleHeight(to.getY()), paint);
    }

	private void drawSemiColoredSegment(CurvePoint from, CurvePoint to,
			Canvas canvas, Paint paint) {
		if (from == null || to == null)
			throw new NullPointerException();
		
		float midX;
		float midY;
		// here we draw the segment with two different colors (half with color
		// of the previous point and half with color of current)
		midX = (from.getX() + to.getX()) / 2f;
		midY = (from.getY() + to.getY()) / 2f;

		paint.setColor(from.isTapped() ? tappedLineColor : mainLineColor);
		canvas.drawLine(scaleWidth(from.getX()), scaleHeight(from.getY()),
		        scaleWidth(midX), scaleHeight(midY), paint);

		paint.setColor(to.isTapped() ? tappedLineColor : mainLineColor);
		canvas.drawLine(scaleWidth(midX), scaleHeight(midY),
		        scaleWidth(to.getX()), scaleHeight(to.getY()), paint);
	}

    /**
     * Scales the value in [0, 1] to [0, surface.width()]
     * @param val - value to scale
     * @return scaled value
     */
    private float scaleWidth(float val) {
        return val * surfaceFrame.width();
    }

    /**
     * Scales the value in [0, 1] to [0, surface.height()]
     * @param val - value to scale
     * @return scaled height
     */
    private float scaleHeight(float val) {
        return val * surfaceFrame.height();
    }
}
