package ifer.android.shoplist.model;

public class Product implements Comparable<Product>{
    private Integer prodid;

    private String descr;

    private Integer catid;

    private String categoryName;

    public Product() {

    }

    public Product(Integer prodid, String descr, Integer catid) {
        this.prodid = prodid;
        this.descr = descr;
        this.catid = catid;
    }
    public Product(Integer prodid, String descr, Integer catid, String categoryName) {
        this.prodid = prodid;
        this.descr = descr;
        this.catid = catid;
        this.categoryName = categoryName;
    }

    public Integer getProdid() {
        return prodid;
    }

    public void setProdid(Integer prodid) {
        this.prodid = prodid;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Integer getCatid() {
        return catid;
    }

    public void setCatid(Integer catid) {
        this.catid = catid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public int compareTo(Product other) {
        return this.getDescr().compareTo(other.getDescr());
    }
}
