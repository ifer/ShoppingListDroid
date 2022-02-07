package ifer.android.shoplist.ui;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.model.ShopitemEditForm;

import static ifer.android.shoplist.util.GenericUtils.*;

public class EditShoplistAdapter extends RecyclerView.Adapter<EditShoplistAdapter.ShopitemViewHolder> {
    private List<ShopitemEditForm> shopitemList;

    private Context context;
    private Integer layoutWidth;
    private Integer layoutHeight;

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

        EditShoplistActivity.changeSelectedCount();
//        EditShoplistActivity.printSelected();
        return new ShopitemViewHolder(view, new CustomEditTextListener(), new CustomOnClickListener());
    }

    @Override
    public void onBindViewHolder(@NonNull final ShopitemViewHolder holder, final int position) {

        //Must be first statements
        holder.customEditTextListener.updatePosition(position);
        holder.customOnClickListener.updateHolder(holder);

        holder.tvProductName.setText(shopitemList.get(position).getProductName());
        holder.etQuantity.setText(shopitemList.get(position).getQuantity());

        if (shopitemList.get(position).isSelected()) {
            holder.etQuantity.setEnabled(true);
        } else {
            holder.etQuantity.setEnabled(false);
        }
        holder.etQuantity.setText(shopitemList.get(position).getQuantity());
        holder.chkSelected.setChecked(shopitemList.get(position).isSelected());

        // Increase bottom margin when at last list item, so that floating button does not overlap the item
        if (position + 1 == getItemCount()) {
            // set bottom margin to 80dp.
            setBottomMargin(holder.itemView, (int) (80 * Resources.getSystem().getDisplayMetrics().density));
        } else {
            // reset bottom margin back to zero. (your value may be different)
            setBottomMargin(holder.itemView, 0);
        }

    }

    public static void setBottomMargin(View view, int bottomMargin) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMargin);
            view.requestLayout();
        }
    }

    public static class ShopitemViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tvProductName;
        CheckBox chkSelected;
        EditText etQuantity;
        CustomEditTextListener customEditTextListener;
        CustomOnClickListener customOnClickListener;
        int realPosition;


        public ShopitemViewHolder(@NonNull View itemView,  CustomEditTextListener customEditTextListener, CustomOnClickListener customOnClickListener) {
            super(itemView);
            this.cv = (CardView) itemView.findViewById(R.id.edit_shoplist_item);
            this.tvProductName = (TextView) itemView.findViewById(R.id.tv_productname);
            this.chkSelected = (CheckBox) itemView.findViewById(R.id.chk_selected);
            this.etQuantity = (EditText) itemView.findViewById(R.id.et_quantity);

            this.customEditTextListener = customEditTextListener;
            this.customOnClickListener = customOnClickListener;
            this.etQuantity.addTextChangedListener(customEditTextListener);
            this.chkSelected.setOnClickListener(customOnClickListener);

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


    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class CustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition( int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String value = charSequence.toString();
//
//            if ( isEmptyOrNull(value) || value.trim().equals("0") ) {
//                value = "1";
//            }

            shopitemList.get(position).setQuantity(value);
//            EditShoplistActivity.changeShopitemQuantity(realPosition,  charSequence.toString());

//EditShoplistActivity.printSelected();
        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    }

    private class CustomOnClickListener implements View.OnClickListener{
        private ShopitemViewHolder holder;

        public void updateHolder (ShopitemViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onClick(View view) {
            if(holder.chkSelected.isChecked()){
                holder.etQuantity.setEnabled(true);
                holder.etQuantity.setText("1");
            }
            else {
                holder.etQuantity.setEnabled(false);
                holder.etQuantity.setText("0");
            }
//            EditShoplistActivity.changeShopitemStatus(holder.realPosition, holder.chkSelected.isChecked(), holder.etQuantity.getText().toString());
            shopitemList.get(holder.getAdapterPosition()).setQuantity(holder.etQuantity.getText().toString());
            shopitemList.get(holder.getAdapterPosition()).setSelected(holder.chkSelected.isChecked());
            EditShoplistActivity.changeSelectedCount();
// EditShoplistActivity.printSelected();

        }
    }
 }

