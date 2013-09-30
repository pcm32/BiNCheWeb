<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <title><%=props.get("title")%></title>

        <script type="text/javascript"
        src="${pageContext.request.contextPath }/javascript/jquery-1.8.2.js"></script>

        <script type="text/javascript">
            function display(value) {
                var val = document.getElementById(value).value;
                if (val == "plain") {
                    document.getElementById('plainInfo').style.display = 'block';
                    document.getElementById('plainDesc').style.display = 'block';
                    document.getElementById('weightDesc').style.display = 'none';
                    document.getElementById('fragmentDesc').style.display = 'none';
                } else if (val == "weighted") {
                    document.getElementById('plainInfo').style.display = 'block';
                    document.getElementById('plainDesc').style.display = 'none'
                    document.getElementById('weightDesc').style.display = 'block';
                    document.getElementById('fragmentDesc').style.display = 'none';
                } else if (val == "fragment") {
                    document.getElementById('plainInfo').style.display = 'none';
                    document.getElementById('plainDesc').style.display = 'none';
                    document.getElementById('weightDesc').style.display = 'none';
                    document.getElementById('fragmentDesc').style.display = 'block';
                }

                refillText(document.getElementById("input"));
            }

            function clearText(textArea) {
                textArea.value = '';   //Clear the area
                textArea.style.color='#000';  //Set font colour to black
            }

            function refillText(textArea) {
                //Based on the type of analysis selected, fill the textbox with examples accordingly
                if($("#weighted").is(":checked")==true || $("#fragment").is(":checked")==true)
                    refillTextWithWeightedIds(textArea);
                else refillTextWithPlainIds(textArea);

            }

            function refillTextWithPlainIds(textArea) {
                textArea.value=textArea.defaultValue; //Set it back to sample ids
                textArea.style.color='#9b9b9b';  //Set font colour back to grey
            }

            function refillTextWithWeightedIds(textArea) {
//                textArea.value='CHEBI:491197	1   \n' +
//                    'CHEBI:591790	0.989   \n' +
//                    'CHEBI:15712	0.915   \n' +
//                    'CHEBI:523039	0.894   \n' +
//                    'CHEBI:28412	0.862   \n' +
//                    'CHEBI:666900	0.862   \n' +
//                    'CHEBI:15649	0.293   \n' +
//                    'CHEBI:491180	0.259   \n' +
//                    'CHEBI:31080	0.252   \n' +
//                    'CHEBI:15712	0.236   \n' +
//                    'CHEBI:28412	0.235   \n' +
//                    'CHEBI:18131	0.23    \n' +
//                    'CHEBI:523039	0.226   \n' +
//                    'CHEBI:521292	0.19    \n' +
//                    'CHEBI:1278800	0.177   \n';
                textArea.value = 'CHEBI:17079	0.7665\n' +
                                 'CHEBI:46816	0.7464999999999999\n' +
                                 'CHEBI:28658	0.7464999999999999\n' +
                                 'CHEBI:28611	0.7464999999999999\n' +
                                 'CHEBI:28594	0.6915\n' +
                                 'CHEBI:17048	0.6915\n' +
                                 'CHEBI:7852	0.60575\n' +
                                 'CHEBI:164200  0.2342\n' +
                                 'CHEBI:8489    0.25321\n' +
                                 'CHEBI:9630    0.2543\n' +
                                 'CHEBI:59477   0.2335\n' +
                                 'CHEBI:9495    0.2433\n' +
                                 'CHEBI:3540	0.509\n';

                textArea.style.color = '#9b9b9b';  //Set font colour back to grey

            }
        </script>
        <style type="text/css">
            #loading {
                position: fixed;
                left: 0;
                top: 0;
                bottom: 0;
                width: 100%;
                height: 100%;
                min-height: 100%;
                background-color: white;
                text-align: center;
                opacity: 0.85;
                filter: alpha(opacity = 85);
            }

            #loadingtext {
                width: 300px;
                height: 100px;
                background-color: #FFF;
                text-align: center;
                padding: 100px 0 0 0;
                margin: 250px auto 0 auto;
            }

            h1 {font-size: 17pt; font-weight: bold; padding: 3px; color: #066; border: 1px solid #bfbfbf; background: #eef5f5;}
        </style>
    </head>

<body style="font-family: 'Times New Roman', Georgia, serif">
<center>
<div id="loading" style="display:none;">
    <div id="loadingtext">
        Please wait while the graph loads...<br />
        <img src="${ pageContext.request.contextPath }/images/ajax-loader.gif"
            border="0" style="margin-top: 10px"/>
    </div>
</div>
<h1><%=props.get("subtitle")%></h1> <br>

<a href="http://www.ebi.ac.uk/chebi/" target="_blank">
    <img src="${pageContext.request.contextPath}/images/ChEBI_logo_mid.gif"
         alt="Chemical Entities of Biological Interest (ChEBI) is a freely available dictionary of molecular entities focused on ‘small’ chemical compounds."
         title="Chemical Entities of Biological Interest (ChEBI) is a freely available dictionary of molecular entities focused on ‘small’ chemical compounds.">
</a> <br> <br> <br>

<form action="${ pageContext.request.contextPath }/ValidateInput" method="post">
    <b>ChEBI ids</b>
    <div class="content" style="vertical-align: middle; width: 500px">
        <div style="padding: 5px" class="textarea">
            <textarea rows="10" cols="50" name="input" id="input" style="color: #9b9b9b; text-align: left"
                      onfocus="clearText(this)" onblur="if(this.value=='') refillText(this);">
CHEBI:108133
CHEBI:108141
CHEBI:10983
CHEBI:114833
CHEBI:1156
CHEBI:116314
CHEBI:1189
CHEBI:11936
CHEBI:12193
CHEBI:12357
CHEBI:12384
CHEBI:1294
CHEBI:1296
CHEBI:1301
CHEBI:13115
CHEBI:13643
CHEBI:13719
CHEBI:142589
CHEBI:144308
CHEBI:144365
CHEBI:14498
CHEBI:14597
CHEBI:147265
CHEBI:14885
CHEBI:15035
CHEBI:15036
CHEBI:15040
CHEBI:15347
CHEBI:15349
CHEBI:15355
CHEBI:15375
CHEBI:15382
CHEBI:15383
CHEBI:15388
CHEBI:15389
CHEBI:15450
CHEBI:15451
CHEBI:15475
CHEBI:15481
CHEBI:15483
CHEBI:15493
CHEBI:15494
CHEBI:15497
CHEBI:15519
CHEBI:15538
CHEBI:15545
CHEBI:15548
CHEBI:15551
CHEBI:15552
CHEBI:15553
CHEBI:15554
CHEBI:15555
CHEBI:15604
CHEBI:15626
CHEBI:15627
CHEBI:15639
CHEBI:15643
CHEBI:15646
CHEBI:15655
CHEBI:15681
CHEBI:15687
CHEBI:15690
CHEBI:15705
CHEBI:15727
CHEBI:15759
CHEBI:15765
CHEBI:15768
CHEBI:15772
CHEBI:15785
CHEBI:15786
CHEBI:15801
CHEBI:15816
CHEBI:15826
CHEBI:16169
CHEBI:16175
CHEBI:16179
CHEBI:16180
CHEBI:16197
CHEBI:16204
CHEBI:16231
CHEBI:16250
CHEBI:16254
CHEBI:16256
CHEBI:16290
CHEBI:16302
CHEBI:16315
CHEBI:16320
CHEBI:16325
CHEBI:16327
CHEBI:16331
CHEBI:16336
CHEBI:16343
CHEBI:16344
CHEBI:16361
CHEBI:16434
CHEBI:16456
CHEBI:16466
CHEBI:16469
CHEBI:16485
CHEBI:16496
CHEBI:16498
CHEBI:16500
CHEBI:16504
CHEBI:16522
CHEBI:16525
CHEBI:16544
CHEBI:16548
CHEBI:16553
CHEBI:16579
CHEBI:16581
CHEBI:16607
CHEBI:16615
CHEBI:16618
CHEBI:16653
CHEBI:16664
CHEBI:16692
CHEBI:16697
CHEBI:1670
CHEBI:16702</textarea>
        </div>
        <div style="padding: 5px" class="textarea-instruction">
            <span style="font-size: 10.5pt; padding: 1px;"><%=props.get("input.text")%>
            </span>
        </div> <br>
        <div style="padding: 5px" class="type-selection">
             <span style="padding: 1px;">
             <b>Select type of analysis: </b> <br/>
                  
                <%
                boolean showAnalysisTypes = Boolean.valueOf(props
                        .getProperty("menu.showAnalysisTypes"));
                if (showAnalysisTypes) {
                %>
                    <span>
                        <input type="radio" id="weighted" name="analysisType" class="analysisType" value="weighted" onclick="display('weighted');" />
                        <b>Weighted Enrichment Analysis </b>
                    </span>
                 <br>
                    <span>
                        <input type="radio" id="plain" name="analysisType" class="analysisType" value="plain" checked="checked" onclick="display('plain');" />
                        <b>Plain Enrichment Analysis </b>
                    </span>
                 <br>
                    <span>
                        <input type="radio" id="fragment" name="analysisType" class="analysisType" value="fragment" onclick="display('fragment');" />
                        <b>Fragment Enrichment Analysis </b>
                    </span>


                    <span id="weightDesc" style="display: none; padding: 2px; font-size: 10.5pt; height: 70px; text-align: left; margin-left: 3.5em">
                             <p>This analysis accepts a list of ChEBI ids and their weights. <br>
                                 Weight is a decimal value between 0 and 1. <br>
                                 Please select your target.
                             </p>
                    </span>

                    <span id="fragmentDesc" style="display: none; padding: 2px; font-size: 10.5pt; height: 70px; text-align: left; margin-left: 3.5em">
                             <p>This analysis accepts a list of ChEBI ids and their weights. <br>
                                 Weight is a decimal value between 0 and 1.<br>
                                 This analysis runs on the ChEBI structure ontology.
                             </p>
                    </span>

                    <span id="plainDesc" style="display: none; padding: 2px; font-size: 10.5pt; height: 70px; text-align: left; margin-left: 3.5em">
                             <p>This analysis accepts a list of ChEBI ids.</p>
                    </span>
		     <%
		     	} else {
		     %>
		     
		     <span style="display:none"> 
		         <input type="radio" id="plain" name="analysisType" class="analysisType" value="plain" checked="checked"  onclick="display('plain');" />
                    <b>Plain Enrichment Analysis </b>
			    </span>
		     
		     <%
		     		     	}
		     		     %>
		      </span>
             <span id="plainInfo" style="display: none; padding: 2px; height: 50px; text-align: left; margin-left: 3em">
                    Select target of enrichment:
                     <select name="targetType">
                     <%
                     	int numTargets = Integer.valueOf(props
                     			.getProperty("menu.countTargets"));
                     	for (int i = 1; i <= numTargets; i++) {

                     		out.print("<option value=\""
                     				+ props.get("menu.targetType." + i) + "\" ");
                     		if (i == 1)
                     			out.print("selected=\"selected\" ");
                     		out.print(">");
                     		out.print(props.get("menu.target.text." + i));
                     		out.println("</option> ");
                     	}
                     %>
                     </select>
		     </span>
            <input type="submit" value="Submit" onclick="document.getElementById('loading').style.display = 'block';" style="margin-top: 2em" />

        </div>
    </div>
</form>

            <script type="text/javascript">
                document.getElementById("loading").style.display = "none";
                display('plain');
            </script>

        </center>
    </body>
</html>
