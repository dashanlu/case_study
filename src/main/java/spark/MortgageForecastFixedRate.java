package spark;

import component.ForecastingEngine;
import model.Macroeconomic;
import model.Mortgage;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MortgageForecastFixedRate extends DataSetLoader{
    private final static Logger LOGGER = LoggerFactory.getLogger(MortgageForecastFixedRate.class);
    public static void main(String[] args) throws ParseException {
        final List<Macroeconomic> macroeconomicList = setUpDataset();
        final double hpi = 0.0448;

        String mortgageFile;
        if (args != null && args.length > 0) {
            mortgageFile = args[0];
        } else {
            mortgageFile = MortgageForecastFixedRate.class.getClassLoader().getResource("mortgage.txt").toString();
        }
        SparkConf conf = new SparkConf().setAppName("Mortgage portfolio forecast engine").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<Mortgage> mortgages = sc.textFile(mortgageFile).map(new Function<String, Mortgage>() {
            public Mortgage call(String s) throws Exception {
                String[] tokens = s.split(" ");
                //B 10/1/2013 CA 2000000 0.8 740 120 0.0525 2000000 0
                Mortgage mortgage = new Mortgage(tokens[0]
                        , sdf.parse(tokens[1])
                        , tokens[2]
                        , Double.valueOf(tokens[3])
                        , Double.valueOf(tokens[4])
                        , Integer.valueOf(tokens[5])
                        , Integer.valueOf(tokens[6])
                        , Double.valueOf(tokens[7])
                        , Double.valueOf(tokens[8])
                        , Integer.valueOf(tokens[9]));
                LOGGER.info("The mortgage is {}", mortgage.toString());
                return mortgage;
            }
        });

        JavaPairRDD<Date, Double> expectedValueGroups = mortgages.flatMapToPair(new PairFlatMapFunction<Mortgage, Date, Double>() {
            public Iterable<Tuple2<Date, Double>> call(Mortgage mortgage) throws Exception {
                List<Tuple2<Date, Double>> result = new ArrayList<Tuple2<Date, Double>>();
                for (int i = 0; i < macroeconomicList.size(); i++) {
                    Macroeconomic macroeconomic = macroeconomicList.get(i);
                    double mtg_t = macroeconomic.getMtg();
                    Date currentDate = macroeconomic.getDate();
                    double hpi_t = 0;
                    if (mortgage.getState().intern() == "NY".intern()) {
                        hpi_t = macroeconomic.getHpi_ny();
                    } else if (mortgage.getState().intern() == "CA".intern()) {
                        hpi_t = macroeconomic.getHpi_ca();
                    }

                    double expectedBalance = ForecastingEngine.calExpectedBalance(mortgage, hpi_t, hpi, mtg_t, i + 1);
                    LOGGER.info("[{}'s expectedBalance is {} at {}]", mortgage.toString(), expectedBalance, sdf.format(currentDate));
                    result.add(new Tuple2<Date, Double>(currentDate, expectedBalance));

                }
                return result;
            }
        });

        JavaPairRDD<Date, Double> expectedValues = expectedValueGroups.reduceByKey(new Function2<Double, Double, Double>() {
            public Double call(Double aDouble, Double aDouble2) throws Exception {
                return aDouble + aDouble2;
            }
        }).sortByKey(true);

        expectedValues.saveAsTextFile(mortgageFile+"output");
        List<Tuple2<Date, Double>> list = expectedValues.take(macroeconomicList.size());
        for (Tuple2<Date, Double> tuple2 : list) {
            System.out.println(String.format("%s expectedBalance is %s", tuple2._1(), tuple2._2()));
        }

        sc.stop();
    }
}
