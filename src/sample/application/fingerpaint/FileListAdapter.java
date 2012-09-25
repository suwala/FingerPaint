package sample.application.fingerpaint;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends ArrayAdapter<Object> {
	public static final Integer text1 = android.R.id.text1;
	public static final Integer text2 = android.R.id.text2;
	public static final Integer icon = android.R.id.icon;
	
	public File[] fc;
	public LayoutInflater mInflater;
	
	public FileListAdapter(Context context,Object[] objects) {
		super(context, text1, objects);
		// TODO 自動生成されたコンストラクター・スタブ
		
		this.fc = (File[])objects;
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自動生成されたメソッド・スタブ
		if(convertView == null){
			//convertView = this.mInflater.inflate(R.layout.list_item_with_icon,null);
			convertView = this.mInflater.inflate(android.R.layout.simple_list_item_1,null);
		}
		
		TextView fName = (TextView)convertView.findViewById(this.text1);
		TextView fTime = (TextView)convertView.findViewById(this.text2);
		ImageView fIcon = (ImageView)convertView.findViewById(this.icon);
		
		fName.setText(fc[position].getName());
		fTime.setText(DateFormat.getDateTimeInstance().format(
				new Date(fc[position].lastModified())));
		
		if(fc[position].isDirectory()){
			fIcon.setImageResource(R.drawable.folder);
		}else{
			//fIcon.setImageResource(R.drawble.text);
			Pattern p = Pattern.compile
					("\\.png$|\\.jpg$|\\.gif$|\\.jpeg$|\\.bmp$",Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(fc[position].getName());
			
			if(m.find()){
				String path = fc[position].getPath();
				BitmapFactory.Options options = new BitmapFactory.Options();
				
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path,options);
				
				Integer scaleW = options.outWidth/64;
				Integer scaleH = options.outHeight/64;
				
				Integer scale = Math.max(scaleW, scaleH);
				options.inJustDecodeBounds = false;
				options.inSampleSize = scale;
				
				Bitmap bmp = BitmapFactory.decodeFile(fc[position].getPath(),options);
				fIcon.setImageBitmap(bmp);
			}
		}
		return convertView;
	}
	
	

	
	
	
}
