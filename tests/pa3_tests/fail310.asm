  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         0
  5         LOAD         -1[LB]
  6         LOADL        0
  7         CALL         arrayref
  8         CALL         putint  
  9         CALL         puteol  
 10         RETURN (0)   1
