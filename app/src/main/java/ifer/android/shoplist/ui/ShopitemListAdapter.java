package ifer.android.shoplist.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.List;

import ifer.android.shoplist.R;
import ifer.android.shoplist.model.Shopitem;
import ifer.android.shoplist.model.ShopitemPrintForm;
import retrofit2.Callback;

public class ShopitemListAdapter extends BaseAdapter {
    private Context context;
    private List<ShopitemPrintForm> shopitemList;

    public ShopitemListAdapter(Context context, List<ShopitemPrintForm> shopitemList){
        this.context = this.context;
        this.shopitemList = shopitemList;
 Log.d(MainActivity.TAG, shopitemList.toString());

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

        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.shoplist_item, null, true);

            holder.itemSelected = (CheckBox) view.findViewById(R.id.itemselected);
            holder.itemProduct = (TextView) view.findViewById(R.id.itemproduct);
            holder.itemQuantity = (TextView) view.findViewById(R.id.itemquantity);
            view.setTag(holder);
        }
        else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }

        String productName = shopitemList.get(position).getProductName();
        String quantity = shopitemList.get(position).getQuantity();
        holder.itemProduct.setText(productName);
        holder.itemQuantity.setText(quantity);
Log.d(MainActivity.TAG, "product=" + productName);

        return view;
    }

    private class ViewHolder {

        protected CheckBox itemSelected;
        private TextView itemProduct;
        private TextView itemQuantity;

    }
}
