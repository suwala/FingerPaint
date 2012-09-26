package sample.application.fingerpaint;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/*
 * new AlertDialog.Builder　で外部のインスタスメソッドを呼び出している　インナークラスの特権か　thisだとエラーになる（インスタンスが違うため）
 * 
 * AlertDialog以降がonOptionsItem　case:R.id.menu_newに類似纏められる？
 * なんらかのキーが押された場合 キーボード含みっぽい
 * 
 * AlertDialog.Builder ab = new AlertDialog.Builder(this);はメソッドチェーンに改築
 * 
 * onOptionsItemSelected(MenuItem)のcase文ながくね？
 */

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
        
        /* ��������
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
			Log.d("save",path);
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
		@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		
		if(keyCode == KeyEvent.KEYCODE_BACK){//バックキーが押されたとき
			new AlertDialog.Builder(this).setTitle(R.string.title_exit).setMessage(R.string.confirm_new)
			.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自動生成されたメソッド・スタブ
					
					finish();
				}
			}).setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自動生成されたメソッド・スタブ
					;
				}
			}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		switch (item.getItemId()) {
		case R.id.menu_save:
			this.save();
			break;
		
		case R.id.menu_open:
		
			Intent intent = new Intent(this,FilePicker.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.menu_color_change:
			final String[] items = getResources().getStringArray(R.array.ColorName);
			final int[] colors = getResources().getIntArray(R.array.Color);//Integer[]だと受け取れなかった
			/*
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle(R.string.menu_color_change);
			ab.setItems(items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO 自動生成されたメソッド・スタブ
				
					paint.setColor(colors[item]);
				}
			});
			ab.show();
			メソッドチェーンにしてみる
			*/
			
			//AlertDialog.Builder(this)の戻り値はインスタンス変数？になるからメソッドチェーン可能？
			new AlertDialog.Builder(this).setTitle(R.string.menu_color_change).setItems(items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO 自動生成されたメソッド・スタブ
					paint.setColor(colors[item]);
				}
			}).show();
			
			break;
			
		case R.id.menu_new:
			new AlertDialog.Builder(this).setTitle(R.string.menu_new).setMessage(R.string.confirm_new)
			.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自動生成されたメソッド・スタブ
					
					canvas.drawColor(Color.WHITE);
					((ImageView)findViewById(R.id.imageview1)).setImageBitmap(bitmap);
					
				}
			})
			.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自動生成されたメソッド・スタブ
					;
				}
			}).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	public void scanMedia(final String fp){
		this.mc = new MediaScannerConnection(this,
				new MediaScannerConnection.MediaScannerConnectionClient() {


			@Override
			public void onScanCompleted(String path, Uri uri) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				disconnect();
			}

			@Override
			public void onMediaScannerConnected() {
				// TODO �����������ꂽ���\�b�h�E�X�^�u


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
	
	public Bitmap loadImage(String path){
		Boolean landscape = false;
		Bitmap bm;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path,options);
		Integer oh = options.outHeight;
		Integer ow = options.outWidth;
		
		if(ow>oh){
			landscape = true;
			oh = options.outWidth;
			ow = options.outHeight;
		}
		
		options.inJustDecodeBounds = false;
		options.inSampleSize = Math.max(ow/w, oh/h);
		bm = BitmapFactory.decodeFile(path,options);
		
		if(landscape){
			Matrix matrix = new Matrix();
			matrix.setRotate(90.0f);
			bm = Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,false);
			Log.d("bitmap","回転しました");
		}
		
		bm = Bitmap.createScaledBitmap(bm, (int)(w), (int)(w*(((double)oh)/((double)ow))),false);
		Bitmap offBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
		Canvas offCanvas = new Canvas(offBitmap);
		offCanvas.drawBitmap(bm, 0,(h-bm.getHeight())/2,null);
		bm = offBitmap;
		return bm;
			
		}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自動生成されたメソッド・スタブ
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK){
			this.bitmap = loadImage(data.getStringExtra("fu"));
			this.canvas = new Canvas(this.bitmap);
			ImageView iv =(ImageView)this.findViewById(R.id.imageview1);
			iv.setImageBitmap(this.bitmap);
		}
		
	}
		
	


}



