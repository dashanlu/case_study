package model;

import java.io.Serializable;
import java.util.Date;

public class Macroeconomic implements Serializable{

    private Date date;
    private double hpi_ny;
    private double hpi_ca;
    //home price index for California
    //an index that measures market rates for mortgages
    private double mtg;

    public Macroeconomic(Date date, double hpi_ny, double hpi_ca, double mtg) {
        this.date = date;
        this.hpi_ny = hpi_ny;
        this.hpi_ca = hpi_ca;
        this.mtg = mtg;
    }

    public Date getDate() {
        return date;
    }

    public double getHpi_ny() {
        return hpi_ny;
    }

    public double getHpi_ca() {
        return hpi_ca;
    }

    public double getMtg() {
        return mtg;
    }
}
