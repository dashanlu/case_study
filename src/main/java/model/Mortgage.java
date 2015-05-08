package model;

import java.io.Serializable;
import java.util.Date;

public class Mortgage implements Serializable {
    private String channel;
    private Date originationDate;
    private String state;
    private double ola;
    private double oltv;
    private int ofico;
    private int term;
    private double interestRate;
    private double currentBalance;
    private int age;

    public Mortgage(String channel
            , Date originationDate
            , String state
            , double ola
            , double oltv
            , int ofico
            , int term
            , double interestRate
            , double currentBalance
            , int age) {
        this.channel = channel;
        this.originationDate = originationDate;
        this.state = state;
        this.ola = ola;
        this.oltv = oltv;
        this.ofico = ofico;
        this.term = term;
        this.interestRate = interestRate;
        this.currentBalance = currentBalance;
        this.age = age;
    }

    public String getChannel() {
        return channel;
    }


    public Date getOriginationDate() {
        return originationDate;
    }

    public String getState() {
        return state;
    }

    public double getOla() {
        return ola;
    }

    public double getOltv() {
        return oltv;
    }

    public int getOfico() {
        return ofico;
    }

    public int getTerm() {
        return term;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mortgage[")
                .append("channel=").append(channel)
                .append(", originationDate=").append(originationDate)
                .append(", state='").append(state)
                .append(", OLA==").append(ola)
                .append(", OLTV=").append(oltv)
                .append(", OFICO=").append(ofico)
                .append(", term=").append(term)
                .append(", interestRate=").append(interestRate)
                .append(", age=").append(age)
                .append("]");

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mortgage mortgage = (Mortgage) o;

        if (Double.compare(mortgage.ola, ola) != 0) return false;
        if (Double.compare(mortgage.oltv, oltv) != 0) return false;
        if (ofico != mortgage.ofico) return false;
        if (term != mortgage.term) return false;
        if (Double.compare(mortgage.interestRate, interestRate) != 0) return false;
        if (Double.compare(mortgage.currentBalance, currentBalance) != 0) return false;
        if (age != mortgage.age) return false;
        if (channel != null ? !channel.equals(mortgage.channel) : mortgage.channel != null) return false;
        if (originationDate != null ? !originationDate.equals(mortgage.originationDate) : mortgage.originationDate != null)
            return false;
        return !(state != null ? !state.equals(mortgage.state) : mortgage.state != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = channel != null ? channel.hashCode() : 0;
        result = 31 * result + (originationDate != null ? originationDate.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        temp = Double.doubleToLongBits(ola);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(oltv);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + ofico;
        result = 31 * result + term;
        temp = Double.doubleToLongBits(interestRate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(currentBalance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + age;
        return result;
    }
}
