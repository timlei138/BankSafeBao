package com.android.bsb.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.android.bsb.R;
import com.android.bsb.bean.EntryData;
import com.android.bsb.util.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends View {

    private String TAG = getClass().getSimpleName();

    private Paint mLabelPaint;
    private Paint mLinePaint;
    private Paint mDataPointPaint;

    private int mLabelColor;
    private int mDataColor;
    private int mLabelTextSize;

    private int defaultWidth;
    private int defaultHeight;

    private int layoutHeight,layoutWidth;


    private int paddingLeft,paddingRight,paddingTop,paddintBottom;

    private int yAxis,xAxis;

    private float maxSize = 20000f;

    private List<EntryData> mDataList = new ArrayList<>();


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

        mLabelTextSize = typedArray.getDimensionPixelSize(R.styleable.ChartView_label_size,20);

        mDataColor = typedArray.getColor(R.styleable.ChartView_data_color,Color.BLACK);

        typedArray.recycle();


        defaultWidth = defaultHeight = getResources().getDimensionPixelSize(R.dimen.chart_minsize);

        paddingLeft  = paddingRight = getResources().getDimensionPixelOffset(R.dimen.chart_padding_leftright);

        paddingTop = paddintBottom = getResources().getDimensionPixelOffset(R.dimen.chart_paddingtopbottom);

        mLabelPaint = new Paint();
        mLabelPaint.setTextSize(mLabelTextSize);
        mLabelPaint.setColor(mLabelColor);

        mDataPointPaint = new Paint();
        mDataPointPaint.setStyle(Paint.Style.STROKE);
        mDataPointPaint.setStrokeWidth(4);
        mDataPointPaint.setColor(mDataColor);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setColor(mDataColor);

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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w >0 && h > 0){
            layoutHeight = h;
            layoutWidth = w;
        }
    }

    public void setData(List<EntryData> datas){
        mDataList.clear();
        mDataList = datas;



        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint.FontMetrics fontMetrics = mLabelPaint.getFontMetrics();

        float labelHeight = fontMetrics.descent - fontMetrics.ascent;

        float avabilHeight = layoutHeight - paddintBottom - labelHeight;

        float rate  = avabilHeight / maxSize;

        mLinePaint.setPathEffect(null);
        canvas.drawLine(paddingLeft,layoutHeight - paddintBottom - labelHeight,layoutWidth - paddingRight,
                layoutHeight - paddintBottom - labelHeight   ,mLinePaint);

        mLinePaint.setPathEffect(new DashPathEffect(new float[]{4,4},0));

        canvas.drawLine(paddingLeft,(layoutHeight - paddintBottom - labelHeight) / 2,layoutWidth - paddingRight,
                (layoutHeight - paddintBottom - labelHeight) / 2 ,mLinePaint);


        xAxis = (layoutWidth - paddingLeft - paddingRight) / 7;
        AppLogger.LOGD(TAG,"xAxis:"+xAxis +",avabilHeight:"+ avabilHeight);
        Path linePath = new Path();
        float labelY = layoutHeight - paddintBottom ;
        for (int i = 0; i< mDataList.size();i++){
            String label = mDataList.get(i).getLabel();
            float  labelX = (paddingLeft + xAxis * i) + mLabelPaint.measureText(label) / 2 ;
            canvas.drawText(label,labelX,labelY,mLabelPaint);
            AppLogger.LOGD(TAG,"label:"+label+",labelX:"+labelX+",labelY:"+labelY);
            float dataY = ( avabilHeight - (mDataList.get(i).getData() * rate));
            float dataX = (paddingLeft + xAxis * i) + mLabelPaint.measureText(label);
            canvas.drawPoint(dataX,dataY,mDataPointPaint);
            String data = mDataList.get(i).getData()+"";
            canvas.drawText(data,dataX - mLabelPaint.measureText(data)/2,dataY - 30,mLabelPaint);
            AppLogger.LOGD(TAG,"data:"+mDataList.get(i).getData()+",dataX:"+dataX+",dataY:"+dataY);
            if(i==0){
                linePath.moveTo(dataX,dataY);
            }else{
                linePath.lineTo(dataX,dataY);
            }
        }
        canvas.drawPath(linePath,mDataPointPaint);

    }
}
