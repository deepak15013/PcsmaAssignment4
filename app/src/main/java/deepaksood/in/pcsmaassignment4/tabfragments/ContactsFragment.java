package deepaksood.in.pcsmaassignment4.tabfragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import deepaksood.in.pcsmaassignment4.R;

public class ContactsFragment extends Fragment {

    private static final String TAG = ContactsFragment.class.getSimpleName();

    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X"};
    String[] web = {
            "Google Plus",
            "Twitter",
            "Windows",
            "Bing",
            "Itunes",
            "Wordpress",
            "Drupal"
    } ;

    ListView list;
    ContactsListAdapter adapter;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"inside oncReate");

        readContacts();

        adapter = new ContactsListAdapter(getActivity(), web, mobileArray);

        Log.v(TAG,":SEtting adapter");
        //list.setAdapter(adapter);
    }

    public void readContacts() {
        ContentResolver contentResolver = this.getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while(cursor.moveToNext()) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(TAG,"Creating view");
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        list = (ListView) view.findViewById(R.id.contacts_list);
        list.setAdapter(adapter);

        return view;
    }
}
