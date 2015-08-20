package com.example.asus.mylock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ASUS
 * 日期：2015/8/14.
 */
public class ViewLock extends View {
    public Point[][] points = new Point[3][3];
    public List<Point> pointPressed_list = new ArrayList<Point>();
    public Boolean isInit = false, isSelect, isFinish, moveNoPoint;
    public int width, height, offsetX = 0, offsetY = 0;
    public float pointX, pointY, moveX, moveY, lastX, lastY;
    public Paint paint = new Paint();

    public ViewLock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewLock(Context context) {
        super(context);
    }

    public ViewLock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重画
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化，不添加标志位isInit作判断的话，每次重画都会进行初始化，所以我们只要初始化一次就够了
        if (!isInit) {
            initPoint();
        }
        //画点
        PointCanvas(canvas);
        //画线
        LineCanvas(canvas);
    }


    /**
     * 初始化各点
     */
    private void initPoint() {

        width = getWidth();
        height = getHeight();

        if (width > height) {//横屏
            offsetX = (width - height) / 2;//计算偏移量，使Lock居中放置
            width = height;
        } else {//竖屏
            offsetY = (height - width) / 2;//计算偏移量，使Lock居中放置
            height = width;
        }

        //各个点之间都间隔width/4，所以每个点的下标都是相差width/4
        points[0][0] = new Point(offsetX + width / 4, offsetY + width / 4);
        points[0][1] = new Point(offsetX + width / 4, offsetY + width * 2 / 4);
        points[0][2] = new Point(offsetX + width / 4, offsetY + width * 3 / 4);

        points[1][0] = new Point(offsetX + width * 2 / 4, offsetY + width / 4);
        points[1][1] = new Point(offsetX + width * 2 / 4, offsetY + width * 2 / 4);
        points[1][2] = new Point(offsetX + width * 2 / 4, offsetY + width * 3 / 4);

        points[2][0] = new Point(offsetX + width * 3 / 4, offsetY + width / 4);
        points[2][1] = new Point(offsetX + width * 3 / 4, offsetY + width * 2 / 4);
        points[2][2] = new Point(offsetX + width * 3 / 4, offsetY + width * 3 / 4);

        isInit = true;//表示初始化完成

    }

    /**
     * 画点
     *
     * @param canvas
     */
    private void PointCanvas(Canvas canvas) {
        //根据每个点计算出来的X,Y，在(x,y)出画一个圆
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                pointX = points[i][j].x;
                pointY = points[i][j].y;
                switch (points[i][j].state) {
                    case 0: {
                        paint.setColor(Color.GRAY);
                        canvas.drawCircle(pointX, pointY, 50, paint);
                    }
                    break;
                    case 1: {
                        paint.setColor(Color.BLUE);
                        canvas.drawCircle(pointX, pointY, 50, paint);
                    }
                    break;
                    case 2: {
                        paint.setColor(Color.RED);
                        canvas.drawCircle(pointX, pointY, 50, paint);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 画线
     *
     * @param canvas
     */
    private void LineCanvas(Canvas canvas) {
        if (pointPressed_list.size() == 0) {//集合中没点，不做处理

        } else if (pointPressed_list.size() == 1) {//集合中只要1个点，不能连线，直接画线到触摸位置
            if (isSelect == true) {
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(20);
                Point point = pointPressed_list.get(0);
                canvas.drawLine(point.x, point.y, moveX, moveY, paint);
            }
        } else {//分两种状态下画线，是否完成onTouch事件
            if (!isFinish) {//onTouch还没结束的话，当集合中最后一个点不是第9个点，则集合最后一点连线到触摸位置
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(20);
                if (pointPressed_list.size() == 1) {
                    Point point = pointPressed_list.get(0);
                    //取出当前点的X,Y
                    canvas.drawLine(point.x, point.y, moveX, moveY, paint);
                } else {//不是最后一个点，两点之间正常连线
                    for (int i = 0; i < pointPressed_list.size() - 1; i++) {
                        Point pointPrevious = pointPressed_list.get(i);
                        Point pointNext = pointPressed_list.get(i + 1);
                        lastX = pointNext.x;
                        lastY = pointNext.y;
                        canvas.drawLine(pointPrevious.x, pointPrevious.y, pointNext.x, pointNext.y, paint);
                    }
                    if (pointPressed_list.size() < 9) {//当然啦，9个点全画了的话，就不用连线触摸点了
                        canvas.drawLine(lastX, lastY, moveX, moveY, paint);
                    }
                }
            } else if (isFinish) {//onTouch事件结束后，就重画所有连线，其中再判断下点的状态是否ERROR
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(20);
                for (int i = 0; i < pointPressed_list.size() - 1; i++) {
                    Point pointPrevious = pointPressed_list.get(i);
                    Point pointNext = pointPressed_list.get(i + 1);
                    if (pointPrevious.state == Point.STATE_ERROR) {
                        paint.setColor(Color.RED);
                    }
                    canvas.drawLine(pointPrevious.x, pointPrevious.y, pointNext.x, pointNext.y, paint);
                }
            }
        }
    }

    /**
     * 触摸事件的逻辑实现
     *
     * @param event
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Point point = null;
        moveNoPoint = false;
        isFinish = false;
        moveX = event.getX();
        moveY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {//每次按下都是清空集合
                resetPoint();
                point = checkSelectedPoint();
                if (point != null) {
                    isSelect = true;//isSelect的作用就是，ACTION_DOWN时候一定要得到一个有效的起始点，手指移动时才会去收集点
                }
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (isSelect) {//如上面所说，有了起始点，才能陆续添加点
                    point = checkSelectedPoint();
                    if (point == null) {
                        moveNoPoint = true;//moveNoPoint是用来判断是否有重复点，
                    }
                }
            }
            break;
            case MotionEvent.ACTION_UP: {//抬手后，isFinish为true，isSelect也回到false以便下次连线使用
                isFinish = true;
                isSelect = false;
            }
            break;
        }

        if (!isFinish && isSelect && point != null) {//选中重复检查
            if (crossPoint(point)) {//重复点判断
                moveNoPoint = true;
            } else {//新点
                point.state = Point.STATE_PRESSED;
                pointPressed_list.add(point);
            }
        }

        if (isFinish) {//onTouch动作结束
            if (pointPressed_list.size() == 1) {//绘制不成立
                resetPoint();
            } else if (pointPressed_list.size() < 5 && pointPressed_list.size() > 2) {//绘制错误，就是点的数量不正确
                errorPoint();
            }
            ;
        }
        //刷新
        postInvalidate();

        return true;
    }

    /**
     * 重复点判断
     *
     * @param point
     * @return
     */
    private boolean crossPoint(Point point) {
        if (pointPressed_list.contains(point)) {//存在点，返回true
            return true;
        } else {
            return false;
        }

    }

    /**
     * 绘制不成立，重置点的集合
     */
    private void resetPoint() {//遍历集合，令点状态从pressed回到normal
        for (Point point : pointPressed_list) {
            point.state = Point.STATE_NORMAL;
        }
        pointPressed_list.clear();//清空集合
    }

    /**
     * 密码长度错误，选中点设置为错误状态
     */
    private void errorPoint() {//遍历集合，令点状态从pressed变为error
        for (Point point : pointPressed_list) {
            point.state = Point.STATE_ERROR;
        }
    }

    private Point checkSelectedPoint() {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                //两点距离小于circle的半径，返回一个有效点
                if (((moveX - points[i][j].x) * (moveX - points[i][j].x) + (moveY - points[i][j].y) * (moveY - points[i][j].y)) < 50 * 50) {
                    if (points[i][j].state != Point.STATE_ERROR) {
                        Point point = points[i][j];
                        return point;
                    }
                }
            }
        }
        return null;
    }


    public static class Point {
        public static int STATE_NORMAL = 0;
        public static int STATE_PRESSED = 1;
        public static int STATE_ERROR = 2;
        public float x, y;
        public int state = 0;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

    }
}
