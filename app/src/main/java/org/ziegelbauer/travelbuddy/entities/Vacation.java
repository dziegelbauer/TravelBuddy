package org.ziegelbauer.travelbuddy.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "vacations")
public class Vacation {
    @PrimaryKey(autoGenerate = true)
    private int vacationID;
    private String vacationName;
    private String vacationLodging;
    private String startDate;
    private String endDate;

    public Vacation(int vacationID, String vacationName, String vacationLodging, String startDate, String endDate) {
        this.vacationID = vacationID;
        this.vacationName = vacationName;
        this.vacationLodging = vacationLodging;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @NonNull
    @Override
    public String toString() {
        return vacationName;
    }

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    public String getVacationName() {
        return vacationName;
    }

    public void setVacationName(String vacationName) {
        this.vacationName = vacationName;
    }

    public String getVacationLodging() {
        return vacationLodging;
    }

    public void setVacationLodging(String vacationLodging) {
        this.vacationLodging = vacationLodging;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
