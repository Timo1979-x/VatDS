package by.gto.helpers;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by timo on 13.01.2017.
 */
public class ExceptionHelpers {
    public static String extractMessage(Throwable t) {
        Throwable e_ = t;
        String errMessage = null;
        int counter = 1;
        while (StringUtils.isEmpty(errMessage) && e_ != null && counter < 10) {
            errMessage = e_.getMessage();
            e_ = e_.getCause();
            counter++;
        }
        return errMessage;
    }
}
