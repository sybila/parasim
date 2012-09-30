package org.sybila.parasim.execution.api;

import java.util.concurrent.Future;
import org.sybila.parasim.model.Mergeable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ExecutionResult<L extends Mergeable<L>> {

    Future<L> full();

    Future<L> partial();

}
