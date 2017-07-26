package test.slice.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AddObjectToList {
	
	public static String init_routine = null;
	
	public static int mem_megabytes = 1000;
	
	public static File observers = null;
	
	public static void main(String[] args) {
		AddObjectToList ao = new AddObjectToList();
		ao.remove_diff_checks("hello.txt");
	}
	
	public void remove_diff_checks (String seq_file) {
	    List<String> cmd = new ArrayList<String>();
	    cmd.add ("java");
	    cmd.add ("-ea");
	    // Add memory size
	    cmd.add (String.format ("-Xmx%dM", mem_megabytes));

	    cmd.add ("randoop.main.Main");
	    cmd.add ("rm-diff-obs");

	    // Add applicable arguments from this call
	    if (observers != null) {
	      cmd.add ("--observers=" + observers.toString());
	    }
	    if (init_routine != null)  {
	       cmd.add ("--init_routine=" + init_routine);
	    }
	    cmd.add (seq_file);
	    cmd.add (seq_file);
	    String[] cmd_array = new String[cmd.size()];
	    run_cmd(cmd_array, null, null);
	  }
	
	 public static void run_cmd (String[] cmd_args, String[] env_arr, File dir) {

	    int result = 0;
	    try {
	      Process p = java.lang.Runtime.getRuntime().exec (cmd_args, env_arr, dir);
	      StreamRedirectThread err_thread
	      = new StreamRedirectThread ("stderr", p.getErrorStream(), System.out);
	      StreamRedirectThread out_thread
	      = new StreamRedirectThread ("stdout", p.getInputStream(), System.out);
	      err_thread.start();
	      out_thread.start();
	      result = p.waitFor();
	      err_thread.join();
	      out_thread.join();
	    } catch (Exception e) {
	      throw new Error (String.format ("error running cmd '%s'",
	                                      Arrays.toString(cmd_args)), e);
	    }
	    if (result != 0)
	      throw new Error (String.format ("cmd '%s' returned status %d",
	                                      Arrays.toString(cmd_args), result));
	  }
}

class StreamRedirectThread extends Thread
{

  private final Reader in;
  private final Writer out;
  private final PrintStream outWriter;

  private static final int BUFFER_SIZE = 2048;
  // private static final int BUFFER_SIZE = 1;

  /**
   * Set up for copy.
   * @param name  Name of the thread
   * @param in    Stream to copy from
   * @param out   Stream to copy to
   */
  public StreamRedirectThread(String name, InputStream in, OutputStream out)
  {
    super(name);
    this.in = new InputStreamReader(in);
    this.out = new OutputStreamWriter(out);
    this.outWriter = new PrintStream(out);

    setPriority(Thread.MAX_PRIORITY - 1);
  }

  /**
   * Copy.
   */
  @Override
  public void run()
  {
    try
    {
      BufferedReader br = new BufferedReader(in, BUFFER_SIZE);

      String line = null;
      while ((line = br.readLine()) != null) {
        outWriter.println(line);
      }
      out.flush();
    }
    catch (IOException exc)
    {
      System.err.println("Child I/O Transfer - " + exc);
    }
  }
}