<%--
  User: Andrei Ostafciuc
  Date: 05-Nov-15
  Time: 19:56 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script src="http://code.jquery.com/jquery-1.9.1.js"></script>


<html>
<head>
    <title></title>
</head>
<body>
<div class="cf">
    <div><span class="coordinates">X </span><input id="xInput" type="text"></div>
    <div><span class="coordinates">Y </span><input id="yInput" type="text"></div>
    <div><span class="coordinates">Z </span><input id="zInput" type="text"></div>
</div>
<%--    <div>
        <button onclick="alterPosition('x','+')">X+</button>
        <button onclick="alterPosition('x','-')">X-</button>
    </div>
    <div>
        <button onclick="alterPosition('y','+')">Y+</button>
        <button onclick="alterPosition('y','-')">Y-</button>
    </div>
    <div>
        <button onclick="alterPosition('z','+')">Z+</button>
        <button onclick="alterPosition('z','-')">Z-</button>
    </div>--%>

<button onclick="getSpindlePosition()">Get Spindle Position</button>
</body>
</html>

<script>
    function getSpindlePosition(){
        window.setInterval(function(){
            ajaxCall();
        }, 24);
    }

    function ajaxCall(){
        $.ajax({
            url: "/CncServlet/interface?load=whatever&action=getSpindlePosition",
            method:"get"
        }).done(function(msg) {
            debugger;
            var coordinates=JSON.parse(msg);
            $("#xInput").val(coordinates.X);
            $("#yInput").val(coordinates.Y);
            $("#zInput").val(coordinates.Z);
            console.log(coordinates);
        }).fail(function(msg){
            debugger;
        });
    }
    function alterPosition(axis, dir) {
        $.ajax({
            url: "/CncServlet/interface?load=whatever&action=alterPosition&axis"+axis+"&dir="+dir,
            method:"get"
        }).done(function(msg) {
            debugger;
        }).fail(function(msg){
            debugger;
        });
    }
</script>

<style>
    .coordinates{
        float:left;
        width: 20px;
        font-weight: bold;
    }
    .cf:before, .cf:after { content: ""; display: table; }
    .cf:after { clear: both; }
    .cf { zoom: 1; }
</style>