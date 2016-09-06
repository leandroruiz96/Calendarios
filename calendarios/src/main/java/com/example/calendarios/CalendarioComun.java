package com.example.calendarios;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by leandro on 5/9/16.
 */
public class CalendarioComun extends View {

    private Paint mTitlePaint;
    private Paint mDaysPaint;
    private Paint mDatesPaint;
    private Paint mGrayDatePaint;
    private Paint mSelectedDatePaint;
    private Paint mCirclePaint;
    private Paint mCircumferencePaint;
    private Paint mPointPaint;
    private Paint mSelectedPointPaint;

    int mWidth;
    int mHeight;

    int mTitleColor;
    int mDaysColor;
    int mDrawablesColor;
    int mSelectedTextColor;

    String mTitleFont;
    String mDaysFont;
    String mDatesFont;

    float mTitleSize;
    float mDaysSize;

    Calendar mMyInstance;
    Calendar mSelected;
    Calendar mToday;

    List<Date> mMarkedDates;

    private Paint mLinePaint;

    Rect mDrawArea = new Rect();
    String[] mDayNames = new DateFormatSymbols(Locale.getDefault()).getShortWeekdays();

    private OnMonthChanged mMonthChanged;
    private OnDateChanged mDateChanged;

    public void setMarkedDates(List<Date> mMarkedDates) {
        this.mMarkedDates = mMarkedDates;
    }

    public void setOnDateChanged(OnDateChanged mDateChanged) {
        this.mDateChanged = mDateChanged;
    }

    public void setOnMonthChanged(OnMonthChanged mMonthChanged) {
        this.mMonthChanged = mMonthChanged;
    }

    public interface OnMonthChanged {
        void onMonthChanged(int month, int year);
    }

    public interface OnDateChanged {
        void onDateChanged(int day, int month, int year);
    }

    public CalendarioComun(Context context) {
        super(context);
        initCalendars();
        defaultStyles();
        setPaints();
    }

    private void defaultStyles() {
        mTitleColor = Color.BLACK;
        mDaysColor = Color.GREEN;
        mDrawablesColor = Color.CYAN;
        mSelectedTextColor = Color.WHITE;
    }

    private void setPaints() {
        AssetManager assets = getContext().getAssets();

        mTitlePaint = createTypefacePaint(assets,mTitleFont,mTitleColor,mTitleSize);
        mDaysPaint = createTypefacePaint(assets,mDaysFont,mDaysColor,mDaysSize);
        mDatesPaint = createTypefacePaint(assets,mDatesFont,Color.BLACK,mDaysSize);
        mSelectedDatePaint = createTypefacePaint(assets,mDatesFont,mSelectedTextColor,mDaysSize);
        mGrayDatePaint = createTypefacePaint(assets,mDatesFont,Color.GRAY,mDaysSize);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.GRAY);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mDrawablesColor);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mCircumferencePaint = new Paint();
        mCircumferencePaint.setColor(mDrawablesColor);
        mCircumferencePaint.setStyle(Paint.Style.STROKE);

        mPointPaint = new Paint();
        mPointPaint.setColor(mDrawablesColor);
        mPointPaint.setStyle(Paint.Style.FILL);

        mSelectedPointPaint = new Paint();
        mSelectedPointPaint.setColor(mSelectedTextColor);
        mSelectedPointPaint.setStyle(Paint.Style.FILL);
    }

    private Paint createTypefacePaint(AssetManager assets,String source, int color, float size) {
        Paint p = new Paint();
        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(color);
        Typeface titleTypeface =
                source==null?
                        Typeface.DEFAULT:
                        Typeface.createFromAsset(assets,source);
        p.setTypeface(titleTypeface);
        p.setTextSize(size);
        return p;
    }

    public CalendarioComun(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCalendars();
        defaultStyles();
        loadAttrs(context.obtainStyledAttributes(attrs,R.styleable.CalendarioComun,0,0));
        setPaints();
    }

    private void initCalendars() {
        mMyInstance = Calendar.getInstance();
        mToday      = Calendar.getInstance();
        mSelected   = Calendar.getInstance();
    }

    private void loadAttrs(TypedArray attrs) {
        mTitleColor = attrs.getColor(R.styleable.CalendarioComun_titleColor,Color.BLACK);
        mDaysColor = attrs.getColor(R.styleable.CalendarioComun_daysColor,Color.GREEN);
        mDrawablesColor = attrs.getColor(R.styleable.CalendarioComun_drawablesColor,Color.BLUE);
        mSelectedTextColor = attrs.getColor(R.styleable.CalendarioComun_selectedTextColor, Color.WHITE);

        mTitleFont = attrs.getString(R.styleable.CalendarioComun_titleFont);
        mDatesFont = attrs.getString(R.styleable.CalendarioComun_datesFont);
        mDaysFont = attrs.getString(R.styleable.CalendarioComun_daysFont);

        mTitleSize = attrs.getDimension(R.styleable.CalendarioComun_titleSize,48f);
        mDaysSize = attrs.getDimension(R.styleable.CalendarioComun_daysSize,24f);

        attrs.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = widthMeasureSpec;
        mHeight = heightMeasureSpec;
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getClipBounds(mDrawArea);
        int width = mDrawArea.width();
        int height = mDrawArea.height();

        int rowHeight = height/8;
        int columnWidth = width/7;
        int rowPos = 0;

        float y = (rowHeight/2+mTitlePaint.getFontMetrics().bottom);
        String monthAndYear = mMyInstance.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault()) + " " + mMyInstance.get(Calendar.YEAR);
        canvas.drawText(monthAndYear,width/2,y, mTitlePaint);

        float margin = rowHeight/5f;
        canvas.drawLine(margin,rowHeight/2,rowHeight/2.2f,margin,mLinePaint);
        canvas.drawLine(margin,rowHeight/2,rowHeight/2.2f,rowHeight-margin,mLinePaint);
        canvas.drawLine(width-margin,rowHeight/2,width-rowHeight/2.2f,margin,mLinePaint);
        canvas.drawLine(width-margin,rowHeight/2,width-rowHeight/2.2f,rowHeight-margin,mLinePaint);

        rowPos += rowHeight;

        //region Begin drawing days
        for (int i=1; i<=7; i++) {
            float centerX = columnWidth*i-columnWidth/2;
            canvas.drawText(mDayNames[i],centerX,rowPos+rowHeight/2,mDaysPaint);
        }
        //endregion

        //region Begin drawing dates
        rowPos += rowHeight;
        mMyInstance.set(Calendar.DAY_OF_MONTH,1);
        int drawingYear = mMyInstance.get(Calendar.YEAR);
        int drawingMonth = mMyInstance.get(Calendar.MONTH);
        int dayOfTheWeek = mMyInstance.get(Calendar.DAY_OF_WEEK);
        mMyInstance.add(Calendar.DATE,-dayOfTheWeek+1);
        for (int week = 0; week<6; week++) {
            for (int i=1; i<=7; i++) {
                float centerX = columnWidth*i-columnWidth/2;
                float centerY = rowPos + rowHeight / 2;
                if (sameDate(mMyInstance,mSelected)) {
                    canvas.drawCircle(centerX,rowPos+rowHeight/2,rowHeight/2,mCirclePaint);
                    canvas.drawText(
                            String.valueOf(mMyInstance.get(Calendar.DAY_OF_MONTH)),
                            centerX,
                            centerY + mSelectedDatePaint.getFontMetrics().bottom,
                            mSelectedDatePaint
                    );
                    if (isMarkedDate(mMyInstance)) {
                        canvas.drawCircle(centerX, centerY+rowHeight/4,3,mSelectedPointPaint);
                    }
                } else {
                    if (sameDate(mMyInstance,mToday)) {
                        canvas.drawCircle(centerX,rowPos+rowHeight/2,rowHeight/2,mCircumferencePaint);
                    }
                    Paint currentPaint = mMyInstance.get(Calendar.MONTH) == drawingMonth ? mDatesPaint : mGrayDatePaint;
                    canvas.drawText(
                            String.valueOf(mMyInstance.get(Calendar.DAY_OF_MONTH)),
                            centerX,
                            centerY + currentPaint.getFontMetrics().bottom,
                            currentPaint
                    );
                    if (isMarkedDate(mMyInstance)) {
                        canvas.drawCircle(centerX, centerY+rowHeight/4,3,mPointPaint);
                    }
                }
                mMyInstance.add(Calendar.DATE,1);
            }
            rowPos += rowHeight;
        }
        //after drawing, pivot inside the current month
        mMyInstance.set(Calendar.MONTH,drawingMonth);
        mMyInstance.set(Calendar.YEAR,drawingYear);
        mMyInstance.set(Calendar.DATE,1);
        //endregion
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int[] positions = {0,0};
                getLocationInWindow(positions);
                float insideX = event.getRawX() - positions[0];
                float insideY = event.getRawY() - positions[1];
                int ixX = columIndexFor(insideX);
                int ixY = rowIndexFor(insideY);
                if (ixY>1) {
                    changeSelectedDate(ixX,ixY);
                } else if (ixY==0 && (ixX==0||ixX==6)) {
                    if (ixX==0) mMyInstance.add(Calendar.MONTH,-1);
                    if (ixX==6) mMyInstance.add(Calendar.MONTH,1);
                    if (mMonthChanged!=null) mMonthChanged.onMonthChanged(mMyInstance.get(Calendar.MONTH),mMyInstance.get(Calendar.YEAR));
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private void changeSelectedDate(int ixX, int ixY) {
        Calendar selectedDate = Calendar.getInstance();
        int workingMonth = mMyInstance.get(Calendar.MONTH);
        int workingYear = mMyInstance.get(Calendar.YEAR);
        selectedDate.set(Calendar.MONTH,workingMonth);
        selectedDate.set(Calendar.YEAR,workingYear);
        selectedDate.set(Calendar.DATE,1);
        int dayOfTheWeek = selectedDate.get(Calendar.DAY_OF_WEEK);
        selectedDate.add(Calendar.DATE,-dayOfTheWeek+1);
        selectedDate.add(Calendar.DATE,(ixY-2)*7+ixX);
        mSelected = selectedDate;
        if (mDateChanged!=null) {
            mDateChanged.onDateChanged(
                    mSelected.get(Calendar.DATE),
                    mSelected.get(Calendar.MONTH),
                    mSelected.get(Calendar.YEAR)
            );
        }
        invalidate();
    }

    private int rowIndexFor(float insideY) {
        int i = 0;
        float rowHeight = getHeight()/8;
        while (i<8) {
            if (rowHeight*i<=insideY&&insideY<=rowHeight*(i+1)) return i;
            i++;
        }
        return 0;
    }

    private int columIndexFor(float insideX) {
        int i = 0;
        float columnWidth = getWidth()/7;
        while (i<7) {
            if (columnWidth*i<=insideX&&insideX<=columnWidth*(i+1)) return i;
            i++;
        }
        return 0;
    }

    private static boolean sameDate(Calendar a, Calendar b){
        return  a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.MONTH) == b.get(Calendar.MONTH) &&
                a.get(Calendar.DATE) == b.get(Calendar.DATE);
    }

    private boolean isMarkedDate(Calendar b) {
        if (mMarkedDates==null) return false;
        Calendar aux = Calendar.getInstance();
        for (Date d : mMarkedDates) {
            aux.setTime(d);
            if (sameDate(aux,b)) return true;
        }
        return false;
    }
}
