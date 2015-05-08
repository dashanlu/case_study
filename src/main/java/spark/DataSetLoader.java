package spark;

import model.Macroeconomic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public abstract class DataSetLoader {
    protected static final SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyy");

    /**
     * It is hardcoded based on the assumption that we have already pre-processed the macro-economic data
     * @return
     * @throws ParseException
     */
    protected static List<Macroeconomic> setUpDataset() throws ParseException {
        List<Macroeconomic> macroeconomicList = new LinkedList<Macroeconomic>();
        macroeconomicList.add(new Macroeconomic(sdf.parse("11/1/2013"), 145.72, 143.54, 0.045));
        macroeconomicList.add(new Macroeconomic(sdf.parse("12/1/2013"), 146.00, 143.45, 0.045));
        macroeconomicList.add(new Macroeconomic(sdf.parse("1/1/2014"), 146.28, 142.54, 0.0452));
        macroeconomicList.add(new Macroeconomic(sdf.parse("2/1/2014"), 146.58, 143.32, 0.0456));
        macroeconomicList.add(new Macroeconomic(sdf.parse("3/1/2014"), 146.90, 144.43, 0.0460));
        macroeconomicList.add(new Macroeconomic(sdf.parse("4/1/2014"), 147.24, 145.00, 0.0464));
        macroeconomicList.add(new Macroeconomic(sdf.parse("5/1/2014"), 147.58, 146.23, 0.0467));
        macroeconomicList.add(new Macroeconomic(sdf.parse("6/1/2014"), 147.92, 147.92, 0.0470));
        macroeconomicList.add(new Macroeconomic(sdf.parse("7/1/2014"), 148.24, 148.54, 0.0472));
        macroeconomicList.add(new Macroeconomic(sdf.parse("8/1/2014"), 148.54, 148.85, 0.0475));
        macroeconomicList.add(new Macroeconomic(sdf.parse("9/1/2014"), 148.83, 149.14, 0.0480));
        macroeconomicList.add(new Macroeconomic(sdf.parse("10/1/2014"), 149.13, 149.44, 0.0487));
        macroeconomicList.add(new Macroeconomic(sdf.parse("11/1/2014"), 149.43, 149.74, 0.0495));
        macroeconomicList.add(new Macroeconomic(sdf.parse("12/1/2014"), 149.76, 150.07, 0.0500));
        macroeconomicList.add(new Macroeconomic(sdf.parse("1/1/2015"), 150.10, 150.42, 0.0501));
        macroeconomicList.add(new Macroeconomic(sdf.parse("2/1/2015"), 150.48, 150.79, 0.0499));
        macroeconomicList.add(new Macroeconomic(sdf.parse("3/1/2015"), 150.87, 151.18, 0.0500));
        macroeconomicList.add(new Macroeconomic(sdf.parse("4/1/2015"), 151.27, 151.58, 0.0505));
        macroeconomicList.add(new Macroeconomic(sdf.parse("5/1/2015"), 151.67, 151.98, 0.0513));
        macroeconomicList.add(new Macroeconomic(sdf.parse("6/1/2015"), 152.06, 152.37, 0.0520));
        macroeconomicList.add(new Macroeconomic(sdf.parse("7/1/2015"), 152.43, 152.74, 0.0524));
        macroeconomicList.add(new Macroeconomic(sdf.parse("8/1/2015"), 152.78, 153.10, 0.0527));
        macroeconomicList.add(new Macroeconomic(sdf.parse("9/1/2015"), 153.15, 153.47, 0.0530));
        return macroeconomicList;
    }
}
