package com.example.shubhamr.kontakts.RecyclerView.RecentLogs;

public class recentModelClass {

    private String time;
    private String date;
    private String duration;
    private String type;

    public recentModelClass(){}

    public recentModelClass(String time,String date,String duration,String type){
        this.time=time;
        this.date=date;
        this.duration=duration;
        this.type=type;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }
}
