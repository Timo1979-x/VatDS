package by.gto.tools;

/**
 * Created by ltv on 26.07.2016.
 */
public interface Callback1<I, O> {
    O call(I msg);
}
