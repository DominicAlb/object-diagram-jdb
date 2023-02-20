package j2d_package;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.io.FileNotFoundException;
import java.io.FileWriter;



import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/*
 * made by Dominic
 * Creates XML Files from object schemes
 * Can read those and turn them into a html graph or an dot graph
 * Here: html
 * 
 */

public class XMLHandler {

    public DocumentBuilderFactory factory;
    public DocumentBuilder builder;
    public Document doc;
    public String className;
    public String dir;

    public XMLHandler(String dir, String fileName) {
        this.dir = dir;
        this.className = fileName;
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
    
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    // takes the info out of the schemes in the xml file and gives them to the GraphCreator
    public void createGraphFromXML() throws SAXException, IOException {
        HashMap<String, List<String>> connections; 
        HashMap<String, String> nameByID;
        GraphCreator gc = new GraphCreator();
        File xmlDoc = new File(dir + className + ".xml");
        Document d = builder.parse(xmlDoc);
        d.getDocumentElement().normalize();

        // steps in code
        NodeList stepList = d.getElementsByTagName("step");
        String[] stepsHTML = new String[stepList.getLength()];
        for (int index1 = 0; index1 < stepList.getLength(); index1++) {
            connections = new HashMap<>(30);
            nameByID = new HashMap<>(15);
            nameByID.put("null", "null");
            gc.createHTMLNode("null");

            Node step = stepList.item(index1);
            if (step.getNodeType() == Node.ELEMENT_NODE) {
                // Element stepElement = (Element) step;

                // known Objects at that step
                NodeList objectList = step.getChildNodes();

                // assign object ids to their object names
                for (int index2 = 0; index2 < objectList.getLength(); index2++) {
                    Node object = objectList.item(index2);
                    if (object.getNodeType() == Node.ELEMENT_NODE) {
                        Element objectElement = (Element) object;
                        String objName = objectElement.getAttribute("name");
                        if (objectElement.hasAttribute("id")) {
                            String objectID = objectElement.getAttribute("id");
                            nameByID.put(objectID, objName);
                        }
                    }
                }

                // loop and create objects with connections
                for (int index2 = 0; index2 < objectList.getLength(); index2++) {
                    Node object = objectList.item(index2);

                    if (object.getNodeType() == Node.ELEMENT_NODE) {
                        Element objectElement = (Element) object;
                        // get general Object Info everything has == name and type
                        String objName = objectElement.getAttribute("name");
                        String objType = objectElement.getAttribute("type");
                        List<String> objectMethods = new ArrayList<String>();
                        // if true -> complex object and not primitive var
                        if (objectElement.hasAttribute("id")) {
                            // get object specific info == anything but name and type
                            String objectID = objectElement.getAttribute("id");
                            // vars of that object
                            NodeList attributeList = object.getChildNodes();

                            Stack<String> attrIDs = new Stack<>();
                            Stack<String> attrTypes = new Stack<>();
                            Stack<String> attrValues = new Stack<>();

                            for (int index3 = 0; index3 < attributeList.getLength(); index3++) {
                                Node attribute = attributeList.item(index3);
                                if (attribute.getNodeType() == Node.ELEMENT_NODE) {
                                    Element attributeElement = (Element) attribute;

                                    if (attributeElement.hasAttribute("type")) {
                                        // get var info
                                        String attrID = attributeElement.getAttribute("id");
                                        String attrType = attributeElement.getAttribute("type");
                                        String attrValue = attributeElement.getTextContent();
                                        attrIDs.push(attrID);
                                        attrTypes.push(attrType);
                                        attrValues.push(attrValue);
                                        if (connections.get(objName) == null) {
                                            if (!isPrimitive(attrType))
                                                if (attrValue.contains("[")) {
                                                    String[] ids = attrValue.replace("[", "").replace("]", "")
                                                            .split(" ");
                                                    for (String id : ids) {
                                                        String o = nameByID.get(id.trim());
                                                        // check if null because of loop
                                                        if (o != null)
                                                            if (connections.get(objName) == null) {
                                                                List<String> l = new ArrayList<String>();
                                                                l.add(o);
                                                                connections.put(objName, l);
                                                            } else {
                                                                List<String> l = connections.get(objName);
                                                                if (!l.contains(o))
                                                                    l.add(o);
                                                                connections.put(objName, l);
                                                            }
                                                    }
                                                } else {
                                                    List<String> l = new ArrayList<String>();
                                                    if (nameByID.get(attrValue) != null)
                                                        l.add(nameByID.get(attrValue));
                                                    connections.put(objName, l);
                                                }
                                        } else {
                                            if (!isPrimitive(attrType))
                                                if (attrValue.contains("[")) {
                                                    String[] ids = attrValue.replace("[", "").replace("]", "")
                                                            .split(" ");
                                                    for (String id : ids) {
                                                        String o = nameByID.get(id.trim());
                                                        if (o != null) {
                                                            List<String> l = connections.get(objName);
                                                            if (!l.contains(o))
                                                                l.add(o);
                                                            connections.put(objName, l);
                                                        }
                                                    }
                                                } else {
                                                    List<String> l = connections.get(objName);
                                                    if (nameByID.get(attrValue) != null)
                                                        if (!l.contains(nameByID.get(attrValue)))
                                                            l.add(nameByID.get(attrValue));
                                                    connections.put(objName, l);
                                                }
                                        }
                                    } else if (attributeElement.hasAttribute("args")) {
                                        String methodID = attributeElement.getAttribute("id");
                                        String methodArgs = attributeElement.getAttribute("args");
                                        String methodRetType = attributeElement.getTextContent();

                                        objectMethods.add(methodID + "(" + methodArgs.replace("°", ", ") + ")" + " : " + methodRetType);
                                    }

                                }
                            }
                            String[] methodArray = new String[objectMethods.size()];
                            for (int i = 0; i < objectMethods.size(); i++) {
                                methodArray[i] = objectMethods.get(i);
                            }
                            gc.createHTMLNode(objName + "Node");
                            gc.createHTMLObject(objName, objectID, objType, attrIDs, attrTypes, attrValues,
                                    methodArray);
                            gc.connectHTMLNodeToObject(objName + "Node", objName);
                        } else { // -> primitive var
                            // get primitive var specific info == only value
                            String objValue = objectElement.getTextContent();
                            gc.createHTMLNode(objName + "Node");
                            gc.createPrimitiveHTMLObject(objName, objType, objValue);
                            gc.connectHTMLNodeToObject(objName + "Node", objName);
                        }
                    }

                }
            }
            // connect the nodes referring to eachother
            for (String key : connections.keySet()) {
                List<String> ends = connections.get(key);
                for (String s : ends) {
                    if (s != null)
                        if (connections.get(s.trim()) != null && connections.get(s.trim()).contains(key)) {
                            connections.get(s.trim()).remove(key);
                            gc.connectHTMLObjectToObjects("b", key, s.trim());
                        } else
                            gc.connectHTMLObjectToObjects("f", key, s.trim());
                }

            }

            stepsHTML[index1] = gc.getFinishedHTMLStep(index1);
        }

        // write the final html file
        //part 1-3 because the strings are too big
        FileWriter fw = new FileWriter(new File(dir + "ObjectDiagram.html"));
        fw.write(gc.getFinishedHTMLStringPart1());
        fw.flush();
        fw.write(gc.getFinishedHTMLStringPart2(stepsHTML));
        fw.flush();
        fw.write(gc.getFinishedHTMLStringPart3());
        fw.flush();
    }
    
    public void createDotGraphFromXML() throws SAXException, IOException {
        HashMap<String, List<String>> connections; 
        HashMap<String, String> nameByID;
        GraphCreator gc = new GraphCreator();
        File xmlDoc = new File(dir + className + ".xml");
        Document d = builder.parse(xmlDoc);
        d.getDocumentElement().normalize();

        // steps in code
        NodeList stepList = d.getElementsByTagName("step");
        String[] stepsDot = new String[stepList.getLength()];
        for (int index1 = 0; index1 < stepList.getLength(); index1++) {
            connections = new HashMap<>(30);
            nameByID = new HashMap<>(15);
            nameByID.put("null", "null");
            gc.createDotNode("null");

            Node step = stepList.item(index1);
            if (step.getNodeType() == Node.ELEMENT_NODE) {
                // Element stepElement = (Element) step;

                // known Objects at that step
                NodeList objectList = step.getChildNodes();

                // assign object ids to their object names
                for (int index2 = 0; index2 < objectList.getLength(); index2++) {
                    Node object = objectList.item(index2);
                    if (object.getNodeType() == Node.ELEMENT_NODE) {
                        Element objectElement = (Element) object;
                        String objName = objectElement.getAttribute("name");
                        if (objectElement.hasAttribute("id")) {
                            String objectID = objectElement.getAttribute("id");
                            nameByID.put(objectID, objName);
                        }
                    }
                }

                // loop and create objects with connections
                for (int index2 = 0; index2 < objectList.getLength(); index2++) {
                    Node object = objectList.item(index2);

                    if (object.getNodeType() == Node.ELEMENT_NODE) {
                        Element objectElement = (Element) object;
                        // get general Object Info everything has == name and type
                        String objName = objectElement.getAttribute("name");
                        String objType = objectElement.getAttribute("type");
                        List<String> objectMethods = new ArrayList<String>();
                        // if true -> complex object and not primitive var
                        if (objectElement.hasAttribute("id")) {
                            // get object specific info == anything but name and type
                            String objectID = objectElement.getAttribute("id");
                            // vars of that object
                            NodeList attributeList = object.getChildNodes();

                            Stack<String> attrIDs = new Stack<>();
                            Stack<String> attrTypes = new Stack<>();
                            Stack<String> attrValues = new Stack<>();

                            for (int index3 = 0; index3 < attributeList.getLength(); index3++) {
                                Node attribute = attributeList.item(index3);
                                if (attribute.getNodeType() == Node.ELEMENT_NODE) {
                                    Element attributeElement = (Element) attribute;

                                    if (attributeElement.hasAttribute("type")) {
                                        // get var info
                                        String attrID = attributeElement.getAttribute("id");
                                        String attrType = attributeElement.getAttribute("type");
                                        String attrValue = attributeElement.getTextContent();
                                        attrIDs.push(attrID);
                                        attrTypes.push(attrType);
                                        attrValues.push(attrValue);
                                        if (connections.get(objName) == null) {
                                            if (!isPrimitive(attrType))
                                                if (attrValue.contains("[")) {
                                                    String[] ids = attrValue.replace("[", "").replace("]", "")
                                                            .split(" ");
                                                    for (String id : ids) {
                                                        String o = nameByID.get(id.trim());
                                                        // check if null because of loop
                                                        if (o != null)
                                                            if (connections.get(objName) == null) {
                                                                List<String> l = new ArrayList<String>();
                                                                l.add(o);
                                                                connections.put(objName, l);
                                                            } else {
                                                                List<String> l = connections.get(objName);
                                                                if (!l.contains(o))
                                                                    l.add(o);
                                                                connections.put(objName, l);
                                                            }
                                                    }
                                                } else {
                                                    List<String> l = new ArrayList<String>();
                                                    if (nameByID.get(attrValue) != null)
                                                        l.add(nameByID.get(attrValue));
                                                    connections.put(objName, l);
                                                }
                                        } else {
                                            if (!isPrimitive(attrType))
                                                if (attrValue.contains("[")) {
                                                    String[] ids = attrValue.replace("[", "").replace("]", "")
                                                            .split(" ");
                                                    for (String id : ids) {
                                                        String o = nameByID.get(id.trim());
                                                        if (o != null) {
                                                            List<String> l = connections.get(objName);
                                                            if (!l.contains(o))
                                                                l.add(o);
                                                            connections.put(objName, l);
                                                        }
                                                    }
                                                } else {
                                                    List<String> l = connections.get(objName);
                                                    if (nameByID.get(attrValue) != null)
                                                        if (!l.contains(nameByID.get(attrValue)))
                                                            l.add(nameByID.get(attrValue));
                                                    connections.put(objName, l);
                                                }
                                        }
                                    } else if (attributeElement.hasAttribute("args")) {
                                        String methodID = attributeElement.getAttribute("id");
                                        String methodArgs = attributeElement.getAttribute("args");
                                        String methodRetType = attributeElement.getTextContent();

                                        objectMethods.add(methodID + "(" + methodArgs.replace("°", ", ") + ")" + " : " + methodRetType);
                                    }

                                }
                            }
                            String[] methodArray = new String[objectMethods.size()];
                            for (int i = 0; i < objectMethods.size(); i++) {
                                methodArray[i] = objectMethods.get(i);
                            }
                            gc.createDotNode(objName + "Node");
                            gc.createDotObject(objName, objectID, objType, attrIDs, attrTypes, attrValues,
                                    methodArray);
                            gc.connectDotNodeToObject(objName + "Node", objName);
                        } else { // -> primitive var
                            // get primitive var specific info == only value
                            String objValue = objectElement.getTextContent();
                            gc.createDotNode(objName + "Node");
                            gc.createPrimitiveDotObject(objName, objType, objValue);
                            gc.connectDotNodeToObject(objName + "Node", objName);
                        }
                    }

                }
            }
            // connect the nodes referring to eachother
            for (String key : connections.keySet()) {
                List<String> ends = connections.get(key);
                for (String s : ends) {
                    if (s != null)
                        if (connections.get(s.trim()) != null && connections.get(s.trim()).contains(key)) {
                            connections.get(s.trim()).remove(key);
                            gc.connectDotObjectToObjects("b", key, s.trim());
                        } else
                            gc.connectDotObjectToObjects("f", key, s.trim());
                }

            }

            stepsDot[index1] = gc.getFinishedDotStep(index1);
        }

        // put the dot graphs in a txt file
        //
        // write the final html file
        //part 1-3 because the strings are too big
        FileWriter fw = new FileWriter(new File(dir + "ObjectDiagram.txt"));
        for(int i = 0; i < stepsDot.length; i++) {
            fw.write(stepsDot[i]);
            fw.flush();
        }
        fw.close(); 
    }

    // builds a xml file based of the previous created schemes, which are now getting filled
    // for an example looak at test.xml
    public void writeXML(List<List<String>> xmlInfo)
            throws TransformerException, ParserConfigurationException, FileNotFoundException, IOException {
        Element rootElement = doc.createElement("code");
        for (int i = 0; i < xmlInfo.size(); i++) {
            List<String> objectList = xmlInfo.get(i);
            Element stepElement = doc.createElement("step");
            stepElement.setAttribute("id", "" + i);
            /*
             * Example object:
             * "name=obj1 , id=5a07e868 , type=Figur , attr={id:liebt-type:Figur-value:null;id:kennt-type:Figur-value:null;id:alter-type:int-value:0;}"
             * "name=i , type=int , value=1"
             */
            for (int j = 0; j < objectList.size(); j++) {
                String data = objectList.get(j);
                String[] temp = data.split(",");
                String[][] details = new String[temp.length][2];
                for (int k = 0; k < temp.length; k++) {
                    details[k] = temp[k].split("=");
                }

                Element objectElement = doc.createElement("object");

                if (details.length > 4) {
                    objectElement.setAttribute("name", details[0][1]);
                    objectElement.setAttribute("id", details[1][1]);
                    objectElement.setAttribute("type", details[2][1]);

                    String[] attrList = details[3][1].replace("{", "").replace("}", "").split(";");
                    for (String a : attrList) {
                        String[] attrTemp = a.split("-");
                        String[][] attrDetails = new String[attrTemp.length][2];
                        for (int l = 0; l < attrTemp.length; l++) {
                            attrDetails[l] = attrTemp[l].split(":");
                        }
                        Element attrElement = doc.createElement("attr");
                        attrElement.setAttribute("id", attrDetails[0][1]);
                        attrElement.setAttribute("type", attrDetails[1][1]);
                        Text attrValue;
                        if (attrDetails[2][1].contains(".")) {
                            attrValue = doc.createTextNode(attrDetails[2][1].replace(".", " "));
                        } else {
                            attrValue = doc.createTextNode(attrDetails[2][1]);
                        }
                        attrElement.appendChild(attrValue);
                        objectElement.appendChild(attrElement);
                    }
                    String[] methodList = details[4][1].replace("{", "").replace("}", "").split(";");
                    for (String a : methodList) {
                        String[] methodTemp = a.split("-");
                        String[][] methodDetails = new String[methodTemp.length][2];
                        for (int l = 0; l < methodTemp.length; l++) {
                            methodDetails[l] = methodTemp[l].split(":");
                        }
                        Element methodElement = doc.createElement("method");
                        methodElement.setAttribute("id", methodDetails[0][1]);
                        if (methodDetails[2].length > 1)
                            methodElement.setAttribute("args", methodDetails[2][1]);
                        else
                            methodElement.setAttribute("args", "");
                        Text methodValue = doc.createTextNode(methodDetails[1][1]);
                        methodElement.appendChild(methodValue);
                        objectElement.appendChild(methodElement);
                    }

                } else {
                    objectElement.setAttribute("name", details[0][1]);
                    objectElement.setAttribute("type", details[1][1]); // details[2][1]
                    Text value = doc.createTextNode(details[2][1]);
                    objectElement.appendChild(value);
                }

                stepElement.appendChild(objectElement);
            }
            rootElement.appendChild(stepElement);
        }

        doc.appendChild(rootElement);

        DOMSource source = new DOMSource(doc);

        File f = new File(dir + className + ".xml");
        System.out.println(dir + className + ".xml");
        Result result = new StreamResult(f);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);

    }
    
    public void delXMLFile() {
        File f = new File(dir + className + ".xml");
        if(f.exists()) {
            f.delete();
        }
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

}
