package ifer.android.shoplist.model;

public class ShopitemEditForm implements Comparable<ShopitemEditForm> {
    private Integer prodid;
    private String productName;
    private Integer catid;
    private String categoryName;
    private String quantity;
    private boolean selected;

    public ShopitemEditForm() {
    }

    public ShopitemEditForm(Integer prodid, String productName, Integer catid, String categoryName, String quantity, boolean selected) {
        this.prodid = prodid;
        this.productName = productName;
        this.catid = catid;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.selected = selected;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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
        String compareProduct = shopitemEditForm.getProductName();

        return this.getProductName().compareTo(compareProduct);

    }
}
