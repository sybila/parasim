package org.sybila.parasim.computation;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.MixedOutput;

/**
 * Modules are basic units performing a computation.
 *
 * Every module accepts some type of data block and produces another one.
 *
 * Since a single batch computation may be bound by a maximal number of
 * steps the state of computation can be preserverd in an intermediate
 * type of data block.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 *
 * @param <Input> input type of data block
 * @param <Inter> intermediate type of data block
 * @param <Output> output type of data block
 */
public interface Module<Input extends DataBlock, Inter extends DataBlock, Output extends DataBlock> {

    /**
     * Executes a computation on the given data block and produces final results
     * in another data block.
     *
     * @param input input data block
     * @return output data block
     * @throws ModuleComputationException if there an error occurs during the computation.
     */
    Output compute(Input input) throws ModuleComputationException;

    /**
     * Executes a computation on a new set of trajectories given by the input
     * data block and a old set given by inter which have already had some
     * processing done on them and the state of computation is preserved
     * in the intermediate data block.
     *
     * The maximum amount of computation steps to perform on each trajectory
     * is given as stepLimit. If stepLimit == 0 then the computation runs until
     * a result is determined or the end of the trajectory is reached.
     *
     * Since the stepLimit could have enabled the computation to finish on
     * some trajectories while others need more computation steps, the result is
     * returned in MixedOutput consisting of two data blocks one Output and one Inter.
     *
     * @param input input data block of new trajectories
     * @param inter intermediate data block of old trajectories
     * @param stepLimit maximum number of compute steps for each trajectory
     * @return Mixed output of Output and Inter data blocks.
     * @throws ModuleComputationException if there an error occurs during the computation.
     */
    MixedOutput<Output, Inter> compute(Input input, Inter inter, int stepLimit) throws ModuleComputationException;
}
