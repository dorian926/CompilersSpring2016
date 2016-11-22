  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADA        0[OB]
  6         LOADL        0
  7         CALL         fieldref
  8         STORE        3[LB]
  9         RETURN (0)   1
