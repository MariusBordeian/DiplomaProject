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
 - [ ] SVG2GCode.py from commandline
 - [ ] Merge Upload Buttons into one ::Upload File (*.svg, *.gnc)
 - [ ] Rename Feed Rate to Machine Speed of movement
 - [ ] Check "Distance" from "Machine Settings" Div.
 - [ ] Android Wear companion.
 - [x] <strike>Android App.</strike>
 - [ ] Concurent Client's Access (for more than one client acccessing the Server and Machine)
 - [ ] Movement feedback (Movement obstruction detected using voltage variation)
 - [ ] SVG Paint (lines only, if free time will be available) 
 - [ ] ??? Position tracking using optical sensors from mouse ???
 - [x] <strike>move Serial Communication from Python Script to Server on Java</strike>
 - [x] <strike>remote-<b>ish</b> onDemand start STM to listen on serial</strike>
 - [x] <strike>Scale Select for Increment Buttons</strike>
 - [x] <strike>implement G00 Ploting on the WebGUI [*FIX*]</strike>
 - [x] <strike>initialize (px, py, pz) with last known coords.</strike>
 - [x] <strike>Global pos.Coords on STM.</strike>
