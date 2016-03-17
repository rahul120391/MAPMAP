package datamodel;

/**
 * Created by osx on 17/12/15.
 */
public class DiscountInfo {
    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    private String objectid;
    private String discount_category;
    private String discount_category_url;
    private String discount_desc;
    private String discount_location;
    private String discount_per;
    private String discount_sub_category;
    private String enddate;
    private String startdate;

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String phonenumber;
    private String comment;

    public String getDiscount_category() {
        return discount_category;
    }

    public void setDiscount_category(String discount_category) {
        this.discount_category = discount_category;
    }

    public String getDiscount_category_url() {
        return discount_category_url;
    }

    public void setDiscount_category_url(String discount_category_url) {
        this.discount_category_url = discount_category_url;
    }

    public String getDiscount_desc() {
        return discount_desc;
    }

    public void setDiscount_desc(String discount_desc) {
        this.discount_desc = discount_desc;
    }

    public String getDiscount_location() {
        return discount_location;
    }

    public void setDiscount_location(String discount_location) {
        this.discount_location = discount_location;
    }

    public String getDiscount_per() {
        return discount_per;
    }

    public void setDiscount_per(String discount_per) {
        this.discount_per = discount_per;
    }

    public String getDiscount_sub_category() {
        return discount_sub_category;
    }

    public void setDiscount_sub_category(String discount_sub_category) {
        this.discount_sub_category = discount_sub_category;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    private Double latitude;
    private Double longitude;

}
