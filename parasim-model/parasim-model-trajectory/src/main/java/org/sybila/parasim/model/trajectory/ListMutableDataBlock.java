package org.sybila.parasim.model.trajectory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A mutable list of trajectories. If T is a MutableTrajectory then append
 * is implemented otherwise it throws an exception.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class ListMutableDataBlock<T extends Trajectory> implements MutableDataBlock<T>
{
    private List<T> trajectories;
    
    public ListMutableDataBlock(List<T> trajectories)
    {
        if (trajectories == null)
        {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        if (trajectories.isEmpty())
        {
            throw new IllegalArgumentException("The number of trajectories has to be a positive number.");
        }
        this.trajectories = new ArrayList<T>(trajectories.size());
        for(T trajectory: trajectories)
        {
            this.trajectories.add(trajectory);
        }
    }
    
    public ListMutableDataBlock(DataBlock<T> trajectories)
    {
        if (trajectories == null)
        {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        this.trajectories = new ArrayList<T>(trajectories.size());
        for(T trajectory: trajectories)
        {
            this.trajectories.add(trajectory);            
        }        
    }

    @Override
    public void append(DataBlock<Trajectory> trajectories)
    {
        if (size() != trajectories.size())
        {
            throw new IllegalArgumentException("The number of given trajectories has to match with size of the block.");
        }
        if (size() == 0) return;
        Iterator<T> it = this.trajectories.listIterator();
        int i = 0;
        while (it.hasNext())
        {
            if (!MutableTrajectory.class.isInstance(it.next()))
            {
                throw new RuntimeException("Trajectory on index ("+i+") is not Mutable.");
            }
            i++;
        }

        it = this.trajectories.listIterator();
        Iterator<Trajectory> appendIt = trajectories.iterator();
        while (it.hasNext() && appendIt.hasNext())
        {
            ((MutableTrajectory)it.next()).append(appendIt.next());
        }      
    }

    @Override
    public T getTrajectory(int index)
    {
        return trajectories.get(index);
    }

    @Override
    public Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            private Iterator<T> iterator = trajectories.iterator();

            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public T next()
            {
                return iterator.next();
            }

            @Override
            public void remove()
            {
                iterator.remove();
            }
        };
    }

    @Override
    public void merge(DataBlock<T> trajectories)
    {
        for(T trajectory: trajectories)
        {            
            this.trajectories.add(trajectory);
        }
    }

    @Override
    public int size()
    {
        return trajectories.size();
    }
}
