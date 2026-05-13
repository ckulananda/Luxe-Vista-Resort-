package com.example.db_luxevista_resort;

public class ReservationModel {
    public int id;
    public int userId;
    public String type, date, status;
    public ReservationModel(int id,int userId,String type,String date,String status){
        this.id=id; this.userId=userId; this.type=type; this.date=date; this.status=status;
    }
}
