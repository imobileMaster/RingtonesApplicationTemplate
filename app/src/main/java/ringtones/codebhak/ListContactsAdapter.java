package ringtones.codebhak;

import java.util.ArrayList;

import ringtones.codebhak.direct.Contact;
import tayebjaaba.miringtones.R;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListContactsAdapter extends ArrayAdapter<Contact>{
	
	private ArrayList<Contact> contacts;
	private Context context;

	public ListContactsAdapter(Context context, int textViewResourceId,
			ArrayList<Contact> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.contacts = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView!=null){
			convertView.setBackgroundResource(R.drawable.list_selector);
		}
		
		TextView textView = getGenericView();
		textView.setBackgroundResource(R.drawable.list_selector);
		textView.setText(contacts.get(position).getName());
		return textView;
	}
	
	public TextView getGenericView() {
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 70);

		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		// Center the text vertically
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		// Set the text starting position
		textView.setPadding(16, 0, 0, 0);
		textView.setTextSize(18);
		textView.setShadowLayer(1, 1, 1, Color.BLACK);
		textView.setTextColor(0xffeeeeee);
		return textView;
	}

}
