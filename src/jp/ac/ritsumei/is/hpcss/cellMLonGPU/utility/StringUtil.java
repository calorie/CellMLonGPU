package jp.ac.ritsumei.is.hpcss.cellMLonGPU.utility;

import java.text.DecimalFormat;

public class StringUtil {

    public static final String lineSep = System.getProperty("line.separator");

    // 小数点以下の桁数をVC++版と同じにする
    private static DecimalFormat df = new DecimalFormat("0.000000");

    public static String doubleToString(double dValue) {
        // return String.valueOf(dValue);
        return df.format(dValue);
    }

}
