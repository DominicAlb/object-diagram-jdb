package j2d_package;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.lang.ProcessBuilder;

public class j2dot {

  PrintStream m_jdb_in;
  static String m_mainclass;
  static String dir;
  static String pack;
  Process p_jdb;
  Process p_java;
  ProcessBuilder proc_java;
  ProcessBuilder proc_jdb;
  private boolean isFinished = false;
  XMLHandler xmlHandler;
  List<List<String>> xmlInfo;
  Analyzer analyzer;
  HashMap<String, String> schemes;
  InputStream outputstream;
  static boolean createHTML;
  static boolean createDot;
  static boolean delXML;
  static boolean loggingIsEnabled;
  int main_class_lines;

  /*
   * made by DominicAlb
   *
   * Analyzes a java programm and creates a fitting xml diagram with the object
   * composition
   */

  j2dot() {
    try {
      ProcessBuilder compJavaFile;
      // compile target File
      if (pack != null) {
        compJavaFile = new ProcessBuilder("cmd.exe", "/c", "cd", dir , "&", "javac", "-g",
            pack + "\\" + m_mainclass + ".java");
      } else {
        compJavaFile = new ProcessBuilder("cmd.exe", "/c", "cd", dir, "&", "javac", "-g",
            m_mainclass + ".java");
      }

      compJavaFile.redirectErrorStream(true);
      Process compProcess = compJavaFile.start();
      compProcess.destroy();
      compJavaFile = null;

      if (pack != null) {
        // build java process (Windows)
        proc_java = new ProcessBuilder("cmd.exe", "/c", "cd", dir , "&", "java",
            "-agentlib:jdwp=transport=dt_socket,address=localhost:8888,server=y,suspend=y", pack + "." + m_mainclass);
      } else {
        // build java process (Windows)
        proc_java = new ProcessBuilder("cmd.exe", "/c", "cd", dir , "&","java",
            "-agentlib:jdwp=transport=dt_socket,address=localhost:8888,server=y,suspend=y", m_mainclass);
      }
      // build jdb process added to java process (Windows)
      proc_jdb = new ProcessBuilder("jdb", "-connect", "com.sun.jdi.SocketAttach:hostname=localhost,port=8888");
      proc_jdb.redirectErrorStream(true);

      /*
       * build java process (Linux)
       * ProcessBuilder proc_java = new ProcessBuilder("java","-Xdebug",
       * "-Xrunjdwp:transport=dt_socket,server=y,address=localhost:8080", "Figur" )
       * 
       * build jdb process added to java process (Linux)
       * ProcessBuilder proc_jdb = new
       * ProcessBuilder("jdb","-attach","localhost:8080");
       */
      if (pack != null) {
        proc_java.directory(new File(dir + pack + "\\"));
        proc_jdb.directory(new File(dir + pack + "\\"));
        // System.out.println(dir + pack + "\\");
      } else {
        proc_java.directory(new File(dir));
        proc_jdb.directory(new File(dir));
        // System.out.println(dir);
      }

      // start both processes
      p_java = proc_java.start(); // Process p_java = proc_java.start();
      p_jdb = proc_jdb.start();

      // to read jdb output
      outputstream = p_jdb.getInputStream();

      // PrintStream
      // to give jdb input commands
      m_jdb_in = new PrintStream(p_jdb.getOutputStream());
      if (pack != null) {
        xmlHandler = new XMLHandler(dir + pack + "\\", "ObjectState");
        analyzer = new Analyzer(dir + pack + "\\", m_mainclass);
      } else {
        xmlHandler = new XMLHandler(dir, "ObjectState");
        analyzer = new Analyzer(dir, m_mainclass);
      }
      xmlInfo = new ArrayList<List<String>>();
      if(loggingIsEnabled) System.out.println("[Info] Scan file for variables");
      schemes = analyzer.getSchemesFromFile();
      main_class_lines = analyzer.getLinesAmount()-1;
    } catch (Exception e) {
      e.printStackTrace();

    }
  }

  public static void main(String[] args) {

    JTextField xField = new JTextField(10);
    JTextField pField = new JTextField(10);
    JTextField yField = new JTextField(30);
    JCheckBox htmlBox = new JCheckBox("HTML", true); 
    JCheckBox dotBox = new JCheckBox("DOT"); 
    JCheckBox delXMLBox = new JCheckBox("Delete XML file after process"); 
    JCheckBox logBox = new JCheckBox("Log into console"); 
    
    JPanel myPanel = new JPanel();
    myPanel.add(new JLabel("class:"));
    myPanel.add(xField);
    myPanel.add(Box.createHorizontalStrut(15));
    myPanel.add(new JLabel("package:"));
    myPanel.add(pField);
    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
    myPanel.add(new JLabel("dir:"));
    myPanel.add(yField);
    myPanel.add(htmlBox);
    myPanel.add(dotBox);
    myPanel.add(delXMLBox);
    myPanel.add(logBox);

    int result = JOptionPane.showConfirmDialog(null, myPanel,
        "Please Enter class and path - eg: class: Test  package: <Empty>  dir: C:\\  or class: Test package: Test   dir: C:\\",
        JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      
      if (htmlBox.isSelected()) createHTML = true;
      else createHTML = false;
      if (dotBox.isSelected()) createDot = true;
      else createDot = false;

      if(!createHTML && !createDot) {
        System.out.println("[Error] Atleast one setting has to be enabled");
        return;
      }

      if (delXMLBox.isSelected()) delXML = true;
      else delXML = false;

      if (logBox.isSelected()) loggingIsEnabled = true;
      else loggingIsEnabled = false;
      
      m_mainclass = xField.getText().trim();
      dir = yField.getText().trim();
      if (!pField.getText().matches(" +"))
        pack = pField.getText().trim();
      else
        pack = null;
      dir = dir.replace("\\", File.separator) + File.separator;
      j2dot j = new j2dot();

      if(loggingIsEnabled) System.out.println("[Info] Start Process");
      j.run();
    }

  }

  void run() {
    try {
      if(loggingIsEnabled) System.out.println("[Info] Set break point");
      // set break point in main classes main()-method
      if (pack != null) {
        jdb_exc("stop in " + pack + "." + m_mainclass + ".main");
      } else {
        jdb_exc("stop in " + m_mainclass + ".main");
      }
      jdb_exc("run");
      //skip first loop because nothing is loaded yet
      jdb_exc("next");

      if(loggingIsEnabled) System.out.println("[Info] Getting Data from file (1/" + main_class_lines+")");

      jdb_exc("thread 1");

      int loops = 1;
      while (true) {
        
        // prints here: BREAK by main
        String jdb_response = jdb_exc("next");

        // if finished -> print it
        if ((jdb_response) == null) {
          System.out.println("BREAK by exception");
          isFinished = true;
        }
        if ((jdb_response).contains("application exited")) {
          isFinished = true;
        }
        if (isFinished) {
          clean_up();
          break;
        }
        String data = jdb_exc("locals");

        processData(data);
        if(loggingIsEnabled) System.out.println("[Info] Getting Data from file (" + loops + "/" + main_class_lines+")");

      }
      if (loggingIsEnabled) System.out.println("[Info] Write to XML");
      xmlHandler.writeXML(xmlInfo);

      if(createHTML) {
        if (loggingIsEnabled) System.out.println("[Info] Creating HTML file");
        xmlHandler.createHTMLGraphFromXML();
      }
      
      if(createDot) {
        if (loggingIsEnabled) System.out.println("[Info] Creating Dot file");
        xmlHandler.createDotGraphFromXML();
      }
 
      if(delXML) {
        if (loggingIsEnabled) System.out.println("[Info] Deleting XML file");
        xmlHandler.delXMLFile();
      }else {
        if (loggingIsEnabled) {
          String dir = xmlHandler.getXMLDir();
          System.out.println("[Info] XML is saved at <" + dir + ">");
        }
      }

      if (loggingIsEnabled)
        System.out.println("[Info] Process finished");
      
    } catch (Exception e) {
    }
  }

  // give jdb commands
  // for info:
  // https://docs.oracle.com/javase/7/docs/technotes/tools/windows/jdb.html
  String jdb_exc(String p_cmd) throws InterruptedException, IOException {
    m_jdb_in.println(p_cmd);
    m_jdb_in.flush();

    String data = jdb_getResponse();
    return data;
  }

  void clean_up() {
    try {
      p_jdb.destroy();
      p_java.destroy();
      m_jdb_in.close();
      proc_java = null;
      proc_jdb = null;
    } catch (Exception e) {
    }
  }

  // prints response and returns if sort of check is needed
  private String jdb_getResponse() throws InterruptedException, IOException {
    Thread.sleep(50);
    String response = jdb_read();
    return response;
  }

  private String jdb_read() throws IOException{
    byte[] buffer = new byte[100000];
    int bytesRead;
    String ret = "";
    while (outputstream.available() > 0) {
        bytesRead = outputstream.read(buffer);
        if (bytesRead > 0) {
            String s = new String(buffer, 0, bytesRead);
            ret += s;
        }
    }
    return ret;
  }
  
  // filters the data out of the jdb response
  private void processData(String data) throws InterruptedException, IOException {
    List<String> dataInfo = new ArrayList<String>();
    List<String> knownVars = getKnownVarsFromData(data);
    String s;
    for (String var : knownVars) {
      String scheme = schemes.get(var);
      String objName = var;
      if (isPrimitiveScheme(scheme)) {
        String value = getPrimitiveValue(objName);
        s = scheme.replace("{0}", value.replace("\nmain[1]", "").trim());
        dataInfo.add(s);
      } else {
        String id = getObjectId(objName);
        HashMap<String, String> attrVs = getAttrValues(objName.trim());
        s = scheme.replace("{0}", id);
        String[] ids = getAttributeListInOrder(s);
        for (int i = 0; i < ids.length; i++) {
          String key = ids[i];
          s = s.replace("{" + (int)(i+1) + "}", attrVs.get(key));
        }
        
        dataInfo.add(s.trim());
      }
    }
    xmlInfo.add(dataInfo);
  }

  private List<String> getKnownVarsFromData(String data) {
    List<String> vars = new ArrayList<String>();
    String[] used = data.split(":");
    String[] lines = used[2].split("\n");
    for (String line : lines) {
      if (line.contains("=")) {
        if (line.trim().contains(" "))
          if (!line.contains("args"))
            vars.add(line.split("=")[0].trim());
      }
    }
    return vars;
  }

  private String[] getAttributeListInOrder(String scheme) {
    String[] parts = scheme.replace("}", "").split(",");
    String[] attrs = parts[3].split(";");
    String[] array = new String[attrs.length];
    int index = 0;
    for (String attr : attrs) {
      array[index] = attr.split("-")[0].split(":")[1];
      index++;
    }
    return array;
  }

  public String getNameFromScheme(String scheme) {
    String name = scheme.split(",")[0].split("=")[1];
    return name;
  }

  private boolean isPrimitiveScheme(String scheme) {
    if (scheme.contains("attr={")) {
      return false;
    } else {
      return true;
    }
  }

  private String getPrimitiveValue(String objName) throws InterruptedException, IOException {
    String resp = jdb_exc("dump " + objName);
    String[] temp = resp.split("=");
    return temp[1].trim();
  }


  private HashMap<String, String> getAttrValues(String objName) throws InterruptedException, IOException {
    HashMap<String, String> attrValues = new HashMap<String, String>();
    String resp = jdb_exc("dump " + objName);
    String[] temp2 = resp.split("\n");
    String[] attribute;
    for (String l : temp2) {
      l = l.trim();
      if (l.contains(":")) {
        attribute = l.split(":");
        if (attribute[1].contains("instance of")) {
          String attrObj;
          if(attribute[1].contains("[")) {
            attrObj = getObjectIdsFromArray(objName + "." + attribute[0]);
          }else {
            attrObj = getObjectId(objName + "." + attribute[0]);
          }
          
          attrValues.put(attribute[0].trim(), attrObj);
        } else {
          attrValues.put(attribute[0].trim(), attribute[1].trim());
        }
      }
    }
    return attrValues;
  }

  // get object adresses from array
  private String getObjectIdsFromArray(String objName) throws InterruptedException, IOException {
    String length = jdb_exc("print " + objName + ".length").split("=")[1].replace("main[1]", "").trim();
    int len = Integer.parseInt(length);
    String ret = "[";
    for(int i = 0; i < len; i++) {
      ret = ret + getObjectId(objName+"["+i+"]") + ".";
    }
    ret = ret.substring(0, ret.length()-1) + "]";
    return ret;
  }

  // get object adress
  private String getObjectId(String objName) throws InterruptedException, IOException {
    String[] idps = jdb_exc("eval " + objName).split("\"");
    String id = idps[1].split("@")[1];
    return id;
  }

}
