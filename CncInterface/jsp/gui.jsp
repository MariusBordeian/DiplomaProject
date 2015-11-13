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
            <select>
                <option>Feed Rate : 300</option>
                <option>Feed Rate : 400</option>
                <option>Feed Rate : 500</option>
            </select>
            <select>
          <%--      <option>Distance : 3</option>
                <option>Distance : 4</option>
                <option>Distance : 5</option>--%>
            </select>
        </div>
        <div class="manual-cnc-control">
            <span>Manual Control</span>
        </div>
        <div class="butttons-section cf">
            <table>
                <tr>
                    <td rowspan="4"><button onclick="manualOverride('X','-')">X-</button></td>
                    <td><button onclick="manualOverride('Z','+')">Z+</button></td>
                    <td rowspan="4"><button onclick="manualOverride('X','+')">X+</button></td>
                </tr>
                <tr>
                    <td><button onclick="manualOverride('Y','+')">Y+</button></td>
                </tr>
                <tr>
                    <td><button onclick="manualOverride('Y','-')">Y-</button></td>
                </tr>
                <tr>
                    <td><button onclick="manualOverride('Z','-')">Z-</button></td>
                </tr>
            </table>
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
            <div class="right-section">

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
                          <button onclick="getSpindlePosition()">Get Coordinates</button>
                          <button onclick="generateDivs()">Generate Divs</button>--%>
            <input type="file" id="file1" onchange="readFile(this)" style="display:none">
            <button onclick="plotObjectByGcode()">Save changes</button>
            <button onclick="openFileOption();return;">Upload SVG</button>
            <button onclick="openFileOption();return;">Upload GCode</button>
            <button onclick="sendToCNC()">Send to CNC</button>
        </div>
    </div>
</div>
<script>


    $(document).ready(function () {
        $("#slider").slider({
            max: 30,
            min: 1,
            step: 0.2,
            slide: function (event, ui) {
                $("#plotArea").css("zoom", ui.value);
            }
        });
        zeroMachine();
    });

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
        var file = document.getElementById("file1").click();

    }
    function zeroMachine() {
        $("#xCoord").html("0.00000");
        $("#yCoord").html("0.00000");
        $("#zCoord").html("0.00000");
    }
    function readFile(obj) {
        var file = obj.files[0];
        if (file) {
            var reader = new FileReader();
            reader.readAsText(file, "UTF-8");
            reader.onload = function (evt) {
                //console.log(evt.target.result);
                generateDivs(evt.target.result);
                plotObjectByGcode();
            };
            reader.onerror = function (evt) {
                console.log("error reading file");
            };
        }
    }

    function sendToCNC() {
        getSpindlePosition();
    }

    function generateDivs(gcode) {
        var lines = gcode.split("\n");
        var pattern = new RegExp("^(G01)\s(?:X|Z)(-?\d*\.\d*)\s?(?:Y)?(-?\d*\.\d*)?");
        var lineCounter = 0;
        $("#gcodeLinesContainer").html("");
        $("#lineCounterContainer").html("");
        debugger;
        for (var i = 0; i < lines.length; i++) {
            if (pattern.test(lines[i])) {
                var coords1 = lines[i].split(" ");
                var x = coords1[1].slice(1, coords1[1].length);
                var y = coords1[2].slice(1, coords1[2].length);
                var z = coords1[3].slice(1, coords1[3].length);
                if (i == 0) {
                    var divElement = "<div class='gcodeLine' contenteditable='false' onClick=\"highlightElement(this)\">" + lines[i] + "</div>";
                } else {
                    var divElement = "<div class='gcodeLine' contenteditable='true' onClick=\"highlightElement(this)\">" + lines[i] + "</div>";
                }

                var lineNumberElement = "<div class='gcodeLineCounter'>" + lineCounter + ":</div>";
                $("#gcodeLinesContainer").append(divElement);
                $("#lineCounterContainer").append(lineNumberElement);
                lineCounter++;
            }
        }

        $(".gcodeLine").keydown(function (event) {
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
                var pattern = new RegExp("(G01 X)([0-9]*.[0-9]*)( Y)([0-9]*.[0-9]*)( Z)([0-9]*.[0-9]*)*");
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
        var coords1 = $(obj).prev().html().split(" ");
        var coords2 = $(obj).html().split(" ");
        var x1 = coords1[1];
        var y1 = coords1[2];
        var x2 = coords2[1];
        var y2 = coords2[2];
        var idKey = "line_" + x1 + y1 + x2 + y2;
        $("line").attr("stroke", "black");
        document.getElementById(idKey).setAttribute("stroke", "red");
    }
    function getSpindlePosition() {
        window.setInterval(function () {
            ajaxCall();
        }, 24);
    }

    function plotObjectByGcode() {
        $("#plotArea").html("");
        var gcodeDivs = $("#generatedDivs").find("div");
        for (var i = 1; i < gcodeDivs.length; i++) {
            var coords1 = gcodeDivs[i - 1].innerHTML.split(" ");
            var coords2 = gcodeDivs[i].innerHTML.split(" ");
            var idKey = "line_" + coords1[1] + "" + coords1[2] + "" + coords2[1] + "" + coords2[2];
            var lineSpecs = {x1: coords1[1].slice(1, coords1[1].length), y1: coords1[2].slice(1, coords1[2].length), x2: coords2[1].slice(1, coords2[1].length), y2: coords2[2].slice(1, coords2[2].length), stroke: 'black', 'stroke-width': 0.2, fill: 'red', id: idKey};
            var line = makeSVG('line', lineSpecs);
            $("#plotArea").append(line);
        }
    }

    function makeSVG(tag, attrs) {
        var el = document.createElementNS('http://www.w3.org/2000/svg', tag);
        for (var k in attrs)
            el.setAttribute(k, attrs[k]);
        return el;
    }

    function ajaxCall() {
        $.ajax({
            url: "/CNC/GUI?load=whatever&action=getSpindlePosition",
            method: "get"
        }).done(function (msg) {
            var coordinates = JSON.parse(msg);
            $("#xCoord").html(coordinates.X);
            $("#yCoord").html(coordinates.Y);
            $("#zCoord").html(coordinates.Z);
            console.log(coordinates);
        }).fail(function (msg) {
        });
    }
    function alterPosition(axis, dir) {
        $.ajax({
            url: "/CNC/GUI?load=whatever&action=alterPosition&axis" + axis + "&dir=" + dir,
            method: "get"
        }).done(function (msg) {
        }).fail(function (msg) {
        });
    }


</script>

</body>
</html>




