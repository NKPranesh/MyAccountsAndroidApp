package com.myapp.myaccounts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

class partyRowInfo{
    public String name,gstNo;
    public partyRowInfo(String name, String gstNo) {
        this.name = name;
        this.gstNo = gstNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGstNo() {
        return gstNo;
    }

    public void setGstNo(String gstNo) {
        this.gstNo = gstNo;
    }
}
public class myAdapterClass extends RecyclerView.Adapter<myAdapterClass.ViewHolder> {
    Context context;
    List<partyRowInfo> fetchedDataList;
    List<String> deleteList;
    String data;
    int flag;
    String ipAddress;
    myAdapterClass(){

    }
    myAdapterClass(Context context,List fetchedDataList){
        this.context=context;
        this.fetchedDataList=fetchedDataList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.partylist_row,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
         ipAddress = IPAddress.getLocalIpAddress().replace(".", "-");
        holder.nameOfParty.setText(fetchedDataList.get(position).name);
        if(fetchedDataList.get(position).gstNo.length()!=0){
            holder.GstNo.setText("GST No :"+fetchedDataList.get(position).gstNo);}
        else{
            holder.GstNo.setText("");
        }
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu=new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.popip_menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
                        alertDialog.setTitle("Delete");
                        alertDialog.setMessage("Do you want to delete the party? This will also delete the record of all its transactions.");
                        alertDialog.setCancelable(true);
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data=holder.nameOfParty.getText()+"---"+holder.GstNo.getText().toString().split(":")[1];
                                if(flag==0)
                                {
                                    deleteList=new ArrayList<>();
                                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(ipAddress).child("purchasePartyList").child(data).child("Transactions");
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot snap:snapshot.getChildren()){
                                                deleteList.add(snap.getKey());
                                            }
                                            StorageReference ref= FirebaseStorage.getInstance().getReference().child(ipAddress).child("purchasePartyList").child(data).child("Transactions");
                                            for(String s:deleteList){
                                                ref.child(s).child("billUpload").delete();
                                                ref.child(s).child("wayBillUpload").delete();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                                    FirebaseDatabase.getInstance().getReference().child(ipAddress).child("purchasePartyList").child(data).removeValue();
                                }
                                else {
                                    deleteList=new ArrayList<>();
                                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(ipAddress).child("salePartyList").child(data).child("Transactions");
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot snap:snapshot.getChildren()){
                                                deleteList.add(snap.getKey());
                                            }
                                            StorageReference ref=FirebaseStorage.getInstance().getReference().child(ipAddress).child("salePartyList").child(data).child("Transactions");
                                            for(String s:deleteList){
                                                ref.child(s).child("billUpload").delete();
                                                ref.child(s).child("wayBillUpload").delete();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                                    FirebaseDatabase.getInstance().getReference().child(ipAddress).child("salePartyList").child(data).removeValue();
                                }
                                Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_LONG).show();
                                fetchedDataList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,fetchedDataList.size());
                            }
                        });
                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog dialog=alertDialog.create();
                        dialog.show();
                        return true;
                    }
                });
                return true;
            }
        });
    }
    @Override
    public int getItemCount() {
        return fetchedDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nameOfParty,GstNo;
        public CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameOfParty=itemView.findViewById(R.id.NameOfPartyID);
            GstNo=itemView.findViewById(R.id.gstNoID);
            cardView=itemView.findViewById(R.id.cardViewID);
            cardView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int pos=getAdapterPosition();
            partyRowInfo item=fetchedDataList.get(pos);
            // Toast.makeText(context,item.name+" "+item.gstNo,Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(context,transactions.class);
            intent.putExtra("name",item.name);
            intent.putExtra("gstNo",item.gstNo);
            intent.putExtra("flag",flag);
            context.startActivity(intent);
        }
    }
    public void filterList(List<partyRowInfo> filteredList){
        fetchedDataList=filteredList;
        if(fetchedDataList.size()==0){
            fetchedDataList.clear();
            partyRowInfo empty=new partyRowInfo("No Party found","");
            fetchedDataList.add(empty);
        }
        notifyDataSetChanged();
    }
}
