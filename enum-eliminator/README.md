# enum Eliminator

This directory contains a simple tool to replace Java `enums` with
integer constants. Enums are still handled only partially by JayHorn,
and sometimes software can be analysed more efficiently after
rewriting `enums`.

To build the enum eliminator, just call `sbt assembly`, and then run
the script `elim-enums`.