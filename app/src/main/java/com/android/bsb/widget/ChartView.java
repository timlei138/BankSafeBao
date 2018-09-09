package com.android.bsb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.OverScroller;

import com.android.bsb.R;
import com.android.bsb.bean.StepData;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends View {

    private String TAG = getClass().getSimpleName();
    //折线
    private Paint mLinePaint;
    //转点
    private Paint mPointPaint;
    //底部星期及底部线
    private Paint mWeeksPaint;


    private int mLabelColor;
    private int mDataColor;
    private int mLabelTextSize;

    private int defaultWidth;
    private int defaultHeight;

    private int layoutHeight,layoutWidth;


    private int paddingLeft,paddingRight,paddingTop,paddintBottom;

    private int xAxis;

    private float maxSize = 20000f;


    private float DEFAULT_SETP = 10000f;

    private List<int[]> mPointList = new ArrayList<>();

    private List<StepData> mDataList = new ArrayList<>();

    private float[] startEndPoint = new float[2];

    private int lastClickIndex = -1;

    private int halfRectWidth = 30;


    public ChartView(Context context) {
        this(context,null);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }



    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setLayerType(LAYER_TYPE_SOFTWARE,mLinePaint);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.ChartView,defStyleAttr,0);

        mLabelColor = typedArray.getColor(R.styleable.ChartView_label_color, Color.BLACK);

        mLabelTextSize = typedArray.getDimensionPixelSize(R.styleable.ChartView_label_size,32);

        mDataColor = typedArray.getColor(R.styleable.ChartView_data_color,Color.BLACK);

        typedArray.recycle();


        defaultWidth = defaultHeight = getResources().getDimensionPixelSize(R.dimen.chart_minsize);

        paddingLeft  = paddingRight = getResources().getDimensionPixelOffset(R.dimen.chart_padding_leftright);

        paddingTop = paddintBottom = getResources().getDimensionPixelOffset(R.dimen.chart_paddingtopbottom);

        halfRectWidth = getResources().getDimensionPixelOffset(R.dimen.chart_rect_click);

        mPointPaint = new Paint();
        mPointPaint.setStyle(Paint.Style.STROKE);
        mPointPaint.setStrokeWidth(4);
        mPointPaint.setColor(mDataColor);
        mPointPaint.setDither(true);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setColor(mDataColor);

        mWeeksPaint = new Paint();
        mWeeksPaint.setTextSize(mLabelTextSize);
        mWeeksPaint.setAntiAlias(true);
        mWeeksPaint.setFakeBoldText(true);
        mWeeksPaint.setDither(true);
        mWeeksPaint.setColor(mLabelColor);
        setClickable(true);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY){
            layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        }else{
            layoutWidth = defaultWidth;
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.EXACTLY){
            layoutHeight = MeasureSpec.getSize(heightMeasureSpec);
        }else{
            layoutHeight = defaultHeight;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();

                int index = getSelectIndex((int) x,(int) y);
                AppLogger.LOGD(TAG,"index:"+index+",lastClickIndex:"+lastClickIndex);
                if(lastClickIndex != index) {
                    lastClickIndex = index;
                    invalidateView();
                }
        }

        return super.onTouchEvent(event);
    }




    private int getSelectIndex(int x,int y){
        int i = 0;
        for (int[] tmp : mPointList){
            Rect rect = new Rect(tmp[0] - halfRectWidth  ,tmp[1] - halfRectWidth  , tmp[0] + halfRectWidth,tmp[1]+halfRectWidth);
            AppLogger.LOGD(TAG,"rect:"+rect.toString()+",x:"+x+",Y:"+y);
            if(rect.contains(x,y)){
                return i;
            }
            i++;
        }
        return -1;


    }



    private void invalidateView(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
            postInvalidateOnAnimation();
        }else{
            invalidate();
        }
    }


    private float getFloatWindowHeight(){
        Paint.FontMetrics fontMetrics = mWeeksPaint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w >0 && h > 0){
            layoutHeight = h;
            layoutWidth = w;
        }
    }

    public void setData(List<StepData> datas){
        mDataList.clear();
        mDataList = datas;
        updateMaxValue();
        invalidateView();
    }



    private void updateMaxValue(){
        float max = 0;
        for (StepData data : mDataList){
            if(data.getStep() > max){
                max = data.getStep();
            }
        }
        if(max > maxSize){
            float rate = max / 10000;
            maxSize = (rate + 1) * 10000f;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        //top line
        mLinePaint.setPathEffect(null);
        canvas.drawLine(paddingLeft,paddingTop ,layoutWidth - paddingLeft,paddingTop,mLinePaint);

        Paint.FontMetrics fontMetrics = mWeeksPaint.getFontMetrics();

        float weeksLabelHeight = fontMetrics.descent - fontMetrics.ascent;
        //bottom line
        canvas.drawLine(paddingLeft,layoutHeight - paddintBottom - weeksLabelHeight,layoutWidth - paddingRight,
                layoutHeight - paddintBottom - weeksLabelHeight   ,mLinePaint);

        float avabilHeight = layoutHeight - paddingTop -paddintBottom - weeksLabelHeight - getFloatWindowHeight();
        float rate  = avabilHeight / maxSize;
        xAxis = (layoutWidth - paddingLeft - paddingRight) / 7;
        float startTop = paddingTop + getFloatWindowHeight();

        mLinePaint.setPathEffect(new DashPathEffect(new float[]{4,4},0));

        canvas.drawLine(paddingLeft,startTop + (avabilHeight- (DEFAULT_SETP * rate)),layoutWidth - paddingRight,
                startTop + (avabilHeight- (DEFAULT_SETP * rate)) ,mLinePaint);
        mPointPaint.setStyle(Paint.Style.STROKE);
        mPointPaint.setShader(null);
        mPointPaint.setColor(mDataColor);
        Path linePath = new Path();
        float labelY = layoutHeight - paddintBottom ;
        mPointList.clear();
        for (int i = 0; i< mDataList.size();i++){
            String label = mDataList.get(i).getToday();
            float  labelX = (paddingLeft + xAxis * i) + mWeeksPaint.measureText(label) / 2 ;
            canvas.drawText(label,labelX,labelY,mWeeksPaint);
            AppLogger.LOGD(TAG,"label:"+label+",labelX:"+labelX+",labelY:"+labelY);
            float dataY = startTop + (avabilHeight - (mDataList.get(i).getStep() * rate));
            float dataX = (paddingLeft + xAxis * i) + mWeeksPaint.measureText(label);
            canvas.drawPoint(dataX,dataY,mPointPaint);
            if(lastClickIndex != -1 && lastClickIndex == i){
                String data = mDataList.get(lastClickIndex).getStep()+"";
                canvas.drawText(data,dataX - mWeeksPaint.measureText(data)/2,dataY - 5,mWeeksPaint);
            }

            mPointList.add(new int[]{(int) dataX,(int) dataY});
            AppLogger.LOGD(TAG,"data:"+mDataList.get(i).getStep()+",dataX:"+dataX+",dataY:"+dataY);
            if(i==0){
                linePath.moveTo(dataX,dataY);
                startEndPoint[0] = dataX;
            }else{
                linePath.lineTo(dataX,dataY);
            }

            if(i == mDataList.size()-1){
                startEndPoint[1] = dataX;
            }


        }
        canvas.drawPath(linePath,mPointPaint);
        mPointPaint.setShader(getLinearGradient(paddingLeft,startTop,paddingLeft,layoutHeight));
        mPointPaint.setStyle(Paint.Style.FILL);
        Path gradientPath = new Path(linePath);
        gradientPath.lineTo(startEndPoint[1],layoutHeight - paddintBottom - weeksLabelHeight);
        gradientPath.lineTo(startEndPoint[0],layoutHeight - paddintBottom - weeksLabelHeight);
        gradientPath.close();
        canvas.drawPath(gradientPath,mPointPaint);

    }



    private LinearGradient getLinearGradient(float startX,float startY,float endX,float endY){
        LinearGradient gradient = new LinearGradient(startX,startY,endX,endY,getResources().getColor(R.color.ColorBackground),Color.TRANSPARENT,Shader.TileMode.CLAMP);
        return gradient;
    }

}
