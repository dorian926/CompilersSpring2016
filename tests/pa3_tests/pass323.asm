  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        3
  6         STORE        3[LB]
  7         RETURN (0)   1
  8         PUSH         0
  9         LOAD         -1[LB]
 10         RETURN (1)   1
 11         LOAD         -1[LB]
 12         RETURN (1)   1
