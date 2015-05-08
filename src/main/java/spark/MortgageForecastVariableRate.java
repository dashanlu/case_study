package spark;

import component.ForecastingEngine;
import model.Macroeconomic;
import model.Mortgage;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MortgageForecastVariableRate extends DataSetLoader {
    final private static Logger LOGGER = LoggerFactory.getLogger(MortgageForecastVariableRate.class);

    public static void main(String[] args) throws ParseException {

        final List<Macroeconomic> macroeconomicList = setUpDataset();


        final double hpi = 0.0448;
        final int simulationTimes = 1000;


        String mortgageFile;
        if (args != null && args.length > 0) {
            mortgageFile = args[0];
        } else {
            mortgageFile = MortgageForecastVariableRate.class.getClassLoader().getResource("mortgage.txt").toString();
        }
        SparkConf conf = new SparkConf().setAppName("Mortgage portfolio forecast engine").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<Mortgage> mortgages = sc.textFile(mortgageFile).map(new Function<String, Mortgage>() {
            public Mortgage call(String s) throws Exception {
                String[] tokens = s.split(" ");
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
                LOGGER.info("The mortgage detail is {}", mortgage.toString());
                return mortgage;
            }
        });

        JavaPairRDD<Tuple2<Integer, Date>, Double> expectedValueGroups = mortgages.flatMapToPair(new PairFlatMapFunction<Mortgage, Tuple2<Integer, Date>, Double>() {
            public Iterable<Tuple2<Tuple2<Integer, Date>, Double>> call(Mortgage mortgage) throws Exception {
                List<Tuple2<Tuple2<Integer, Date>, Double>> result = new ArrayList<Tuple2<Tuple2<Integer, Date>, Double>>();
                for (int j = 0; j < simulationTimes; j++) {
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
                        double rate_t = ForecastingEngine.generateRate(mortgage.getInterestRate());
                        double expectedBalance = ForecastingEngine.calExpectedBalanceWithVariableRate(mortgage, hpi_t, hpi, mtg_t, i + 1, rate_t);
                        mortgage.setInterestRate(rate_t);
                        result.add(new Tuple2<Tuple2<Integer, Date>, Double>(new Tuple2(j, currentDate), Double.valueOf(expectedBalance)));
                    }
                }
                return result;
            }
        });


        JavaPairRDD<Tuple2<Integer, Date>, Double> portfolioExpectedBalances = expectedValueGroups.reduceByKey(new Function2<Double, Double, Double>() {
            public Double call(Double aDouble, Double aDouble2) throws Exception {
                return aDouble + aDouble2;
            }
        });



        JavaRDD<Tuple2<Integer, Tuple2<Date, Double>>> expectedValues1 = portfolioExpectedBalances.flatMap(new FlatMapFunction<Tuple2<Tuple2<Integer, Date>, Double>, Tuple2<Integer, Tuple2<Date, Double>>>() {

            public Iterable<Tuple2<Integer, Tuple2<Date, Double>>> call(Tuple2<Tuple2<Integer, Date>, Double> tuple2DoubleTuple2) throws Exception {
                List<Tuple2<Integer, Tuple2<Date, Double>>> result = new ArrayList<Tuple2<Integer, Tuple2<Date, Double>>>();
                result.add(new Tuple2<Integer, Tuple2<Date, Double>>(tuple2DoubleTuple2._1()._1(), new Tuple2<Date, Double>(tuple2DoubleTuple2._1()._2(), tuple2DoubleTuple2._2())));
                return result;
            }
        });

        //group the portfolio by path
        JavaPairRDD<Integer, Tuple2<Date, Double>> expectedValues2 = expectedValues1.flatMapToPair(new PairFlatMapFunction<Tuple2<Integer, Tuple2<Date, Double>>, Integer, Tuple2<Date, Double>>() {
            public Iterable<Tuple2<Integer, Tuple2<Date, Double>>> call(Tuple2<Integer, Tuple2<Date, Double>> integerTuple2Tuple2) throws Exception {
                List<Tuple2<Integer, Tuple2<Date, Double>>> list = new ArrayList<Tuple2<Integer, Tuple2<Date, Double>>>();
                list.add(integerTuple2Tuple2);
                return list;
            }
        });

        //group the portfolio within the same path into a list, and sort the portfolio by date
        JavaPairRDD<Integer, List<Tuple2<Date, Double>>> expectedValues3 = expectedValues2.aggregateByKey(new ArrayList<Tuple2<Date, Double>>(), new Function2<List<Tuple2<Date, Double>>, Tuple2<Date, Double>, List<Tuple2<Date, Double>>>() {
            public List<Tuple2<Date, Double>> call(List<Tuple2<Date, Double>> tuple2s, Tuple2<Date, Double> dateDoubleTuple2) throws Exception {
                tuple2s.add(dateDoubleTuple2);
                tuple2s.sort(new Comparator<Tuple2<Date, Double>>() {
                    public int compare(Tuple2<Date, Double> o1, Tuple2<Date, Double> o2) {
                        return o1._1().compareTo(o2._1());
                    }
                });
                return tuple2s;
            }
        }, new Function2<List<Tuple2<Date, Double>>, List<Tuple2<Date, Double>>, List<Tuple2<Date, Double>>>() {
            public List<Tuple2<Date, Double>> call(List<Tuple2<Date, Double>> tuple2s, List<Tuple2<Date, Double>> tuple2s2) throws Exception {
                tuple2s.addAll(tuple2s);
                tuple2s.sort(new Comparator<Tuple2<Date, Double>>() {
                    public int compare(Tuple2<Date, Double> o1, Tuple2<Date, Double> o2) {
                        return o1._1().compareTo(o2._1());
                    }
                });
                return tuple2s;
            }
        }).sortByKey();


        expectedValues3.saveAsTextFile(mortgageFile+"output");
        List<Tuple2<Integer, List<Tuple2<Date, Double>>>> list = expectedValues3.take(simulationTimes * macroeconomicList.size());
        for (Tuple2<Integer, List<Tuple2<Date, Double>>> t : list) {
            System.out.println(String.format("Path %s ", t._1()));
            for (Tuple2<Date, Double> item : t._2()) {
                System.out.println(String.format("Date %s : expectedBalance %s", sdf.format(item._1()), item._2()));
            }

        }
        LOGGER.info("=============Well done===================");
        sc.stop();
    }

}
