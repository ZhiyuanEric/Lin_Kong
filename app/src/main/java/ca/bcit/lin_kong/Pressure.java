package ca.bcit.lin_kong;

import java.sql.Time;
import java.util.Date;

public class Pressure {
    String id;
    String UserId;
    Date readDate;
    int Systolic;
    int Diastolic;

    public Pressure() { }

    public Pressure(String id, String UserId, Date readDate, int Systolic, int Diastolic) {
        this.id = id;
        this.UserId = UserId;
        this.readDate = readDate;
        this.Systolic = Systolic;
        this.Diastolic = Diastolic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public int getSystolic() {
        return Systolic;
    }

    public void setSystolic(int systolic) {
        Systolic = systolic;
    }

    public int getDiastolic() {
        return Diastolic;
    }

    public void setDiastolic(int diastolic) {
        Diastolic = diastolic;
    }
}
