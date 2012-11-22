<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <title>BiNChe -- Enrichment analysis using ChEBI</title>
    <script type="text/javascript">
        function display(value) {
            var val = document.getElementById(value).value;
            if (val == "plain") {
                document.getElementById('plainInfo').style.display = 'block';
                document.getElementById('weightInfo').style.display = 'none';
            } else if (val == "weighted") {
                document.getElementById('plainInfo').style.display = 'none';
                document.getElementById('weightInfo').style.display = 'block';
            }
        }

        function clearText(textArea) {
            textArea.value = '';   //Clear the area
            textArea.style.color='#000';  //Set font colour to black
        }

        function refillText(textArea) {
            if(textArea.value=='') {
                textArea.value=textArea.defaultValue; //Set it back to sample ids
                textArea.style.color='#c7c7c7';  //Set font colour back to grey
            }
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
            width: 100px;
            height: 100px;
            background-color: #FFF;
            text-align: center;
            padding: 100px 0 0 0;
            margin: 250px auto 0 auto;
        }

        h1 {font-size: 17pt; font-weight: bold; padding: 3px; color: #066; border: 1px solid #bfbfbf; background: #eef5f5;}
    </style>
</head>

<body>
<center>
    <div id="loading" style="display:none;">
        <div id="loadingtext">
            Please wait...<br /> <img
                src="${ pageContext.request.contextPath }/images/ajax-loader.gif"
                border="0" />
        </div>
    </div>
    <h1>ENRICHMENT ANALYSIS</h1> <br>
    <a href="http://www.ebi.ac.uk/chebi/">
        <img src="${pageContext.request.contextPath}/images/ChEBI_logo_mid.gif"
             alt="Chemical Entities of Biological Interest (ChEBI) is a freely available dictionary of molecular entities focused on ‘small’ chemical compounds."
             title="Chemical Entities of Biological Interest (ChEBI) is a freely available dictionary of molecular entities focused on ‘small’ chemical compounds.">
    </a> <br> <br>
    <p> Please enter your list of ChEBI ids, separated by tab, newline or comma.</p> <br>


    <form action="${ pageContext.request.contextPath }/ValidateInput"
          method="post">
        <b>ChEBI ids</b>
        <table style="vertical-align: middle">
            <tr>
                <td style="padding: 10px">
                    <textarea rows="10" cols="50" name="input" id="input" style="color: #c7c7c7" onfocus="clearText(this)" onblur="refillText(this)">
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
CHEBI:16702</textarea></td>
            </tr>
            <tr>
                <td style="clear: both; padding: 10px"><br> <b>Select
                    type of analysis </b> <br /> <span> <input type="radio"
                                                               id="weighted" name="analysisType" value="weighted"
                                                               onclick="display('weighted');" /> <b>Weighted
                    Enrichment Analysis </b>
					</span> <span> <input type="radio" id="plain" name="analysisType"
                                          value="plain" checked="checked" onclick="display('plain');" /> <b>Plain Enrichment
                    Analysis </b> <br />
					</span> <br> <span id="weightInfo"
                                       style="display: none; padding: 2px"> This analysis accepts a
							list of ChEBI ids and their weights. <br> Weight is a
							decimal value between 0 and 1.<br> This analysis runs on the
							ChEBI structure ontology.

					</span> <span id="plainInfo" style="display: none; padding: 2px">
							This method accepts a list of ChEBI ids only.<br /> <br> <b>Select
                    target of enrichment</b> <select name="targetType">
                    <option value="structure" selected="selected">ChEBI
                        structure classification</option>
                    <option value="role">ChEBI role classification</option>
                    <option value="both">ChEBI structures and roles</option>
                </select>
					</span><br> <br> <input type="submit" value="Submit"
                                            onclick="document.getElementById('loading').style.display = 'block';" />
                </td>
            </tr>
        </table>
    </form>
<%--
    <br> <br> <i>Test data (For reference)</i> <br>

    <table>
        <tr>
            <td>
                <table>
                    <!-- Weighted table -->
                    <tr>
                        <td>CHEBI:17079</td>
                        <td>0.7665</td>
                    </tr>
                    <tr>
                        <td>CHEBI:46816</td>
                        <td>0.7465</td>
                    </tr>
                    <tr>
                        <td>CHEBI:28658</td>
                        <td>0.7465</td>
                    </tr>
                    <tr>
                        <td>CHEBI:28611</td>
                        <td>0.7465</td>
                    </tr>
                    <tr>
                        <td>CHEBI:28594</td>
                        <td>0.6915</td>
                    </tr>
                    <tr>
                        <td>CHEBI:17048</td>
                        <td>0.6915</td>
                    </tr>
                    <tr>
                        <td>CHEBI:7852</td>
                        <td>0.60575</td>
                    </tr>
                    <tr>
                        <td>CHEBI:3540</td>
                        <td>0.509</td>
                    </tr>
                </table>
            </td>
            <td>
                <table>
                    <!-- Plain table -->
                    <tr>
                        <td>CHEBI:17079</td>
                    </tr>
                    <tr>
                        <td>CHEBI:46816</td>
                    </tr>
                    <tr>
                        <td>CHEBI:28658</td>
                    </tr>
                    <tr>
                        <td>CHEBI:28611</td>
                    </tr>
                    <tr>
                        <td>CHEBI:28594</td>
                    </tr>
                    <tr>
                        <td>CHEBI:17048</td>
                    </tr>
                    <tr>
                        <td>CHEBI:7852</td>
                    </tr>
                    <tr>
                        <td>CHEBI:3540</td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>--%>

    <script type="text/javascript">
        document.getElementById("loading").style.display = "none";
        display('plain');
    </script>

</center>
</body>
</html>
