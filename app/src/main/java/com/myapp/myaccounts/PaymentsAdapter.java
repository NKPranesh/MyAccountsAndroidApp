package com.myapp.myaccounts;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

class paymentRowInfo
{
    String date,paidAmt,balAmt,dnt;

    public paymentRowInfo(String date, String paidAmt, String balAmt,String dnt) {
        this.date = date;
        this.paidAmt = paidAmt;
        this.balAmt = balAmt;
        this.dnt=dnt;
    }

    public String getDnt() {
        return dnt;
    }

    public void setDnt(String dnt) {
        this.dnt = dnt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaidAmt() {
        return paidAmt;
    }

    public void setPaidAmt(String paidAmt) {
        this.paidAmt = paidAmt;
    }

    public String getBalAmt() {
        return balAmt;
    }

    public void setBalAmt(String balAmt) {
        this.balAmt = balAmt;
    }
}

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.ViewHolder> {
    Context context;
    List<paymentRowInfo> fetchedDataList;
    String name="",gstNo="",invoicendate="";
    String ip;
    int flag;
    PaymentsAdapter(){}
    public PaymentsAdapter(Context context, List<paymentRowInfo> fetchedDataList) {
        this.context = context;
        this.fetchedDataList = fetchedDataList;
    }
    @NonNull
    @Override
    public PaymentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PaymentsAdapter.ViewHolder holder, final int position) {
        ip=IPAddress.getLocalIpAddress().replace(".","-");
        holder.dateText.setText(fetchedDataList.get(position).date);
        if(!fetchedDataList.get(position).paidAmt.equals("Paid Amt")) {

            holder.paidAmtText.setText(String.format("%.2f", Float.parseFloat(fetchedDataList.get(position).paidAmt)));
            holder.balAmtText.setText(String.format("%.2f", Float.parseFloat(fetchedDataList.get(position).balAmt)));
        }
        else {
            holder.paidAmtText.setText(fetchedDataList.get(position).paidAmt);
            holder.balAmtText.setText(fetchedDataList.get(position).balAmt);
        }
        holder.dateText.setTypeface(null, Typeface.NORMAL);
        holder.paidAmtText.setTypeface(null, Typeface.NORMAL);
        holder.balAmtText.setTypeface(null, Typeface.NORMAL);
        if(fetchedDataList.get(position).date.equals("Date"))
        {
            holder.dateText.setTypeface(null, Typeface.BOLD);
            holder.paidAmtText.setTypeface(null, Typeface.BOLD);
            holder.balAmtText.setTypeface(null, Typeface.BOLD);
        }
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!fetchedDataList.get(position).date.equals("Date")) {
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.getMenuInflater().inflate(R.menu.popip_menu, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle("Delete");
                            alertDialog.setMessage("Do you want to delete this payment record?");
                            alertDialog.setCancelable(true);

                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (flag == 0)
                                        FirebaseDatabase.getInstance().getReference().child(ip).child("purchasePartyList").child(name + "---" + gstNo).child("Transactions").child(invoicendate).child("Payments").child(fetchedDataList.get(position).dnt).removeValue();

                                    else
                                        FirebaseDatabase.getInstance().getReference().child(ip).child("salePartyList").child(name + "---" + gstNo).child("Transactions").child(invoicendate).child("Payments").child(fetchedDataList.get(position).dnt).removeValue();
                                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_LONG).show();
                                    fetchedDataList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, fetchedDataList.size());
                                }
                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();
                            return true;
                        }
                    });
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return fetchedDataList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView dateText,paidAmtText,balAmtText;
        public CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText=itemView.findViewById(R.id.date1ID);
            paidAmtText=itemView.findViewById(R.id.paidAmount);
            balAmtText=itemView.findViewById(R.id.balanceAmt);
            cardView=itemView.findViewById(R.id.cardID);
        }
    }

}
