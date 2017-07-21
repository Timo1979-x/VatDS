package by.gto.model;

import java.util.Date;

public class AgreementData {
    private int id;
    private int unp;
    private String number;
    private Date date;

    public AgreementData() {
    }

    public AgreementData(int unp, String number, Date date) {
        this.unp = unp;
        this.number = number;
        this.date = date;
    }

    public AgreementData(int id, int unp, String number, Date date) {
        this.id = id;
        this.unp = unp;
        this.number = number;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUnp() {
        return unp;
    }

    public void setUnp(int unp) {
        this.unp = unp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "№ " + number + " от " + date;
    }
}