package org.sybila.parasim.visualization;

import java.util.Random;
import org.sybila.parasim.model.trajectory.*;

/**
 * Class creates new data block with what fake simulation of |number|*|dimension| trajectories with lenght |times|.
 * Trajectories behave simmilary as their surrounding.
 * It is possible to add new points - only to all trajectories at once, though.
 * 
 * @author <a href="mailto:xstreck1@fi.muni.cz">Adam Streck</a>
 */
public class FakeSimulation {
    
    /**
     * Class that holds a point position with direction and correction property.
     * Next point in trajectory has position+= (direction += correction)
     */
    private class DirectedPoint {
        float [] position ;
        float [] direction;
        float [] correction;
        
        DirectedPoint(float [] point_position) {
            position = point_position;
            direction = new float [dimensions];
            correction = new float [dimensions];
        }
    }
    
    // Constants for simulation - lenght of the time step and 
    private final float start_distance = 1.0f;
    private final float time_step = 0.1f;
    
    // properties of the simulation
    private int number; // How many trajectories per dimension
    private int dimensions; // How many dimensions
    private int times; // How many points is single trajectory
    
    // Utilities
    private Random generator;
    private DirectedPoint [] start_points; // First point after the end of each trajectory
    
    // Actual data
    private ArrayDataBlock simulation;

    ArrayDataBlock getDataBlock () {return simulation;}
    
    /**
     * Creates a simulation from the start space with |(number)^dimensions| trajectories and with |times| points per trajectory.
     * 
     * @param number number of values per dimension
     * @param dimensions number of dimensions
     * @param times number of times aka points in trajectory
     */
    FakeSimulation(int number, int dimensions, int times){
        if (times < 1) throw new IllegalArgumentException ("Number of times is lower then 1.");
        if (dimensions < 1) throw new IllegalArgumentException ("Number of dimensions is lower then 1.");
        if (number < 1) throw new IllegalArgumentException ("Number of trajectories per dimension is lower then 1.");
        
        this.number = number;
        this.dimensions = dimensions;
        this.times = times;
        generator = new Random();
        generator.setSeed(System.currentTimeMillis());
                
        simulation = startTrajectories();
    }
    
    /**
     * Create all the trajectories.
     * 
     * @param times how many points each trajectory should have
     */
    private ArrayDataBlock<LinkedTrajectory> startTrajectories() {
        start_points = creteStartPoints();         // Create |dimensions|D initial space with direction properties
        
        // Create data arrays for trajectories - it uses the same time for all trajectories
        float [] time_values = new float [times];
        for (int i = 0; i < times; i++)
            time_values[i] = i*time_step;
        
        // Create trajectories.
        LinkedTrajectory [] trajectories = new LinkedTrajectory[start_points.length];
        for (int trajectory = 0; trajectory < trajectories.length; trajectory++) {
            LinkedTrajectory temp = new LinkedTrajectory(createTrajectory(start_points[trajectory], time_values));
            trajectories[trajectory] = temp;
        }
        
        // Create and return the data block.
        return new ArrayDataBlock<LinkedTrajectory>(trajectories);
    }
    
    /**
     * Creates a |dimensions|D space of directed starting points.
     * For directioning it uses weighted sum of directions of the corner points.
     * 
     * @return All the start points with directions.
     */
    private DirectedPoint [] creteStartPoints () {

        // Initialize necessary arrays
        float [] positions   = new float [dimensions]; // Starting position for trajectory in each direction
        int   [] dim_updates = new int   [dimensions]; // When to start going in new direction
        for (int i = 0; i < dimensions; i++) {
            positions[i]   = 0.0f; // First trajectory starts at (0,0,...)
            dim_updates[i] = (int)Math.pow((int) number, (int)i); // When number of trajectory is equal to the number, start number must be updated
        }
        DirectedPoint [] corners = CreateCorners(); // Create directed corners.
        final float distances_sum = start_distance * (number - 1) * 0.5f * corners.length * dimensions; // Sum of distances from corner points is same for each point in the start space. Placed here for better efficiency.
         
        // Create all the trajectories, each positioned side_step from all others in all dimensions
        int trajectory_count = (int) Math.pow(number, dimensions); // Complete number of trajectories
        DirectedPoint [] start_points = new DirectedPoint[trajectory_count]; // For every trajectory there must be a start point.
        // Create the actual trajectories at the provided positions
        for (int trajectory = 0; trajectory < trajectory_count; trajectory++) {
            // for each dimension there must be an position update, if needed
            for (int dim = 0; dim < dimensions; dim++) {
                if ( trajectory!= 0 && trajectory % dim_updates[dim] == 0) {
                    positions[dim] += start_distance;
                    // When dimension is iterated, all lower are reset to the starting position
                    for (int lower_dim = 0; lower_dim < dim; lower_dim++)
                       positions[lower_dim] = 0.0f;
                }
            }
            start_points[trajectory] = createStartPoint (positions, corners, distances_sum);
        }
        return start_points;
    }
    
    /**
     * Creates the directed start point from with direction being the weighted sum of direction of the corner points.
     * 
     * @param positions position of the point
     * @param corners reference to corner points
     * @param distances_sum sum of distances from all the corner poins
     * @return 
     */
    final private DirectedPoint createStartPoint (final float [] positions, final DirectedPoint [] corners, final float distances_sum) {
        DirectedPoint new_point = new DirectedPoint(positions.clone());
        float [] distances = new float [corners.length];
       
        // Determine direction and correction from distance from all the corner points
        for (int corner = 0; corner < corners.length; corner++) {
            // Count distance from the corner point.
            for (int dim = 0; dim < dimensions; dim++) {
                distances[corner] += Math.abs(new_point.position[dim] - corners[corner].position[dim]);
            }

            // Add the weighted direction and correction.
            // Weight is (maximum possible distance - current distance)/(sum of distances)
            // On the cube 3*3*3 it would be (9 - distance)/(36), therefore sum of weights == 1
            for (int dim = 0; dim < dimensions; dim++) {
                new_point.direction[dim] += (corners[corner].direction[dim] * ((2*distances_sum)/corners.length - distances[corner]) / distances_sum);
                new_point.correction[dim] += (corners[corner].correction[dim] * ((2*distances_sum)/corners.length - distances[corner]) / distances_sum);
            } 
        }
        return new_point;
    }
    
    /**
     * Creates trajectory of from the start point. 
     * At the end the start point is the first point after the end of the trajectory.
     * 
     * @param start_point point to start from
     * @param time_values how many new points the trajectory should have
     * @return new trajectory from the start point
     */
    private ArrayTrajectory createTrajectory (DirectedPoint start_point, final float [] time_values) {
        float [] all_points = new float [time_values.length * dimensions];
        
        // Add new positions to the simulation in the direction and correct the direction
        for (int point = 0; point < time_values.length; point++) {
            for (int dim = 0; dim < dimensions; dim++) {
                all_points[point*dimensions + dim] = start_point.position[dim];
                start_point.position[dim] += start_point.direction[dim];
                start_point.direction[dim] += start_point.correction[dim];
            }
        }
        
        return new ArrayTrajectory(all_points, time_values, dimensions); // Return new trajectory.
    }
    
    /**
     * Function creates corner points, places them to the correct position and provides them with direction and correction property.
     *
     * @return all the corners as directed points
     */
    private DirectedPoint [] CreateCorners() {
        int corners_count = (int) Math.pow(2, dimensions); // Number of points that are used for determining the direction
        DirectedPoint [] corners = new DirectedPoint [corners_count]; // Array for corner points
        float [] current_position = new float [dimensions]; // Position on which next point will be created
       
        // Create all the corner points
        for (int i = 0; i < corners_count; i++) {
            
            // Positioning of the point
            corners[i] = new DirectedPoint(current_position.clone()); // Create at current position
            // Change position "binary way"
            if (i != corners_count - 1) {
                int point_dim = 0; 
                while (current_position[point_dim] != 0 ) {
                    current_position[point_dim] = 0;
                    point_dim++;
                }
                current_position[point_dim] = (number - 1) * start_distance; 
            }
            // Create directioning properties
            for (int d = 0; d < dimensions; d++) {
                corners[i].direction[d] = generator.nextFloat();
                corners[i].correction[d] = (0.5f - generator.nextFloat()) / 5.0f;
            }    
        }
        return corners;
    }
    
    /**
     * Add points to the existing trajectories.
     * 
     * @param new_times how many new points it should have?
     */
    public void AddPoints(int new_times) {
        if (new_times < 1) throw new IllegalArgumentException ("Number of times is lower then 1.");
        
        // Create new values
        float [] time_values = new float [new_times];
        for (int i = times; i < times + new_times; i++)
            time_values[i - times] = i*time_step;
        
        // Append new trajectory to the old one
        for (int trajectory = 0; trajectory < start_points.length; trajectory++)
             ((LinkedTrajectory)(simulation.getTrajectory(trajectory))).append(createTrajectory(start_points[trajectory], time_values));
        
        times += new_times; // Update times.
    }
}
