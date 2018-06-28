package com.example.shubhamr.kontakts.RecyclerView.ContactList;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shubhamr.kontakts.R;
import com.example.shubhamr.kontakts.clickListenerInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class contactRecyclerView extends RecyclerView.Adapter<contactRecyclerView.contactViewHolder> {

    private List<contactModelClass> contactList;
    private Context context;
    private clickListenerInterface clickListeners = null;

    public contactRecyclerView(Context context){
        this.context=context;
    }


    public class contactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CircleImageView contactImage;
        public TextView contactName;
        public TextView contactNumber;
        public TextView nameLetter;

        public contactViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            contactImage = (CircleImageView) view.findViewById(R.id.contactImage);
            contactName = (TextView) view.findViewById(R.id.contactName);
            contactNumber = (TextView) view.findViewById(R.id.contactNumber);
            nameLetter = (TextView)view.findViewById(R.id.nameLetter);
        }

        @Override
        public void onClick(View view){
            if(clickListeners!=null){
                clickListeners.itemClicked(view,getAdapterPosition(),contactImage,nameLetter);
            }
        }

    }


    public void setClickListeners(clickListenerInterface clickListeners){
        this.clickListeners = clickListeners;
    }


    @Override
    public contactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_recyclerview, parent, false);
        return new contactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull contactViewHolder holder, int position) {

        //Due to some bug picture are not loading properly (recycling basically) so recycling is made off
       //  holder.setIsRecyclable(false);

        contactModelClass contact = contactList.get(position);
        holder.contactName.setText(contact.getContactName());
        holder.contactNumber.setText(contact.getContactNumber());

        //If no image present put First Character  of contact on image holder
        if(contact.getContactImage()!=null&&!contact.getContactImage().toString().equals("")){
           holder.contactImage.setImageURI(null);
            holder.contactImage.setImageURI(contact.getContactImage());
           holder.nameLetter.setText("");
        }
        else{
            //To remove old cache image with white background so no repeat of image occur
            holder.contactImage.setImageResource(R.color.whiteColor);
            String letter = contact.getContactName().substring(0,1).toUpperCase();
            holder.nameLetter.setText(letter);
        }
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    //Retrieving all contacts
    public void getAllContacts(Cursor cursor) {
       contactList = new ArrayList<contactModelClass>();

        contactModelClass contact;
        if (cursor!=null&&cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                //to check whether contact have phone number or not
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    //set name and id for contact object
                    contact = new contactModelClass();
                    contact.setContactName(name);
                    contact.setID(id);

                    //now setting number for contact object
                    Cursor phoneCursor = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if(phoneCursor!=null){
                        if (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contact.setContactNumber(phoneNumber);
                        }
                        phoneCursor.close();}
                    else{contact.setContactNumber("");}


                    //now setting contact image
                    Cursor imageCursor = context.getContentResolver()
                            .query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                                    + "'", null, null);

                    if (imageCursor!=null&&imageCursor.moveToFirst()) {
                        Uri person = ContentUris.withAppendedId(
                                ContactsContract.Contacts.CONTENT_URI,Long.valueOf(id));
                        contact.setContactImage(Uri.withAppendedPath(person,
                                ContactsContract.Contacts.Photo.CONTENT_DIRECTORY));
                        imageCursor.close();
                    }
                    contactList.add(contact);
                }
            }
            cursor.close();
        }
    }


    //Retrieving contact list
    public List<contactModelClass> getContactList(){
        return contactList;
    }

    //GET single contact from list;
    public contactModelClass getContactFromList(int position){
        return contactList.get(position);
    }

    //UPDATE item in contactList
    public void setContactInList(int position,contactModelClass contact){
        contactList.set(position,contact);
        notifyItemChanged(position);
    }

    //DELETE a contact
    public void deleteContactInList(int position){
        contactList.remove(position);
        notifyItemRemoved(position);
    }

    //ADD a contact
    public void addContactInList(contactModelClass contact){
        contactList.add(contact);

        //Sort contactList alphabetically by using collection comparator
        Collections.sort(contactList, new Comparator<contactModelClass>() {
            @Override
            public int compare(final contactModelClass object1, final contactModelClass object2) {
                return object1.getContactName().compareTo(object2.getContactName()); }});
          notifyDataSetChanged();
    }




    }

