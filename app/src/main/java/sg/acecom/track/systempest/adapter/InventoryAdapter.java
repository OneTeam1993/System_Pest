package sg.acecom.track.systempest.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sg.acecom.track.systempest.InventoryActivity;
import sg.acecom.track.systempest.R;
import sg.acecom.track.systempest.database.DatabaseHelper;
import sg.acecom.track.systempest.model.Inventory;
import sg.acecom.track.systempest.util.MyPreferences;

/**
 * Created by jmingl on 17/4/18.
 */

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.MyViewHolder>{

    private Activity mContext;
    private List<Inventory> inventoryList;
    private MyPreferences pref;
    AlertDialog.Builder alert_dialog;
    DatabaseHelper db;
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView inventoryItem, itemQuantity;
        public Button buttonRemove;
        public ImageView imageAdd, imageMinus;

        public MyViewHolder(View view) {
            super(view);
            inventoryItem = view.findViewById(R.id.inventoryItem);
            itemQuantity = view.findViewById(R.id.itemQuantity);
            buttonRemove = view.findViewById(R.id.buttonRemove);
            imageAdd = view.findViewById(R.id.imageAdd);
            imageMinus = view.findViewById(R.id.imageMinus);
        }
    }


    public InventoryAdapter(Activity mContext, List<Inventory> inventoryList) {
        this.mContext = mContext;
        this.inventoryList = inventoryList;
        pref = new MyPreferences(mContext);
        db = new DatabaseHelper(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_inventory, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // Set the view to fade in
        setFadeAnimation(holder.itemView);
        final Inventory inventory = inventoryList.get(position);
        holder.inventoryItem.setText(inventory.getItemName());
        //holder.itemQuantity.setText(String.valueOf(inventory.getItemQuantity()));
        final int[] quantity = {0};
        inventory.setStockOutQuantity(0);
        holder.imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity[0] >= Integer.parseInt(inventory.getItemQuantity())){
                    Toast.makeText(mContext, "Exceed number of quantity in Inventory", Toast.LENGTH_SHORT).show();
                }else{
                    quantity[0] = quantity[0] + 1;
                    inventory.setStockOutQuantity(quantity[0]);
                    holder.itemQuantity.setText(String.valueOf(quantity[0]));
                }

            }
        });
        holder.imageMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity[0] <= 0){

                }else{
                    quantity[0] = quantity[0] - 1;
                    inventory.setStockOutQuantity(quantity[0]);
                    holder.itemQuantity.setText(String.valueOf(quantity[0]));
                }
            }
        });

        holder.buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);

                if(mContext instanceof InventoryActivity){
                    ((InventoryActivity)mContext).loadStorage();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteItem(int position) {
        // deleting the note from db
        db.deleteInventory(inventoryList.get(position));

        // removing the note from the list
        inventoryList.remove(position);
        notifyItemRemoved(position);
    }
}
