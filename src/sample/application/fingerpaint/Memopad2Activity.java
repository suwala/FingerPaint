package sample.application.fingerpaint;



import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.os.Bundle;

//�ǋL
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Selection;

import android.widget.EditText;
import android.widget.ImageView;

import java.text.DateFormat;
//import android.text.format.DateFormat;���̔��
import java.util.Date;






//Memopad2Activity�N���X��` extends �g���@android.app.Activity�i�X�[�p�[�N���X�j
public class Memopad2Activity extends Activity{

	
	//���j���[���J���i�����j
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
    	MenuInflater mi = this.getMenuInflater();
    	mi.inflate(R.menu.menu,menu);
    	
    	Absn abs = new Abs(10);
    	abs.sage();
    	Log.d("abs",String.valueOf(abs.i));
    	
    	abs = new Abs2(10);
    	abs.sage();
    	Log.d("abs2",String.valueOf(abs.i));
    	
    	
		return super.onCreateOptionsMenu(menu);
		
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK){
			EditText et = (EditText) this.findViewById(R.id.editText1);
			
			switch(requestCode){
			case 0:
				et.setText(data.getStringExtra("text"));
				break;
			}
		}
	}

	//�C�x���g�n���h���[ ���j���[���X�g����I�������s
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
		EditText et = (EditText)this.findViewById(R.id.editText1);
		switch(item.getItemId()){
		case R.id.menu_save:
			this.saveMemo();
			break;
		case R.id.menu_open:
			//�A�N�e�B�r�e�B�̐؂�ւ� ���̏ꍇ��Memolist
			Intent i = new Intent(this,Memolist.class);//class
			this.startActivityForResult(i, 0);
			break;
		case R.id.menu_new:
			et.setText("");
			break;
		}
				
		return super.onOptionsItemSelected(item);
	}

	/** Called when the activity is first created. */
       
    //�N�����Ɏ��s
    //onCreate�����\�b�h��
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        
        
        EditText et = (EditText) this.findViewById(R.id.editText1);
    	SharedPreferences pref = this.getSharedPreferences("MemoPrefs", MODE_PRIVATE);
    	et.setText(pref.getString("memo", ""));
    	et.setSelection(pref.getInt("cursor",0));
    	
        
    }
	
	
    //�o�b�N�O���E���h���Ɏ��s
    @Override
    protected void onStop(){
    	super.onStop();
    	
    	//findViewById�̏������ʂ�et�ɑ��
    	EditText et = (EditText) this.findViewById(R.id.editText1);
    	SharedPreferences pref = this.getSharedPreferences("MemoPrefs",MODE_PRIVATE);
    	SharedPreferences.Editor editor=pref.edit();
    	editor.putString("memo",et.getText().toString());
    	editor.putInt("cursor",Selection.getSelectionStart(et.getText()));
    	editor.commit();
    	
    }
    
    public void saveMemo(){
    	EditText et = (EditText)this.findViewById(R.id.editText1) ;
    	String title;
    	String memo = et.getText().toString();
    	
    	
    	
    	if(memo.trim().length()>0){//������0�������傫���Ȃ���s
    		if(memo.indexOf("\n") == -1)//20�����ȏ�Ȃ���s�ǉ�
    			title = memo.substring(0,Math.min(memo.length(),20));
    		else
    			title = memo.substring(0,Math.min(memo.indexOf("\n"),20));
    		String ts = DateFormat.getDateTimeInstance().format(new Date());//���ݓ����̎擾
    		MemoDBHelper memos = new MemoDBHelper(this);//memoDB(�f�[�^�x�[�X)~�̍쐬
    		SQLiteDatabase db = memos.getWritableDatabase();//DB�̎擾
    		ContentValues values = new ContentValues();//DB�ɏ�������
    		values.put("title", title+"\n"+ts);
    		values.put("memo", memo);
    		db.insertOrThrow("memoDB", null,values);
    		memos.close();//DB�����
    		}
    	}
    }
