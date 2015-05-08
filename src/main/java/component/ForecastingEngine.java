package component;

import model.Mortgage;

import java.util.Random;

import static java.lang.Math.exp;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static sun.swing.MenuItemLayoutHelper.max;

public class ForecastingEngine {
    private static final Random random = new Random();

    public static double calExpectedBalance(Mortgage mortgage, double hpi_t, double hpi, double mtg, int gap) {
        return calExpectedBalance(mortgage.getOla(), mortgage.getInterestRate(), mortgage.getTerm(), mortgage.getAge() + gap
                , mortgage.getInterestRate(), mtg
                , mortgage.getOfico(), mortgage.getOltv(), hpi_t, hpi);
    }

    public static double calExpectedBalanceWithVariableRate(Mortgage mortgage, double hpi_t, double hpi, double mtg, int gap, double rate_t) {
        return calExpectedBalance(mortgage.getOla(), mortgage.getInterestRate(), mortgage.getTerm(), mortgage.getAge() + gap
                , rate_t, mtg
                , mortgage.getOfico(), mortgage.getOltv(), hpi_t, hpi);
    }

    public static double calExpectedBalance(double originationLoan, double rate, double term, int age//parameter for balance
            , double rate_t, double mtg_t//prepay possibility
            , double ofico, double oltv, double hpi_state_t, double hpi//default possibility
    ) {
        double balance = balance(originationLoan, rate, term, age);
        double prepayPossibility = prepayPossibility(rate_t, mtg_t);
        double defaultPossibility = defaultPossibility(ofico, age, balance, originationLoan, oltv, hpi_state_t, hpi);

        //expected balance model is 0*Pprepay+balance*Pdefault+balance(1-Pprepay-Pdefault)
        return balance * defaultPossibility + balance * (1 - defaultPossibility - prepayPossibility);

    }

    public static double balance(double originationLoan, double rate, double term, int age) {
        double c = rate / 12;
        double commonOperand1 = pow(1 + c, term);//(1+c)^N
        double commonOperand2 = pow(1 + c, age);//(1+c)^k
        return originationLoan * (commonOperand1 - commonOperand2) / (commonOperand1 - 1);
    }


    /**
     * @param rate_t //interest rate at time t, it shound be constant, if it is fixed rate mortgage
     * @param mtg_t
     * @return the possibility of prepay the rest of balance
     */
    public static double prepayPossibility(double rate_t, double mtg_t) {
        return 1 / (1 + exp(3.4761 - 101.09 * (rate_t - mtg_t)));
    }


    public static double defaultPossibility(double ofico, int age_t
            , double bal_t
            , double obal //the balance of origination of loan, constant value from table1
            , double oltv //the orginal loan-to-value ratio from table1
            , double hpi_state_t//hpi value at time t from table2
            , double hpi) {

        double ltv_t_value = ltv(bal_t, obal, oltv, hpi_state_t, hpi);
        double f_value = f(ofico, ltv_t_value, age_t);

        return 1 / (1 + exp(f_value));
    }

    public static double generateRate(double preRate) {
        return preRate * 0.7 + 0.018 + 0.005 * random.nextGaussian();
    }


    //it is part of default posssibility calculation

    /**
     * @param ofico credit risk scroe at origination of loan
     * @param ltv_t
     * @param age_t
     * @return the parameter for possibilty of default
     */
    private static double f(double ofico, double ltv_t, int age_t) {
        return 4.4 + 0.01 * ofico - 4 * ltv_t
                + 0.2 * min(max(age_t - 36, 0), 24)
                + 0.1 * min(max(age_t - 60, 0), 60)
                - 0.05 * min(max(age_t - 120, 0), 120);
    }

    private static double ltv(double bal_t
            , double obal //the balance of origination of loan, constant value from table1
            , double oltv //the orginal loan-to-value ratio from table1
            , double hpi_state_t//hpi value at time t from table2
            , double hpi)//hpi at origination of the loan from table2
    {
        return bal_t / ((obal / oltv) * (hpi_state_t / hpi));
    }
}
