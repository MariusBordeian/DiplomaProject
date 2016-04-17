# DiplomaProject
CNC Machine Firmware and Add-Ons

This Repozitory reprezents the work on the Diploma Project for Bachelor's Degree for the Technical University of Iasi, Automation and Computer Science.

::Pre-Conditions
 - SVG to contain *ONLY* Paths
 - ATM no arcs available (no G02|G03 implemented), all paths shall be "exploded" into lines (G01)

::Documentation_Notes
 - <b>Prototip</b>
 - "Concluzii si Dezvoltari Ulterioare" (Concluzii	: Utilizari, etc.; 
										 Dezvoltari	: Schimbi capul si cu mici adaptari la *core* - endless possibilities)

::TODO
 - [ ] Detachable motor wires using couplings
 - [ ] redesign interface "/CNC/GUI"
 - [x] SVG2GCode.py from commandline
 - [x] "Machine Settings" Params for SVG2GCode.py
 - [x] <strike>Single Upload Buttons ::Upload File (*.svg, *.gnc)</strike>
 - [x] <strike>Rename Feed Rate to Machine Speed of movement</strike>
 - [x] <strike>Android App.</strike>
 - [x] <strike>Android Wear companion.</strike>
 - [x] <strike>move Serial Communication from Python Script to Server on Java</strike>
 - [x] <strike>remote-<b>ish</b> onDemand start STM to listen on serial</strike>
 - [x] <strike>Scale Select for Increment Buttons</strike>
 - [x] <strike>implement G00 Ploting on the WebGUI [*FIX*]</strike>
 - [x] <strike>initialize (px, py, pz) with last known coords.</strike>
 - [x] <strike>Global pos.Coords on STM.</strike>
 - [ ] <strike>Concurent Client's Access (for more than one client acccessing the Server and Machine)</strike>
 - [ ] <strike>Position tracking using optical sensors from mouse ???</strike>
 - [ ] <strike>Movement feedback (Movement obstruction detected using voltage variation)</strike>
 - [ ] <strike>SVG Paint (lines only, if free time will be available)</strike>
