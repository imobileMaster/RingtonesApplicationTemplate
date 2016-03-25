package ringtones.codebhak;

import java.util.ArrayList;

import ringtones.codebhak.direct.Contact;
import ringtones.codebhak.direct.SongInfo;
import ringtones.codebhak.start.Util;
import tayebjaaba.miringtones.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SelectContactActivity extends Activity {

	private ArrayList<Contact> listContacts = new ArrayList<Contact>();
	
	private ArrayList<SongInfo> listSong = new ArrayList<SongInfo>();
	private ListContactsAdapter adapter;
	private Util util = new Util();
	private ListView list;
	private EditText txt_search;
	private ArrayList<Contact> listSearch;
	
	private Handler guiThread;
	
	private Runnable updateTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mycontacts);
		
		list = (ListView)findViewById(R.id.list);
		txt_search = (EditText)findViewById(R.id.txt_search);

		final int position = this.getIntent().getIntExtra("position", 0);

		listSong = util.getAllSong(this);

		listContacts = util.getAllContact(this);
		
		Log.i("LOG", "Size: " + listContacts.size());
		
		adapter = new ListContactsAdapter(this, android.R.layout.simple_list_item_1, listContacts);
		list.setAdapter(adapter);
		list.setTextFilterEnabled(true);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				util.assignRingtoneToContact(SelectContactActivity.this,
						listSong.get(position), listContacts.get(arg2));
				Toast.makeText(
						SelectContactActivity.this,
						"Ringtone set successfully",
						Toast.LENGTH_LONG).show();
				finish();
			}
		});
		
		innitThread();
		
		txt_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				queueUpdate(500);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}
	
	private void queueUpdate(long delayMillisecond) {
		guiThread.removeCallbacks(updateTask);
		// update data if no change in textSearch after time config
		// timer by = milliseconds
		guiThread.postDelayed(updateTask, delayMillisecond);
	}
	
	private void innitThread() {
		guiThread = new Handler();
		updateTask = new Runnable() {

			@Override
			public void run() {
				
				String word = txt_search.getText().toString().trim();
				if (word.equalsIgnoreCase("")) {
					// if not change set listView first
					list.setAdapter(new ListContactsAdapter(SelectContactActivity.this,
							android.R.layout.simple_list_item_1, listContacts));
				} else
				// if txtSearch not null
				{

					// get data from webservice
					getDataByKeywords(word);
					// Show on list
					listSearch = new ArrayList<Contact>();

					// get data from webservice
					listSearch = getDataByKeywords(word);
					
					list.setAdapter(new ListContactsAdapter(SelectContactActivity.this, android.R.layout.simple_list_item_1, listSearch));
					adapter.notifyDataSetChanged();
				}

			}
		};
	}
	
	public ArrayList<Contact> getDataByKeywords(String keyword) {
		listSearch = new ArrayList<Contact>();
		keyword = keyword.toUpperCase();
		for (int i = 0; i < listContacts.size(); i++) {
			String contain = listContacts.get(i).getName().toUpperCase();
			if (contain.contains(keyword)) {
				listSearch.add(listContacts.get(i));
			}
		}
		return listSearch;
	}

}
