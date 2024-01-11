/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package sdk.chat.ui.fragments;

import static sdk.chat.ui.ContactUtils.contactArrayList;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.jakewharton.rxrelay2.PublishRelay;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import sdk.chat.core.dao.User;
import sdk.chat.core.events.EventType;
import sdk.chat.core.events.NetworkEvent;
import sdk.chat.core.interfaces.UserListItem;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.core.types.ConnectionType;
import sdk.chat.core.types.SearchActivityType;
import sdk.chat.core.utils.UserListItemConverter;
import sdk.chat.ui.AppAudioCall;
import sdk.chat.ui.AudioActivity;
import sdk.chat.ui.ChatSDKUI;
import sdk.chat.ui.ContactList;
import sdk.chat.ui.ContactListviewAdapter;
import sdk.chat.ui.ContactUtils;
import sdk.chat.ui.DemoContact;
import sdk.chat.ui.R;
import sdk.chat.ui.VideoActivity;
import sdk.chat.ui.adapters.UsersListAdapter;
import sdk.chat.ui.interfaces.SearchSupported;
import sdk.chat.ui.provider.MenuItemProvider;
import sdk.chat.ui.utils.DialogUtils;
import sdk.guru.common.Optional;
import sdk.guru.common.RX;

/**
 * Created by itzik on 6/17/2014.
 */
public class ContactsFragment extends BaseFragment implements SearchSupported {

    protected UsersListAdapter adapter;

    protected PublishRelay<User> onClickRelay = PublishRelay.create();
    protected PublishRelay<User> onLongClickRelay = PublishRelay.create();
    protected Disposable listOnClickListenerDisposable;
    protected Disposable listOnLongClickListenerDisposable;

    protected String filter;

    protected List<User> sourceUsers = new ArrayList<>();


    protected FrameLayout root;

    protected Button voiceCall;
    protected Button button2;

    protected Button sipCall;

    ContactListviewAdapter adapter1;

    ListView listview;

    private static final int REQUEST_READ_CONTACTS = 123; // You can use any integer value



    @Override
    protected @LayoutRes int getLayout() {
        return R.layout.fragment_contacts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);


        root = view.findViewById(R.id.root);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            // Permission is already granted, you can proceed with accessing contacts
            ContactUtils.getContacts(getActivity());
        }


        listview = view.findViewById(R.id.listView);




//        voiceCall = view.findViewById(R.id.voiceCall);
//        button2 = view.findViewById(R.id.button2);
//        sipCall = view.findViewById(R.id.sipCall);

//        DemoContact mustafaVaiContact = new DemoContact("Mustafa Vai","8801754105098");
//        DemoContact jubairContact = new DemoContact("Jubair","8801730330016");
//        DemoContact marufContact = new DemoContact("Maruf Vai","8801932383889");
//        DemoContact appleContact = new DemoContact("Apple Vai", "8801743801850");
//        DemoContact easinContact = new DemoContact("Easin Vai", "8801941199607");
//        DemoContact avijitContact = new DemoContact("Avijit Da","8801730330021");
//        DemoContact sazidContact = new DemoContact("Sazid Vai", "8801730042594");



//        ArrayList<ContactList> arrayList = new ArrayList<>();

//        arrayList.add(mustafaVaiContact);
//        arrayList.add(jubairContact);
//        arrayList.add(appleContact);
//        arrayList.add(easinContact);
//        arrayList.add(marufContact);
//        arrayList.add(avijitContact);
//        arrayList.add(sazidContact);


        adapter1 = new ContactListviewAdapter(getActivity(),contactArrayList);
//        Log.e("Adapter","Adapter Called");
//        for(int i = 0 ; i<contactArrayList.size();i++){
//            Log.e("Contact Nameswqe",contactArrayList.get(i).getContactName()+ "---------"+contactArrayList.get(i).getContactNumber());
//        }
        listview.setAdapter(adapter1);





//        sipCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(getContext(), AudioActivity.class);
//                startActivity(intent);
//            }
//        });



//                voiceCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(getContext(), VideoActivity.class);
//                intent.putExtra("type","audio");
//                startActivity(intent);
//            }
//        });



//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), VideoActivity.class);
//                intent.putExtra("type","video");
//                startActivity(intent);
//            }
//        });








        initViews();

        loadData(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        addListeners();

    }

    @Override
    public void onStop() {
        super.onStop();

        dm.dispose();
    }

    public void addListeners() {

        if (listOnClickListenerDisposable != null) {
            listOnClickListenerDisposable.dispose();
        }
        listOnClickListenerDisposable = adapter.onClickObservable().subscribe(o -> {
            if (o instanceof User) {
                final User clickedUser = (User) o;

                onClickRelay.accept(clickedUser);
                startProfileActivity(clickedUser.getEntityID());
            }
        });

        if (listOnLongClickListenerDisposable != null) {
            listOnLongClickListenerDisposable.dispose();
        }
        listOnLongClickListenerDisposable = adapter.onLongClickObservable().subscribe(o -> {
            if (o instanceof User) {
                final User user = (User) o;
                onLongClickRelay.accept(user);

                DialogUtils.showToastDialog(getContext(), R.string.delete_contact, 0, R.string.delete, R.string.cancel, () -> {
                    ChatSDK.contact()
                            .deleteContact(user, ConnectionType.Contact)
                            .subscribe(ContactsFragment.this);
                }, null);
            }
        });

        dm.add(ChatSDK.events().sourceOnMain()
                .filter(NetworkEvent.filterContactsChanged())
                .subscribe(networkEvent -> loadData(true)));

        dm.add(ChatSDK.events().sourceOnMain()
                .filter(NetworkEvent.filterType(EventType.UserPresenceUpdated))
                .subscribe(networkEvent -> loadData(true)));

        dm.add(ChatSDK.events().sourceOnMain()
                .filter(NetworkEvent.filterType(EventType.UserMetaUpdated))
                .subscribe(networkEvent -> loadData(true)));
    }

    public void startProfileActivity(String userEntityID) {
        ChatSDK.ui().startProfileActivity(getContext(), userEntityID);
    }

    public void initViews() {

        // Create the adapter only if null this is here so we wont
        // override the adapter given from the extended class with setAdapter.
        adapter = new UsersListAdapter();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        ChatSDKUI.provider().menuItems().addAddItem(getContext(), menu, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /* Cant use switch in the library*/
        int id = item.getItemId();

        // Each user that will be found in the filter context will be automatically added as a contact.
        if (id == MenuItemProvider.addItemId) {
            startSearchActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startSearchActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final List<SearchActivityType> activities = new ArrayList<>(ChatSDK.ui().getSearchActivities());

        if (activities.size() == 1) {
            activities.get(0).startFrom(getActivity());
            return;
        }

        String[] items = new String[activities.size()];
        int i = 0;

        for (SearchActivityType activity : activities) {
            items[i++] = activity.title;
        }

        builder.setTitle(getActivity().getString(R.string.search)).setItems(items, (dialogInterface, index) -> {
            // Launch the appropriate context
            activities.get(index).startFrom(getActivity());
        });

        builder.show();
    }

    public void loadData(final boolean force) {
        dm.add(Single.create((SingleOnSubscribe<Optional<List<UserListItem>>>) emitter -> {
            final ArrayList<User> originalUserList = new ArrayList<>(sourceUsers);
            reloadData();
            if (!originalUserList.equals(sourceUsers) || force) {
                emitter.onSuccess(new Optional<>(UserListItemConverter.toUserItemList(sourceUsers)));
            } else {
                emitter.onSuccess(new Optional<>());
            }
        }).subscribeOn(RX.db()).observeOn(RX.main()).subscribe(listOptional -> {
            if (!listOptional.isEmpty()) {
                adapter.setUsers(listOptional.get(), true);
            }
        }));
    }

    @Override
    public void clearData() {
        if (adapter != null) {
            adapter.clear();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(true);
    }

    @Override
    public void reloadData() {
        sourceUsers.clear();
        sourceUsers.addAll(filter(ChatSDK.contact().contacts()));
    }

    public Observable<User> onClickObservable() {
        return onClickRelay;
    }

    public Observable<User> onLongClickObservable() {
        return onLongClickRelay;
    }

    @Override
    public void filter(String text) {
        filter = text;
        loadData(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing contacts
                ContactUtils.getContacts(getContext());
            } else {
                // Permission denied, handle accordingly (e.g., show a message or take alternative actions)
            }
        }
    }

    public List<User> filter(List<User> users) {
        if (filter == null || filter.isEmpty()) {
            return users;
        }

        List<User> filteredUsers = new ArrayList<>();
        for (User u : users) {
            if (u.getName() != null && u.getName().toLowerCase().contains(filter.toLowerCase())) {
                filteredUsers.add(u);
            }
        }
        return filteredUsers;
    }

}
