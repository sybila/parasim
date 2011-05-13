/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

/**
 * Manages all existing comuptations.
 *
 * @author sven
 */
public class ComputationManager implements Runnable {

	private List<ComputationInterface> activeComps;

	private List<ComputationInterface> finishedComps;

	private static Logger logger = Logger.getLogger(ComputationManager.class.getName());

    ComputationManager ()
    {
        activeComps = new LinkedList();
        finishedComps = new LinkedList();
        String threadName = Thread.currentThread().getName();
        logger.info(threadName+" CompMan"); //FIXME
    }

    /**
     * Inserts the computation into the appropriate list.
     * @param c
     */
    public boolean add(ComputationInterface c)
    {
        logger.info("Adding computation to manager... ");
        if (!c.finished())
        {
            logger.info("Active");
            return activeComps.add(c);
        }
        else
        {
            return finishedComps.add(c);
        }
    }

    /**
     * Deletes the given computation from the list of active or finished
     * computations.
     *
     * @param c target computation to be deleted
     * @return true on successful deletion
     */
    public boolean delete(ComputationInterface c)
    {
        if (activeComps.contains(c))
        {
            return activeComps.remove(c);
        }
        else if (finishedComps.contains(c))
        {
            return finishedComps.remove(c);
        }
        else return false;
    }

    /**
     * The given computation is deleted from the finishedComps and added to
     * the actieComps.
     *
     * @param c
     * @return true on success
     */
    public boolean reactivate(ComputationInterface c)
    {
        if (finishedComps.contains(c))
        {
            if (finishedComps.remove(c))
            {
                return this.add(c);
            }
        }
        return false;
    }

    /**
     * The main effort of this method is to cyclicaly run all the computations
     * while ensuring a balanced distribution of computation time.
     * <p/>
     * To establish this each computation's batch time is compared to the
     * given (overall batch time) divided by (number of computations)
     * and it's relative speed is modified with the assumption of a
     * steady computation rate.
     */
    @Override
    public void run() 
    {
        final int sleepTime = 100; /* FIXME */
        final int batchTime = 100; /* FIXME */
        long compTime;

        
        while (true)
        {
            int running = 0;
            for (ComputationInterface compInt : activeComps)
            {
                if (!compInt.paused()) running++;
            }
            if (running > 0) {
                for (ListIterator<ComputationInterface> it = activeComps.listIterator(); it.hasNext(); )
                {
                    ComputationInterface comp = it.next();
                    if (!comp.paused()) {
                        compTime = comp.compute();
                        if (compTime > (batchTime / running)*1.1f)
                        {
                            comp.setSpeed(comp.getSpeed()/1.1f);
                        }
                        else if (compTime < (batchTime / running)/1.1f)
                        {
                            comp.setSpeed(comp.getSpeed()*1.1f);
                        }
                        if (comp.finished())
                        {
                            it.remove();
                            finishedComps.add(comp);
                        }
                    }
                }
            }
            String threadName = Thread.currentThread().getName();
            //logger.info(threadName+" CompMan running...");
            try
            {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

}
