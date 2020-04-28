package ifer.android.shoplist.model;

public class Shopitem {
    private Integer itemid;

    private Integer prodid;

    private String quantity;

    private String comment;


    public Shopitem() {

    }


    public Shopitem(Integer itemid, Integer prodid, String quantity, String comment) {
        super();
        this.itemid = itemid;
        this.prodid = prodid;
        this.quantity = quantity;
        this.comment = comment;
    }

    public Integer getItemid() {
        return itemid;
    }

    public void setItemid(Integer itemid) {
        this.itemid = itemid;
    }

    public Integer getProdid() {
        return prodid;
    }

    public void setProdid(Integer prodid) {
        this.prodid = prodid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
