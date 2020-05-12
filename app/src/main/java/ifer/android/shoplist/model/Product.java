package ifer.android.shoplist.model;

import java.io.Serializable;
import java.util.Objects;

public class Product implements Comparable<Product>, Serializable, Cloneable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return prodid.equals(product.prodid) &&
                descr.equals(product.descr) &&
                catid.equals(product.catid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prodid, descr, catid);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
