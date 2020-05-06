package ifer.android.shoplist.model;

public class Category  implements Comparable<Category> {
    private Integer catid;
    private String descr;



    public Category() {

    }

    public Category(Integer catid, String descr) {
        this.catid = catid;
        this.descr = descr;
    }

    @Override
    public String toString(){
        return this.descr;
    }

    public Integer getCatid() {
        return catid;
    }

    public void setCatid(Integer catid) {
        this.catid = catid;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Override
    public int compareTo(Category other) {
        return this.getDescr().compareTo(other.getDescr());
    }
}
