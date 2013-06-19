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