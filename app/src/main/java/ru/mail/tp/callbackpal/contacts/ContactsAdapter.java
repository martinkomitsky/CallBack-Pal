package ru.mail.tp.callbackpal.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import ru.mail.tp.callbackpal.R;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>{
    private List<Contact> contactList;
    private Context mContext;
    public ContactsAdapter(List<Contact> contactList, Context mContext){
        this.contactList = contactList;
        this.mContext = mContext;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_single_contact, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {
        final Contact contact= contactList.get(position);
        holder.tvContactName.setText(contact.getContactName());
        holder.tvPhoneNumber.setText(contact.getContactNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String currentPhone = holder.tvPhoneNumber.getText().toString().replaceAll("[^0-9+]", "");
//                holder.tvPhoneNumber.getText(contactList.get(position).getContactNumber());
                Toast.makeText(mContext,
                        currentPhone, Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        ImageView ivContactImage;
        TextView tvContactName;
        TextView tvPhoneNumber;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
        }
    }
}
