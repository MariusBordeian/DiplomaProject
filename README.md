# DiplomaProject
CNC Machine Firmware and Add-Ons

This Repozitory reprezents the work on the Diploma Project for Bachelor's Degree for the Technical University of Iasi, Automation and Computer Science.

::Pre-Conditions
 - SVG to contain *ONLY* Paths
 - ATM no arcs available (no G02|G03 implemented), all paths shall be "exploded" into lines (G01)



::TODO
 - remote onDemand start STM to listen on serial
 - SVG2GCode.py from commandline
 - Scale Select for Increment Buttons
 - SVG Paint (lines only)
 - Merge Upload Buttons into one ::Upload File (*.svg, *.gnc)
 - Rename Feed Rate to Machine Speed of movement
 - Check "Distance" from "Machine Settings" Div.
 - move Serial Communication from Python Script to Server on Java
 - Android App.
 - Concurent Client's Access (for more than one client acccessing the Server and Machine)
 - <strike>implement G00 Ploting on the WebGUI [*FIX*]</strike>
 - <strike>initialize (px, py, pz) with last known coords.</strike>
 - <strike>Global pz on STM.</strike>
