package org.sybila.parasim.model.trajectory;

/**
 * Represents a pair of data blocks, Output is a final result of some computation
 * and Inter is some intermediate result and computation representation.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @param <Output> Final output results data block.
 * @param <Inter> Intermediate computation representation data block.
 */
public class MixedOutput<Output extends DataBlock, Inter extends DataBlock> {

    private final Output output;
    private final Inter inter;

    public MixedOutput(Output output, Inter inter) {
        this.output = output;
        this.inter = inter;
    }

    public Output getOutput() {
        return output;
    }

    public Inter getIntermediate() {
        return inter;
    }
}
