package j2d_package;

import java.util.Stack;

/*
 * made by Dominic
 * 
 * Finished
 * last update: 20.07.2022
 * 
 * Creates graphs in dot or html
 */

public class GraphCreator {

    private String dotGraph;
    private String dotConnections;
    private String dotObjects;

    private Stack<String> htmlAllStepConnections;
    private int connStepIndex;
    private String htmlConnections;
    private String htmlObjects;
    private int stepInt = 0;

    public GraphCreator() {
        this.dotConnections = "";
        this.dotObjects = "";

        this.htmlObjects = "";
        this.htmlConnections = "var connections0 = [";
        this.htmlAllStepConnections = new Stack<String>();
        this.connStepIndex = 1;

        this.dotGraph = """

                fontname = \"Bitstream Vera Sans\"
                fontsize = 8

                node [
                        fontname = \"Bitstream Vera Sans\"
                        fontsize = 8
                        shape = \"record\"
                ]

                edge [
                        fontname = \"Bitstream Vera Sans\"
                        fontsize = 8
                ]
                """;
    }

    /*
     * 
     * ///////////////////////////////////////////////////////
     * 
     *                  Dot (Graphviz)
     * 
     * ///////////////////////////////////////////////////////
     * 
     */

    // https://graphviz.org/doc/info/arrows.html
    // https://graphviz.org/doc/info/shapes.html

    public void resetDotGraph() {
        this.dotConnections = "";
        this.dotObjects = "";

        this.dotGraph = """

                fontname = \"Bitstream Vera Sans\"
                fontsize = 8

                node [
                        fontname = \"Bitstream Vera Sans\"
                        fontsize = 8
                        shape = \"record\"
                ]

                edge [
                        fontname = \"Bitstream Vera Sans\"
                        fontsize = 8
                ]
                """;
    }

    public String getFinishedDotGraph() {
        dotGraph = dotGraph + dotObjects + dotConnections;
        return "digraph G {\n" + dotGraph + "\n}";
    }

    public void createDotObject(String name, String id, String type, Stack<String> attributes,
            Stack<String> attributesTypes, Stack<String> attributesValues, String... methods) {
        String obj = name + " [\n\tlabel = \"{" + id + " : " + type + "|";
        String attrInfo = "";
        String methodInfo = "";
        while (!attributes.empty()) {
            attrInfo = attrInfo + attributesTypes.pop() + " " + attributes.pop() + " = " + attributesValues.pop()
                    + "\\l";
        }
        if (methods != null) {
            for (String m : methods) {
                methodInfo = methodInfo + m + "\\l";
            }
        }
        obj = obj + attrInfo + "|" + methodInfo + "\\l}\"";
        dotObjects = dotObjects + obj + "\n]\n";
    }

    public void createPrimitiveDotObject(String name, String type, String value) {
        String obj = name + " [\n\tlabel = \"{ : " + type + "|+value = " + value + "}\"\n]\n";
        dotObjects = dotObjects + obj;
    }

    public void createDotNode(String name) {
        dotObjects = dotObjects + name + " [\n\tshape = \"point\"\n]\n";
    }

    public void connectDotNodeToObject(String node, String objectID) {
        String s = "\t" + node + " -> " + objectID + " [label=\" " + node.replace("Node", "").trim()
                + "\" arrowhead=\"vee\" arrowsize=0.5 fontsize=\"12pt\"]\n";
        dotConnections = dotConnections + s;
    }

    public void connectDotObjectToObjects(String arrowType, String dir, String obj1, String... obj2) {
        String s = "\t" + obj1 + " -> {" + String.join(" ", obj2) + "} [arrowhead=\"" + arrowType + "\" dir=\"" + dir
                + "\"]\n";
        dotConnections = dotConnections + s;
    }

    /*
     * ///////////////////////////////////////////////////////
     * 
     *                         HTML
     * 
     * ///////////////////////////////////////////////////////
     */

    public void createHTMLNode(String name) {
        /*
         * <div id="dot" name="node1" class="wrapper" style="left:75%">
         * <header class="dot">mensch1</header>
         * </div>
         */
        String obj = "\t\t<div id=\"dot\" name=\"" + stepInt + "-" + name
                + "\" class=\"wrapper\">\n\t\t\t<header class=\"dot\">" + name.replace("Node", "")
                + "</header>\n\t\t</div>";
        htmlObjects = htmlObjects + obj + "\n";
    }

    public void createPrimitiveHTMLObject(String name, String type, String value) {
        String obj = "\t\t<div name=\"" + stepInt + "-" + name + "\" class=\"wrapper\">\n\t\t\t<header><u> : " + type
                + "</u></header>\n\t\t\t<div class=\"content\">\n\t\t\t\t<p class=\"arguments\">" + "value = " + value
                + "</p></div>\n\t\t\t<div class=\"splitter\"></div>\n\t\t</div>";
        htmlObjects = htmlObjects + obj;
    }

    public void createHTMLObject(String name, String id, String type, Stack<String> attributes,
            Stack<String> attributesTypes, Stack<String> attributesValues, String... methods) {
        String obj = "\t\t<div name=\"" + stepInt + "-" + name + "\" class=\"wrapper\">\n\t\t\t<header><u>" + id + " : "
                + type
                + "</u></header>\n\t\t\t<div class=\"content\">\n\t\t\t\t<p class=\"arguments\">";
        String attrInfo = "";
        String methodInfo = "";
        String[] attrInfos = new String[attributes.size()];
        int i = 0;
        while (!attributes.empty()) {
            attrInfos[i] = attributesTypes.pop() + " " + attributes.pop() + " = " + attributesValues.pop();
            i++;
        }
        attrInfo = String.join("</br>", attrInfos);
        methodInfo = String.join("</br>", methods);
        obj = obj + attrInfo
                + "</p>\n\t\t\t</div>\n\t\t\t<div class=\"splitter\"></div>\n\t\t\t<div class=\"content\">\n\t\t\t\t<p class=\"methods\">"
                + methodInfo + "</p>\n\t\t\t</div>\n\t\t</div>";
        htmlObjects = htmlObjects + obj + "\n";
    }

    public void connectHTMLNodeToObject(String node, String objectName) {
        htmlConnections = htmlConnections + "[\"" + stepInt + "-" + node + "\",\"" + stepInt + "-" + objectName
                + "\", \"f\"],";
    }

    public void connectHTMLObjectToObjects(String dir, String obj1, String obj2) {
        // var connections1 = [["obj2", "obj1", "f"], ["node1", "obj2", "b"]];
        htmlConnections = htmlConnections + "[\"" + stepInt + "-" + obj1 + "\",\"" + stepInt + "-" + obj2 + "\", \""
                + dir + "\"],";
    }

    public String getFinishedHTMLStep(int stepIndex) {
        String step = "\t<div id=\"step" + stepIndex + "\">\n" + htmlObjects + "\t</div>\n";
        htmlAllStepConnections.push(htmlConnections.substring(0, htmlConnections.length() - 1) + "];");
        htmlObjects = "";
        htmlConnections = "\t\tvar connections" + connStepIndex + " = [";
        stepInt = connStepIndex;
        connStepIndex++;
        return step;
    }

    public String getFinishedHTMLStringPart2(String[] steps) {
        String conns = getConnectionBlock();
        String finalHTML = String.join("\n", steps) + HTMLDOCPART2 + conns;
        return finalHTML;
    }

    public String getFinishedHTMLStringPart1() {
        return HTMLDOCPART1;
    }

    public String getFinishedHTMLStringPart3() {
        return HTMLDOCPART3;
    }

    private String getConnectionBlock() {
        String conn = "";
        for (String con : htmlAllStepConnections) {
            conn = conn + con + "\n";
        }
        conn = conn + "\t\tvar steps = [";
        for (int i = 0; i < connStepIndex - 1; i++) {
            conn = conn + "connections" + i + ", ";
        }
        conn = conn.substring(0, conn.length() - 2) + "];\n";
        return conn;
    }

    private final String HTMLDOCPART1 = """
            <!DOCTYPE html>
            <html>

            <head>
                <title>Diagram</title>
                <meta name=\"viewport\" content=\"width=device-width, inital-scale=1.0\" />
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                        font-family: none;
                    }

                    body {
                        display: flex;
                        align-items: top;
                        justify-content: center;
                        min-height: 100vh;
                        background: radial-gradient(#4671EA, #AC34E7);
                        margin: 5px;
                    }

                    .wrapper {
                        background: white;
                        width: 250px;
                        position: absolute;
                        top: 50%;
                        left: 50%;
                        transform: translate(-50%, -50%) scale(1.2);
                        border-radius: 10px;
                        z-index: 1;
                        border-width: 2px;
                        border-style: solid;
                        border-color: white;
                        visibility: visible;
                    }

                    .selectedStep {
                        font-size: 15px;
                        background: linear-gradient(#fa51c7, #6340ff);
                        width: auto;
                        position: absolute;
                        padding: 10px;
                        transform: translate(-50%, -50%) scale(1.4);
                        border-radius: 10px;
                        left: 40%;
                        top: 4%;
                        border-width: 2px;
                        border-style: solid;
                        border-color: black;
                    }


                    .showStep {
                        font-size: 15px;
                        background: linear-gradient(#fa51c7, #6340ff);
                        width: auto;
                        position: absolute;
                        padding: 10px;
                        transform: translate(-50%, -50%) scale(1.4);
                        border-radius: 10px;
                        left: 50%;
                        top: 4%;
                        border-width: 2px;
                        border-style: solid;
                        border-color: black;
                    }

                    .outOf {
                        font-size: 15px;
                        background: linear-gradient(#fa51c7, #6340ff);
                        width: auto;
                        position: absolute;
                        padding: 10px;
                        transform: translate(-50%, -50%) scale(1.4);
                        border-radius: 10px;
                        left: 53%;
                        top: 4%;
                        border-width: 2px;
                        border-style: solid;
                        border-color: black;
                    }

                    .showStepSize {
                        font-size: 15px;
                        background: linear-gradient(#fa51c7, #6340ff);
                        width: auto;
                        position: absolute;
                        padding: 10px;
                        transform: translate(-50%, -50%) scale(1.4);
                        border-radius: 10px;
                        left: 56%;
                        top: 4%;
                        border-width: 2px;
                        border-style: solid;
                        border-color: black;
                    }

                    .wrapper header {
                        color: #6f36ff;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 18px;
                        font-weight: 500;
                        padding: 15px 25px;
                        border-bottom: 1px solid #bfbfbf;
                    }

                    .wrapper .content {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        flex-direction: column;
                        margin: 10px 10px 10px;
                    }

                    .content p {
                        font-size: 14px;
                        text-align: left;

                    }

                    .wrapper header.active {
                        cursor: move;
                        user-select: none;
                    }

                    .wrapper .splitter {
                        color: #6f36ff;
                        font-size: 18px;
                        font-weight: 500;
                        width: 247px;
                        padding: 5px;
                        border-bottom: 1px solid #bfbfbf;
                    }

                    .wrapper .dot {
                        border-bottom-style: hidden;
                        text-shadow: 1px 1px 1px white,
                            1px -1px 1px white,
                            -1px 1px 1px white,
                            -1px -1px 1px white;
                        font-size: 20px;
                        font-weight: 900;
                        z-index: 700;
                    }

                    .inputs {
                        margin: 20px 0 27px;
                        padding: 0;
                        box-sizing: border-box;
                        font-family: 'Poppins', sans-serif;
                    }

                    .inputs input {
                        width: 100%;
                        height: 60px;
                        outline: none;
                        padding: 0 17px;
                        font-size: 19px;
                        border-radius: 5px;
                        border: 1px solid #999;
                        transition: 0.1s ease;
                    }

                    .inputs input::placeholder {
                        color: #999999;
                    }

                    .inputs input:focus {
                        box-shadow: 0 3px 6px rgba(0, 0, 0, 0.13);
                    }

                    .inputs input:focus::placeholder {
                        color: #bebebe;
                    }

                    .inputs button {
                        width: 100%;
                        height: 56px;
                        border: none;
                        opacity: 0.7;
                        outline: none;
                        color: #fff;
                        cursor: pointer;
                        font-size: 17px;
                        margin-top: 20px;
                        border-radius: 5px;
                        pointer-events: none;
                        background: #AA57CC;
                        transition: opacity 0.15s ease;
                    }

                    .inputs button.active {
                        opacity: 1;
                        pointer-events: auto;
                    }

                    .selectStep,
                    .popup {
                        position: absolute;
                        left: 50%;
                    }

                    .popup {
                        background: #fff;
                        padding: 25px;
                        border-radius: 15px;
                        top: -150%;
                        max-width: 380px;
                        width: 100%;
                        opacity: 0;
                        z-index: 900;
                        pointer-events: none;
                        box-shadow: 0px 10px 15px rgba(0, 0, 0, 0.1);
                        transform: translate(-50%, -50%) scale(1.2);
                        transition: top 0s 0.2s ease-in-out,
                            opacity 0.2s 0s ease-in-out,
                            transform 0.2s 0s ease-in-out;
                    }

                    .popup header {
                        display: flex;
                        padding-bottom: 10px;
                        border-bottom: 1px solid #ebedf9;
                    }

                    header .close {
                        position: absolute;
                        right: 10px;
                        top: 10px;
                        color: transparent;
                        display: block;
                        height: 1.5rem;
                        width: 1.5rem;
                        overflow: hidden;
                        background-image:
                            linear-gradient(to top right,
                                transparent 48%,
                                black 48%,
                                black 52%,
                                transparent 52%),
                            linear-gradient(to top left,
                                transparent 48%,
                                black 48%,
                                black 52%,
                                transparent 52%);
                        cursor: pointer;
                    }

                    header .close:hover,
                    .close:focus {
                        background-image:
                            linear-gradient(to top right,
                                transparent 46%,
                                black 46%,
                                black 54%,
                                transparent 54%),
                            linear-gradient(to top left,
                                transparent 46%,
                                black 46%,
                                black 54%,
                                transparent 54%);
                    }

                    header span {
                        font-size: 21px;
                        font-weight: 600;
                    }

                    .popup.show {
                        top: 50%;
                        opacity: 1;
                        z-index: 900;
                        pointer-events: auto;
                        transform: translate(-50%, -50%) scale(1);
                        transition: top 0s 0s ease-in-out,
                            opacity 0.2s 0s ease-in-out,
                            transform 0.2s 0s ease-in-out;
                    }

                    .popup .content {
                        margin: 20px 0;
                    }

                    button {
                        outline: none;
                        cursor: pointer;
                        font-weight: 500;
                        border-radius: 4px;
                        border: 2px solid transparent;

                        transition: background 0.1s linear, border-color 0.1s linear, color 0.1s linear;
                    }

                    .selectStep {
                        font-size: 15px;
                        font-weight: 600;
                        background: linear-gradient(#fa51c7, #6340ff);
                        width: auto;
                        position: absolute;
                        padding: 10px;
                        transform: translate(-50%, -50%) scale(1.4);
                        border-radius: 10px;
                        left: 20%;
                        top: 4%;
                        border-width: 2px;
                        border-style: solid;
                        border-color: black;
                    }

                    .shuffle {
                        font-size: 15px;
                        font-weight: 600;
                        background: linear-gradient(#fa51c7, #6340ff);
                        width: auto;
                        position: absolute;
                        padding: 10px;
                        transform: translate(-50%, -50%) scale(1.4);
                        border-radius: 10px;
                        left: 85%;
                        top: 4%;
                        border-width: 2px;
                        border-style: solid;
                        border-color: black;
                    }

                    #dot {
                        height: 25px;
                        width: 25px;
                        z-index: 700;
                        background-color: black;
                        border-radius: 50%;
                        border-width: 2px;
                        border-style: solid;
                        border-color: white;
                        display: inline-block;
                    }


                    .line {
                        position: absolute;
                        width: 8px;
                        background-color: rgb(245, 48, 123);
                        border-width: 2px;
                        border-style: solid;
                        border-color: rgb(245, 48, 123);
                        border-radius: 5px;
                        left: 0px;
                        top: 0px;
                        z-index: 400;
                        visibility: visible;
                    }

                    .normal::before, .normal::after {
                        position: absolute;
                        content: '';
                        top: -5px;
                        left: -30px;
                        width: 0;
                        height: 0;
                        z-index: 500;

                        border-bottom: 35px solid rgb(245, 48, 123);
                        border-right: 30px solid transparent;
                        border-left: 30px solid transparent;

                        transform: scale(1, 1.5);
                    }

                    .normalb::before, .normalb::after {
                        position: absolute;
                        content: '';
                        bottom: -5px;
                        right: -30px;
                        width: 0;
                        height: 0;
                        z-index: 500;

                        border-top: 35px solid rgb(245, 48, 123);
                        border-right: 30px solid transparent;
                        border-left: 30px solid transparent;

                        transform: scale(1, 1.5);
                    }

                    .onormal::before {
                        position: absolute;
                        content: '';
                        top: -5px;
                        left: -30px;
                        width: 0;
                        height: 0;
                        z-index: 100;

                        border-bottom: 35px solid rgb(245, 48, 123);
                        border-right: 30px solid transparent;
                        border-left: 30px solid transparent;

                        transform: scale(1, 1.5);
                    }

                    .onormal::after {
                        position: absolute;
                        content: '';
                        top: 0px;
                        left: -26px;
                        width: 0;
                        height: 0;
                        z-index: 100;
                        border-bottom: 28px solid #4671EA;
                        border-right: 25px solid transparent;
                        border-left: 25px solid transparent;
                        border-top: -20x solid transparent;

                        transform: scale(0.9, 1.5);
                    }

                    .onormalb::before {
                        position: absolute;
                        content: '';
                        bottom: -5px;
                        right: -30px;
                        width: 0;
                        height: 0;
                        border-top: 35px solid rgb(245, 48, 123);
                        border-right: 30px solid transparent;
                        border-left: 30px solid transparent;

                        transform: scale(1, 1.5);
                    }

                    .onormalb::after {
                        position: absolute;
                        content: '';
                        bottom: 0px;
                        left: -26px;
                        width: 0;
                        height: 0;
                        border-top: 28px solid #4671EA;
                        border-right: 25px solid transparent;
                        border-left: 25px solid transparent;
                        border-bottom: -20x solid transparent;

                        transform: scale(0.9, 1.5);
                    }

                    .line.diamond::before,
                    .line.diamond::after {
                        position: absolute;
                        content: '';
                        top: -5px;
                        left: -30px;
                        width: 0;
                        height: 0;
                        z-index: 100;
                        border-bottom: 30px solid rgb(245, 48, 123);
                        border-right: 30px solid rgb(245, 48, 123);
                        border-left: 30px solid rgb(245, 48, 123);
                        border-top: 30px solid rgb(245, 48, 123);

                        transform: rotate(50deg) skew(10deg);
                    }

                    .line.odiamond::before {
                        position: absolute;
                        content: '';
                        top: -5px;
                        left: -30px;
                        width: 0;
                        height: 0;

                        border-style: solid;
                        border-color: rgb(245, 48, 123);
                        border-radius: 5px;
                        z-index: 100;
                        border-bottom: 30px solid rgb(245, 48, 123);
                        border-right: 30px solid rgb(245, 48, 123);
                        border-left: 30px solid rgb(245, 48, 123);
                        border-top: 30px solid rgb(245, 48, 123);

                        transform: rotate(50deg) skew(10deg);
                    }

                    .line.odiamond::after {
                        position: absolute;
                        content: '';
                        top: -5px;
                        left: -30px;
                        width: 0;
                        height: 0;

                        z-index: 101;
                        border-bottom: 30px solid #4671EA;
                        border-right: 30px solid #4671EA;
                        border-left: 30px solid #4671EA;
                        border-top: 30px solid #4671EA;

                        transform: rotate(50deg) skew(10deg) scale(0.8);
                    }


                </style>
            </head>

            <body onload=\"init('wrapper')\">
                <button class=\"selectStep\">Select Step</button>
                <h3 class=\"selectedStep\">Selected Step</h3>
                <h3 class=\"showStep\" id=\"showStep\" style='float:left'>-</h3>
                <h3 class=\"outOf\" id=\"outOf\" style='float:left'>/</h3>
                <h3 class=\"showStepSize\" id=\"showStepSize\" style='float:left'>-</h3>
                <button class=\"shuffle\" id=\"shuffle\" style=\"float:left\">Shuffle</button>
                <div class=\"popup\">
                    <header>
                        <h1>Insert a step to select</h1>
                        <a href=\"#\" class=\"close\">Close</a>
                    </header>
                    <div class=\"inputs\">
                        <input id=\"input\" type=\"text\" spellcheck=\"false\" placeholder=\"Enter a number\">
                        <button>Select</button>
                    </div>

                </div>
                    """;

    private final String HTMLDOCPART2 = """

            <script type=\"text/javascript\">

                """;

    private final String HTMLDOCPART3 = """
                    var previousStep = null;
                    var chooseElement;
                    var lines = [];
                    var lineIndex = 0;
                    var scale = 1;
                    var mouseX, mouseY = 0;

                    function init(e) {
                        document.getElementById('showStepSize').innerText = steps.length - 1;
                        let element = document.getElementById('input');
                        element.placeholder = \"Enter a number between 0 and \" + (steps.length - 1) + \" incl.\";
                        unloadAllWrapper();
                        move(e);
                    }

                    function unloadAllWrapper() {
                        let allWrapper = document.getElementsByClassName('wrapper');
                        for (let index = 0; index < allWrapper.length; index++) {
                            allWrapper[index].style.visibility = \"hidden\";
                        }
                    }

            		const shuffleBtn = document.querySelector(\".shuffle\");

                    shuffleBtn.onclick = () => {
                        shuffle();
                    }

                    function shuffle() {
                        if (previousStep != null) {
                            var elements = document.getElementsByClassName('wrapper');
                            for (let i = 0; i < elements.length; i++) {
                                const element = elements[i];
                                var posInfo = element.getBoundingClientRect();
                                var width = posInfo.width;
                                var height = posInfo.height;

                                var posx = (Math.random() * (window.innerWidth - width) + width / 2).toFixed();
                                var posy = (Math.random() * (window.innerHeight - height) + height / 2).toFixed();

                                element.style.top = `${posy}px`;
                                element.style.left = `${posx}px`;
                            };
                            reconnectElements();
                        }
                    }

                    function selectStepState(stepIndex) {
                        unloadLines();
                        lineIndex = 0;
                        lines = [];
                        for (let i = 0; i < steps[stepIndex].length; i++) {
                            createLine(steps[stepIndex][i][2]);
                        }
                        if (previousStep != null) {
                            let unloadElements = document.getElementById('step' + previousStep).getElementsByClassName('wrapper');
                            for (let index = 0; index < unloadElements.length; index++) {
                                unloadElements[index].style.visibility = \"hidden\";
                            }
                        }
                        var loadElements = document.getElementById('step' + stepIndex).getElementsByClassName('wrapper');
                        for (let index = 0; index < loadElements.length; index++) {
                            loadElements[index].style.visibility = \"visible\";
                        }
                        reconnectElements();
                    }

                    function unloadLines() {
                        let unloadLines = document.getElementsByClassName('line');
                        let index = 0;
                        while (unloadLines[unloadLines.length-1] != null) {
                            if(index >= unloadLines.length) index = 0;
                            unloadLines[index].parentNode.removeChild(unloadLines[index]);
                            index++;
                        }

                    }

                    function createLine(t) {
                        var lineDiv = document.createElement(\"div\");
                        lineDiv.classList.add('line');
                        lineDiv.id = \"line\"+lineIndex;
                        lines.push(lineDiv);
                        lineIndex++;
                        var arrowhead1 = document.createElement(\"div\");
                        arrowhead1.classList.add('normal');
                        lineDiv.appendChild(arrowhead1);
                        if(t == \"b\") {
                            var arrowhead2 = document.createElement(\"div\");
                            arrowhead2.classList.add('normalb')
                            lineDiv.appendChild(arrowhead2);
                        }
                        lineDiv.style.visibility = \"visible\";
                        document.body.appendChild(lineDiv);
                    }

                    const move = function (element) {
                        const elements = document.querySelectorAll(\".wrapper\");
                        elements.forEach(element => {
                            let header = element.querySelector(\"header\");
                            header.addEventListener(\"mousedown\", () => {
                                chooseElement = element;
                                header.classList.add(\"active\");
                                header.addEventListener(\"mousemove\", onDrag)
                            })
                            document.addEventListener(\"mouseup\", () => {
                                header.classList.remove(\"active\");
                                header.removeEventListener(\"mousemove\", onDrag)
                            })
                        })
                    };
                    document.onmouseup = function (e) {
                        chooseElement = null;
                    }
                    function onDrag({ movementX, movementY }) {
                        let getStyle = window.getComputedStyle(chooseElement);
                        let leftVal = parseInt(getStyle.left);
                        let topVal = parseInt(getStyle.top);
                        chooseElement.style.left = `${leftVal + movementX}px`;
                        chooseElement.style.top = `${topVal + movementY}px`;
                        reconnectElements()
                    }
                    function reconnectElements() {
                        for (let i = 0; i < steps[selected].length; i++) {
                            adjustLine(
                                document.getElementsByName(steps[selected][i][0])[0],
                                document.getElementsByName(steps[selected][i][1])[0],
                                lines[i]
                            );
                        }
                    }
                    var selected = 0;
                    function adjustLine(from, to, line) {
                        var fT = from.offsetTop;
                        var tT = to.offsetTop;
                        var fL = from.offsetLeft;
                        var tL = to.offsetLeft;
                        if ((fL - tL) < -(to.offsetWidth / 2)) {
                            if (to.id != 'dot') {
                            tL = tL - to.offsetWidth * 1.162 / 2;
                            }
                            if (from.id != 'dot') {
                                fL = fL + from.offsetWidth * 1.162 / 2;
                            }
                        } else if ((fL - tL) > (to.offsetWidth / 2)) {
                            if (to.id != 'dot') {
                            tL = tL + to.offsetWidth * 1.14 / 2;
                            }
                            if (from.id != 'dot') {
                                fL = fL - from.offsetWidth * 1.14 / 2;
                            }
                        }
                        if ((fT - tT) < -(to.offsetHeight / 2)) {
                            if (to.id != 'dot') {
                            tT = tT - to.offsetHeight * 1.175 / 2;
                            }
                            if (from.id != 'dot') {
                                fT = fT + from.offsetHeight * 1.175 / 2;
                            }
                        } else if ((fT - tT) > (to.offsetHeight / 2)) {
                            if (to.id != 'dot') {
                            tT = tT + to.offsetHeight * 1.178 / 2;
                            }
                            if (from.id != 'dot') {
                                fT = fT - from.offsetHeight * 1.178 / 2;
                            }
                        }
                        var CA = (tT - fT);
                        var CO = tL - fL;
                        var H = Math.sqrt(CA * CA + CO * CO);
                        var ANG = Math.atan(CA / CO) * 180 / Math.PI + 90;
                        if (tT > fT) {
                            var top = (tT - fT) / 2 + fT;
                        } else if (tT < fT) {
                            var top = (fT - tT) / 2 + tT;
                        } else {
                            var top = fT;
                        }
                        if (tL > fL) {
                            var left = (tL - fL) / 2 + fL;
                        } else if (tL < fL) {
                            var left = (fL - tL) / 2 + tL;
                        } else {
                            var left = fL;
                        }
                        if (fL > tL) {
                            ANG = ANG + 180;
                        }
                        top -= H / 2;
                        line.style[\"-webkit-transform\"] = 'rotate(' + ANG + 'deg)';
                        line.style[\"-moz-transform\"] = 'rotate(' + ANG + 'deg)';
                        line.style[\"-ms-transform\"] = 'rotate(' + ANG + 'deg)';
                        line.style[\"-o-transform\"] = 'rotate(' + ANG + 'deg)';
                        line.style[\"-transform\"] = 'rotate(' + ANG + 'deg)';
                        line.style.top = top + 'px';
                        line.style.left = left + 'px';
                        line.style.height = H + 'px';
                    }
                    const txtInput = document.querySelector(\".inputs input\"),
                        popup = document.querySelector(\".popup\"),
                        viewBtn = document.querySelector(\".selectStep\"),
                        close = popup.querySelector(\".close\"),
                        checkBtn = document.querySelector(\".inputs button\");
                    txtInput.value = "";
                    viewBtn.onclick = () => {
                        popup.classList.toggle(\"show\");
                    }
                    close.onclick = () => {
                        viewBtn.click();
                    }
                    let filterInput;
                    checkBtn.addEventListener(\"click\", () => {
                        if (!(steps.length > filterInput)) {
                            window.alert(\"StepIndex out of bounds. Try another one\");
                            selected = previousStep;
                            txtInput.value = "";
                        } else {
                            document.getElementById(\"showStep\").innerText = filterInput;
                            previousStep = selected;
                            selected = filterInput;
                            selectStepState(selected);
                            txtInput.value = "";
                            reconnectElements();
                        }
                        viewBtn.click();
                        checkBtn.classList.remove(\"active\");
                    });
                    txtInput.addEventListener(\"keyup\", () => {
                        filterInput = parseInt(txtInput.value);
                        if (filterInput != null) {
                            return checkBtn.classList.add(\"active\");
                        }
                        checkBtn.classList.remove(\"active\");
                    });
                </script>
            </body>
            </html>
            """;
}
