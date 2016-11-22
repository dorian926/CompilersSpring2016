  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
  5         LOADL        1
  6         STORE        3[LB]
  7         LOADL        3
  8         STORE        4[LB]
  9         LOAD         3[LB]
 10         JUMPIF (0)   L11
 11         PUSH         1
 12         LOADL        5
 13         STORE        5[LB]
 14         POP          1
 15         JUMP         L12
 16  L11:   LOADL        4
 17         STORE        4[LB]
 18  L12:   LOAD         4[LB]
 19         CALL         putint  
 20         CALL         puteol  
 21         RETURN (0)   1
