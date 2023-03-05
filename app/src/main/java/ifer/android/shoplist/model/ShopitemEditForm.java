package ifer.android.shoplist.model;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.Objects;

public class ShopitemEditForm implements Comparable<ShopitemEditForm>, Cloneable {
    private Integer prodid;
    private String productΝame;
    private Integer catid;
    private String categoryName;
    private String quantity;
    private boolean selected;

    public ShopitemEditForm() {
    }

    public ShopitemEditForm(Integer prodid, String productName, Integer catid, String categoryName, String quantity, boolean selected) {
        this.prodid = prodid;
        this.productΝame = productName;
        this.catid = catid;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.selected = selected;
    }


    public String getProductΝame() {
        return productΝame;
    }

    public void setProductΝame(String productΝame) {
        this.productΝame = productΝame;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Integer getProdid() {
        return prodid;
    }

    public void setProdid(Integer prodid) {
        this.prodid = prodid;
    }

    public Integer getCatid() {
        return catid;
    }

    public void setCatid(Integer catid) {
        this.catid = catid;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int compareTo(ShopitemEditForm shopitemEditForm) {
        //int compareQuantity = ((Fruit) compareFruit).getQuantity();
        String compareProduct = shopitemEditForm.getProductΝame();

        return this.getProductΝame().compareTo(compareProduct);

    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ShopitemEditForm that = (ShopitemEditForm) o;
        return selected == that.selected &&
                prodid.equals(that.prodid) &&
                quantity.equals(that.quantity);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static Comparator<ShopitemEditForm> productByCategoryComparator = new Comparator<ShopitemEditForm>() {

        public int compare(ShopitemEditForm sef1, ShopitemEditForm sef2) {

            String categ1 = sef1.getCategoryName();
            String categ2 = sef2.getCategoryName();

            return categ1.compareTo(categ2);

        }};
}
