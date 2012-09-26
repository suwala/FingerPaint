package sample.application.fingerpaint;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FilePicker extends ListActivity {

	public String dir,externalStorageDir;
	public FileFilter fFileter;
	public Comparator<Object> comparator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filelist);//filelist.xml内のlistviewのIDを変えないとエラー落ち
		this.externalStorageDir = Environment.getExternalStorageDirectory().toString();
		Log.d("filePicker",Environment.getExternalStorageDirectory().toString());
		/*dir = externalStorageDir+"/text";
		SharedPreferences pref = this.getSharedPreferences("MemoFilePickerPrefs", MODE_PRIVATE);
		this.dir =pref.getString("Folder", externalStorageDir+"/text");
		*/
		
		SharedPreferences pref = this.getSharedPreferences("FilePickerPrefs", MODE_PRIVATE);
		this.dir =pref.getString("Folder", externalStorageDir+"/mypaint");
		
		this.makeFileFilter();
		this.makeCompatator();
		this.showList();
	}
	
	@Override
	protected void onStop() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onStop();
		
		//SharedPreferences pref = this.getSharedPreferences("MeomoFilePickerPrefs", MODE_PRIVATE);
		SharedPreferences pref = this.getSharedPreferences("FilePickerPrefs", MODE_PRIVATE);
		SharedPreferences.Editor edtitor = pref.edit();
		edtitor.putString("Folder", this.dir);
		edtitor.commit();
	}

	public void makeFileFilter(){
		this.fFileter = new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				//Pattern p = Pattern.compile
				//		("\\.txt$|\\.ini$|\\.xml$|\\.htm$|\\.csv$|\\.java$",Pattern.CASE_INSENSITIVE);
				
				Pattern p = Pattern.compile
						("\\.png$|\\.jpg$|\\.gif$|\\.jpeg$|\\.bmp$",Pattern.CASE_INSENSITIVE);
				
				Matcher m = p.matcher(file.getName());
				Boolean shown = (m.find()||file.isDirectory())&&!file.isHidden();
				return shown;
			}
		};
	}
	
	public void makeCompatator(){
		this.comparator = new Comparator<Object>() {
			public int compare(Object object1,Object object2){
				Integer pad1 = 0;
				Integer pad2 = 0;
				File file1 = (File)object1;
				File file2 = (File)object2;
				if(file1.isDirectory())pad1=-65536;
				if(file2.isDirectory())pad2=-65536;
				return pad1 - pad2 + file1.getName().compareToIgnoreCase(file2.getName());
			}
		};
	}
	
	public void showList(){
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			this.finish();
		File file = new File(this.dir);
		if(!file.exists()){
			this.dir = this.externalStorageDir;
			file = new File(this.dir);
		}
		
		this.setTitle(this.dir);
		File[] fc = file.listFiles(this.fFileter);
		final FileListAdapter adapter = new FileListAdapter(this,fc);
		adapter.sort(this.comparator);
		ListView lv = (ListView)findViewById(android.R.id.list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener( new OnItemClickListener(){
		
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				
				if(((File)adapter.getItem(position)).isDirectory()){
					dir = ((File)adapter.getItem(position)).getPath();
					showList();
				}else{
					Intent i = new Intent();
					i.putExtra("fu", ((File)adapter.getItem(position)).toString());
					setResult(RESULT_OK,i);
					finish();
				}
			}
			
		});
		
		if(dir.equals(Environment.getExternalStorageDirectory().toString())){
			findViewById(R.id.button1).setEnabled(false);
		}else{
			findViewById(R.id.button1).setEnabled(true);
		}
			
	}
	
	public void upButtonClick(View v){
		this.dir = new File(this.dir).getParent();
		this.showList();
	}
	
	public void dirButtonClick(View v){
		if(v.getId() == R.id.button1){
			this.dir = new File(this.dir).getParent();
			this.showList();
		}else{
			this.dir = new File(Environment.getExternalStorageDirectory().toString() + "/mypaint").getPath();
			this.showList();
		}
	}
	
}
