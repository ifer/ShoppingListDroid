package ifer.android.shoplist.model;

public class ShopitemPrintForm implements Comparable<ShopitemPrintForm>, Cloneable {
    private String productName;
    private String categoryName;
    private String quantity;

    public ShopitemPrintForm() {
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

    @Override
    public int compareTo(ShopitemPrintForm other) {
        return this.getProductName().compareTo(other.getProductName());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
