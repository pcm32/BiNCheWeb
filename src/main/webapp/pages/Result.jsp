<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="net.sourceforge.metware.binche.execs.BiNCheExecWeb" %>

<html>
<head>
    <title>BiNChe -- Enrichment analysis using ChEBI ontology</title>

    <!-- Style sheets -->
    <link rel="stylesheet" type="text/css"
          href="${ pageContext.request.contextPath }/css/result.css?v=3.1.3">

<%--    <link rel="stylesheet" type="text/css"
          href="${ pageContext.request.contextPath }/css/jquery-ui-1.9.0.custom.css">--%>

    <!-- JavaScript -->
    <script type="text/javascript"
            src="${ pageContext.request.contextPath }/javascript/json2.min.js"></script>

    <script type="text/javascript"
            src="${ pageContext.request.contextPath }/javascript/AC_OETags.min.js"></script>

    <script type="text/javascript"
            src="${ pageContext.request.contextPath }/javascript/cytoscapeweb.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath }/javascript/jquery-1.8.2.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath }/javascript/cytoscapeweb-file.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath }/javascript/cytoscapeweb-styles-demo.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath }/javascript/flash_detect_min.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath }/javascript/jquery-ui-1.9.1.js"></script>

    <script type="text/javascript">
        $(function () {
            $("#draggable").draggable({ containment: "#cytoweb_container", scroll: false});
        });

    </script>

    <%--    <script type="text/javascript">
        $ (function() {
            $("#slider").slider();
        });
    </script>--%>
</head>
<body>
<jsp:useBean id="bincheexec"
             class="net.sourceforge.metware.binche.execs.BiNCheExecWeb"></jsp:useBean>

<%--Get the nodes and edges from the BiNCheWeb class--%>
<%
    HashMap<String, String> inputMap = (HashMap<String, String>) session.getAttribute("inputMap");

    bincheexec.processData(inputMap, request, response);

    Object nodeList = session.getAttribute("nodeList");
    Object edgeList = session.getAttribute("edgeList");
%>

<div align="center" style="padding: 5px">
    <h3>
        The graph from <%=request.getSession().getAttribute("analysisType")%> enrichment analysis using ChEBI <%=request.getSession().getAttribute("targetType")%> ontology
    </h3>
</div>
<br>

<div id="cytoweb_container">

<div class="gradient_container" id="draggable">
    <div id="gradient_info">
        <div id="gradient_info_max">1.0</div>
        <div id="gradient_info_min">0.0</div>
    </div>
    <div class="gradient"></div>
</div>

<div id="export_graph">
    <ul<%-- id="menu"--%>>
        <li class="name"><a href="#"> Export Graph </a>
            <ul>
                <li class="name"> <a href="#"> Export as Network Data </a>
                    <ul>
                        <li id="export_xgmml"> <a href="#"> XGMML</a> </li>
                        <li id="export_graphml"> <a href="#"> GraphML </a> </li>
                        <li id="export_sif"> <a href="#"> SIF </a> </li>
                    </ul>
                </li>
                <li class="name"> <a href="#"><span>Export as Image</span> </a>
                    <ul>
                        <li id="export_svg"> <a href="#"> SVG </a> </li>
                        <li id="export_png"> <a href="#"> PNG </a> </li>
                    </ul>
                </li>
                <li class="name"> <a href="#"> Export as Table</a> </li>
            </ul>
        </li>
        <li class="name"> <a href="#"> Style</a>
            <ul>
                <li id="show_node_labels"><a href="#"> Show node labels</a> </li>
            </ul>
        </li>
    </ul>

</div>

<script type="text/javascript">
    window.onload = function() {
        // id of Cytoscape Web container div
        var div_id = "cytoscapeweb";

        // specify the data schema and data to use (grab the data from server)
        var network_json = {
            dataSchema : {
                nodes : [ {
                    name : "label",
                    type : "string"
                }, {
                    name : "color",
                    type : "string"
                }, {
                    name : "alpha",
                    type : "string"
                }, {
                    name : "pValue",
                    type : "string"
                }],

                edges : [ {
                    name : "directed",
                    type : "boolean",
                    defValue : true
                } ]
            },

            data : {
                nodes : <%=nodeList%>,

                edges : <%=edgeList%>
            }
        };

        // initialization options
        var options = {
            swfPath : "${ pageContext.request.contextPath }/swf/CytoscapeWeb",
            flashInstallerPath : "${ pageContext.request.contextPath }/swf/playerProductInstall"
        };

        // initialise
        var vis = new org.cytoscapeweb.Visualization(div_id, options);

        //specify the graph and layout to draw
        var draw_options = {
            network : network_json,
            layout : {
                name : "ForceDirected"
            },
            nodeTooltipsEnabled : true,
            nodeLabelsVisible : false,
            visualStyle : {
                global : {
                    /*backgroundColor : "#003333"*/
                    backgroundColor : "#fff"
                },
                nodes : {
                    color : { passthroughMapper : { attrName : "color" } },
                    opacity : { passthroughMapper : { attrName : "alpha" } },

                    borderColor : "#000",

                    selectionGlowColor : "#0000ff" /*"#ffb599"*/,
                    selectionGlowOpacity : 1.0,
                    selectionOpacity : 1.0,

                    hoverGlowOpacity : 1.0,
                    hoverGlowColor : "#ffff33",
                    hoverOpacity : 1.0,

                    labelGlowStrength : 100,
                    labelFontSize : 16,
                    labelFontColor : "#000",
                    labelVerticalAnchor : "top",

                    tooltipBackgroundColor : "#ffffa8"
                },
                edges : {
                    color : "#000"
                }
            }
        };

        //Function that sets the tool tip for each node
        vis["customTooltip"] = function (data) {
            var pValue = data["pValue"];
            var label = data["label"];
            var id = data["id"];
            var pValueSciNot = Number(pValue).toPrecision(3);
            var proportionOfSample = 0.68 + '(dummy value)';
            var fold = 0.5 + '(dummy value)';
            return label + ' (CHEBI:' + id + ')\np-value : ' + pValueSciNot + '\n% of sample : ' +proportionOfSample + '\nfold of enrichment : ' +fold;
        };

        vis.ready(function() {

            //Add custom tooltip to nodes
            var style = vis.visualStyle();
            style.nodes.tooltipText = { customMapper : { functionName : "customTooltip" } };
            vis.visualStyle(style);

            //Exporters
            $("#export_xgmml").click(function() {
                vis.exportNetwork('xgmml','${ pageContext.request.contextPath }/GraphExporter?type=xml');
            });

            $("#export_graphml").click(function() {
                vis.exportNetwork('graphml','${ pageContext.request.contextPath }/GraphExporter?type=xml');
            });

            $("#export_sif").click(function() {
                vis.exportNetwork('sif',
                        '${ pageContext.request.contextPath }/GraphExporter?type=txt');
            });

            $("#export_png").click(function() {
                vis.exportNetwork('png','${ pageContext.request.contextPath }/GraphExporter?type=png');
            });

            $("#export_svg").click(function() {
                vis.exportNetwork('svg','${ pageContext.request.contextPath }/GraphExporter?type=svg');
            });

            //Context menu items
            vis.addContextMenuItem("Go to entry page in ChEBI", "nodes",
                    function (evt) {
                        // Get the right-clicked node:
                        var node = evt.target;

                        //Get the chebi id of the node (it is inside a property called 'data')
                        var id = node.data.id;

                        //Create a link to the corresponding page in ChEBI and open in a separate tab
                        var link = "http://www.ebi.ac.uk/chebi/advancedSearchFT.do?searchString=" +id +"&queryBean.stars=2";
                        window.open(link, '_blank');
                    })

                    .addContextMenuItem("Select first neighbors", "nodes",
                    function (evt) {
                        // Get the right-clicked node:
                        var rootNode = evt.target;

                        // Get the first neighbors of that node:
                        var fNeighbors = vis.firstNeighbors([rootNode]);
                        var neighborNodes = fNeighbors.neighbors;

                        // Select the root node and its neighbors:
                        vis.select([rootNode]).select(neighborNodes);
                    }
            );

            //Function to toggle node labels on/off
            $(function () {
                $("#show_node_labels").click( function() {
                    if (vis.nodeLabelsVisible()==false) {
                    vis.nodeLabelsVisible(true);
                    this.innerHTML="<a href='#'>Hide node labels</a> ";
                    }

                    else {
                        vis.nodeLabelsVisible(false);
                        this.innerHTML="<a href='#'>Show node labels</a> ";
                    }

                });
            });

        });

        //draw
        vis.draw(draw_options);

    };
</script>

<div id="cytoscapeweb">The graph goes here.</div>

<%--    To implement the filter again, please remember to uncomment the corresponding stylesheet from result.css
and the script that calls the function 'slider' (at approx. line 42)  --%>

<%--<div class="side">
    <div class="side_header">
        <span>Filter</span>
    </div>
    <div id="filter">
        <div class="slider_area">
            <span class="slider_min">30</span> <span class="slider_max">80</span>
            <div id="slider" class="slider">
                <div class="ui-slider-range ui-widget-header"
                     style="left: 0%; width: 100%;"></div>
                <a href="#" class="ui-slider-handle" style="left: 0%;"></a> <a
                    href="#" class="ui-slider-handle" style="left: 100%;"></a>
            </div>
        </div>
    </div>
</div>--%>

</div>
</body>
</html>