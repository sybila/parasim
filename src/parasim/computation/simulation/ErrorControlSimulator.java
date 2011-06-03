/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.computation.simulation;

/**
 *
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public interface ErrorControlSimulator {

     void setMaxRelError(float maxRelError);
     float getMaxRelError();
     void setMaxAbsError(float[] maxAbsError);
     float[] getAbsError();

}
