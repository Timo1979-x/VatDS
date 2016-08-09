package by.gto.helpers;

/**
 * Created by ltv on 09.08.2016.
 */
public class VatHelpers {
    public static String vatNumber(Integer vatUnp, Short year, Long num) {

        if (vatUnp == null || year == null || num == null) {
            return "";
        } else {
            return String.format("%09d-%04d-%010d", vatUnp, year, num);
        }
    }
}
