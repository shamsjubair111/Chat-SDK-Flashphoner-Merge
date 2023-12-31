package sdk.chat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class ContactListviewAdapter extends ArrayAdapter<DemoContact> {

    Context context;
    ArrayList<DemoContact> list;

    public ContactListviewAdapter(Context context, ArrayList<DemoContact> items){
        super(context, R.layout.list_row,items);
        this.context = context;
        list = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_row,null);

            TextView contactName = convertView.findViewById(R.id.contactName);
            contactName.setText(list.get(position).getName());

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
                    intent.putExtra("  ", list.get(position).getNumber());
                    getContext().startActivity(intent);
                }
            });



        }
        return convertView;

    }


}
