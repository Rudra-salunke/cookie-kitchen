package model;

public class Address {
    private int id;
    private int user_id;
    private String line1;
    private String line2;
    private String city;
    private String pincode;
    private boolean is_default;
    public Address(int id,int user_id, String line1,String line2,String city,String pincode,boolean is_default){
        this.id=id;
        this.user_id=user_id;
        this.line1=line1;
        this.line2=line2;
        this.city=city;
        this.pincode=pincode;
        this.is_default=is_default;
    }
    public Address(int userId, String line1, String line2, String city, String pincode) {
        this(0, userId, line1, line2, city, pincode, false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public boolean Is_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }
}
