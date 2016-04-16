<!DOCTYPE html>
<html>
<head>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="js/jquery-1.7.1.min.js"></script>
    <script src="js/slider-jqueryui.js"></script>
    <script src="js/jquery-ui.min.js"></script>
    <link rel="stylesheet" type="text/css" href="css/jquery-ui.css">
    <link rel="stylesheet" type="text/css" href="css/custom.css">
</head>
<body>

<div id="applicationContainer" class="cf">
    <div class="left-col">
        <div class="title-section">
            <span>Machine Position</span>
        </div>
        <div class="coordinates-section">
            <div><span>X: </span><span id="xCoord"></span></div>
            <div><span>Y: </span><span id="yCoord"></span></div>
            <div><span>Z: </span><span id="zCoord"></span></div>
        </div>
        <div class="zero-machine-section">
            <button onclick="zeroMachine()" style="width: 100%;height: 35px;">Zero Machine (G92)</button>
        </div>
        <div class="splindle-settings">
            <span>Machine Settings</span>
        </div>
        <div class="settings-section">
            <div>
                <label>zSafe : </label>
                <input id="zSafe" type="number" min="0" value="0"/>
            </div>
            <div>
                <label>zDepth : </label>
                <input id="zDepth" type="number" value="0"/>
            </div>
            <div>
                <label>zStep : </label>
                <input id="zStep" type="number" value="0"/>
            </div>
            <div>
                <label>zSurface : </label>
                <input id="zSurface" type="number" value="0"/>
            </div>
            <div>
                <label>Tool diameter  :</label>
                <input id="toolDiameter" type="number" value="5"/>
            </div>

            <select id="speed">
                <option value=460>Delay between steps : 460 &#181;s</option>
                <option value=500>Delay between steps : 500 &#181;s</option>
                <option value=600>Delay between steps : 600 &#181;s</option>
            </select>

        </div>
        <div class="manual-cnc-control">
            <span>Manual Control</span>
        </div>
         <div class="cf" style="margin-top:20px;margin-left:50px;">
             <span style="">Toggle spindle</span>
             <input style="" type="checkbox" onchange="toggleSpindle()"  id="toggleSpindle">
         </div>
        <div class="butttons-section cf">
            <table>
                <tr>
                    <td rowspan="4"><button onclick="alterPosition('X','minus')">X-</button></td>
                    <td><button onclick="alterPosition('Z','plus')">Z+</button></td>
                    <td rowspan="4"><button onclick="alterPosition('X','plus')">X+</button></td>
                </tr>
                <tr>
                    <td><button onclick="alterPosition('Y','plus')">Y+</button></td>
                </tr>
                <tr>
                    <td><button onclick="alterPosition('Y','minus')">Y-</button></td>
                </tr>
                <tr>
                    <td><button onclick="alterPosition('Z','minus')">Z-</button></td>
                </tr>
            </table>
            <div>
            <button onclick="alterIScale('minus')">-</button>
            <input type="number" id="iScale" value=1>
            <button onclick="alterIScale('plus')">+</button>


            </div>
        </div>
    </div>
    <div class="right-col">
        <div class="top-section cf">
            <div class="left-section">
                <div class="plotContainer">
                    <div style="width: 100%;height:350px;overflow:scroll">
                        <svg id="plotArea" width="300px" height="300px"></svg>
                    </div>
                    <div style="width:100%;margin-left: 10px;margin-top: 10px;display: inline-flex;"><span>Zoom</span>

                        <div style="width: 80%; margin-left: 20px;" id="slider"></div>
                    </div>
                </div>
            </div>
        </div>
        <div id="generatedDivs" class="bottom-section">
            <div id="gcodeLinesContainer">

            </div>
            <div id="lineCounterContainer">

            </div>
        </div>
        <div class="bottom-buttons-section">
            <%--              <button onclick="plotObjectByGcode()">Plot</button>
                          <button onclick="startPositionListener()">Get Coordinates</button>
                          <button onclick="generateDivs()">Generate Divs</button>--%>
            <div id="fileDiv"></div>
            <button onclick="plotObjectByGcode()">Save changes</button>
            <button onclick="openFileOption()">Upload File (SVG/GCode)</button>
            <button onclick="sendToCNC()" id="SendCNCButton">Send to CNC</button>
        </div>
    </div>
</div>
<script>
	var pattern = /^(G00|G01)\s(?:X|Z)(-?\d*\.\d*)\s?(?:Y)?(-?\d*\.\d*)?/mi;
	var prevHighLithedKey;
	var toggleSpindleCounter=0;
    var stateType={
        off:"off",
        on:"on"
    };
	var sendToCncStatus=stateType.off;
    $(document).ready(function () {
        $("#slider").slider({
            max: 30,
            min: 1,
            step: 0.2,
            slide: function (event, ui) {
                $("#plotArea").css("zoom", ui.value);
            }
        });
        startPositionListener();
    });
    function alterIScale(type){
        var currentScale=document.getElementById("iScale").value;
        switch(type){
            case "plus":
                document.getElementById("iScale").value=currentScale-0+1;
            break;
            case "minus":
                document.getElementById("iScale").value=currentScale-0-1;
            break;
        }
    }
    function manualOverride(axe,dir){
        var increment=0;
        if(dir=="+"){
            increment=1;
        }else{
            increment=-1;
        }
        switch (axe){
            case "X": $("#xCoord").html($("#xCoord").html()-0.0+increment); break;
            case "Y": $("#yCoord").html($("#yCoord").html()-0.0+increment);  break;
            case "Z": $("#zCoord").html($("#zCoord").html()-0.0+increment);  break;
        }
    }
    function openFileOption() {
        resetInterface();
        document.getElementById("fileDiv").innerHTML="";
        document.getElementById("fileDiv").innerHTML="<input type=\"file\" id=\"file1\" accept=\".ngc, .svg\"  onchange=\"readFile(this)\" style=\"display:none\">";
        var file = document.getElementById("file1").click();
    }

    function zeroMachine() {
    var speed=document.getElementById("speed").value;
        $.ajax({
                    url: "/CNC/GUI?load=whatever&action=zeroMachine&speed="+speed,
                    method: "get"
                }).done(function (msg) {
                    getSpindlePosition();
                }).fail(function (msg) {
                });
    }
    function readFile(obj) {
        var file = obj.files[0];
        var extensionType={
             svg:"svg",
            gcode:"ngc"
        };
        if (file) {
            var reader = new FileReader();
            reader.readAsText(file, "UTF-8");

            var fileNameComponents=file.name.split('.');
            var extension=fileNameComponents[fileNameComponents.length-1];

            if(extension==extensionType.svg){
                reader.onload = function (evt) {
                    var str=evt.target.result;
                    var zSafe=$("#zSafe").val();
                    var zDepth=$("#zDepth").val();
                    var zStep=$("#zStep").val();
                    var zSurface=$("#zSurface").val();
                    var toolDiameter=$("#toolDiameter").val();


                    $.ajax({
                        url: "/CNC/GUI",
                        type: 'POST',
                        dataType:'application/x-www-form-urlencoded',
                        data:'action=getGCodeFromSVGFile&zSafe='+zSafe+'&zDepth='+zDepth+'&zStep='+zStep+'&zSurface='+zSurface+'&toolDiameter='+toolDiameter+'&data='+encodeURIComponent(str)
                    }).done(function (msg) {
                        generateDivs(msg.responseText);
                        plotObjectByGcode();
                    }).fail(function (msg) {
                        console.log("in fail!");
                        generateDivs(msg.responseText);
                        plotObjectByGcode();
                    });
                };
            }
            else if(extension==extensionType.gcode){
                reader.onload = function (evt) {
                    //console.log(evt.target.result);
                    generateDivs(evt.target.result);
                    plotObjectByGcode();
                };
            }

            reader.onerror = function (evt) {
                console.log("error reading file");
            };
        }

    }

	function resetInterface() {
		$("#plotArea").html("");
		$("#gcodeLinesContainer").html("");
        $("#lineCounterContainer").html("");
		
		prevHighLithedKey = null;
	}
	
    function sendToCNC() {
        if(sendToCncStatus==stateType.off){
            var lineElements=document.getElementsByClassName("gcodeLine");
            var speed=document.getElementById("speed").value;
            var toSendArray=[];
            var matcher=[];
            for(var i=0;i<lineElements.length;i++){
                matcher=pattern.exec(lineElements[i].innerHTML);
                if(!matcher[3]){
                    toSendArray.push(speed+"#"+matcher[2]+"\n");
                }else{
                    toSendArray.push(speed+"#"+matcher[2]+"#"+matcher[3]+"\n");
                }
            }

             $.ajax({
                    url: "/CNC/GUI",
                    type: 'POST',
                    dataType:"application/json",
                    data:'action=sendToCnc&data='+toSendArray.toString()
                }).done(function (msg) {
                    sendToCncStatus=!sendToCncStatus;
                }).fail(function (msg) {
                    //sendToCncStatus=!sendToCncStatus;
                });
        }else if(sendToCncStatus==stateType.on){
            $.ajax({
                url: "/CNC/GUI",
                type: 'POST',
                dataType:"application/json",
                data:'action=stopCnc'
            }).done(function (msg) {
            }).fail(function (msg) {
            });
        }
    }

    function toggleSpindle(){

        toggleSpindleCounter++;
        var state=stateType.off;
        if(toggleSpindleCounter%2==0){
            state=stateType.off;
            toggleSpindleCounter=0;
        }else{
            state=stateType.on;
            toggleSpindleCounter=1;
        }
         $.ajax({
                url: "/CNC/GUI?load=whatever&action=toggleSpindle&state="+state,
                method: "get"
         });

    }
    function generateDivs(gcode) {
        var lines = gcode.split("\n");
        var lineCounter = 0;
        $("#gcodeLinesContainer").html("");
        $("#lineCounterContainer").html("");
		
		var divElement = "<div class='gcodeLine' contenteditable='true' onClick=\"highlightElement(this)\">G00 X0.00000 Y0.00000</div>";
        var lineNumberElement = "<div class='gcodeLineCounter'>" + lineCounter + ":</div>";
		$("#gcodeLinesContainer").append(divElement);
        $("#lineCounterContainer").append(lineNumberElement);
		lineCounter++;
        //debugger;
		var gcodeLineRegex = /((?:G00|G01)\s(?:X|Z)-?\d*\.\d*(?:\sY-?\d*\.\d*(?:\sZ-?\d*\.\d*)?)?)/i;
        for (var i = 0; i < lines.length; i++) {
			var currentLine = gcodeLineRegex.exec(lines[i]);
            if (currentLine && currentLine[1]) {
                var divElement = "<div class='gcodeLine' contenteditable='true' onClick=\"highlightElement(this)\">" + currentLine[1] + "</div>";
                var lineNumberElement = "<div class='gcodeLineCounter'>" + lineCounter + ":</div>";
                $("#gcodeLinesContainer").append(divElement);
                $("#lineCounterContainer").append(lineNumberElement);
                lineCounter++;
            }
        }
        gcodeLines=$(".gcodeLine");
        gcodeLines.keydown(function (event) {
            var object_;
            if (event.which == 38) {
                //arrow up
                object_ = $(this).prev();
                object_.focus();
            } else if (event.which == 40) {
                //arrow down
                object_ = $(this).next();
                object_.focus();
            } else if (event.which == 46) {
                //delete
                object_ = $(this).next();
                $(this).remove();
                object_.focus();
                object_.html("G" + object_.html());
                //remove last lineCounter
                $("#lineCounterContainer").find("div")[$("#lineCounterContainer").find("div").length - 1].remove();
            } else {
                //any other key
                if ($(this).html() == "") {
                    object_ = $(this).next();
                    $(this).remove();
                    object_.focus();
                    object_.html("G" + object_.html());
                    //remove last lineCounter
                    $("#lineCounterContainer").find("div")[$("#lineCounterContainer").find("div").length - 1].remove();
                } else {
                    if (pattern.test($(this).html()) == false) {
                        $(this).css("border", "1px solid red");
                    } else {
                        $(this).css("border", "");
                    }
                }
            }

            highlightElement(object_);
        });
    }
    //$(".gcodeLine").onfocus(highlightElement(this));
	
    function highlightElement(obj) {
        if (prevHighLithedKey) {
            var prevSVGElement = document.getElementById(prevHighLithedKey);
            prevSVGElement.setAttribute("stroke-width", "0.2");
            if (sendToCncStatus == stateType.off) {
                prevSVGElement.setAttribute("stroke", (prevHighLithedKey.indexOf("G01") >= 0) ? "black" : "green");
            }
        }

		var localPattern = /^(G00|G01)\s(?:X)(-?\d*\.\d*)\s?(?:Y)?(-?\d*\.\d*)?/mi;
		var localPrev = $(obj).prev();
		var matcher1 ;//= localPattern.exec(localPriv.html());
		var matcher2 = localPattern.exec($(obj).html());
		while ((localPrev.length > 0) && !(matcher1 = localPattern.exec(localPrev.html()))) {
			localPrev = localPrev.prev();
		}
		if (matcher1 && matcher2) {
			//var coords1 = $(obj).prev().html().split(" ");
			//var coords2 = $(obj).html().split(" ");
			if (matcher1[3] && matcher2[3]) {
				var x1 = matcher1[2];
				var y1 = matcher1[3];
				var x2 = matcher2[2];
				var y2 = matcher2[3];
				var idKey = "line_"+matcher2[1]+"X" + x1 + "Y" + y1 + "X" + x2 + "Y" + y2;
				prevHighLithedKey=idKey;
                var currentSVGElement = document.getElementById(idKey);
                currentSVGElement.setAttribute("stroke", "red");
                currentSVGElement.setAttribute("stroke-width", "0.5");
			}
		}
    }

    var interval;
    function startPositionListener() {
        interval = setInterval(function () {
                getSpindlePosition();
            }, 100);
    }

    function plotObjectByGcode() {
        $("#plotArea").html("");
		
		var gcodeDivs = $("#generatedDivs").find("div");
		
		var prevLine = pattern.exec(gcodeDivs[0].innerHTML);
		var currentLine = pattern.exec(gcodeDivs[1].innerHTML);
        var i = 1;
		while (i < gcodeDivs.length) {
			//var coords1 = gcodeDivs[i - 1].innerHTML.split(" ");
            //var coords2 = gcodeDivs[i].innerHTML.split(" ");
            if ((prevLine && prevLine[3]) && (currentLine && currentLine[3])) {
				var coordx1 = prevLine[2];
				var coordy1 = prevLine[3];
				var coordx2 = currentLine[2];
				var coordy2 = currentLine[3];
				var idKey = "line_"+currentLine[1]+"X" + coordx1 + "Y" + coordy1 + "X" + coordx2 + "Y" + coordy2;
				var lineSpecs = {x1: coordx1, y1: coordy1, x2: coordx2, y2: coordy2, stroke: (currentLine[1] == "G00")?'green':'black', 'stroke-width': 0.2, fill: 'red', id: idKey};
				var line = makeSVG('line', lineSpecs);
				$("#plotArea").append(line);
				
				prevLine=currentLine;
				i++;
				currentLine=pattern.exec(gcodeDivs[i].innerHTML);
			} else if (currentLine && currentLine[3]) {
				prevLine=currentLine;
				i++;
				currentLine=pattern.exec(gcodeDivs[i].innerHTML);
				continue;
			} else {
				i++;
                if(i < gcodeDivs.length)
				    currentLine=pattern.exec(gcodeDivs[i].innerHTML);
				continue;
			}
        }
    }

    function makeSVG(tag, attrs) {
        var el = document.createElementNS('http://www.w3.org/2000/svg', tag);
        for (var k in attrs)
            el.setAttribute(k, attrs[k]);
        return el;
    }
    var lastCoords="";
    var lastColoredLine=0;
    var gcodeLines=[];

    function getSpindlePosition() {

        $.ajax({
            url: "/CNC/GUI?load=whatever&action=getSpindlePosition",
            method: "get"
        }).done(function (msg) {

            if(lastCoords!=msg && sendToCncStatus==stateType.on){
                lastCoords=msg;
                highlightElement(gcodeLines[lastColoredLine]);
                lastColoredLine++;
            }else if(sendToCncStatus==stateType.off){
                lastColoredLine=0;
            }


            var coordinates="";
            if(msg.indexOf("#")>-1){
                coordinates = msg.split("#");
                if (coordinates.length > 3) {
                    $("#xCoord").html(coordinates[0]);
                    $("#yCoord").html(coordinates[1]);
                    $("#zCoord").html(coordinates[2]);
                    $("#toggleSpindle").prop('checked', (coordinates[3] == stateType.on));
                    sendToCncStatus = coordinates[4];

                    if (sendToCncStatus == stateType.on) {
                        $("#generatedDivs *").style("pointer-events", "none");
                        $("#SendCNCButton").html("Stop CNC");
                    } else if (sendToCncStatus == stateType.off) {
                        $("#generatedDivs *").style("pointer-events", "all");
                        $("#SendCNCButton").html("Start CNC");
                    }
                }

            }
            //console.log("Current position "+msg);
        }).fail(function (msg) {
        });
    }
    function alterPosition(axis, dir) {
        var currentX=document.getElementById("xCoord").innerHTML;
        var currentY=document.getElementById("yCoord").innerHTML;
        var currentZ=document.getElementById("zCoord").innerHTML;
        var incrementScale=document.getElementById("iScale").value;
        var speed=document.getElementById("speed").value;
        $.ajax({
            url: "/CNC/GUI?load=whatever&action=alterPosition&axis=" + axis + "&dir=" + dir+ "&currX=" + currentX + "&currY=" + currentY+ "&currZ=" + currentZ+"&incrementScale="+incrementScale+"&speed="+speed,
            method: "get"
        }).done(function (msg) {

        }).fail(function (msg) {
        });
    }


</script>

</body>
</html>




