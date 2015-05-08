package component;

import model.Macroeconomic;
import model.Mortgage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;


/**
 * Created by dashanlu on 05/05/2015.
 */
public class ForecastingEngineTest {

    private static double ola = 2000000;//Original Load Amount
    private static double oltv = 0.8;//the loan-to-value at the point of origination
    private static double ofico = 740; //credit risk score of the customer at origination
    private static double term = 120; //term of the load, in months
    private static double rate = 0.0525; //fixed interest rate
    private static int age = 1; //age of the loan
    private static double hpi_t = 145.45;
    private static double hpi = 145.45;
    private static double mtg_t = 0.0448;
    private static double testBalance;
    private static double testExpectedBalance;
    private static double testPrepayPossibility;
    private static double testDefaultPossibility;

    @Test
    public void testBalance() throws Exception {
        testBalance = ForecastingEngine.balance(ola, rate, term, age);
        assertTrue(testBalance < ola);
    }

    @Test
    public void testPrepayPossibility() throws Exception {
        testPrepayPossibility = ForecastingEngine.prepayPossibility(rate, mtg_t);
        assertTrue(testPrepayPossibility > 0);
    }

    @Test
    public void testDefaultPossibility() throws Exception {
        testBalance = 1987292;
        testDefaultPossibility = ForecastingEngine.defaultPossibility(ofico, age, testBalance, ola, oltv, hpi_t, hpi);
        assertTrue(testDefaultPossibility > 0);
    }

    @Test
    public void testExpectedBalance() throws Exception {
        testBalance = 1987292;
        testPrepayPossibility = 0.063109;
        testDefaultPossibility = 0.00018;

        testExpectedBalance = ForecastingEngine.calExpectedBalance(ola, rate, term, 1, rate, mtg_t, ofico, oltv, hpi_t, hpi);
        assertTrue(testExpectedBalance > 0);

        double mtg_t2 = 0.045;
        double hpi_t2 = 143.54;
        testExpectedBalance = ForecastingEngine.calExpectedBalance(ola, rate, term, 2, rate, mtg_t2, ofico, oltv, hpi_t2, hpi);
        assertTrue(testExpectedBalance > 0);
    }

    @Test
    public void setTestExpectedBalanceWithPOJOs() throws Exception {
        Mortgage mortgage = new Mortgage("A", new Date(), "NY", ola, oltv, (int) ofico, (int) term, rate, ola, age);
        Macroeconomic macroeconomic = new Macroeconomic(new Date(), hpi_t, hpi_t, mtg_t);
        double testExpectedBalance1 = ForecastingEngine.calExpectedBalance(mortgage, macroeconomic.getHpi_ny(), hpi, macroeconomic.getMtg(), 1);
        double testExpectedBalance2 = ForecastingEngine.calExpectedBalance(mortgage, macroeconomic.getHpi_ny(), hpi, macroeconomic.getMtg(), 2);

        assertTrue("Both expected load balances should be greater than 0", testExpectedBalance1 > 0 && testExpectedBalance2 > 0);
        assertTrue("Previous one should be greater than the latest one", testExpectedBalance1 > testExpectedBalance2);
    }
}