package ifer.android.shoplist.model;

public class ShopitemPrintForm implements Comparable<ShopitemPrintForm>, Cloneable {
    private String productΝame;
    private String categoryName;
    private String quantity;

    public ShopitemPrintForm() {
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

    @Override
    public int compareTo(ShopitemPrintForm other) {
        return this.getProductΝame().compareTo(other.getProductΝame());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
