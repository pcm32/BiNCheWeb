<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="net.sourceforge.metware.binche.execs.BiNCheExecWeb" %>

<%@page import="java.util.Properties" %>
<%@page import="java.io.FileInputStream" %>


<%
Properties props = new Properties();
try {
	props.load(getClass().getClassLoader().getResourceAsStream(
			"/binche_gui.properties"));
} catch (Exception e) {
	out.println("Unable to load properties file due to: "
			+ e.getMessage());
	out.println("Root of class path "+getClass().getClassLoader().getResource("/"));
}
%>

<html>
<head>

    <link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/sidebars.css"   type="text/css" />
    <link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/basicstyles.css"   type="text/css" />
    <link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/contents.css"   type="text/css" />
    <link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/headings.css"   type="text/css" />


    <link type="text/css" rel="stylesheet" href="//www.ebi.ac.uk/web_guidelines/css/mitigation/develop/ebi-mitigation.css" />
    <link type="text/css" rel="stylesheet" href="//www.ebi.ac.uk/web_guidelines/css/mitigation/develop/embl-petrol-colours.css" />
    <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/cookiebanner.js"></script>
    <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/foot.js"></script>

    <title><%= props.get("title") %></title>

    <!-- Style sheets -->
    <link rel="stylesheet" type="text/css"
          href="${ pageContext.request.contextPath }/css/binche.css">
    <link rel="stylesheet" type="text/css"
          href="${ pageContext.request.contextPath }/css/result.css?v=3.1.3">

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
<%@ include file="header.html" %>

<div id="content">
<br/>

<table class="contentspane" id="contentspane" summary="The main content pane of the page" style="width: 100%">
<tbody>
<tr>

<td class="leftmenucell" id="leftmenucell">
    <%@ include file="menu.html" %>
</td>

<td id="contentsarea" class="contentsarea" style="border-left: 1px solid #dedede;">
<div class="breadcrumbs">
    <a href="http://www.ebi.ac.uk/" class="firstbreadcrumb">EBI</a><a href ="http://www.ebi.ac.uk/Databases/">Databases</a><a href ="http://www.ebi.ac.uk/Databases/smallmolecules.html">Small Molecules</a><a href="http://www.ebi.ac.uk/chebi">ChEBI</a><a href="/chebi/tools/binche">BiNChE</a>
</div>
<h1 class="local-header"><a href="/chebi/tools/binche" title="Back to BiNChE homepage"><%=props.get("subtitle")%></a></h1>

<jsp:useBean id="bincheexec"
             class="net.sourceforge.metware.binche.execs.BiNCheExecWeb"></jsp:useBean>

<%--Get the nodes and edges from the BiNCheWeb class--%>
<%
    HashMap<String, String> inputMap = (HashMap<String, String>) session.getAttribute("inputMap");

    bincheexec.processData(inputMap, request, response);

    Object nodeList = session.getAttribute("nodeList");
    Object edgeList = session.getAttribute("edgeList");
%>

<div id="resultDesc" align="center">
    <h3>
        The graph from <%=request.getSession().getAttribute("analysisType")%> enrichment analysis using the <%=request.getSession().getAttribute("targetType")%> ontology
    </h3>
</div>

<div id="cytoweb_container">

<div class="gradient_container" id="draggable">
    <div id="gradient_info">
        <div id="gradient_info_max">None</div>
        <div id="gradient_info_min">High</div>
        <div id="gradient_info_desc">Significance</div>
    </div>
    <div class="gradient"></div>
</div>

<div id="export_graph">
    <ul<%-- id="menu"--%>>
        <li class="name"><a href="#"> Export Graph </a>
            <ul>
                <li class="name"> <a href="#"> Export as Network Data </a>
                    <ul>
                        <li id="export_xgmml"> <a href="#">XGMML</a> </li>
                        <li id="export_graphml"> <a href="#">GraphML</a> </li>
                        <li id="export_sif"> <a href="#">SIF</a> </li>
                    </ul>
                </li>
                <li class="name"> <a href="#"><span>Export as Image</span> </a>
                    <ul>
                        <li id="export_svg"> <a href="#">SVG</a> </li>
                        <li id="export_png"> <a href="#">PNG</a> </li>
                    </ul>
                </li>
                <li id="export_table"> <a href="#">Export as Table</a> </li>
            </ul>
        </li>
        <li class="name"><a href="#"> Layout </a>
            <ul>
                <li id="force_directed_layout"> <a href="#">Force directed</a></li>
                <li id="circle_layout"> <a href="#">Circular</a></li>
                <li id="radial_layout"> <a href="#">Radial</a></li>
                <li id="tree_layout"> <a href="#">Tree</a></li>
            </ul>
        </li>
        <li class="name"> <a href="#"> Style</a>
            <ul>
                <li id="show_node_labels"><a href="#"> Hide node labels</a> </li>
                <li id="toggle_high_pvalues"><a href="#"> Hide non-significant nodes</a>
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
                }, {
                    name : "corrPValue", 
                    type : "string"
                }, {
                    name : "propOfSample",
                    type : "string"
                }, {
                    name : "fold",
                    type : "string"
                }
            ],

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
            nodeLabelsVisible : true,
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
            var pValueSciNot = Number(pValue).toExponential(2);
            var corrPValue = Number(data["corrPValue"]).toExponential(2)
            if(isNaN(corrPValue)) {
                corrPValue = "N/A";
            }
            var percOfSample = Number(data["propOfSample"]*100).toPrecision(2);
            var fold = Number(data["fold"]).toPrecision(2);
            return label + ' (CHEBI:' + id + ')\np-value : ' + pValueSciNot+ '\ncorr. p-value : ' + corrPValue + '\n% of sample : ' +percOfSample + '\nfold of enrichment : ' +fold;
        };
        
        var filterPValueOn = false;

        vis.ready(function() {

            //Add custom tooltip to nodes
            var style = vis.visualStyle();            
            style.nodes.tooltipText = { customMapper : { functionName : "customTooltip" } };
            vis.visualStyle(style);

            //Exporters
            $("#export_xgmml").click(function() {
                vis.exportNetwork('xgmml','${ pageContext.request.contextPath }/GraphExporter?type=xgmml');
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
            
            $("#export_table").click(function() {
                vis.exportNetwork('tab', '${ pageContext.request.contextPath }/GraphExporter?type=tab');
            });
            
            //Layout
            $("#circle_layout").click(function() {
                vis.layout('Circle'); 
            });
            $("#tree_layout").click(function() {
                vis.layout('Tree'); 
            });
            $("#radial_layout").click(function() {
                vis.layout('Radial'); 
            });
            $("#force_directed_layout").click(function() {
                vis.layout('ForceDirected'); 
            });
            //Context menu items
            vis.addContextMenuItem("Go to entry page", "nodes",
                    function (evt) {
                        // Get the right-clicked node:
                        var node = evt.target;

                        //Get the chebi id of the node (it is inside a property called 'data')
                        var id = node.data.id;

                        //Create a link to the corresponding page in ChEBI and open in a separate tab
                        var link = "<%= props.get("graph.link.prefix") %>" +id + "<%=props.get("graph.link.suffix")%>";
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
                    })
                    
//                    .addContextMenuItem("Select one level of children nodes", "nodes",
//                    function(evt) {
//                        var rootNode = evt.target;
//                        var selected = vis.selected("nodes");
//                        
//                        selected.push(rootNode);
//                        var toAddSelection = [];
//                        for(var parent in selected) {
//                            toAddSelection = toAddSelection.concat(vis.childNodes(parent));
//                        }
//                        
//                        selected = selected.concat(toAddSelection);
//                        alert("Total Number of selected : "+selected.length);
//                        vis.select(selected);
//                    })
                    ;

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
            
            $(function () {
                $("#toggle_high_pvalues").click( function() {
                        if(!filterPValueOn) {
                            vis.filter("nodes",function(node)
                            {   if(!isNaN(node.data.corrPValue)) {
                                    return node.data.corrPValue <= 0.05;
                                } else {
                                    return node.data.pValue <= 0.05;
                                }
                            });
                            filterPValueOn = true;
                            this.innerHTML="<a href='#'>Show non-significant nodes</a> ";
                        } else {
                            vis.removeFilter();
                            filterPValueOn = false;
                            this.innerHTML="<a href='#'>Hide non-significant nodes</a> ";
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


</td>
</tr>
</tbody>
</table>
<%@ include file=   "footer.html" %>

</div>
</body>
</html>