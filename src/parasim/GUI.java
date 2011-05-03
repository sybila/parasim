/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import java.io.File;

/**
 *
 * @author sven
 */
public class GUI
{
  //int mili_secs;
  boolean mouseFlag;
  //boolean computation_paused;
  //float speed;
  boolean draw_if_not_focused;
  boolean model_loaded;
  //float dc_counter;
  //float sim_counter;
  String ode_file;
  String property_file;
  int dialog_status;
  int dialog_type;

  private ComputationManager compMan;
  private InitCondProbComputation comp;
  private ComputationView compView;

  public GUI(JPanel panel, ComputationManager compMan)
  {
    //mili_secs = 0;
    //dc_counter = 0;
    //sim_counter = 0;
    mouseFlag = true;
    //computation_paused = true;
    //speed = 0.1f;
    draw_if_not_focused = false;
    model_loaded = false;
    //ode_file = null;
    property_file = null;
    ode_file = "data/examples/bayramov.ode";
    property_file = "data/examples/bayramov_oscillation_prop.ltl";
    dialog_status = Utils.GUI_DIALOG_STATUS_NONE;
    dialog_type = Utils.GUI_DIALOG_TYPE_NONE;

    comp = new InitCondProbComputation();
    String err_str;
    if (ode_file != null)
    {
        err_str = comp.load_ode_file(ode_file);
        if (err_str != null)
        {
            System.out.println("ERROR when loading ODE: "+err_str);
        }
    }

    if (comp.isLoaded() && property_file != null)
    {
        err_str = comp.load_property_file(property_file);
        if (err_str != null)
        {
            System.out.println("ERROR when loading property: "+err_str);
        }
    }

    compView = new ComputationView();
    compView.init();
    panel.add(compView);
    compView.setComputation(comp);
    this.compMan = compMan;

    compMan.add(comp);
  }

  String get_ode_file()
  {
    return ode_file;
  }

  String get_property_file()
  {
    return property_file;
  }

  /**
   * Displays a dialog to choose *.ode files, returns filepath and name
   * on success or null if nothing was chosen.
   *
   * @param extensions possible file extensions
   * @param descriptions text descriptions of extensions both arrays are
   *        expected to have same length
   **/
  String choose_ode_file(/*String extensions[], String descriptions[]*/)
  {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Choose ODE file");
    chooser.setFileFilter(chooser.getAcceptAllFileFilter());

    //ExampleFileFilter filter = new ExampleFileFilter();
    //filter.addExtension("ode");
    //filter.addExtension("gif");
    //filter.setDescription("NumSimVer ODE file");
    //chooser.setFileFilter(filter);

    File dataDir = new File(Utils.getCWD(), "data");
    chooser.setSelectedFile(dataDir);

    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      //println("You chose to open this file: " + chooser.getSelectedFile().getName());
      return chooser.getSelectedFile().getAbsolutePath();
    }
    return null;
  }

  String choose_property_file()
  {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Choose LTL file");
    chooser.setFileFilter(chooser.getAcceptAllFileFilter());

    //ExampleFileFilter filter = new ExampleFileFilter();
    //filter.addExtension("ode");
    //filter.addExtension("gif");
    //filter.setDescription("NumSimVer ODE file");
    //chooser.setFileFilter(filter);

    File dataDir = new File(Utils.getCWD(), "data");
    chooser.setSelectedFile(dataDir);

    int returnVal = chooser.showOpenDialog(null);
    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      //println("You chose to open this file: " + chooser.getSelectedFile().getName());
      return chooser.getSelectedFile().getAbsolutePath();
    }
    return null;
  }

  void launch_chooser(final int type, final String dialog_title)
  {    
    dialog_status = Utils.GUI_DIALOG_STATUS_WAIT;
    dialog_type = type;
    SwingUtilities.invokeLater(
      new Runnable()
      {
        public void run()
        {
          try
          {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle(dialog_title);
            File curr_dir = new File(Utils.getCWD(), "data");
            fc.setSelectedFile(curr_dir);
            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
              File file = fc.getSelectedFile();
              String name = file.getAbsolutePath();
              file_chosen(name);
              /*if (name.endsWith(".jpg") || name.endsWith(".gif"))
              {
                image = loadImage(file.getAbsolutePath());
                fileChosen(result, dest_mode);
              }*/
            }
            else
            {
              file_chosen(null);
            }
            process_dialog_results();
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      }
    );
  }

  void file_chosen(String filename)
  {
    switch (dialog_type)
    {
      case Utils.GUI_DIALOG_TYPE_ODE:
        ode_file = filename;
        break;
      case Utils.GUI_DIALOG_TYPE_PROPERTY:
        property_file = filename;
        break;
    }
    dialog_status = Utils.GUI_DIALOG_STATUS_DONE;
  }

  void process_dialog_results()
  {
    switch (dialog_type)
    {
      case Utils.GUI_DIALOG_TYPE_ODE:
        if (ode_file != null)
        {
          String err_str = comp.load_ode_file(ode_file);
          if (err_str != null)
          {
            System.out.println("ERROR when loading ODE: "+err_str);
          }
          else
          {
            model_loaded = true;
            //computation_paused = true;
            compView.setComputation(comp);
            compMan.delete(comp);
            compMan.add(comp);
            //view2 = new ComputationView(comp.ode);
          }
        }
        break;
      case Utils.GUI_DIALOG_TYPE_PROPERTY:
        if (property_file != null)
        {
          String err_str = comp.load_property_file(property_file);
          if (err_str != null)
          {
            System.out.println("ERROR when loading property: "+err_str);
          }
          else
          {
            //computation_paused = true;
          }
        }
        break;
    }
    dialog_status = Utils.GUI_DIALOG_STATUS_NONE;
    dialog_type = Utils.GUI_DIALOG_TYPE_NONE;
  }

}
