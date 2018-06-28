package com.example.shubhamr.kontakts;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.shubhamr.kontakts.HelperClass.getSingleContact;
import com.example.shubhamr.kontakts.RecyclerView.ContactList.contactModelClass;
import com.example.shubhamr.kontakts.RecyclerView.ContactList.contactRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MainActivity extends AppCompatActivity implements clickListenerInterface, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.contactList) RecyclerView contactRecyclerViews;
    public contactRecyclerView contactAdapter;
    public int LAST_ITEM_CLICKED;
    public final int CONTACT_EDITING_REQUEST_CODE = 150;
    public final int CONTACT_ADDED_REQUEST_CODE = 50;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        MainActivityPermissionsDispatcher.initializeLoaderWithPermissionCheck(this);
        contactRecyclerViews.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        contactRecyclerViews.setHasFixedSize(true);
    }


    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    public void initializeLoader(){
        getLoaderManager().initLoader(1, null, this);
    }


    @Override
    public Loader onCreateLoader(int arg0, Bundle arg1) {
        Uri CONTACT_URI = ContactsContract.Contacts.CONTENT_URI;

        //Retrieve data in ascending order ignoring lower and upper case
        CursorLoader cursorLoader = new CursorLoader(this, CONTACT_URI, null,
                null, null, "upper("+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        cursor.moveToFirst();
        contactAdapter = new contactRecyclerView(this);
        contactAdapter.getAllContacts(cursor);
        contactRecyclerViews.setAdapter(contactAdapter);
        contactAdapter.setClickListeners(this);
    }


    @Override
    public void onLoaderReset(Loader arg0) {
        contactAdapter.getAllContacts(null);
    }



    @Override
    public void itemClicked(View view, int position,CircleImageView sharedImage,TextView sharedText){
        LAST_ITEM_CLICKED = position;
        contactModelClass contact = contactAdapter.getContactFromList(position);
        Intent intent = new Intent(this,DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("contactID",contact.getID());
        intent.putExtras(bundle);
        Pair<View, String> p1 = Pair.create((View)sharedImage,"contactSharedImage");
        Pair<View,String> p2 = Pair.create((View)sharedText,"contactSharedName");

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1,p2);
        ActivityCompat.startActivityForResult(this,intent, CONTACT_EDITING_REQUEST_CODE,options.toBundle());
    }



    //Calling adding contact intent when add contact button is clicked
    @OnClick(R.id.addContactButton)
    public void setAddContactButton(View view){
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra("finishActivityOnSaveCompleted", true);
        startActivityForResult(intent,CONTACT_ADDED_REQUEST_CODE);
    }


    //Result of editing a contact
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CONTACT_EDITING_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            //To check whether contact is deleted or edited
            boolean EDITING_RESULT = data.getBooleanExtra("CONTACT_EDITED", false);
            boolean DELETED = data.getBooleanExtra("CONTACT_DELETED", false);

            //If contact is edited retrieve all details of contact for replacing
            if (EDITING_RESULT) {
                String id = data.getStringExtra("id");
                String name = data.getStringExtra("editedName");
                String number = data.getStringExtra("editedNumber");
                String uri = data.getStringExtra("editedUri");
                Uri image = Uri.parse(uri);
                contactModelClass contact = new contactModelClass(id, name, number, image);
                contactAdapter.setContactInList(LAST_ITEM_CLICKED, contact);
            }

            //If contact is deleted
            if (DELETED) {
                contactAdapter.deleteContactInList(LAST_ITEM_CLICKED);
              }
        }

        if (requestCode == CONTACT_ADDED_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            {
                String name = "fake", uri = "fake";
                Uri contactImage;
                Uri newContactUri = data.getData();
                contactModelClass contact = getSingleContact.getContactByUri(this,newContactUri);
                if(contact!=null) {
                contactAdapter.addContactInList(contact);
                }
                }

            }
        }


    //Permission Dispatcher
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }



}



