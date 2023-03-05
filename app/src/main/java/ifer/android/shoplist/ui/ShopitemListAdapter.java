package ifer.android.shoplist.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.model.Shopitem;
import ifer.android.shoplist.model.ShopitemPrintForm;
import retrofit2.Callback;

public class ShopitemListAdapter extends BaseAdapter {
    private Context context;
    private List<ShopitemPrintForm> shopitemList;
    private String prevCategory;

    public ShopitemListAdapter(List<ShopitemPrintForm> shopitemList){
        this.context = AppController.getAppContext();
        this.shopitemList = shopitemList;
        this.prevCategory = "";

    }



    @Override
    public int getCount() {
        return shopitemList.size();
    }


    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final int index = position;

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.shoplist_item, null, true);

            holder.itemSelected = (CheckBox) view.findViewById(R.id.itemselected);
            holder.itemName = (TextView) view.findViewById(R.id.itemname);
            holder.itemQuantity = (TextView) view.findViewById(R.id.itemquantity);
            holder.itemSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isChecked = ((CheckBox)view).isChecked();
                    if (isChecked){
                        MainActivity.addPurchased(holder.itemName.getText().toString());
//                        MainActivity.printPurchased();
                    }
                    else{
                        MainActivity.removePurchased(holder.itemName.getText().toString());
//                        MainActivity.printPurchased();
                    }
                }
            });
            view.setTag(holder);
        }
        else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }

        if (shopitemList.get(position).getProductΝame() == null){
            String categoryName = shopitemList.get(position).getCategoryName();
            holder.itemName.setText(categoryName);
            holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
            holder.itemName.setTextColor(Color.parseColor("#0000FF"));
            holder.itemQuantity.setText("");
            holder.itemSelected.setVisibility(View.INVISIBLE);
        }
        else {
            String productName = shopitemList.get(position).getProductΝame();
            String quantity = shopitemList.get(position).getQuantity();
            holder.itemName.setText(productName);
            holder.itemName.setTextColor(Color.parseColor("#000000"));
            holder.itemQuantity.setText(quantity);
            holder.itemSelected.setVisibility(View.VISIBLE);
            if(MainActivity.isItemPurchased(productName)){
                holder.itemSelected.setChecked(true);
            }
            else {
                holder.itemSelected.setChecked(false);
            }
        }

//Log.d(MainActivity.TAG, "product=" + productName);

        return view;
    }

    private class ViewHolder {

        protected CheckBox itemSelected;
        private TextView itemName;
        private TextView itemQuantity;

    }
}
