:- discontiguous table/2.
:- discontiguous tuple/2.

dbase(truthtables,[truthtable,ports,inputs,outputs,rows,cells]).

table(truthtable,[ttid,name]).
table(ports,[pid,name,ttid]).
table(inputs, [iid,pid]).
table(outputs, [oid,pid]).
table(rows, [rid, ttid]).
table(cells, [cid,value,pid,rid]).

tuple(truthtable,[A,B]) :- truthtable(A,B).
tuple(ports,[A,B,C]) :- ports(A,B,C).
tuple(inputs, [A,B]) :- inputs(A,B).
tuple(outputs, [A,B]) :- outputs(A,B).
tuple(rows, [A,B]) :- rows(A,B).
tuple(cells, [A,B,C,D]) :- cells(A,B,C,D).

/* Tree Code */
/* Node Code */
table(node, [nid, name]).
tuple(node, [Nid, Name]) :- node(Nid, Name).

/* Edge Code */
table(edge, [pnid, cnid, value]).
tuple(edge, [Pnid, Cnid, Value]) :- edge(Pnid, Cnid, Value).

truthtable(t1,'Test').

ports(p1,'A',t1).
ports(p2,'B',t1).
ports(p3,'C',t1).
ports(p4,'D',t1).
ports(p5,'S',t1).

inputs(i1,p1).
inputs(i2,p2).
inputs(i3,p3).
inputs(i4,p4).

inputPorts(X,Y,Z) :- inputs(X,A),ports(A,Y,Z).

outputs(o1,p5).

outputPorts(X,Y,Z) :- outputs(X,A),ports(A,Y,Z).

rows(r1,t1).
rows(r2,t1).
rows(r3,t1).
rows(r4,t1).
rows(r5,t1).
rows(r6,t1).
rows(r7,t1).
rows(r8,t1).
rows(r9,t1).

cells(c1,false,p1,r1).
cells(c2,false,p2,r1).
cells(c3,null ,p3,r1).
cells(c4,null ,p4,r1).
cells(c5,false,p5,r1).

cells(c6,false,p1,r2).
cells(c7,true ,p2,r2).
cells(c8,false,p3,r2).
cells(c9,false,p4,r2).
cells(c10,true,p5,r2).

cells(c11,false,p1,r3).
cells(c12,true ,p2,r3).
cells(c13,false,p3,r3).
cells(c14,true ,p4,r3).
cells(c15,false,p5,r3).

cells(c16,false,p1,r4).
cells(c17,true ,p2,r4).
cells(c18,true ,p3,r4).
cells(c19,null ,p4,r4).
cells(c20,false,p5,r4).

cells(c21,true ,p1,r5).
cells(c22,false,p2,r5).
cells(c23,false,p3,r5).
cells(c24,false,p4,r5).
cells(c25,false,p5,r5).

cells(c26,true ,p1,r6).
cells(c27,false,p2,r6).
cells(c28,true ,p3,r6).
cells(c29,false,p4,r6).
cells(c30,true ,p5,r6).

cells(c31,true ,p1,r7).
cells(c32,null ,p2,r7).
cells(c33,null ,p3,r7).
cells(c34,true ,p4,r7).
cells(c35,false,p5,r7).

cells(c36,true ,p1,r8).
cells(c37,true ,p2,r8).
cells(c38,false,p3,r8).
cells(c39,false,p4,r8).
cells(c40,true ,p5,r8).

cells(c41,true, p1,r9).
cells(c42,true, p2,r9).
cells(c43,true, p3,r9).
cells(c44,false,p4,r9).
cells(c55,false,p5,r9).
