<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="net.sourceforge.metware.binche.execs.BiNCheExecWeb" %>

<html>
<head>
    <title>BiNChe -- Enrichment analysis using ChEBI</title>

    <!-- Style sheets -->
    <link rel="stylesheet" type="text/css"
          href="${ pageContext.request.contextPath }/css/result.css?v=2.7.0">

    <link rel="stylesheet" type="text/css"
          href="${ pageContext.request.contextPath }/css/jquery-ui-1.9.0.custom.css">

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

    <%-- This loads the cytoscapeweb site! <script type="text/javascript"
  src="${pageContext.request.contextPath }/javascript/demo.js"></script> --%>

    <script type="text/javascript"
            src="${pageContext.request.contextPath }/javascript/cytoscapeweb-styles-demo.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath }/javascript/flash_detect_min.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath }/javascript/jquery-ui-1.9.0.custom.js"></script>

    <script type="text/javascript">
        $ (function() {
            $("#slider").slider();
        });
    </script>
</head>
<body>
<jsp:useBean id="bincheexec"
             class="net.sourceforge.metware.binche.execs.BiNCheExecWeb"></jsp:useBean>

<%
    HashMap<String, String> inputMap = (HashMap<String, String>) request.getAttribute("inputMap");

    bincheexec.generateJson(inputMap, request, response);

    Object nodeList = session.getAttribute("nodeList");
    Object edgeList = session.getAttribute("edgeList");
%>

<div align="center" style="padding: 5px">
    <h3>
        The graph from '<%=request.getParameter("analysisType")%>' enrichment analysis for the target ChEBI '<%=request.getParameter("targetType")%>'
    </h3>
</div>
<br>

<div id="cytoweb_container">

<%--<div id="save_file">
    <label style="cursor: pointer;">Save File</label>
</div>--%>

<div id="save_file">
    <ul>
        <li class="name"><a href="#"><span>Export Graph</span> </a>
            <ul>
                <li class="name"> <a href="#"><span>Export as Network Data</span> </a>
                    <ul>
                        <li id="save_xgmml"> <a href="#"> <span>XGMML</span> </a> </li>
                        <li id="save_graphml"> <a href="#"> <span>GraphML</span> </a> </li>
                        <li id="save_sif"> <a href="#"> <span>SIF</span> </a> </li>
                    </ul>
                </li>
                <li class="name"> <a href="#"><span>Export as Image</span> </a>
                    <ul>
                        <li id="save_svg"> <a href="#"> <span>SVG</span> </a> </li>
                        <li id="save_png"> <a href="#"> <span>PNG</span> </a> </li>
                    </ul>
                </li>
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
                /*options : {
                 orientation : "topToBottom",
                 breadthSpace : 150,
                 depthSpace : 30,
                 subtreeSpace : 60
                 }*/
            },
            nodeTooltipsEnabled : true,
            visualStyle : {
                global : {
                    backgroundColor : "#003333"
                    /*backgroundColor : "#ffffcc"*/
                },
                nodes : {
                    /*shape : "roundrect",
                     size : "auto",*/
                    color : { passthroughMapper : { attrName : "color" } },
                    opacity : { passthroughMapper : { attrName : "alpha" } },
                    labelGlowStrength : 100,
                    labelFontSize : 16,
                    labelFontColor : "#ffffff",
                    borderColor : "#ffffff",
                    labelVerticalAnchor : "top"
                },
                edges : {
                    color : "#ffffff"
                }
            }
        };

        vis["customTooltip"] = function (data) {
            var pValue = data["pValue"];
            var label = data["label"];
            var id = data["id"];
            return 'Compound - ' + label + '\n' + 'ChEBI id - CHEBI:' + id + '\n' + 'p-value - ' + pValue;
        };

        vis.ready(function() {

            var style = vis.visualStyle();
            style.nodes.tooltipText = { customMapper : { functionName : "customTooltip" } };
            vis.visualStyle(style);

            $("#save_xgmml").click(function() {
                vis.exportNetwork('xgmml','${ pageContext.request.contextPath }/GraphExporter?type=xml');
            });

            $("#save_graphml").click(function() {
                vis.exportNetwork('graphml','${ pageContext.request.contextPath }/GraphExporter?type=xml');
            });

            $("#save_sif").click(function() {
                vis.exportNetwork('sif',
                        '${ pageContext.request.contextPath }/GraphExporter?type=txt');
            });

            $("#save_png").click(function() {
                vis.exportNetwork('png','${ pageContext.request.contextPath }/GraphExporter?type=png');
            });

            $("#save_svg").click(function() {
                vis.exportNetwork('svg','${ pageContext.request.contextPath }/GraphExporter?type=svg');
            });

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
        });

        //draw
        vis.draw(draw_options);

    };
</script>

<div id="cytoscapeweb">The graph goes here.</div>

<%--
<div class="save_screen">
    <div class="back">
        <span>Back to graph</span>
        <div class="icon"></div>
    </div>
    <div class="selections">
        <div class="description">Select a file type.</div>
        <h3>Network Data</h3>
        <div class="data_formats">

            <!-- XGMML Format -->
            <!-- XGMML Format -->
            <div class="selection" id="save_xgmml">
                <div class="description">
                    <label>XGMML</label> <span>eXtensible Graph Markup and
                                Modeling Language</span>
                </div>
                <div id="exporter_xgmml"></div>
            </div>

            <!-- GRAPHML Format -->
            <div class="selection" id="save_graphml">
                <div class="description">
                    <label>GraphML</label> <span>Graph Markup Language</span>
                </div>
                <div id="exporter_graphml"></div>
            </div>

            <!-- SIF Format -->
            <div class="selection" id="save_sif">
                <div class="description">
                    <label>SIF</label> <span>Simple Interaction Format</span>
                </div>
                <div id="exporter_sif"></div>
            </div>

        </div>
        <h3>Image</h3>
        <div class="image_formats">

            <!-- SVG Format -->
            <div class="selection" id="save_svg">
                <div class="description">
                    <label>SVG</label> <span>Vector Image</span>
                </div>
                <div id="exporter_svg"></div>
            </div>

            <!--  PNG Format -->
            <div class="selection" id="save_png">
                <div class="description">
                    <label>PNG</label> <span>Bitmap Image</span>
                </div>
                <div id="exporter_png"></div>
            </div>

        </div>
    </div>

</div>
--%>

<div class="side">
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
</div>

</div>
</body>
</html>