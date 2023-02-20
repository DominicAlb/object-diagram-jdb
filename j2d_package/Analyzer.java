package j2d_package;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/*
 * 
 * made by DominicAlb
 * 
 * Analyzes the class composition and creates schemes for the object information
 * which you can use for example to create a history diagram or an object diagram
 * 
 */
public class Analyzer {

    BufferedReader br;
    HashMap<String, String> classVars;
    HashMap<String, String> primitiveVars;
    HashMap<String, String> methods;
    String main_file_name;
    String dir;

    public Analyzer(String dir, String main_file_name) {
        this.dir = dir;
        this.main_file_name = main_file_name;
        classVars = new HashMap<String, String>();
        primitiveVars = new HashMap<String, String>();
        methods = new HashMap<String, String>();
        try {
            br = new BufferedReader(new FileReader(new File(dir + main_file_name + ".java")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // creates schemes for every var type used
    public HashMap<String, String> getSchemesFromFile() throws IOException {
        HashMap<String, String> schemes = new HashMap<String, String>();

        //gets the currently known vars
        getInitiatedVars(main_file_name);

        for (String key : primitiveVars.keySet()) {
            schemes.put(key, createPrimitiveVarScheme(key, primitiveVars.get(key)));
        }
        for (String key : classVars.keySet()) {
            schemes.put(key, createClassScheme(key, classVars.get(key), getClassAttributes(classVars.get(key))));
        }

        return schemes;
    }

    // gets the info of a complex object by analizing its local java file in the package
    public HashMap<String, String> getClassAttributes(String class_type) {

        HashMap<String, String> attributes = new HashMap<String, String>();
        String m = ",methods={";
        try {
            File f = new File(getClassLocation(class_type));
            BufferedReader bReader = new BufferedReader(new FileReader(f));
            String line;
            String[] parts;
            boolean inClass = false;
            int braces = 0;

            // read though each line of code
            while ((line = bReader.readLine()) != null) {
                char isVisible = '+';
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '{') {
                        braces++;
                    }
                    if (line.charAt(i) == '}') {
                        braces--;
                    }
                }

                if (inClass && braces == 0) {
                    inClass = false;
                }

                // select the symbol based of the accessibility
                if (line.trim().startsWith("private"))
                    isVisible = '-';
                if (line.trim().startsWith("protected"))
                    isVisible = '#';
                line = line.replace("public", "").replace("private", "").replace("protected", "").trim();
                parts = line.split(" ");

                if (parts == null) {
                    continue;
                }
                if (parts.length < 2) {
                    continue;
                }
                if (parts[0] == null || parts[1] == null) {
                    continue;
                }

                if (parts[0].equals("class") && parts[1].equals(class_type)) {
                    inClass = true;
                }
                if (inClass) {
                    //braces == 1 -> currently in class but not in a method
                    if (braces == 1) {
                        if (Character.isUpperCase(line.charAt(0))) {
                            // check if it is a method else it is a complex var because of uppercase char
                            if (!parts[1].contains("("))
                                //put var into attributes list
                                attributes.put(parts[1].replace("=", "").replace(";", ""), isVisible + " " + parts[0]);
                            else if (parts[1].contains("(")) {
                                // analyze the method and create the part of a scheme
                                String retType, name, args;
                                name = parts[1].split("\\(")[0].trim();
                                retType = parts[0];
                                if (line.contains("()"))
                                    args = "";
                                else
                                    args = parts[1].split("\\(")[1] + " " + parts[2].split("\\)")[0];
                                m = m + "mname:" + isVisible + " " + name + "-mtype:" + retType + "-margs:" + args
                                        + ";";
                            }
                        // if lower case char check if it a primitive var
                        } else if (isPrimitive(parts[0])) {
                            if (!parts[1].contains("("))
                                attributes.put(parts[1].replace("=", "").replace(";", ""), isVisible + " " + parts[0]);
                        // because the public/private/protected is already processed the line can start with void, which is neither a complex var type nor a primitive type
                        } else if (line.startsWith("void")) {
                            String retType, name, args;
                            String[] mparts = parts[1].split("\\(");
                            name = mparts[0].trim();
                            retType = parts[0];
                            if (line.contains("()"))
                                args = "";
                            else
                                args = parts[1].split("\\(")[1] + " " + parts[2].split("\\)")[0];
                            m = m + "mname:" + isVisible + " " + name + "-mtype:" + retType + "-margs:" + args + ";";
                        }
                    //braces == 2 -> currently in a method
                    } else if (braces == 2) {
                        if (Character.isUpperCase(line.charAt(0))) {
                            if (parts[1].contains("(")) {
                                String retType, name, args;
                                String[] mparts = parts[1].split("\\(");
                                name = mparts[0].trim();
                                retType = parts[0];
                                if (line.contains("()"))
                                    args = "";
                                else
                                    args = parts[1].split("\\(")[1] + " " + parts[2].split("\\)")[0];
                                m = m + "mname:" + isVisible + " " + name + "-mtype:" + retType + "-margs:" + args
                                        + ";";
                            }
                        } else if (line.startsWith("void")) {
                            String retType, name, args;
                            String[] mparts = parts[1].split("\\(");
                            name = mparts[0].trim();
                            retType = parts[0];
                            if (line.contains("()"))
                                args = "";
                            else
                                args = parts[1].split("\\(")[1] + " " + parts[2].split("\\)")[0];
                            m = m + "mname:" + isVisible + " " + name + "-mtype:" + retType + "-margs:" + args
                                    + ";";
                        }
                    }
                }
            }

            bReader.close();
            methods.put(class_type, m + "}");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return attributes;
    }

    // returns the full class path
    public String getClassLocation(String class_name) {
        String location = dir + class_name + ".java";
        File f = new File(location);
        if (f.exists() && !f.isDirectory()) {
            return location;
        } else {
            return dir + main_file_name + ".java";
        }
    }

    // takes the data and creates a scheme of a complex object
    //structure: "name=*STRING*,id=*STRING*,type=*STRING*,attr={id:*STRING*-type:*STRING*-value:{*STRING*}},methods={mname:*STRING*-mtype:*STRING*-margs:*STRING*}"
    //splitable because of different divider eg.: attributes divided by '-' and assigned by ':' 
    public String createClassScheme(String class_name, String class_type, HashMap<String, String> idType) {
        String attr = "";
        int index = 1;
        for (String key : idType.keySet()) {
            attr = attr + "id:" + key + "-type:" + idType.get(key) + "-value:{" + index + "};";
            index++;
        }
        String ret = "name=" + class_name + ",id={0},type=" + class_type + ",attr={" + attr + "}"
                + methods.get(class_type);

        return ret;
    }

    // simple primitive var scheme structure: "name=*STRING*,type=*STRING*,value={0}""
    public String createPrimitiveVarScheme(String var_name, String var_type) {
        return "name=" + var_name + ",type=" + var_type + ",value={0}";
    }

    //get the currently initiated vars of the file
    public void getInitiatedVars(String main_class) {
        try {
            String line;
            String[] parts;
            boolean inClass = false, inMain = false;
            int braces = 0;

            //read through each line of the file and get each var from main()
            while ((line = br.readLine()) != null) {
                // keep track of the opened braces, if amount = 2 -> in a method, if amount = 1 -> in a class
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '{') {
                        braces++;
                    }
                    if (line.charAt(i) == '}') {
                        braces--;
                    }
                }
                if (inClass && braces == 0) {
                    inClass = false;
                }
                if (inClass && braces <= 1) {
                    inMain = false;
                }
                if (line.contains("public static void main"))
                    inMain = true;
                line = line.replace("public", "").replace("private", "").replace("protected", "").trim();
                parts = line.split(" ");

                if (parts == null) {
                    continue;
                }
                if (parts.length < 2) {
                    continue;
                }
                if (parts[0] == null || parts[1] == null) {
                    continue;
                }

                if (parts[0].equals("class") && parts[1].equals(main_class)) {
                    inClass = true;
                }

                if (inClass && inMain) {
                    if (Character.isUpperCase(line.charAt(0))) {
                        if (parts[0].equals("String")) {
                            primitiveVars.put(parts[1].replace("=", "").replace(";", ""), parts[0]);
                        } else {
                            classVars.put(parts[1].replace("=", "").replace(";", ""), parts[0]);
                        }

                    } else if (isPrimitive(parts[0])) {
                        primitiveVars.put(parts[1].replace("=", "").replace(";", ""), parts[0]);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // getter/setter etc:

    public HashMap<String, String> getClassVars() {
        return this.classVars;
    }

    public HashMap<String, String> getPrimitiveVars() {
        return this.primitiveVars;
    }

    private boolean isPrimitive(String s) {
        switch (s) {
            case "int":
                return true;
            case "byte":
                return true;
            case "short":
                return true;
            case "long":
                return true;
            case "float":
                return true;
            case "double":
                return true;
            case "boolean":
                return true;
            case "char":
                return true;
            default:
                return false;
        }
    }

    public void setDir(String new_dir) {
        this.dir = new_dir;
    }

    public void setMainFile(String mainFile) {
        this.main_file_name = mainFile;
    }

    public String getDir() {
        return this.dir;
    }

    public String getMainFile() {
        return this.main_file_name;
    }

}
