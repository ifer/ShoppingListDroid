package ifer.android.shoplist.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import ifer.android.shoplist.R;

public class EditShoplistAdapter extends RecyclerView.Adapter<EditShoplistAdapter.ShopitemViewHolder> {
    @NonNull
    @Override
    public EditShoplistAdapter.ShopitemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editshoplist_item, parent, false);
        return new ShopitemViewHolder(view);
    }

    public static class ShopitemViewHolder extends RecyclerView.ViewHolder {
        public CardView cv;
        public TextView tvProductName;
        public CheckBox chkSelected;
        public TextView tvQuantity;
        public ImageButton btnOK;

        public ShopitemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cv = (CardView) itemView.findViewById(R.id.edit_shoplist_item);
            this.tvProductName = (TextView) itemView.findViewById(R.id.tv_productname);
            this.chkSelected = (CheckBox) itemView.findViewById(R.id.chk_selected);
            this.tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            this.btnOK = (ImageButton) itemView.findViewById(R.id.btnOK);
        }
}


    @Override
    public void onBindViewHolder(@NonNull EditShoplistAdapter.ShopitemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
 }

