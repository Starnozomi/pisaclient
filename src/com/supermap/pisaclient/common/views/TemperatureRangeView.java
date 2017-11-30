package com.supermap.pisaclient.common.views;

import java.util.Arrays;

import com.supermap.pisaclient.common.CommonUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.view.View;

public class TemperatureRangeView extends View {
	
	private int mTemprature[][] ={{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}};
	private int temp[] = new int[14] ;
	private int w;
	private int h;
	private int left;
	private int right;
	private int top;
	private int down;
	private int x_tance;
	private int y_tance;
	private boolean isDrawTemp = true;
	private  int padding_distance ;
	private Context mContext;

	public TemperatureRangeView(Context context) {
		super(context);
		this.mContext = context;
	}

	
	public void setData(int tem[][]){
		this.mTemprature = tem;
		this.invalidate();
	}
	
	public void setpadding(int dp){
		padding_distance = CommonUtil.dip2px(mContext, dp);
	}
	
	/**
	 * 温度数据不对，不需要重绘
	 */
	private void checkTempr(){
		if ((mTemprature[0][0]==0)&&(mTemprature[0][1]==0)) {
			isDrawTemp = false;
		}
		else
		{
			isDrawTemp=true;
		}
	}
	
	private void change(){
		int k = 0;
		for(int i = 0;i<7;i++){
			for(int j =0;j<2;j++){
				temp[k++] = mTemprature[i][j];
			}
		}
		Arrays.sort(temp);
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		
		change();
		checkTempr();
		
		w = getWidth();
		h = getHeight();
		left =  10; //左右两边间距10
		right = w - 10;
	
		top = padding_distance;   //上下间距10
		down = h-padding_distance ;
		
		Paint paint = new Paint(); 
		paint.setStyle(Paint.Style.STROKE); //空心 
		paint.setColor(Color.WHITE);  
		paint.setAlpha(127);//半透明
        paint.setStrokeWidth(1);  
        //画背景
        //画笔设置虚线效果
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);    
        paint.setPathEffect(effects); 
        
        Path path1 = new Path();
        int temx = w/7;
        // 必须为float 以便top+temy*10误差较小
        float temy = (h -2*padding_distance)/10f;
        
        path1.moveTo(left, top);  
        path1.lineTo(right, top);
        //横向第二条到第十条虚线  
        for(int i =1;i<10;i++){
        	path1.moveTo(left, top+temy*i);  
            path1.lineTo(right, top+temy*i);
        }
       
        //横向最后一条虚线  
        path1.moveTo(left, down); 
        path1.lineTo(right, down);
        
        //纵向第一条到第七条虚线
        for(int j =0;j<7;j++){
        	 path1.moveTo(temx/2+temx*j, top); 
             path1.lineTo(temx/2+temx*j, down);
        }
        canvas.drawPath(path1, paint);
        
        
        //计算14个温度值对应的坐标
        int y_xy = 0; // 1℃对应的y坐标间距
        int y_0  = 0; // 原点的y坐标
        if((temp.length!=0)&&((temp[13]-temp[0])!=0)){
        	y_xy= (int) ((h-2*padding_distance-2*temy)/(temp[13]-temp[0]));
        }
        y_0 = (int) (h -padding_distance - temy);
        
        //计算每个温度值对应的坐标
        paint.setPathEffect(null); 
        int xy[][][] = new int[7][2][2];
        for(int i = 0 ;i<7;i++){
        	for(int j = 0 ; j<2;j++){
        		xy[i][j][0] = temx/2+i*temx;
        		xy[i][j][1] =y_0 - (mTemprature[i][j] -temp[0])*y_xy;
        	}
        }
        
//     // 画多边形并填充透明颜色  
        if (isDrawTemp) {
        	 paint.reset();//重置  
             paint.setColor(Color.WHITE);  
             paint.setAlpha(20);
             paint.setStyle(Paint.Style.FILL);//设置空心  
             paint.setStrokeWidth(4);  
             Path pathadd=new Path();  
             
             pathadd.moveTo(temx/2, xy[0][0][1]);  
             pathadd.lineTo(temx/2+temx, xy[1][0][1]); 
             pathadd.lineTo(temx/2+temx*2, xy[2][0][1]);
             pathadd.lineTo(temx/2+temx*3, xy[3][0][1]);
             pathadd.lineTo(temx/2+temx*4, xy[4][0][1]);
             pathadd.lineTo(temx/2+temx*5, xy[5][0][1]);
             pathadd.lineTo(temx/2+temx*6, xy[6][0][1]);
             pathadd.lineTo(temx/2+temx*6, xy[6][1][1]);
             pathadd.lineTo(temx/2+temx*5, xy[5][1][1]);
             pathadd.lineTo(temx/2+temx*4, xy[4][1][1]);
             pathadd.lineTo(temx/2+temx*3, xy[3][1][1]);
             pathadd.lineTo(temx/2+temx*2, xy[2][1][1]);
             pathadd.lineTo(temx/2+temx, xy[1][1][1]);
             pathadd.lineTo(temx/2, xy[0][1][1]);
             pathadd.close();
             
             pathadd.close();//封闭  
             canvas.drawPath(pathadd, paint);
             //画14个圆圈
             paint.reset();
             paint.setStyle(Paint.Style.STROKE); //空心 
             paint.setColor(Color.WHITE);    
             paint.setStrokeWidth(4); 
             for(int i = 0 ;i<7;i++){
             	for(int j = 0 ; j<2;j++){
             		xy[i][j][0] = temx/2+i*temx;
             		xy[i][j][1] =y_0 - (mTemprature[i][j] -temp[0])*y_xy;
             		canvas.drawCircle(xy[i][j][0], xy[i][j][1], 5, paint);
             	}
             }
             
            //画上面趋势线
             
             paint.reset();
             paint.setStyle(Paint.Style.STROKE); //
     		paint.setColor(Color.WHITE);  
             paint.setStrokeWidth(4);  
             Path path2 = new Path();
             for(int i = 0; i< 6;i++){
             	 path2.moveTo(xy[i][1][0]+5,xy[i][1][1]);
                  path2.lineTo(xy[i+1][1][0]-5, xy[i+1][1][1]);
             }
             //画下面趋势线
             for(int i = 0; i< 6;i++){
            	    path2.moveTo(xy[i][0][0]+5,xy[i][0][1]);
                 path2.lineTo(xy[i+1][0][0]-5, xy[i+1][0][1]);
            }
             canvas.drawPath(path2, paint);
             //画温度值
             paint.setColor(Color.WHITE);  
             paint.setStyle(Paint.Style.FILL); 
             paint.setStrokeWidth(4); 
             paint.setTextSize(25);
             paint.setTextAlign(Align.CENTER); 
             for(int i = 0;i<7;i++){
             	canvas.drawText(mTemprature[i][0]+"℃", xy[i][0][0],  xy[i][0][1]+2*temy, paint);
             	canvas.drawText(mTemprature[i][1]+"℃", xy[i][0][0],  xy[i][1][1]-temy, paint);
             }
		}
       
		super.onDraw(canvas);
	}
	
	
	
	
	
}
