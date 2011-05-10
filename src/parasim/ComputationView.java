/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import processing.core.PApplet;
import processing.core.PFont;
import processing.opengl.*;
import java.io.File;


/**
 *
 * @author sven
 */
public class ComputationView extends PApplet {
   private InitCondProbComputation comp;
   private ViewPort view;
   private int oldWidth, oldHeight;
   private PFont basefont;
   private boolean mouseFlag;

   public ComputationView()
   {
       //basefont = createFont("verdana", 30);
   }

   public void setComputation(InitCondProbComputation c)
   {
     comp = c;
     view = new ViewPort(c.getODE());
     println("setComputation");

     //println("CWD = "+Utils.getCWD());     
     //println("FontPath = "+dataPath("Verdana-30.vlw"));
     /*
     File f = new File(dataPath("Verdana-30.vlw"));

     if (!f.exists())
     {
       println("Font not found");
       exit();
     }*/
     
     
     basefont = loadFont("Verdana-30.vlw");
     //basefont = createFont("verdana", 30);
     textFont(basefont);
   }

   public void resize()
   {
       if (g instanceof PGraphicsOpenGL){
           oldWidth = width;
           oldHeight = height;

           PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;
           pgl.gl.glViewport(0, 0, width, height);           
           
           //basefont = createFont("verdana", 30);
           basefont = loadFont("Verdana-30.vlw");
           textFont(basefont);
       }
   }

    @Override
    public void setup () {
        oldWidth = width;
        oldHeight = height;

        size(oldWidth, oldHeight, OPENGL);
        hint(ENABLE_OPENGL_4X_SMOOTH);
        hint(ENABLE_NATIVE_FONTS);

        background(0);
        frameRate(30);
        curveDetail(20);

        String err_str;
        mouseFlag = true;
    }

    @Override
    public void draw ()
    {
        if (width != oldWidth || height != oldHeight)
        {            
            resize();
        }
        if (view != null)
        {
            view.setup_viewport();

            if (view.draw_if_not_focused || focused
                /*|| gui.dialog_status != GUI_DIALOG_STATUS_NONE*/)
            {                
                view.draw_axes();
                view.draw_simulations(comp.getSimulationStorage());
                view.draw_distance_checking(comp.getSimulationStorage());
            }            
        }
    }

    @Override
    public void mouseDragged()
    {        
        if (mouseButton == LEFT)
        {
            if (view.d_view == 3 && (!keyPressed || keyCode != SHIFT))
            {
                float rate = 0.01f;
                view.rotX((mouseY-pmouseY) * rate);
                view.rotY((mouseX-pmouseX) * rate);
            }

            if ((keyPressed && keyCode == SHIFT) ||
                view.d_view == 2 ||
                view.d_view == 1)
            {
                view.transX( (mouseX-pmouseX)*cos(view.getRotY())
                  -(mouseY-pmouseY)*sin(view.getRotY())*-sin(view.getRotX()) );

                view.transY( (mouseY-pmouseY)*cos(view.getRotX()) );

                view.transZ( (mouseX-pmouseX)*sin(view.getRotY())+
                   (mouseY-pmouseY)*cos(view.getRotY())*-sin(view.getRotX()) );
            }
        }
    }

    @Override
    public void mousePressed()
    {
        if (mouseFlag && view.closest_trajectory > -1)
        {
            if (view.selected_trajectory == view.closest_trajectory)
            {
                view.selected_trajectory = -1;
            }
            else
            {
                view.selected_trajectory = view.closest_trajectory;
                println("Selected trajectory = "+view.selected_trajectory);
            }
        }
    }

    @Override
    public void mouseReleased()
    {
        mouseFlag = true;
    }
    
    @Override
    public void keyPressed()
    {
        if (key == '+')
        {
            for (int i=0; i < view.zoom.length; i++)
            {
                view.zoom[i] *= 1.1;
                view.translation[i] *=1.1;
            }
            view.max_zoom *=1.1;

            if (view.max_zoom > 5000) view.ozoom = round(view.max_zoom/5000);
            else view.ozoom = 1;
        }
        if (key == '-')
        {
            for (int i=0; i < view.zoom.length; i++)
            {
                view.zoom[i] /= 1.1;
                view.translation[i] /=1.1;
            }
            view.max_zoom /=1.1;

            if (view.max_zoom > 5000) view.ozoom = round(view.max_zoom/5000);
            else view.ozoom = 1;
        }
        if (key == 'i')
        {
            println("X: "+view.getRotX()+" Y: "+view.getRotY());
            if (false && view.selected_trajectory != -1)
            {
              /*int sim_index = view.selected_trajectory;
              SimulationData sim = comp.sims.simulations[view.selected_trajectory];
              comp.ode.compute_derivatives(sim.get_last_point(1));
              print("Sim["+sim_index+"].dx = ");
              float[] dx = new float[comp.ode.dims];
              arrayCopy(comp.ode.dx, dx);
              print_array(dx);
              print("Sim["+sim_index+"].last_s_point = ");
              print_array(sim.get_last_point(1).c);
              print("Sim["+sim_index+"].last_d_point = ");
              print_array(sim.get_last_point(0).c);
              print("Sim["+sim_index+"]: end_status = "+
                    SIM_END_STATUS_STR[sim.end_status]);
              println(", Points = (D:"+sim.total_dense_length+
                      ", S:"+sim.total_sparse_length+")");
              for (int i=0; i<dx.length; i++)
              {
               dx[i] *= comp.ode.max_check_dist[0];
              }
              */
              /*Point new_p = comp.ode.simulate(sim.get_last_point(0),
                                              comp.ode.max_check_dist[0],true);
              print("New_p = ");
              print_array(new_p.c);
              print("Last_d_PDB = ");
              print_array(sim.last_d_pdb.points);*/
            }
        }
        if (key == 'e')
        {
            if (view.traj_detail > 1) view.traj_detail /= 2;
        }
        if (key == 'd')
        {
            if (view.traj_detail < 8192) view.traj_detail *= 2;
        }
        /*
        if (key == 'o' || key == 'O')
        {
            if (gui.dialog_status == GUI_DIALOG_STATUS_NONE)
            {
                gui.launch_chooser(GUI_DIALOG_TYPE_ODE, "Choose ODE file");
            }
        }
        if (key == 'p' || key == 'P')
        {
            if (gui.dialog_status == GUI_DIALOG_STATUS_NONE)
            {
                gui.launch_chooser(GUI_DIALOG_TYPE_PROPERTY, "Choose LTL file");
            }
        }
        */
        if (key == ' ')
        {
            if (comp.paused())
            {
                comp.resume();
                println("Computation resumed, total time "+(comp.getTotalTime()/1000.0f)+" seconds");
            }
            else
            {
                comp.pause();
                println("Computation paused, total time "+(comp.getTotalTime()/1000.0f)+" seconds");
            }
        }
        /*
        if (key == '.')
        {
            if (gui.speed > 0.001)
            {
                gui.speed /= 1.2;
            }
        }
        if (key == ',')
        {
            if (gui.speed < 10)
            {
                gui.speed *= 1.2;
            }
        }
        */
        if (key == 'c')
        {
            view.show_traj_comp = !view.show_traj_comp;
        }
        if (key == 'f')
        {
            view.show_traj_fin = !view.show_traj_fin;
        }
        if (key == 'v')
        {
            view.show_prop_validity = !view.show_prop_validity;
        }
    }

    /**
     * Computes the distance of the mouse cursor from the projection of point[x,y,z]
     * onto the screen using current matrix setup.
     * @param x
     * @param y
     * @param z
     * @return mouse distance from point
     */
    private float mouseDist3D(float x, float y, float z)
    {
        return dist(mouseX, mouseY, screenX(x,y,z), screenY(x,y,z));
    }

   /**
    * FIXME
    */
   private class ViewPort
    {
      private ODE ode;
      private float[] rotation;    // rotation of the viewport
      private float[] translation; // translation of the viewport
      private float[] zoom;        // zooming on each variable axes
      private float max_zoom;
      /* the maximum of all selected visible axial zooms */
      private float ozoom;
      /* OpenGL Zoom: when max_zoom is too large numerical approximations
         are visible during rendering, therefore ozoom is increased and
         the scale factor of the drwaing matrix is decreased. */
      private int[] axes;
      /* 3 element field, variables displayed on individual axes */
      private int d_view;
      /* controls the 3D/2D view, if 2D is chosen, first 2 elements of axes are used
       */
      private int traj_detail;
      /* every n-th point of the trajectory is added to curveVertex */

      private int selected_trajectory;
      /* index of selected trajectory or -1 if not selected */
      private int closest_trajectory;
      /* index of closest trajectory to mouse cursor, or -1 if no trajectory
         point is closer than 10px in current detail level */      

      private boolean show_traj_comp;
      private boolean show_traj_fin;
      private boolean show_prop_validity;
      private boolean draw_if_not_focused;

      /*ViewPort()
      {
        basefont = loadFont("Verdana-30.vlw");
        textFont(basefont);

        int dims = 3;
        axes = new int[]{0,0,0};
        d_view = 3;
        rotation = new float[dims];
        translation = new float[dims];
        zoom = new float[dims];
        traj_detail = 1;
        selected_trajectory = -1;
        closest_trajectory = -1;
        for (int i = 0; i < dims; i++)
        {
          rotation[i] = 0;
          translation[i] = 0;
          zoom[i] = 1000;
        }
        max_zoom = 1000;
        ozoom = 1;

        show_traj_comp = true;
        show_traj_fin = true;
        show_prop_validity = true;
        draw_if_not_focused = true;
      }*/

      ViewPort(ODE ode)
      {
        this.ode = ode;
        int dims = ode.dims;
        if (dims < 2) exit();

        //basefont = loadFont("Verdana-30.vlw");
        //textFont(basefont);

        if (dims > 3)
        {
          axes = new int[]{1,2,3};
          d_view = 3;

          //axes = new int[]{0,1,2};
          //d_view = 1;

        }
        else if (dims == 3)
        {
          axes = new int[]{1,2,0};
          d_view = 2;
          /*
          axes = new int[]{0,1,2};
          d_view = 1;
          */
        }
        else
        {
          axes = new int[]{0,1};
          d_view = 1;
        }

        rotation = new float[dims];
        translation = new float[dims];
        zoom = new float[dims];
        traj_detail = 4;
        selected_trajectory = -1;
        closest_trajectory = -1;

        max_zoom = 0;
        for (int i = 0; i < axes.length; i++)
        {
          if (axes[i] != 0)
          {
            max_zoom = max(max_zoom, (500/ode.gl_max[axes[i]]));
          }
        }
        for (int i = 0; i < dims; i++)
        {
          rotation[i] = 0;
          translation[i] = 0;
          zoom[i] = max_zoom;
        }
        zoom[0] = 500/ode.gl_max[0];
        //zoom[0] = 1;

        rotation[axes[0]] += radians(-10);
        rotation[axes[1]] += radians(-20);

        if (max_zoom > 5000) ozoom = round(max_zoom/5000);
        else ozoom = 1;
        //ozoom = 1;

        //ozoom = 10;
        /* ozoom is expeted to be 500 FIXME */

        show_traj_comp = true;
        show_traj_fin = true;
        show_prop_validity = true;
        draw_if_not_focused = true;
      }

      /* called after loading ODE or selecting different dimensions for axes
         adjusts zoom scales according to size of global max bounds */
      private void adjust_zoom(ODE ode)
      {
        max_zoom = 0;
        for (int i = 0; i < axes.length; i++)
        {
          if (axes[i] != 0)
          {
            max_zoom = max(max_zoom, (500/ode.gl_max[axes[i]]));
          }
        }
        for (int i = 1; i < ode.dims; i++)
        {
          zoom[i] = max_zoom;
        }
        zoom[0] = 400/ode.gl_max[0];

        if (max_zoom > 5000) ozoom = round(max_zoom/5000);
        else ozoom = 1;
      }

      /**
       * Draws a line between two points using the current ViewPort setting.
       **/
      private void point_line(Point a, Point b)
      {
        //if (d_view == 3)
        //{
          line(a.get(axes[0])*ozoom,
              -a.get(axes[1])*ozoom,
               a.get(axes[2])*ozoom,
               b.get(axes[0])*ozoom,
              -b.get(axes[1])*ozoom,
               b.get(axes[2])*ozoom);
        //}
        /*else if (d_view == 2)
        {
          line(a.c[axes[0]]*ozoom,
              -a.c[axes[1]]*ozoom,
               0,
               b.c[axes[0]]*ozoom,
              -b.c[axes[1]]*ozoom,
               0);
        }
        else if (d_view == 1)
        {
          line(a.c[axes[0]]*ozoom,
              -a.c[axes[1]]*ozoom,
               0,
               b.c[axes[0]]*ozoom,
              -b.c[axes[1]]*ozoom,
               0);
        } */
      }

      private void point_vertex(Point a)
      {
        /*if (d_view == 3)
        { */
          vertex(a.get(axes[0])*ozoom,
                -a.get(axes[1])*ozoom,
                 a.get(axes[2])*ozoom);
        /*}
        else if (d_view == 2)
        {
          vertex(a.c[axes[0]]*ozoom,
                -a.c[axes[1]]*ozoom,
                 0);
        }
        else if (d_view == 1)
        {
          vertex(a.c[axes[0]]*ozoom,
                -a.c[axes[1]]*ozoom,
                 0);
        }   */
      }

      private void point_curveVertex(Point a)
      {
        /*if (d_view == 3)
        { */
          curveVertex(a.get(axes[0])*ozoom,
                     -a.get(axes[1])*ozoom,
                      a.get(axes[2])*ozoom);
        /*}
        else if (d_view == 2)
        {
          curveVertex(a.c[axes[0]]*ozoom,
                     -a.c[axes[1]]*ozoom,
                      0);
        }
        else if (d_view == 1)
        {
          curveVertex(a.c[axes[0]]*ozoom,
                     -a.c[axes[1]]*ozoom,
                      0);
        } */
      }

      void setup_viewport()
      {
        background(0);
        textFont(basefont);
        translate(width/2, height/2);        

        if (d_view == 3)
        {
          rotateX(getRotX());
          rotateY(getRotY());
          rotateZ(getRotZ());
          translate(translation[axes[0]],translation[axes[1]],translation[axes[2]]);
          scale(zoom[axes[0]]/ozoom,
                zoom[axes[1]]/ozoom,
                zoom[axes[2]]/ozoom);
        }
        else if (d_view == 2)
        {
          translate(translation[axes[0]],translation[axes[1]],0);
          scale(zoom[axes[0]]/ozoom,
                zoom[axes[1]]/ozoom,
                0);
        }
        else if (d_view == 1)
        {
          translate(translation[axes[0]],translation[axes[1]],0);
          scale(zoom[axes[0]]/ozoom,
                zoom[axes[1]]/ozoom,
                0);
        }
      }

      float getRotX()
      {
        return rotation[axes[0]];
      }

      float getRotY()
      {
        return rotation[axes[1]];
      }

      float getRotZ()
      {
        return rotation[axes[2]];
      }

      void rotX(float diff)
      {
        rotation[axes[0]] += diff;
      }

      void rotY(float diff)
      {
        rotation[axes[1]] += diff;
      }

      void rotZ(float diff)
      {
        rotation[axes[2]] += diff;
      }

      void transX(float diff)
      {
        translation[axes[0]] += diff;
      }

      void transY(float diff)
      {
        translation[axes[1]] += diff;
      }

      void transZ(float diff)
      {
        translation[axes[2]] += diff;
      }

      void point_translate(Point a)
      {
        translate(a.get(axes[0])*ozoom,
                 -a.get(axes[1])*ozoom,
                  a.get(axes[2])*ozoom);
      }

      float point_screenX(Point a)
      {
        return screenX( a.get(axes[0])*ozoom,
                       -a.get(axes[1])*ozoom,
                        a.get(axes[2])*ozoom);
      }

      float point_screenY(Point a)
      {
        return screenY( a.get(axes[0])*ozoom,
                       -a.get(axes[1])*ozoom,
                        a.get(axes[2])*ozoom);
      }

      /**
       * Outputs the given string to the position of the given point facing the
       * screen as if it was flat.
       **/
      void print3d(String str, Point p)
      {
        print2d(str, point_screenX(p), point_screenY(p));
      }

      /**
       * Outputs the given string as if the viewport was a flat screen with [0,0]
       * in the top left corner.
       **/
      void print2d(String str, float x, float y)
      {
        pushMatrix2d();
        text(str, x-width/2, -height/2+y, 0);
        popMatrix();
      }

      void pushMatrix2d()
      {
        if (d_view == 3)
        {
          pushMatrix();
          scale(ozoom/zoom[axes[0]],
                ozoom/zoom[axes[1]],
                ozoom/zoom[axes[2]]);
          //scale(max_zoom/ozoom); ???
          translate(-translation[axes[0]],
                    -translation[axes[1]],
                    -translation[axes[2]]);
          rotateZ(-getRotZ());
          rotateY(-getRotY());
          rotateX(-getRotX());

          translate(0.0f,0.0f,623.51f);
          scale(0.1f);
        }
        else if (d_view == 2)
        {
          pushMatrix();
          scale(ozoom/zoom[axes[0]],
                ozoom/zoom[axes[1]],
                0);
          translate(-translation[axes[0]],-translation[axes[1]],0);

          //scale(max_zoom/ozoom);???

          //translate(0,0,623.51);
          //scale(0.1);
        }
        else if (d_view == 1)
        {
          pushMatrix();
          scale(ozoom/zoom[axes[0]],
                ozoom/zoom[axes[1]],
                ozoom/zoom[axes[2]]);
          //scale(max_zoom/ozoom); ???
          rotateZ(-getRotZ());
          rotateY(-getRotY());
          rotateX(-getRotX());
          translate(0.0f,0.0f,623.51f);
          scale(0.1f);
        }
      }

      void draw_axes()
      {
          if (d_view == 1) // Time course
          {
            if (true)
            {
              return;
            }
            float text_size = 30.0f;
            /* Basic axes for total scale */
            stroke(255,255,255,128);
            strokeWeight(1.0f);
            line (0, 0, 0, 1, 0, 0);  /* X */
            line (0, 0, 0, 0, -1, 0); /* Y */
            //line (0, 0, 0, 0, 0, 1);  /* Z */

            /* Drawing the scale on each axis */
            pushMatrix();
            int i;

            scale(max_zoom/zoom[axes[0]],
                  max_zoom/zoom[axes[1]],
                  max_zoom/zoom[axes[2]]);

            fill(0,255,0);
            float ts = text_size/max_zoom*ozoom;

            textSize(ts);
            text("   "+ode.var_names[axes[0]]+" -->",-ts,1.5f*ts,0);
            textSize(ts/2);
            int order = round(log(zoom[axes[0]])/log(10));
            float dorder = pow(0.1f, order-2);
            for (i=0; i<10; i++)
            {
              text("|"+Utils.pf(i,2-order),
                   zoom[axes[0]]/max_zoom*i*dorder*ozoom,
                   ts/2.0f,0);
            }
            rotateZ(3*HALF_PI);
            //rotateX(3*HALF_PI);

            textSize(ts);
            text("   "+ode.var_names[axes[1]]+" --> ",-ts,-1.0f*ts,0);
            textSize(ts/2);
            order = round(log(zoom[axes[1]])/log(10));
            dorder = pow(0.1f,order-2);
            for (i=0; i<10; i++)
            {
              text("|"+Utils.pf(i,2-order),
                   zoom[axes[1]]/max_zoom*i*dorder*ozoom,
                   -ts/3.0f,0);
            }

            popMatrix();

            /* initial conditions box */
            pushMatrix();
            translate(( ode.ic_min[axes[0]]+ode.ic_max[axes[0]])/2*ozoom,
                      (-ode.ic_min[axes[1]]-ode.ic_max[axes[1]])/2*ozoom,
                      ( ode.ic_min[axes[2]]+ode.ic_max[axes[2]])/2*ozoom);
            noFill();
            stroke(255,255,0,128);
            strokeWeight(2.0f);
            box((ode.ic_max[axes[0]]-ode.ic_min[axes[0]])*ozoom,
                (ode.ic_max[axes[1]]-ode.ic_min[axes[1]])*ozoom,
                (ode.ic_max[axes[2]]-ode.ic_min[axes[2]])*ozoom);

            popMatrix();

            /* global maximum box */
            pushMatrix();
            translate( ode.gl_max[axes[0]]/2*ozoom,
                      -ode.gl_max[axes[1]]/2*ozoom,
                       ode.gl_max[axes[2]]/2*ozoom);
            noFill();
            stroke(255,255,50,128);
            strokeWeight(2.0f);
            box(ode.gl_max[axes[0]]*ozoom,
                ode.gl_max[axes[1]]*ozoom,
                ode.gl_max[axes[2]]*ozoom);

            popMatrix();

            /* Highlights the portion of the axes between min and max of the initial
               conditions region. */
            stroke(255,100,0);
            strokeWeight(5.0f);
            line (ode.ic_min[0]*ozoom, 0, 0,
                  ode.ic_max[0]*ozoom, 0, 0);  /* X */
            line (0, -ode.ic_min[axes[1]]*ozoom, 0,
                  0, -ode.ic_max[axes[1]]*ozoom, 0); /* Y */
          }
          else if (d_view == 2) // 2D
          {
            float text_size = 30.0f;
            /* Basic axes for total scale */
            stroke(255,255,255,128);
            strokeWeight(1.0f);
            line (0, 0, 0, 1, 0, 0);  /* X */
            line (0, 0, 0, 0, -1, 0); /* Y */
            //line (0, 0, 0, 0, 0, 1);  /* Z */

            /* Drawing the scale on each axis */
            pushMatrix();
            int i;

            scale(max_zoom/zoom[axes[0]],
                  max_zoom/zoom[axes[1]],
                  max_zoom/zoom[axes[2]]);

            fill(0,255,0);
            float ts = text_size/max_zoom*ozoom;

            textSize(ts);
            text("   "+ode.var_names[axes[0]]+" -->",-ts,1.5f*ts,0);
            textSize(ts/2);
            int order = round(log(zoom[axes[0]])/log(10));
            float dorder = pow(0.1f,order-2);
            int mult = 1;
            if (zoom[axes[0]]/max_zoom*dorder*ozoom <
                textWidth("|"+Utils.pf(0,2-order)))
            {
              mult = 2;
            }
            for (i=0; i<10; i++)
            {
              text("|"+Utils.pf(i*mult,2-order),
                   zoom[axes[0]]/max_zoom*i*mult*dorder*ozoom,
                   ts/2.0f,0);
            }
            rotateZ(3*HALF_PI);

            textSize(ts);
            text("   "+ode.var_names[axes[1]]+" --> ",-ts,-1.0f*ts,0);
            textSize(ts/2);
            order = round(log(zoom[axes[1]])/log(10));
            dorder = pow(0.1f,order-2);
            mult = 1;
            if (zoom[axes[1]]/max_zoom*dorder*ozoom <
                textWidth("|"+Utils.pf(0,2-order)))
            {
              mult = 2;
            }
            for (i=0; i<10; i++)
            {
              text("|"+Utils.pf(i*mult,2-order),
                   zoom[axes[1]]/max_zoom*i*mult*dorder*ozoom,
                   -ts/3.0f,0);
            }
            popMatrix();

            /* initial conditions box */
            pushMatrix();
            translate(( ode.ic_min[axes[0]]+ode.ic_max[axes[0]])/2*ozoom,
                      (-ode.ic_min[axes[1]]-ode.ic_max[axes[1]])/2*ozoom,
                      ( ode.ic_min[axes[2]]+ode.ic_max[axes[2]])/2*ozoom);
            noFill();
            stroke(255,255,0,128);
            strokeWeight(2.0f);
            box((ode.ic_max[axes[0]]-ode.ic_min[axes[0]])*ozoom,
                (ode.ic_max[axes[1]]-ode.ic_min[axes[1]])*ozoom,
                (ode.ic_max[axes[2]]-ode.ic_min[axes[2]])*ozoom);

            popMatrix();

            /* global maximum box */
            pushMatrix();
            translate( ode.gl_max[axes[0]]/2*ozoom,
                      -ode.gl_max[axes[1]]/2*ozoom,
                       ode.gl_max[axes[2]]/2*ozoom);
            noFill();
            stroke(255,255,50,128);
            strokeWeight(2.0f);
            box(ode.gl_max[axes[0]]*ozoom,
                ode.gl_max[axes[1]]*ozoom,
                ode.gl_max[axes[2]]*ozoom);

            popMatrix();

            /* Highlights the portion of the axes between min and max of the initial
               conditions region. */
            stroke(255,100,0);
            strokeWeight(5.0f);
            line (ode.ic_min[axes[0]]*ozoom, 0, 0,
                  ode.ic_max[axes[0]]*ozoom, 0, 0);  /* X */
            line (0, -ode.ic_min[axes[1]]*ozoom, 0,
                  0, -ode.ic_max[axes[1]]*ozoom, 0); /* Y */
            /*line (0, 0, ode.ic_min[axes[2]]*ozoom,
                  0, 0, ode.ic_max[axes[2]]*ozoom);  */
          }
          else if (d_view == 3) // 3D
          {
            /* Basic axes for total scale */
            stroke(255,255,255,128);
            strokeWeight(1.0f);
            line (0, 0, 0, 1, 0, 0);  /* X */
            line (0, 0, 0, 0, -1, 0); /* Y */
            line (0, 0, 0, 0, 0, 1);  /* Z */

            /* Drawing the scale on each axis */
            pushMatrix();
            int i;

            scale(max_zoom/zoom[axes[0]],
                  max_zoom/zoom[axes[1]],
                  max_zoom/zoom[axes[2]]);

            fill(0,255,0);            
            textSize(20/max_zoom*ozoom);
            text("   "+ode.var_names[axes[0]]+" -->",0,0,0);
            textSize(10/max_zoom*ozoom);
            int order = round(log(zoom[axes[0]])/log(10));
            float dorder = pow(0.1f,order-2);
            int mult = 1;

            if (zoom[axes[0]]/max_zoom*dorder*ozoom <
                textWidth("|"+Utils.pf(0,2-order)))
            {
              mult = 2;
            }

            for (i=0; i<10; i++)
            {
              text("|"+Utils.pf(i*mult,2-order),
                   zoom[axes[0]]/max_zoom*i*mult*dorder*ozoom,
                   10/max_zoom*ozoom,0);
            }
            rotateZ(3*HALF_PI);
            rotateX(3*HALF_PI);

            textSize(20/max_zoom*ozoom);
            text("   "+ode.var_names[axes[1]]+" --> ",0,0,0);
            textSize(10/max_zoom*ozoom);
            order = round(log(zoom[axes[1]])/log(10));
            dorder = pow(0.1f,order-2);
            mult = 1;
            if (zoom[axes[1]]/max_zoom*dorder*ozoom <
                textWidth("|"+Utils.pf(0,2-order)))
            {
              mult = 2;
            }
            for (i=0; i<10; i++)
            {
              text("|"+Utils.pf(i*mult,2-order),
                   zoom[axes[1]]/max_zoom*i*mult*dorder*ozoom,
                   10/max_zoom*ozoom,0);
            }
            rotateY(HALF_PI);
            rotateZ(-HALF_PI);

            textSize(20/max_zoom*ozoom);
            text("   "+ode.var_names[axes[2]]+" -->",0,0,0);
            textSize(10/max_zoom*ozoom);
            order = round(log(zoom[axes[2]])/log(10));
            dorder = pow(0.1f,order-2);
            mult = 1;
            if (zoom[axes[2]]/max_zoom*dorder*ozoom <
                textWidth("|"+Utils.pf(0,2-order)))
            {
              mult = 2;
            }
            for (i=0; i<10; i++)
            {
              text("|"+Utils.pf(i*mult,2-order),
                   zoom[axes[2]]/max_zoom*i*mult*dorder*ozoom,
                   10/max_zoom*ozoom,0);
            }
            popMatrix();

            /* initial conditions box */
            pushMatrix();
            translate(( ode.ic_min[axes[0]]+ode.ic_max[axes[0]])/2*ozoom,
                      (-ode.ic_min[axes[1]]-ode.ic_max[axes[1]])/2*ozoom,
                      ( ode.ic_min[axes[2]]+ode.ic_max[axes[2]])/2*ozoom);
            noFill();
            stroke(255,255,0,128);
            strokeWeight(2.0f);
            box((ode.ic_max[axes[0]]-ode.ic_min[axes[0]])*ozoom,
                (ode.ic_max[axes[1]]-ode.ic_min[axes[1]])*ozoom,
                (ode.ic_max[axes[2]]-ode.ic_min[axes[2]])*ozoom);

            popMatrix();

            /* global maximum box */
            pushMatrix();
            translate( ode.gl_max[axes[0]]/2*ozoom,
                      -ode.gl_max[axes[1]]/2*ozoom,
                       ode.gl_max[axes[2]]/2*ozoom);
            noFill();
            stroke(255,255,50,128);
            strokeWeight(2.0f);
            box(ode.gl_max[axes[0]]*ozoom,
                ode.gl_max[axes[1]]*ozoom,
                ode.gl_max[axes[2]]*ozoom);

            popMatrix();

            /* Highlights the portion of the axes between min and max of the initial
               conditions region. */
            stroke(255,100,0);
            strokeWeight(5.0f);
            line (ode.ic_min[axes[0]]*ozoom, 0, 0,
                  ode.ic_max[axes[0]]*ozoom, 0, 0);  /* X */
            line (0, -ode.ic_min[axes[1]]*ozoom, 0,
                  0, -ode.ic_max[axes[1]]*ozoom, 0); /* Y */
            line (0, 0, ode.ic_min[axes[2]]*ozoom,
                  0, 0, ode.ic_max[axes[2]]*ozoom);  /* Z */
          }
        }


        void draw_validity(SimulationData sim)
        {
            float radius = 5/max_zoom*ozoom;
            strokeWeight(1.0f);
            //noStroke();
            noFill();

            switch (sim.getPropertyStatus())
            {
                case Utils.PC_STATUS_NONE:
                  stroke(100,100,100,100);
                  fill(100,100,100,50);
                  break;
                case Utils.PC_STATUS_COMP:
                  stroke(255,100,100,100);
                  fill(255,100,100,50);
                  break;
                case Utils.PC_STATUS_VALID:
                  stroke(0,255,0,100);
                  fill(0,255,0,50);
                  break;
                case Utils.PC_STATUS_INVALID:
                  stroke(255,55,0,100);
                  fill(255,55,0,50);
                  break;
                case Utils.PC_STATUS_DONTKNOW:
                  stroke(0,0,255,100);
                  fill(0,0,255,50);
                  break;
            }
            pushMatrix();
            Point p = sim.get_first_point();
            translate( p.get(axes[0])*ozoom,
                      -p.get(axes[1])*ozoom,
                       p.get(axes[2])*ozoom);

            scale(max_zoom/zoom[axes[0]],
                  max_zoom/zoom[axes[1]],
                  max_zoom/zoom[axes[2]]);

            box(radius*2);
            popMatrix();
        }

        /**
         * Draws the given trajectory, if axes.length == 2 then
         * the trajectory is drawn in 2D with axes[0] on the Xs axes and axes[1] on the
         * Ys axes, in case of length == 3, trajectory is drawn in 3D on XYZ.
         *
         * If mouse is close to the trajectory (less then 10px), the index of the closest
         * point is returned. Othervise -1 is returned.
         */
        int draw_simulation(Point[] data, boolean selected,
                            int cycle_start, FloatW spd/*, int pc_status*/)
        {
            int count = data.length;
            int selected_point_index = -1; // selected point index
            float selected_point_distance = 10; // selected point distance
            float tmp_distance;

            noFill();

            if (d_view == 3 || d_view == 2)
            {
                if (selected)
                {
                    strokeWeight(3.5f);
                }
                else
                {
                  strokeWeight(1.5f);
                }

                beginShape();
                point_curveVertex(data[0]);
                for (int i = 0; i<count; i++)
                {
                    point_curveVertex(data[i]);
                    tmp_distance = mouseDist3D( data[i].get(axes[0])*ozoom,
                                               -data[i].get(axes[1])*ozoom,
                                                data[i].get(axes[2])*ozoom);
                    if (selected_point_distance > 0 &&
                        tmp_distance < selected_point_distance)
                    {
                        selected_point_index = i;
                        selected_point_distance = tmp_distance;
                    }
                }
                point_curveVertex(data[count-1]);
                endShape();

                float radius = 5/max_zoom*ozoom;
                /* All the points of the selected simulation in current detail
                   level are shown as small skeleton boxes */
                strokeWeight(1.0f);
                if (selected)
                {
                    noFill();
                    stroke(255,100,0,200);
                    for (int i = 0; i<count; i++)
                    {
                        pushMatrix();
                        translate( data[i].get(axes[0])*ozoom,
                                  -data[i].get(axes[1])*ozoom,
                                   data[i].get(axes[2])*ozoom);
                        scale(max_zoom/zoom[axes[0]],
                              max_zoom/zoom[axes[1]],
                              max_zoom/zoom[axes[2]]);
                        if (cycle_start != -1 && i >= cycle_start)
                        {
                            stroke(255,200,50,200);
                        }
                        box(radius/5);
                        popMatrix();
                    }
                }

                // The last point is drawn as a bigger box with full sides.
                stroke(255,100,0,200);
                fill(255,100,0,200);
                pushMatrix();
                translate( data[count-1].get(axes[0])*ozoom,
                          -data[count-1].get(axes[1])*ozoom,
                           data[count-1].get(axes[2])*ozoom);

                scale(max_zoom/zoom[axes[0]],
                      max_zoom/zoom[axes[1]],
                      max_zoom/zoom[axes[2]]);

                box(radius);
                popMatrix();

                /*
                noStroke();
                noFill();

                switch (pc_status)
                {
                  case PC_STATUS_NONE:
                    stroke(100,100,100,100);
                    fill(100,100,100,50);
                    break;
                  case PC_STATUS_COMP:
                    stroke(255,100,100,100);
                    fill(255,100,100,50);
                    break;
                  case PC_STATUS_VALID:
                    stroke(0,255,0,100);
                    fill(0,255,0,50);
                    break;
                  case PC_STATUS_INVALID:
                    stroke(255,55,0,100);
                    fill(255,55,0,50);
                    break;
                  case PC_STATUS_DONTKNOW:
                    stroke(0,0,255,100);
                    fill(0,0,255,50);
                    break;
                }
                pushMatrix();
                translate( data[0].c[axes[0]]*ozoom,
                          -data[0].c[axes[1]]*ozoom,
                           data[0].c[axes[2]]*ozoom);

                scale(max_zoom/zoom[axes[0]],
                      max_zoom/zoom[axes[1]],
                      max_zoom/zoom[axes[2]]);

                box(radius*2);
                popMatrix(); */
            }
            else if (d_view == 1)
            {
                int dims = data[0].getDims();
                /*int[] dim_colors = new int[dims];
                for (int j=1; j<dims; j++)
                {
                  dim_colors =
                } */

                for (int j=1; j<dims; j++)
                {
                    if (selected)
                    {
                        strokeWeight(3.5f);
                        stroke((100/dims)*j+50, 150-(100/dims)*j, 100/(j*j)+50, 250);
                    }
                    else
                    {
                        strokeWeight(1.5f);
                        stroke((100/dims)*j+50, 150-(100/dims)*j, 100/(j*j)+50, 150);
                    }
                    noFill();
                    beginShape();
                    curveVertex(data[0].get(j)*ozoom, -data[0].get(j)*ozoom, 0);

                    for (int i = 0; i<count; i++)
                    {
                        curveVertex(data[i].get(0)*ozoom, -data[i].get(j)*ozoom, 0);
                        tmp_distance = mouseDist3D( data[i].get(0)*ozoom,
                                                   -data[i].get(j)*ozoom,
                                                    0);
                        if (selected_point_distance > 0 &&
                            tmp_distance < selected_point_distance)
                        {
                            selected_point_index = i;
                            selected_point_distance = tmp_distance;
                        }
                    }
                    curveVertex(data[count-1].get(0)*ozoom, -data[count-1].get(j)*ozoom, 0);
                    endShape();

                    float radius = 5/max_zoom*ozoom;
                    /* All the points of the selected simulation in current
                       detail level are shown as small skeleton boxes */
                    strokeWeight(1.0f);
                    if (selected)
                    {
                        noFill();
                        stroke(255,100,0,200);
                        for (int i = 0; i<count; i++)
                        {
                            pushMatrix();
                            translate( data[i].get(0)*ozoom, -data[i].get(j)*ozoom, 0);
                            scale(max_zoom/zoom[0], max_zoom/zoom[j], 0);
                            if (cycle_start != -1 && i >= cycle_start)
                            {
                                stroke(255,200,50,200);
                            }
                            box(radius/5);
                            popMatrix();
                        }
                    }
                    // The last point is drawn as a bigger box with full sides.
                    stroke(255,100,0,200);
                    fill(255,100,0,200);
                    pushMatrix();
                    translate( data[count-1].get(0)*ozoom,
                              -data[count-1].get(j)*ozoom, 0);
                    scale(max_zoom/zoom[0], max_zoom/zoom[j], 0);

                    box(radius);
                    popMatrix();
                }
            }
            if (selected_point_index != -1)
            {
                spd.set(selected_point_distance);
            }
            return selected_point_index;
        }

        void draw_simulations(SimulationStorage sims)
        {
            noFill();

            int tmp_spi = -1;
            /* selected point index */
            FloatW tmp_spd = new FloatW();
            /* selected point distance from mouse cursor */
            int closestSimIndex = -1;
            float selected_point_distance = 10;
            int cycle_start;

            Point[] points;
            Point selected_point = null;

            for (int i=0; i<sims.count; i++)
            {
                if ((!show_traj_fin && sims.simulations[i].status == Utils.SIM_STATUS_FIN) ||
                    (!show_traj_comp && sims.simulations[i].status != Utils.SIM_STATUS_FIN))
                {
                    continue;
                }
                if (sims.simulations[i].status == Utils.SIM_STATUS_FIN)
                {
                    stroke(25,24,75,150);
                }
                else
                {
                    stroke(50,50,150,200);
                }
                if (selected_trajectory == i) strokeWeight(3.5f);
                else strokeWeight(1.5f);
                curveTightness(0.0f);

                points = sims.simulations[i].get_points_subset(traj_detail,1);

                cycle_start = -1;
                if (sims.simulations[i].cycle_s_pdb != null)
                {
                    /* expects points to be a subset of the sparse array */
                    cycle_start = (sims.simulations[i].cycle_s_pdb.first_point_index +
                                   sims.simulations[i].cycle_offset)/traj_detail;
                }

                tmp_spi = draw_simulation(points, selected_trajectory == i,
                                          cycle_start, tmp_spd/*,
                                      sims.simulations[i].property_check.status*/);

                if (tmp_spi > -1)
                {
                    if (selected_point_distance > tmp_spd.get())
                    {
                        selected_point_distance = tmp_spd.get();
                        selected_point = points[tmp_spi];
                        closestSimIndex = i;
                    }
                }
            }

            if (show_prop_validity)
            {
                for (int i=0; i<sims.count; i++)
                {
                    draw_validity(sims.simulations[i]);
                }
            }

            closest_trajectory = closestSimIndex;
            // show the position of the closest point in all planes
            if (selected_point != null && closest_trajectory > -1)
            {
                fill(255,205,205,200);
                //stroke(255,255,205,200);

                textSize(20);
                if (d_view == 3)
                {
                    String coord_info = "["+selected_point.get(axes[0])+","+
                                          selected_point.get(axes[1])+","+
                                          selected_point.get(axes[2])+"]";
                    print3d(coord_info, selected_point);
                    //println("POINT INFO, sim["+closest_trajectory+"][?] = "+coord_info);
                    pushMatrix();
                    float tmpx = selected_point.get(axes[0])*ozoom;
                    float tmpy = selected_point.get(axes[1])*ozoom;
                    float tmpz = selected_point.get(axes[2])*ozoom;
                    translate(tmpx/2.0f, -tmpy/2.0f, tmpz/2.0f);
                    noFill();
                    box(tmpx, -tmpy, tmpz);
                    popMatrix();
                }
                else if (d_view == 2)
                {
                    String coord_info = "["+selected_point.get(axes[0])+","+
                                          selected_point.get(axes[1])+"]";
                    print3d(coord_info, selected_point);
                    pushMatrix();
                    float tmpx = selected_point.get(axes[0])*ozoom;
                    float tmpy = selected_point.get(axes[1])*ozoom;
                    translate(tmpx/2.0f, -tmpy/2.0f, 0);
                    noFill();
                    box(tmpx, -tmpy, 0);
                    popMatrix();
                }
                else if (d_view == 1)
                {
                    //FIXME
                    /*pushMatrix();
                    float tmpx = selected_point.c[axes[0]]*ozoom;
                    float tmpy = selected_point.c[axes[1]]*ozoom;
                    translate(tmpx/2.0f, -tmpy/2.0f, 0);
                    box(tmpx, -tmpy, 0);
                    popMatrix();*/
                }
            }
        }

        void draw_distance_checking(SimulationStorage sims)
        {
            if (d_view == 1) return;
            if (selected_trajectory < 0 || selected_trajectory >= sims.count)
            {
                return;
            }

            SimulationData simulation = sims.simulations[selected_trajectory];
            DistanceCheckDataBlock dc_data;
            Interval interval;

            int interval_col;
            int point_col;
            Point point;
            PointPosition pos;
            StatusMod status = new StatusMod();
            int sparser;

            for (int i = 0; i<simulation.dist_check.length; i++)
            {
                dc_data = simulation.dist_check[i];

                if (dc_data.check_status == Utils.DC_STATUS_COMP)
                {
                    interval_col = color(255,50,50,140);
                    point_col = color(255,50,150,140);
                }
                else
                {
                    interval_col = color(120,50,50,80);
                    point_col = color(120,50,80,80);
                }

                pushMatrix();
                noFill();
                stroke(interval_col);
                strokeWeight(2.0f);
                float radius = 10/max_zoom*ozoom;

                /* draw the box ode.max_check_dist aroung the local point */
                point = dc_data.local_pos.pdb.points[dc_data.local_pos.offset];
                point_translate(point);

                box(ode.max_check_dist[axes[0]]*ozoom*2,
                    ode.max_check_dist[axes[1]]*ozoom*2,
                    ode.max_check_dist[axes[2]]*ozoom*2);

                scale(max_zoom/zoom[axes[0]],
                      max_zoom/zoom[axes[1]],
                      max_zoom/zoom[axes[2]]);

                fill(point_col);
                stroke(point_col);
                box(radius);
                popMatrix();

                /* draw the intervals being distance checked at the moment */
                for (int j=0; j<dc_data.intervals.length; j++)
                {
                    interval = dc_data.intervals[j];

                    fill(interval_col);
                    noStroke();
                    beginShape(TRIANGLE_FAN);
                    point_vertex(dc_data.local_pos.pdb.points[dc_data.local_pos.offset]);
                    pos = interval.first;
                    sparser = 0;
                    status.set(Utils.SIM_END_STATUS_COMP);
                    while (pos.pdb != null && !pos.equals(interval.last))
                    {
                        if (sparser == 0)
                        {
                            point_vertex(pos.pdb.points[pos.offset]);
                        }
                        pos = pos.get_next_point_pos(dc_data.neighbour, status);
                        sparser = (sparser+1)%10;
                    }
                    point_vertex(interval.last.pdb.points[interval.last.offset]);
                    endShape();

                    pushMatrix();
                    /* draw the first point of the interval in interval_col and last in
                     point_col */
                    point = interval.first.pdb.points[interval.first.offset];
                    point_translate(point);

                    fill(interval_col);
                    stroke(interval_col);
                    strokeWeight(2.0f);
                    box(max_zoom/zoom[axes[0]]*radius,
                        max_zoom/zoom[axes[1]]*radius,
                        max_zoom/zoom[axes[2]]*radius);

                    point = interval.last.pdb.points[interval.last.offset];
                    popMatrix();
                    pushMatrix();
                    point_translate(point);

                    box(max_zoom/zoom[axes[0]]*radius,
                        max_zoom/zoom[axes[1]]*radius,
                        max_zoom/zoom[axes[2]]*radius);
                    popMatrix();
                }
            }
        }

    }

}
