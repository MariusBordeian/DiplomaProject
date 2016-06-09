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
        <!--Import Google Icon Font-->
        <link href="css/icon.css" rel="stylesheet">

        <!--Import materialize.css-->
        <link type="text/css" rel="stylesheet" href="css/materialize.css" media="screen,projection" />
		
		<link type="text/css" rel="stylesheet" href="css/loader.css" />
		<link type="text/css" rel="stylesheet" href="css/materialLoader.css" />

        <script type="text/javascript" src="js/materialize.min.js"></script>
</head>

<body>

    <!-- LOADER -->
    <div id="loader-wrapper">
        <div id="loader"></div>

        <div class="loader-section section-left"></div>
        <div class="loader-section section-right"></div>
    </div>
    <!-- END LOADER -->

    <!-- TITLE -->
    <div id="titleCard" class="card-panel light-blue accent-4" style="margin-top: 0; padding: 10px 20px;">
        <h5><span onclick="hideTitle()" class="cyan-text text-lighten-5" style="cursor:pointer;">CNC Interface</span></h5>
    </div>
    <!-- END TITLE -->
	
    <div id="applicationContainer">
        <div class="row">
            <div class="col s3">
                <div class="card-panel">

                    <div class="title-section">
                        <span>Machine Position</span>
                    </div>
                    <div class="coordinates-section">
                        <div class="card"><span>X: </span><span id="xCoord"></span>
                        </div>
                        <div class="card"><span>Y: </span><span id="yCoord"></span>
                        </div>
                        <div class="card"><span>Z: </span><span id="zCoord"></span>
                        </div>
                    </div>
                    <div class="zero-machine-section">
                        <button onclick="zeroMachine()" class="waves-effect waves-teal btn">Zero Machine (G92)</button>
                    </div>
                    <div class="splindle-settings">
                        <span>Machine Settings</span>
                    </div>
                    <div class="settings-section cf">
					
					<div class="left-col-settings">
						 <div>
                            <label>zSafe : </label>
                            <input id="zSafe" type="number" min="0" value="0" />
                        </div>
                        <div>
                            <label>zDepth : </label>
                            <input id="zDepth" type="number" value="0" />
                        </div>
                        <div>
                            <label>zStep : </label>
                            <input id="zStep" type="number" value="0" />
                        </div>
					</div>
					
					<div class="right-col-settings">
					 <div>
                            <label>zSurface : </label>
                            <input id="zSurface" type="number" value="0" />
                        </div>
                        <div>
                            <label>Tool diameter :</label>
                            <input id="toolDiameter" type="number" value="5" />
                        </div>
						<div class="fix">
            <select id="speed">
                <option value=300>300 &#181;s</option>
                <option value=460>460 &#181;s</option>
                <option value=500>500 &#181;s</option>
                <option value=600>600 &#181;s</option>
                <option value=650>650 &#181;s</option>
                <option value=700>700 &#181;s</option>
                <option value=750>750 &#181;s</option>
                <option value=800>800 &#181;s</option>
                <option value=850>850 &#181;s</option>
                <option value=900>900 &#181;s</option>
                <option value=950>950 &#181;s</option>
                <option value=1000>1000 &#181;s</option>
							</select>
							<label class="active" >Delay between steps : </label>
						</div>	
					</div>
                       
                       
                    </div>
                    <div class="manual-cnc-control">
                        <span>Manual Control</span>
                    </div>
                    <div class="cf" style="margin-top:20px;margin-left:50px;">
                        <form>
                            <p>
                                <input type="checkbox" class="filled-in" onchange="toggleSpindlee()" id="toggleSpindle" />
                                <label for="toggleSpindle">Toggle spindle</label>
                            </p>
                        </form>
                    </div>
                    <div class="butttons-section cf">
                    </div>

                    <table>
                        <tr>
                            <td rowspan="4">
                                <button onclick="alterPosition('X','minus')" class="waves-effect waves-light btn">X-</button>
                            </td>
                            <td>
                                <button onclick="alterPosition('Z','plus')" class="waves-effect waves-light btn">Z+</button>
                            </td>
                            <td rowspan="4">
                                <button onclick="alterPosition('X','plus')" class="waves-effect waves-light btn">X+</button>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <button onclick="alterPosition('Y','plus')" class="waves-effect waves-light btn">Y+</button>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <button onclick="alterPosition('Y','minus')" class="waves-effect waves-light btn">Y-</button>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <button onclick="alterPosition('Z','minus')" class="waves-effect waves-light btn">Z-</button>
                            </td>
                        </tr>
                    </table>
                    <div class="scaleButtons cf">
                        <button onclick="alterIScale('minus')" class="btn-floating btn-large waves-effect waves-light">-</button>
                        <input type="number" id="iScale" value=1>
                        <button onclick="alterIScale('plus')" class="btn-floating btn-large waves-effect waves-light">+</button>
                    </div>
                </div>
            </div>
            <div class="col s9">
                <div class="card-panel">
                    <div class="top-section cf">
                        <div class="left-section">
						<div id="materialLoader" class="loader materialLoaderCenter">
						  <svg class="circular">
							<circle class="path" cx="50" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10"/>
						  </svg>
						</div>
                            <div class="plotContainer">
                                <div style="width: 100%;height:550px;overflow:scroll">
                                    <svg id="plotArea" width="800px" height="550px">
										<!-- svg goes here -->
									</svg>
                                </div>
                                <div style="width:100%;margin-left: 10px;margin-top: 10px;display: inline-flex;">
                                    <span style="display: inline-block; line-height: 60px;">Zoom</span>

                                    <!-- <div style="width: 80%; margin-left: 20px;" id="slider"></div> -->
                                    <div style="width: 90%; margin-left: 20px;">
                                        <p class="range-field" style="width: 100%;">
                                            <input id="zoomFactor" type="range" oninput="zoom()" min="1" max="30" step="0.1" value="7" />
                                        </p>
                                    </div>

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
                        <!--
							  <button onclick="plotObjectByGcode()">Plot</button>
							  <button onclick="startPositionListener()">Get Coordinates</button>
							  <button onclick="generateDivs()">Generate Divs</button> 
						-->
                        <div id="fileDiv"></div>
                        <button onclick="plotObjectByGcode()" class="waves-effect waves-light btn"><span class="icon-save"></span>Save changes</button>
                        <button onclick="openFileOption()" class="waves-effect waves-light btn"><span class="icon-upload"></span>Upload File (SVG/GCode)</button>
                        <button onclick="sendToCNC()" id="SendCNCButton" class="waves-effect waves-light btn"><span class="icon-send"></span>Send to CNC</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script>
        var pattern = /^(G00|G01)\s(?:X|Z)(-?\d*\.\d*)\s?(?:Y)?(-?\d*\.\d*)?/mi;
        var prevHighLithedKey;
        var toggleSpindleCounter = 0;
        var stateType = {
            off: "off",
            on: "on"
        };
        var sendToCncStatus = stateType.off;
		var ws ;
        $(document).ready(function() {
			// loader 
			setTimeout(function(){
				$('body').addClass('loaded');
			}, 1500);
			// make select materialized	
			$("#speed").material_select();
			// set zoom
			zoom();
			
			/*setTimeout(function(){
				ws.send("");
			},1000);*/
            //startPositionListener();
        });
		function zoom(){
			 $("#plotArea").css("zoom",$("#zoomFactor").val());
		}
		function showLoader(){
			$("#materialLoader").show();
		}
		
		function hideLoader(){
			$("#materialLoader").hide();
		}
		
		function hideTitle(){
			 $('#titleCard').slideToggle('slow');
		}
		
        function alterIScale(type) {
            var currentScale = document.getElementById("iScale").value;
            switch (type) {
                case "plus":
                    document.getElementById("iScale").value = currentScale - 0 + 1;
                    break;
                case "minus":
                    document.getElementById("iScale").value = currentScale - 0 - 1;
                    break;
            }
        }

        function manualOverride(axe, dir) {
            var increment = 0;
            if (dir == "+") {
                increment = 1;
            } else {
                increment = -1;
            }
            switch (axe) {
                case "X":
                    $("#xCoord").html($("#xCoord").html() - 0.0 + increment);
                    break;
                case "Y":
                    $("#yCoord").html($("#yCoord").html() - 0.0 + increment);
                    break;
                case "Z":
                    $("#zCoord").html($("#zCoord").html() - 0.0 + increment);
                    break;
            }
        }

        function openFileOption() {
			showLoader();
            resetInterface();
            document.getElementById("fileDiv").innerHTML = "";
            document.getElementById("fileDiv").innerHTML = "<input type=\"file\" id=\"file1\" accept=\".ngc, .svg\"  onchange=\"readFile(this)\" style=\"display:none\">";
            var file = document.getElementById("file1").click();
        }

        function zeroMachine() {
            var speed = document.getElementById("speed").value;
            $.ajax({
                url: "/CNC/GUI?load=whatever&action=zeroMachine&speed=" + speed,
                method: "get"
            }).done(function(msg) {
                getSpindlePosition();
            }).fail(function(msg) {});
        }

        function readFile(obj) {
			
            var file = obj.files[0];
            var extensionType = {
                svg: "svg",
                gcode: "ngc"
            };
            if (file) {
                var reader = new FileReader();
                reader.readAsText(file, "UTF-8");

                var fileNameComponents = file.name.split('.');
                var extension = fileNameComponents[fileNameComponents.length - 1];

                if (extension == extensionType.svg) {
                    reader.onload = function(evt) {
                        var str = evt.target.result;
                        var zSafe = $("#zSafe").val();
                        var zDepth = $("#zDepth").val();
                        var zStep = $("#zStep").val();
                        var zSurface = $("#zSurface").val();
                        var toolDiameter = $("#toolDiameter").val();


                        $.ajax({
                            url: "/CNC/GUI",
                            type: 'POST',
                            dataType: 'application/x-www-form-urlencoded',
                            data: 'action=getGCodeFromSVGFile&zSafe=' + zSafe + '&zDepth=' + zDepth + '&zStep=' + zStep + '&zSurface=' + zSurface + '&toolDiameter=' + toolDiameter + '&data=' + encodeURIComponent(str)
                        }).done(function(msg) {
                            generateDivs(msg.responseText);
                            plotObjectByGcode();
                        }).fail(function(msg) {
                            console.log("in fail!");
                            generateDivs(msg.responseText);
                            plotObjectByGcode();
                        });
                    };
                } else if (extension == extensionType.gcode) {
                    reader.onload = function(evt) {
                        //console.log(evt.target.result);
                        generateDivs(evt.target.result);
                        plotObjectByGcode();
                    };
                }

                reader.onerror = function(evt) {
                    console.log("error reading file");
                };
            }
			hideLoader();
        }

        function resetInterface() {
            $("#plotArea").html("");
            $("#gcodeLinesContainer").html("");
            $("#lineCounterContainer").html("");

            prevHighLithedKey = null;
        }

        function sendToCNC() {
            if (sendToCncStatus == stateType.off) {
                var lineElements = document.getElementsByClassName("gcodeLine");
                var speed = document.getElementById("speed").value;
                var toSendArray = [];
                var matcher = [];
                for (var i = 0; i < lineElements.length; i++) {
                    matcher = pattern.exec(lineElements[i].innerHTML);
                    if (!matcher[3]) {
                        toSendArray.push(speed + "#" + matcher[2] + "\n");
                    } else {
                        toSendArray.push(speed + "#" + matcher[2] + "#" + matcher[3] + "\n");
                    }
                }

                $.ajax({
                    url: "/CNC/GUI",
                    type: 'POST',
                    dataType: "application/json",
                    data: 'action=sendToCnc&data=' + toSendArray.toString()
                }).done(function(msg) {
                    //sendToCncStatus = !sendToCncStatus;
                }).fail(function(msg) {
                    //sendToCncStatus=!sendToCncStatus;
                });
		sendToCncStatus=stateType.on;
            } else if (sendToCncStatus == stateType.on) {
                $.ajax({
                    url: "/CNC/GUI",
                    type: 'POST',
                    dataType: "application/json",
                    data: 'action=stopCnc'
                }).done(function(msg) {
		}).fail(function(msg) {
		});
		sendToCncStatus=stateType.off;
            }
        }

        function toggleSpindlee() {
            toggleSpindleCounter++;
            var state = stateType.off;
            if (toggleSpindleCounter % 2 == 0) {
                state = stateType.off;
                toggleSpindleCounter = 0;
            } else {
                state = stateType.on;
                toggleSpindleCounter = 1;
            }
            $.ajax({
                url: "/CNC/GUI?load=whatever&action=toggleSpindle&state=" + state,
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
                gcodeLines = $(".gcodeLine");
                gcodeLines.keydown(function(event) {
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
                if(prevSVGElement!=null){
					prevSVGElement.setAttribute("stroke-width", "0.2");
					if (sendToCncStatus == stateType.off) {
						prevSVGElement.setAttribute("stroke", (prevHighLithedKey.indexOf("G01") >= 0) ? "black" : "green");
					}				
				}
            }

            var localPattern = /^(G00|G01)\s(?:X)(-?\d*\.\d*)\s?(?:Y)?(-?\d*\.\d*)?/mi;
            var localPrev = $(obj).prev();
            var matcher1; //= localPattern.exec(localPriv.html());
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
                    var idKey = "line_" + matcher2[1] + "X" + x1 + "Y" + y1 + "X" + x2 + "Y" + y2;
                    prevHighLithedKey = idKey;
                    var currentSVGElement = document.getElementById(idKey);
					if(currentSVGElement!=null){
						currentSVGElement.setAttribute("stroke", "red");
						currentSVGElement.setAttribute("stroke-width", "0.5");
					}
                }
            }
        }

        var interval;

        function startPositionListener() {
            interval = setInterval(function() {
                getSpindlePosition();
            }, 100);
        }

        function plotObjectByGcode() {
			showLoader();
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
                    var idKey = "line_" + currentLine[1] + "X" + coordx1 + "Y" + coordy1 + "X" + coordx2 + "Y" + coordy2;
                    var lineSpecs = {
                        x1: coordx1,
                        y1: coordy1,
                        x2: coordx2,
                        y2: coordy2,
                        stroke: (currentLine[1] == "G00") ? 'green' : 'black',
                        'stroke-width': 0.2,
                        fill: 'red',
                        id: idKey
                    };
                    var line = makeSVG('line', lineSpecs);
                    $("#plotArea").append(line);

                    prevLine = currentLine;
                    i++;
                    currentLine = pattern.exec(gcodeDivs[i].innerHTML);
                } else if (currentLine && currentLine[3]) {
                    prevLine = currentLine;
                    i++;
                    currentLine = pattern.exec(gcodeDivs[i].innerHTML);
                    continue;
                } else {
                    i++;
                    if (i < gcodeDivs.length)
                        currentLine = pattern.exec(gcodeDivs[i].innerHTML);
                    continue;
                }
            }
			
			hideLoader();
        }

        function makeSVG(tag, attrs) {
            var el = document.createElementNS('http://www.w3.org/2000/svg', tag);
            for (var k in attrs)
                el.setAttribute(k, attrs[k]);
            return el;
        }
        var lastCoords = "";
        var lastColoredLine = -1;
        var gcodeLines = [];

        function getSpindlePosition() {

            $.ajax({
                url: "/CNC/GUI?load=whatever&action=getSpindlePosition",
                method: "get"
            }).done(function(msg) {
//                console.log(msg+"\t"+msg.length);

                if (lastCoords != msg && sendToCncStatus == stateType.on) {
                    lastCoords = msg;
                    highlightElement(gcodeLines[lastColoredLine]);
                    lastColoredLine++;
                } else if (sendToCncStatus == stateType.off) {
                    lastColoredLine = -1;
                }
                var coordinates = "";
                if (msg.indexOf("#") > -1) {
                    coordinates = msg.split("#");
                    if (coordinates.length > 3) {
                        $("#xCoord").html(coordinates[0]);
                        $("#yCoord").html(coordinates[1]);
                        $("#zCoord").html(coordinates[2]);
                        $("#toggleSpindle").prop('checked', (coordinates[3] == stateType.on));
                        sendToCncStatus = coordinates[4];

                        if (sendToCncStatus == stateType.on) {
                            $("#generatedDivs *").css("pointer-events", "none");
                            $("#SendCNCButton").html("Stop CNC");
                        } else if (sendToCncStatus == stateType.off) {
                            $("#generatedDivs *").css("pointer-events", "all");
                            $("#SendCNCButton").html("Start CNC");
                        }
                    }

                }
                //console.log("Current position "+msg);
            }).fail(function(msg) {});
        }

        function alterPosition(axis, dir) {
            var currentX = document.getElementById("xCoord").innerHTML;
            var currentY = document.getElementById("yCoord").innerHTML;
            var currentZ = document.getElementById("zCoord").innerHTML;
            var incrementScale = document.getElementById("iScale").value;
            var speed = document.getElementById("speed").value;
            $.ajax({
                url: "/CNC/GUI?load=whatever&action=alterPosition&axis=" + axis + "&dir=" + dir + "&currX=" + currentX + "&currY=" + currentY + "&currZ=" + currentZ + "&incrementScale=" + incrementScale + "&speed=" + speed,
                method: "get"
            }).done(function(msg) {

            }).fail(function(msg) {});
        }
		
		ws = new WebSocket("ws://"+window.location.host+"/CNC/ws");
		
		ws.onopen = function(){
		};
		
		ws.onmessage = function(message){
			var msg=message.data;
			console.log(lastColoredLine+" " +msg);

		lastColoredLine++;

		if (lastCoords.substring(0, lastCoords.lastIndexOf("#")) != msg.substring(0, msg.lastIndexOf("#")) 
			&& sendToCncStatus == stateType.on) {
                    
                    highlightElement(gcodeLines[lastColoredLine+1]);
                    
                } 
		
		lastCoords = msg;
                
		var coordinates = "";
                if (msg.indexOf("#") > -1) {
                    coordinates = msg.split("#");
                    if (coordinates.length > 3) {
                        $("#xCoord").html(coordinates[0]);
                        $("#yCoord").html(coordinates[1]);
                        $("#zCoord").html(coordinates[2]);
                        $("#toggleSpindle").prop('checked', (coordinates[3] == stateType.on));
                        sendToCncStatus = coordinates[4];
			if (sendToCncStatus == stateType.off) {
                   		 lastColoredLine = -1;
                	}
                        if (sendToCncStatus == stateType.on) {
                            $("#generatedDivs *").css("pointer-events", "none");
                            $("#SendCNCButton").html("Stop CNC");
                        } else if (sendToCncStatus == stateType.off) {
                            $("#generatedDivs *").css("pointer-events", "all");
                            $("#SendCNCButton").html("Start CNC");
                        }
                    }

                }

		};
	
		function connectWs(){
			while(ws.readyState!=1){
				
			}
			ws.send("");
		}
	
         function postToServer(){
            ws.send("");
        }
        function closeConnect(){
            ws.close();
        }
    </script>

</body>

</html>