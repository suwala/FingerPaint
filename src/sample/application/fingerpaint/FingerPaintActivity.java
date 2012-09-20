package sample.application.fingerpaint;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class FingerPaintActivity extends Activity implements OnTouchListener{
		
	public Canvas canvas;
	public Paint paint;
	public Path path;
	public Bitmap bitmap;
	public Float x1,y1;
	Integer w,h;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fingerpaint);
		
		ImageView iv = (ImageView)this.findViewById(R.id.imageview1);
        Display disp = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        
        /* ‚©‚«•ª‚¯
        WindowManager wm = (WindowManager)this.getSystemService(Context.WIFI_SERVICE);
        Display disp = wm.getDefaultDisplay();
        */
        
        this.w=disp.getWidth();
        this.h=disp.getHeight();
        this.bitmap = Bitmap.createBitmap(this.w,this.h,Bitmap.Config.ARGB_8888);
        this.paint=new Paint();
        this.path=new Path();
        this.canvas = new Canvas();
        
        this.paint.setStrokeWidth(5);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.canvas.drawColor(Color.WHITE);
        iv.setImageBitmap(this.bitmap);
        iv.setOnTouchListener(this);
        
	}
	
	@Override
	public boolean onTouch (View v, MotionEvent event){

		//return Boolean2.values();

		Float x=event.getX();
		Float y=event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.path.reset();
			this.path.moveTo(x, y);
			this.x1=x;
			this.y1=y;
			break;
		case MotionEvent.ACTION_MOVE:
			this.path.quadTo(this.x1, this.y1, x, y);
			this.x1=x;
			this.y1=y;
			this.canvas.drawPath(path, paint);
			this.path.reset();
			this.path.moveTo(x, y);
			break;
		case MotionEvent.ACTION_UP:
			if(x==this.x1&&y==this.y1)
				this.y1=this.y1+1;
			this.path.quadTo(this.x1, this.y1, x, y);
			this.canvas.drawPath(path, paint);
			this.path.reset();
			break;
		}
		ImageView iv = (ImageView)this.findViewById(R.id.imageview1);
		iv.setImageBitmap(this.bitmap);

		return true;

	}
}


