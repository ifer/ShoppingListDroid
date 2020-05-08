package ifer.android.shoplist.api;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.model.Product;


public class ProductListAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;

    public ProductListAdapter(List<Product> productList) {
        this.productList = productList;
        this.context = AppController.getAppContext();
    }

    @Override
    public int getCount() {
        return  productList.size();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.product_item, null, true);

            holder.productName = (TextView) view.findViewById(R.id.productname);
            holder.category = (TextView) view.findViewById(R.id.category);
            view.setTag(holder);
        }
        else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();

        }

        holder.productName.setText(productList.get(position).getDescr());
        holder.category.setText(productList.get(position).getCategoryName());

        return view;
    }


    private class ViewHolder {

        private TextView productName;
        private TextView category;

    }
}
