package com.example.new_cv24;

public class TotalData {
    private String member_num;
    private String member_time;
    private String member_longitude;
    private String member_latitude;
    private String member_address;

    public String getMember_num() {
        return member_num;
    }
    public String getMember_time() {
        return member_time;
    }
    public String getMember_longitude() { return member_longitude; }
    public String getMember_latitude() { return member_latitude; }
    public String getMember_address() { return member_address; }

    public void setMember_num(String member_num) { this.member_num = member_num; }
    public void setMember_time(String member_time) { this.member_time = member_time; }
    public void setMember_latitude(String member_latitude) { this.member_latitude = member_latitude; }
    public void setMember_longitude(String member_longitude) { this.member_longitude = member_longitude; }
    public void setMember_address(String member_address) { this.member_address = member_address; }
}
