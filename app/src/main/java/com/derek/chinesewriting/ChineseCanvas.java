package com.derek.chinesewriting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class ChineseCanvas extends View {

    private Paint mPaint;
    private Path mPath;
    private ArrayList<Point> pathList;

    public ChineseCanvas(Context context) {
        this(context, null);
    }

    public ChineseCanvas(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChineseCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#f57c00"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(getResources().getDimension(R.dimen.paint_width));

        mPath = new Path();
        pathList = new ArrayList<>();
    }

    public ArrayList<Point> getPathList() {
        return pathList;
    }

    public void clear() {
        mPath.reset();
        pathList.clear();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(touchX, touchY);//重新设置即将出现的线的起点
                pathList.add(new Point(touchX, touchY));
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(touchX, touchY);//连线
                pathList.add(new Point(touchX, touchY));
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();//通知系统重绘
        return true;//要处理当前事件
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }
}
