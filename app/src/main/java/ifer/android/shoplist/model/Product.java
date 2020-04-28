package ifer.android.shoplist.model;

public class Product {
    private Integer prodid;

    private String descr;

    private Integer catid;

    public Product() {

    }

    public Product(Integer prodid, String descr, Integer catid) {
        this.prodid = prodid;
        this.descr = descr;
        this.catid = catid;
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
}
