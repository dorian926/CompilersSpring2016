  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         0
  5         RETURN (0)   1
  6         PUSH         2
  7         LOADL        3
  8         STORE        3[LB]
  9         LOAD         -1[LB]
 10         STORE        4[LB]
 11         RETURN (0)   1
