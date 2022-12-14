package es.eltrueno.modserverutils.playtime;

import java.util.Date;

public class Playtime {

    private long totalSeconds;
    private Date todayDate;
    private long todaySeconds;

    public Playtime(){
        this.totalSeconds = 0;
        this.todayDate = new Date();
        this.todaySeconds = 0;
    }

    public Playtime(long totalSeconds, Date todayDate, long todaySeconds) {
        this.totalSeconds = totalSeconds;
        this.todayDate = todayDate;
        this.todaySeconds = todaySeconds;
    }

    public long getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(long totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public Date getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(Date todayDate) {
        this.todayDate = todayDate;
    }

    public long getTodaySeconds() {
        return todaySeconds;
    }

    public void setTodaySeconds(long todaySeconds) {
        this.todaySeconds = todaySeconds;
    }

    public void addSeconds(long seconds){
        this.todaySeconds+=seconds;
        this.totalSeconds+=seconds;
    }

    public void addSecond(){
        addSeconds(1);
    }
}
