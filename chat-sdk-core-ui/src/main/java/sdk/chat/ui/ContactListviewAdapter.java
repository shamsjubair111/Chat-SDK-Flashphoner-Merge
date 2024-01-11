package sdk.chat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ContactListviewAdapter extends ArrayAdapter<ContactList> {

    Context context;
    ArrayList<ContactList> list;

    public ContactListviewAdapter(Context context, ArrayList<ContactList> items){
        super(context, R.layout.list_row,items);
        this.context = context;
        list = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.e("GetView", "GetView Called");
        if(convertView == null) {


            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_row, null);
        }
//            for(int i = 0 ; i<list.size();i++){
//                Log.e("Contacts Name",list.get(i).getContactName()+"---------------"+list.get(i).getContactNumber());
//            }

            TextView contactName = convertView.findViewById(R.id.contactName);
            contactName.setText(list.get(position).getContactName());

            ImageButton videoCall = convertView.findViewById(R.id.videoCall);

            ImageButton audioCall = convertView.findViewById(R.id.audioCall);

            ImageButton sipCallButton = convertView.findViewById(R.id.sipCallButton);

            videoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),VideoActivity.class);
                    intent.putExtra("type","video");
                    getContext().startActivity(intent);

                }
            });


            audioCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),VideoActivity.class);
                    intent.putExtra("type","audio");
                    getContext().startActivity(intent);

                }
            });


            sipCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),AudioActivity.class);
                    intent.putExtra("callee", validPhoneNumber(list.get(position).getContactNumber()));

                    getContext().startActivity(intent);
                }
            });



        return convertView;

    }

    public static String validPhoneNumber(String mobileNumber) {
        mobileNumber = mobileNumber.replaceAll("[\\s-]+", "");
        mobileNumber = mobileNumber.substring(mobileNumber.length() - 11);
        mobileNumber = "88" + mobileNumber;

        return mobileNumber;
    }

}
