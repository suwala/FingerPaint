package sample.application.fingerpaint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.graphics.drawable.Drawable;
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
 * new AlertDialog.Builder縲�〒螟夜Κ縺ｮ繧､繝ｳ繧ｹ繧ｿ繧ｹ繝｡繧ｽ繝�ラ繧貞他縺ｳ蜃ｺ縺励※縺�ｋ縲�う繝ｳ繝翫�繧ｯ繝ｩ繧ｹ縺ｮ迚ｹ讓ｩ縺九�this縺�→繧ｨ繝ｩ繝ｼ縺ｫ縺ｪ繧具ｼ医う繝ｳ繧ｹ繧ｿ繝ｳ繧ｹ縺碁＆縺�◆繧�ｼ� * 
 * AlertDialog莉･髯阪′onOptionsItem縲�ase:R.id.menu_new縺ｫ鬘樔ｼｼ郤上ａ繧峨ｌ繧具ｼ� * 縺ｪ繧薙ｉ縺九�繧ｭ繝ｼ縺梧款縺輔ｌ縺溷�蜷�繧ｭ繝ｼ繝懊�繝牙性縺ｿ縺｣縺ｽ縺� * 
 * AlertDialog.Builder ab = new AlertDialog.Builder(this);縺ｯ繝｡繧ｽ繝�ラ繝√ぉ繝ｼ繝ｳ縺ｫ謾ｹ遽� * 
 * BitmapとDrawableの違い
 * onOptionsItemSelected(MenuItem)縺ｮcase譁�↑縺後￥縺ｭ�� */

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
        
        /* �ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ
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
			do{//必ず実行されるwhile文
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
		FileOutputStream fo=null;//tryの中では見えないので　外で宣言する
		try{
			fo=new FileOutputStream(file);
			this.bitmap.compress(CompressFormat.PNG, 100, fo);
			fo.flush();
			fo.close();
		}catch(IOException e){//書き込み時にディスクの異常　同一ファイルへアクセス なんかで起きる
			System.out.println(e.getLocalizedMessage());
			return false;//FileOutputStreamで開いてるにも拘らず　開いたままエラー処理に走る可能性がある　fo.cloce();をつけるべし
		}finally{//必ず実行される
			try {
				fo.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		return true;
	}
		@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO 閾ｪ蜍慕函謌舌＆繧後◆繝｡繧ｽ繝�ラ繝ｻ繧ｹ繧ｿ繝�		
		if(keyCode == KeyEvent.KEYCODE_BACK){//繝舌ャ繧ｯ繧ｭ繝ｼ縺梧款縺輔ｌ縺溘→縺�			
			new AlertDialog.Builder(this).setTitle(R.string.title_exit).setMessage(R.string.confirm_new)
			.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 閾ｪ蜍慕函謌舌＆繧後◆繝｡繧ｽ繝�ラ繝ｻ繧ｹ繧ｿ繝�					
					finish();
				}
			}).setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 閾ｪ蜍慕函謌舌＆繧後◆繝｡繧ｽ繝�ラ繝ｻ繧ｹ繧ｿ繝�					;
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
		// TODO �ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ黷ｽ�ｽ�ｽ�ｽ\�ｽb�ｽh�ｽE�ｽX�ｽ^�ｽu
		
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO �ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ黷ｽ�ｽ�ｽ�ｽ\�ｽb�ｽh�ｽE�ｽX�ｽ^�ｽu
		
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
			final int[] colors = getResources().getIntArray(R.array.Color);//Integer[]縺�→蜿励¢蜿悶ｌ縺ｪ縺九▲縺�			
			/*
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle(R.string.menu_color_change);
			ab.setItems(items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO 閾ｪ蜍慕函謌舌＆繧後◆繝｡繧ｽ繝�ラ繝ｻ繧ｹ繧ｿ繝�				
					paint.setColor(colors[item]);
				}
			});
			ab.show();
			繝｡繧ｽ繝�ラ繝√ぉ繝ｼ繝ｳ縺ｫ縺励※縺ｿ繧�			*/
			
			//AlertDialog.Builder(this)縺ｮ謌ｻ繧雁�縺ｯ繧､繝ｳ繧ｹ繧ｿ繝ｳ繧ｹ螟画焚�溘↓縺ｪ繧九°繧峨Γ繧ｽ繝�ラ繝√ぉ繝ｼ繝ｳ蜿ｯ閭ｽ��			
			new AlertDialog.Builder(this).setTitle(R.string.menu_color_change).setItems(items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO 閾ｪ蜍慕函謌舌＆繧後◆繝｡繧ｽ繝�ラ繝ｻ繧ｹ繧ｿ繝�					paint.setColor(colors[item]);
				}
			}).show();
			
			break;
			
		case R.id.menu_new:
			new AlertDialog.Builder(this).setTitle(R.string.menu_new).setMessage(R.string.confirm_new)
			.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 閾ｪ蜍慕函謌舌＆繧後◆繝｡繧ｽ繝�ラ繝ｻ繧ｹ繧ｿ繝�					
					canvas.drawColor(Color.WHITE);
					((ImageView)findViewById(R.id.imageview1)).setImageBitmap(bitmap);
					
				}
			})
			.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 閾ｪ蜍慕函謌舌＆繧後◆繝｡繧ｽ繝�ラ繝ｻ繧ｹ繧ｿ繝�					;
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
				// TODO �ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ黷ｽ�ｽ�ｽ�ｽ\�ｽb�ｽh�ｽE�ｽX�ｽ^�ｽu
				disconnect();
			}

			@Override
			public void onMediaScannerConnected() {
				// TODO �ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ黷ｽ�ｽ�ｽ�ｽ\�ｽb�ｽh�ｽE�ｽX�ｽ^�ｽu


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
			Log.d("bitmap","蝗櫁ｻ｢縺励∪縺励◆");
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
		// TODO 閾ｪ蜍慕函謌舌＆繧後◆繝｡繧ｽ繝�ラ繝ｻ繧ｹ繧ｿ繝�		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK){
			this.bitmap = loadImage(data.getStringExtra("fu"));
			this.canvas = new Canvas(this.bitmap);
			ImageView iv =(ImageView)this.findViewById(R.id.imageview1);
			iv.setImageBitmap(this.bitmap);
		}
		
	}
		
	


}



