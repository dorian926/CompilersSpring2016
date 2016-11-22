  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        -1
  6         LOADL        0
  7         CALL         newobj  
  8         STORE        3[LB]
  9         CALL         L11
 10         RETURN (0)   1
 11  L11:   PUSH         1
 12         LOADA        0[OB]
 13         CALL         L11
 14         LOADA        0[OB]
 15         CALL         L12
 16         STORE        3[LB]
 17         RETURN (0)   0
 18  L12:   PUSH         0
 19         LOADL        5
 20         RETURN (1)   1
 21         LOADL        5
 22         RETURN (1)   1
