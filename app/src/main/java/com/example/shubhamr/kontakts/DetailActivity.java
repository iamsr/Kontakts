package com.example.shubhamr.kontakts;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shubhamr.kontakts.HelperClass.getSingleContact;
import com.example.shubhamr.kontakts.HelperClass.timeFormatterClass;
import com.example.shubhamr.kontakts.RecyclerView.ContactList.contactModelClass;
import com.example.shubhamr.kontakts.RecyclerView.RecentLogs.recentModelClass;
import com.example.shubhamr.kontakts.RecyclerView.RecentLogs.recentRecyclerViewAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class DetailActivity extends AppCompatActivity {

    public String name,number,uri,id;
    public Uri contactImage;
    @BindView(R.id.detailContactName)TextView detailContactName;
    @BindView(R.id.contactDetailImage)CircleImageView contactDetailImage;
    @BindView(R.id.numberDetail)CardView cardView;
    @BindView(R.id.detailNumber)TextView detailNumber;
    @BindView(R.id.recentRecyclerView)RecyclerView recentRecyclerView;
    @BindView(R.id.detailNameLetter)TextView detailNameLetter;
    public final int EDIT_CONTACT_RESULT = 100;
    public List<recentModelClass> recentList;
    public recentRecyclerViewAdapter recentAdapter;
    public boolean CONTACT_EDITED, CONTACT_DELETED;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        CONTACT_EDITED = false;   //setting default value false
        CONTACT_DELETED = false; //default value for delete variable
        setContactDetails();
        DetailActivityPermissionsDispatcher.getAllRecentWithPermissionCheck(this);
       }




    //Call edit contact activity when card view is clicked
    @OnClick(R.id.numberDetail)
    public void OnClickNumberDetail(){
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)));
        intent.putExtra("finishActivityOnSaveCompleted", true);
        startActivityForResult(intent, EDIT_CONTACT_RESULT);
    }




    //Open dialer with number
    @OnClick(R.id.callButton)
    public void callButtonClicked(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+ number));  //tel necessary
        intent.putExtra("finishActivityOnSaveCompleted", true);
        startActivity(intent);
    }




    //opening sms application with number
    @OnClick(R.id.messageButton)
    public void messageButton(){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + number));
        sendIntent.putExtra("finishActivityOnSaveCompleted", true);
        startActivity(sendIntent);
    }





    //calling deleting function with permission
    @OnClick(R.id.deleteButton)
    public void deleteButtonClicked(){
        DetailActivityPermissionsDispatcher.deleteContactWithPermissionCheck(this);
    }
    //Contact deleting method
    @NeedsPermission(Manifest.permission.WRITE_CONTACTS)
    public void deleteContact(){
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,id); //filtering with id
        getContentResolver().delete(uri,null,null);
        Toast.makeText(this,"Deleted",Toast.LENGTH_LONG).show();
        CONTACT_DELETED =true;
        finish();
    }




    //To set detail of contact when detailActivity is open
    public void setContactDetails(){

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("contactID");
        //Using ID getting contact detail
        contactModelClass contact = getSingleContact.getContactById(this,id);

        //setting variable for other purpose
        name = contact.getContactName();
        number = contact.getContactNumber();
        uri = contact.getContactImage().toString();

        //setting name, number and image with postponeentertransition() so shared element transition go smoothly
        detailContactName.setText(contact.getContactName());
        detailNumber.setText(contact.getContactNumber());
        if(contact.getContactImage()!=null&&!contact.getContactImage().toString().equals("")){
            postponeEnterTransition();
        Picasso.get().load(contact.getContactImage()).fit().into(contactDetailImage, new Callback() {
            @Override
            public void onSuccess() {
                startPostponedEnterTransition();
            }

            @Override
            public void onError(Exception e) {
                startPostponedEnterTransition();
            }
        });}
        else{
            String firstLetter = name.substring(0,1).toUpperCase();
            detailNameLetter.setText(firstLetter);
        }
    }



    //Refresh contact if changed
    public void onEditedContact(){

        //Retrieving using getSingleContact helper class static method
        contactModelClass contact = getSingleContact.getContactById(this,id);

       //updating value of variables
        name = contact.getContactName();
        number = contact.getContactNumber();
        uri = contact.getContactImage().toString();

        //Replacing old contact detail with new one
        detailContactName.setText(contact.getContactName());
        detailNumber.setText(contact.getContactNumber());

        //If contact have set image else set first letter of contact name
        if(contact.getContactImage()!=null&&!contact.getContactImage().toString().equals("")){
            contactDetailImage.setImageURI(contact.getContactImage());
            detailNameLetter.setText("");
        }
            else {
            contactDetailImage.setImageResource(R.color.whiteColor); //To replace old image with white background
            String firstLetter = name.substring(0,1).toUpperCase();
            detailNameLetter.setText(firstLetter);

        }
    }



    //Getting recent logs data for recycler view
    @NeedsPermission(Manifest.permission.READ_CALL_LOG)
    public void getAllRecent(){
        recentList = new ArrayList<recentModelClass>();
        recentModelClass recent;
        String date,duration,type,finalCallType="",time;

        //query contact log with selection argument as phone number and order as Descending order
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, CallLog.Calls.NUMBER + " = ? ", new String[]{number} ,
                CallLog.Calls.DATE + " DESC");

        if (cursor!=null){
            while (cursor.moveToNext ()) {
                date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));

                //Checking type of call
                int typeCode = Integer.parseInt(type);
                switch (typeCode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        finalCallType = "OUTGOING";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        finalCallType = "INCOMING";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        finalCallType = "MISSED";
                        break;
                }

                //Helper class made called timeFormatterClass to get time, date in particular format
                recent = new recentModelClass(timeFormatterClass.getTime(date),
                        timeFormatterClass.getDate(date),timeFormatterClass.getDurationTime(duration),
                        finalCallType);
                recentList.add(recent);
            }
            cursor.close(); }


        recentAdapter = new recentRecyclerViewAdapter(recentList);
        recentRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recentRecyclerView.setAdapter(recentAdapter);


    }





    //Result of contact edited intent and If contact edited refresh the contact method call
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == EDIT_CONTACT_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                CONTACT_EDITED = true;
                onEditedContact();

            }
        }
    }




    //Permission Dispatcher
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        DetailActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }





    //Return data according to contact edited or deleted
    @Override
    public void finish(){
        Intent returnIntent = new Intent();
        if(CONTACT_EDITED) {
            returnIntent.putExtra("CONTACT_EDITED", true);
            returnIntent.putExtra("editedName",name);
            returnIntent.putExtra("editedNumber",number);
            returnIntent.putExtra("editedUri",uri);
            returnIntent.putExtra("id",id);
        }

        if(CONTACT_DELETED){
            returnIntent.putExtra("CONTACT_DELETED", true);
        }
        setResult(RESULT_OK,returnIntent);
        super.finish();
    }

}
