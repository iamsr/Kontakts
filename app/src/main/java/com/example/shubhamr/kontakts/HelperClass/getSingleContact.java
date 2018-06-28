package com.example.shubhamr.kontakts.HelperClass;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.example.shubhamr.kontakts.RecyclerView.ContactList.contactModelClass;

public class getSingleContact {


    //For retrieving details of single contact using id;
    public static contactModelClass getContactById(Context context, String id) {

        String name = null, uri = null, number = null;
        Uri contactImage;
        //Projection Fields is for the column we want in cursor
        String[] projectionFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI};

        //Putting selection condition where id is equal to id send by previous activity
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projectionFields,
                ContactsContract.Contacts._ID + " = ?", new String[]{id}, null);


        if (cursor != null && cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            uri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
            cursor.close();
        }

        //if image not present putting blank uri path
        if (uri == null) {
            uri = "";
        }
        contactImage = Uri.parse(uri);

        //retrieving phone number
        Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{id}, null);

        while (phoneCursor != null && phoneCursor.moveToNext()) {
            number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        phoneCursor.close();

        contactModelClass contact = new contactModelClass(id, name, number, contactImage);
        return contact;
    }


    //For retrieving details of newly added contact using uri
    public static contactModelClass getContactByUri(Context context, Uri uri) {

        String name = null, number = null, id = null, imageUri = null;
        Uri contactImage;

        if (uri != null) {
            String[] projectionFields = new String[]{ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_URI};

            //Getting name phone id using uri
            Cursor cursor = context.getContentResolver().query(uri, projectionFields, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                cursor.close();
            }

            //retrieving phone number
            Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{id}, null);

            while (phoneCursor != null && phoneCursor.moveToNext()) {
                number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phoneCursor.close();
            if (imageUri == null) {
                imageUri = "";
            }
            contactImage = Uri.parse(imageUri);

            contactModelClass contact = new contactModelClass(id, name, number, contactImage);
            return contact;

        }
        return null;
    }

}
