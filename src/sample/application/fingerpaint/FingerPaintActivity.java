package sample.application.fingerpaint;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	public MediaScannerConnection mc;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fingerpaint);
		
		ImageView iv = (ImageView)this.findViewById(R.id.imageview1);
        Display disp = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        
        /* かき分け
        WindowManager wm = (WindowManager)this.getSystemService(Context.WIFI_SERVICE);
        Display disp = wm.getDefaultDisplay();
        */
        
        this.w=disp.getWidth();
        this.h=disp.getHeight();
        this.bitmap = Bitmap.createBitmap(this.w,this.h,Bitmap.Config.ARGB_8888);
        this.paint=new Paint();
        this.path=new Path();
        this.canvas = new Canvas(this.bitmap);
        
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
	
	public void save(){
		SharedPreferences prefs = this.getSharedPreferences("FingarPaintPreferences", MODE_PRIVATE);
		Integer imageNumber = prefs.getInt("imageNumber", 1);
		File file=null;
		
		if(externalMediaChecker()){
			DecimalFormat form = new DecimalFormat("0000");
			String path = Environment.getExternalStorageDirectory()+"/mypaint/";
			File outDir = new File(path);
			if(!outDir.exists())
				outDir.mkdir();
			do{
				file= new File(path+"img"+form.format(imageNumber)+".png");
				imageNumber++;
			}while(file.exists())
				;
			if(writeImage(file)){
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt("imageNumber", imageNumber);
				editor.commit();
				
			}
			
			if(writeImage(file)){
				this.scanMedia(file.getPath());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt("imageNumber", imageNumber);
				editor.commit();
			}
		}
	}
	
	public boolean writeImage(File file){
		try{
			FileOutputStream fo=new FileOutputStream(file);
			this.bitmap.compress(CompressFormat.PNG, 100, fo);
			fo.flush();
			fo.close();
		}catch(Exception e){
			System.out.println(e.getLocalizedMessage());
			return false;
		}
		return true;
	}
	
	public boolean externalMediaChecker(){
		boolean result = false;
		String status = Environment.getExternalStorageState();
		if(status.equals(Environment.MEDIA_MOUNTED))
			result=true;
		return result;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO 自動生成されたメソッド・スタブ
		
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu_f, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO 自動生成されたメソッド・スタブ
		
		switch (item.getItemId()) {
		case R.id.menu_save:
			this.save();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	public void scanMedia(final String fp){
		this.mc = new MediaScannerConnection(this,
				new MediaScannerConnection.MediaScannerConnectionClient() {


			@Override
			public void onScanCompleted(String path, Uri uri) {
				// TODO 自動生成されたメソッド・スタブ
				disconnect();
			}

			@Override
			public void onMediaScannerConnected() {
				// TODO 自動生成されたメソッド・スタブ


				scanFile(fp);
			}
		});
		this.mc.connect();
	}

	public void scanFile(String fp){
		this.mc.scanFile(fp, "image/png");
	}
	public void disconnect(){
		this.mc.disconnect();
	}


}



