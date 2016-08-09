package by.gto.tools;

/**
 * Created by ltv on 26.07.2016.
 */
public interface Callback2<I1, I2, O> {
    O call(I1 param1, I2 param2);
}
