package org.sybila.parasim.computation;

/**
 * Module are basic units doing a computation. Every module accepts some type
 * of data block and produces another one. 
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @param <Input> input type of data block
 * @param <Output> output type of data block
 */
public interface Module<Input extends DataBlock, Output extends DataBlock> {

    /**
     * Executes a computation on the given data block and produces another one.
     * 
     * @param input input data block
     * @return output data block
     * @throws ModuleComputationException if there an error occurs during the computation.
     */
    Output compute(Input input) throws ModuleComputationException;
}
