package sample.application.fingerpaint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

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
		
	}
	
	@Override
	public boolean onTouch (View v, MotionEvent event){
		
		//return Boolean2.values();
		return Boolean.valueOf(true);
	}

}
