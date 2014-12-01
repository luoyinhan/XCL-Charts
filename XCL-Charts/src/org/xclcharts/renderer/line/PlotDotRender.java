/**
 * Copyright 2014  XCL-Charts
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 	
 * @Project XCL-Charts 
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */
package org.xclcharts.renderer.line;

import org.xclcharts.common.MathHelper;
import org.xclcharts.renderer.XEnum;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * @ClassName PlotDotRender
 * @Description  绘制交叉点的形状
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 *  
 */
public class PlotDotRender {
	
	private static PlotDotRender instance = null;
	
	protected Paint mPaintFill = null;
	
	private Path mPath = null;
	private RectF mRect = new RectF();

	public PlotDotRender()
	{

	}
	
	public static synchronized PlotDotRender getInstance()
	{
		if(instance == null)
		{
			instance = new PlotDotRender();
		}
		return instance;
	}
	
	private void initPath()
	{
		if(null == mPath)
		{
			mPath = new Path();
		}else{
			mPath.reset();
		}		
	}	

	
	/**
	 * 开放填充内部环形的画笔
	 */
	public Paint getInnerFillPaint()
	{
		if(null == mPaintFill)
		{
			mPaintFill = new Paint();
			mPaintFill.setColor(Color.WHITE); 
			mPaintFill.setStyle(Style.FILL);
			mPaintFill.setAntiAlias(true);
		}
		return mPaintFill; 
	}
	
	/**
	 * 绘制线上的坐标点
	 * 
	 * @param pDot	点类型
	 * @param left	左边x坐标
	 * @param top	左边Y坐标
	 * @param right	右边x坐标
	 * @param bottom 右边Y坐标
	 * @param paint	画笔
	 */	
	public RectF renderDot(Canvas canvas, PlotDot pDot, 
						  float left, float top, float right,float bottom, Paint paint) {
				
		float radius = pDot.getDotRadius();		
		//mRect.setEmpty();		
		mRect.left =  0.0f;
		mRect.top =  0.0f;
		mRect.right =  0.0f;
		mRect.bottom = 0.0f;		
		
		if(Float.compare(radius, 0.0f) == 0 
				|| Float.compare(radius, 0.0f) == -1){			
			return mRect;
		}						
		
		float cX = 0.0f;
		if(XEnum.DotStyle.DOT == pDot.getDotStyle() ||
				XEnum.DotStyle.RING == pDot.getDotStyle() ||
						XEnum.DotStyle.X == pDot.getDotStyle()	)
		{			
			cX =  left + Math.abs(right - left);
			
			mRect.left =  (cX - radius);
			mRect.top =   (bottom - radius);
			mRect.right =  (cX + radius);
			mRect.bottom =  (bottom + radius);			
		}
		
		switch (pDot.getDotStyle()) {
		case DOT:										
			canvas.drawCircle(cX, bottom,radius, paint);			
			break;
		case RING:					
			renderRing(canvas,paint,radius,pDot,cX ,bottom);
			break;
		case TRIANGLE: // 等腰三角形
			renderTriangle(canvas,paint,radius,right,bottom);			
			break;
		case PRISMATIC: // 棱形 Prismatic			
			renderPrismatic(canvas,paint,radius,right,bottom,left );            
			break;
		case RECT:
			renderRect(canvas,paint,radius,right,bottom );			
			break;
		case X:
			renderX(canvas,paint);			    
			break;
		case CROSS:
			renderCross(canvas,paint,radius,right,bottom);			    
			break;						
		case HIDE:
		default:
		}						
		return mRect;
	}
	
	
	
	public RectF renderDot(Canvas canvas, PlotDot pDot, 
			  float cirX, float cirY, Paint paint) {
		
		float radius = pDot.getDotRadius();
						
		if(Float.compare(radius, 0.0f) == 0 
				|| Float.compare(radius, 0.0f) == -1){			
			return new RectF(0.0f,0.0f,0.0f,0.0f);
		}	
		//return renderDot(canvas,  pDot, 
		//		cirX - radius , cirY - radius, 
		//		cirX + radius , cirY + radius, paint);
		
		float left = cirX - radius ;
		float top = cirY - radius; 
		float right = cirX + radius ;
		float bottom = cirY + radius ;
		
		if(XEnum.DotStyle.DOT == pDot.getDotStyle() ||
				XEnum.DotStyle.RING == pDot.getDotStyle() ||
						XEnum.DotStyle.X == pDot.getDotStyle()	)
		{
			mRect.left =  left;
			mRect.top =   top ;
			mRect.right =  right;
			mRect.bottom = bottom;	
		}
		
		switch (pDot.getDotStyle()) {
		case DOT:										
			canvas.drawCircle(cirX, cirY,radius, paint);			
			break;
		case RING:					
			renderRing(canvas,paint,radius,pDot,cirX, cirY);
			break;
		case TRIANGLE: // 等腰三角形
			renderTriangle(canvas,paint,radius,cirX, cirY);			
			break;
		case PRISMATIC: // 棱形 Prismatic			
			//renderPrismatic(canvas,paint,radius,right,bottom,left );       
			renderPrismatic(canvas,paint,radius,cirX, cirY); 
			break;
		case RECT:
			renderRect(canvas,paint,radius,cirX, cirY);			
			break;
		case X:
			renderX(canvas,paint);			    
			break;
		case CROSS:
			renderCross(canvas,paint,radius,cirX, cirY);			    
			break;	
		case HIDE:
		default:
		}						
		return mRect;		
	}
	
	private void renderRing(Canvas canvas,Paint paint,float radius,
													PlotDot pDot,float cX ,float bottom)
	{
		float ringRadius = radius * 0.7f; // MathHelper.getInstance().mul(radius, 0.7f);		
        canvas.drawCircle(cX, bottom, radius, paint);

        getInnerFillPaint().setColor(pDot.getRingInnerColor());
        canvas.drawCircle(cX, bottom,ringRadius, getInnerFillPaint());         
	}
	
	private void renderTriangle(Canvas canvas,Paint paint,float radius,float cirX,float cirY)
	{
		
		float halfRadius = MathHelper.getInstance().div(radius , 2f);		
		float triganaleHeight = radius + radius / 2;
		
		initPath();
		mPath.moveTo(cirX - radius, cirY + halfRadius);
		mPath.lineTo(cirX, cirY - triganaleHeight);
		mPath.lineTo(cirX + radius, cirY + halfRadius);
		mPath.close();
        canvas.drawPath(mPath, paint);
        mPath.reset();
                    
        mRect.left =  (cirX - radius);
		mRect.top = ( cirY - triganaleHeight);
		mRect.right =  ( cirX + radius);
		mRect.bottom =  ( cirY + halfRadius);	
	}
	
	private void renderPrismatic(Canvas canvas,Paint paint,
			float radius,float cirX,float cirY)
	{
		initPath();
		
		float left = cirX - radius ;
		float right = cirX + radius ;
		float centerX = left + (right - left) / 2;
		
		float top = cirY - radius ;
		float bottom = cirY + radius ;
		
		mPath.moveTo(centerX ,  top);
		mPath.lineTo(left, cirY );
		mPath.lineTo(centerX,  bottom );
		mPath.lineTo(right, cirY );
		mPath.lineTo(centerX ,  top);
				
		mPath.close();
		canvas.drawPath(mPath, paint);
		mPath.reset();
		
		mRect.left =left;
		mRect.top =  top;
		mRect.right =  right;
		mRect.bottom =  bottom;
	}
	
	private void renderPrismatic(Canvas canvas,Paint paint,
								float radius,float right,float bottom,float left )
	{
		initPath();
		mPath.moveTo(right - radius, bottom);
		mPath.lineTo(right, bottom - radius);
		mPath.lineTo(right + radius, bottom);
		mPath.lineTo(left + (right - left), bottom + radius);
		mPath.close();
        canvas.drawPath(mPath, paint);
        mPath.reset();
        
    	mRect.left = ( right -  radius  );
		mRect.top =  ( bottom - radius);
		mRect.right =  ( right + radius);
		mRect.bottom =  (bottom + radius);
	}
	
	
	private void renderRect(Canvas canvas,Paint paint,float radius,float cirX,float cirY )
	{
		paint.setStyle(Style.FILL);	
		
		mRect.left =  (cirX - radius);
		mRect.top =   (cirY - radius); 
		mRect.right =  (cirX + radius);
		mRect.bottom = (cirY + radius);
		canvas.drawRect(mRect,paint);
	}
	
	private void renderX(Canvas canvas,Paint paint)
	{
		canvas.drawLine(mRect.left, mRect.top, mRect.right, mRect.bottom , paint);
	    canvas.drawLine(mRect.left, mRect.bottom, mRect.right, mRect.top, paint);	   
	}
	
	private void renderCross(Canvas canvas,Paint paint,float radius,float cirX,float cirY)
	{
		canvas.drawLine(cirX - radius, cirY, cirX + radius, cirY , paint);
	    canvas.drawLine(cirX, cirY - radius,cirX, cirY + radius, paint);	   
	}
	
}
