package ifer.android.shoplist.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.model.ShopitemEditForm;

import static ifer.android.shoplist.util.GenericUtils.*;

public class EditShoplistAdapter extends RecyclerView.Adapter<EditShoplistAdapter.ShopitemViewHolder> {
    private List<ShopitemEditForm> shopitemList;
    private Context context;

    public EditShoplistAdapter(List<ShopitemEditForm> shopitemList) {
        this.shopitemList = shopitemList;
        this.context = AppController.getAppContext();
//Log.d(MainActivity.TAG, "size=" + shopitemList.size());

    }

    @NonNull
    @Override
    public ShopitemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editshoplist_item, parent, false);
        return new ShopitemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopitemViewHolder holder, int position) {
        holder.tvProductName.setText(shopitemList.get(position).getProductName());
        holder.etQuantity.setText(shopitemList.get(position).getQuantity());
        if (shopitemList.get(position).isSelected()){
            holder.etQuantity.setEnabled(true);
            holder.btnOK.setVisibility(View.VISIBLE);
        }
        else {
            holder.etQuantity.setEnabled(false);
            holder.btnOK.setVisibility(View.INVISIBLE);
        }
        holder.etQuantity.setText(shopitemList.get(position).getQuantity());
        holder.chkSelected.setChecked(shopitemList.get(position).isSelected());
//Log.d(MainActivity.TAG, "product="+ shopitemList.get(position).getProductName());
    }


    public static class ShopitemViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tvProductName;
        CheckBox chkSelected;
        EditText etQuantity;
        ImageButton btnOK;

        public ShopitemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cv = (CardView) itemView.findViewById(R.id.edit_shoplist_item);
            this.tvProductName = (TextView) itemView.findViewById(R.id.tv_productname);
            this.chkSelected = (CheckBox) itemView.findViewById(R.id.chk_selected);
            this.etQuantity = (EditText) itemView.findViewById(R.id.et_quantity);
            this.btnOK = (ImageButton) itemView.findViewById(R.id.btnOK);
        }
    }

    @Override
    public int getItemCount() {
        return shopitemList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
 }

