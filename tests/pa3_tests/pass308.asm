  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        20
  6         LOADA        0[OB]
  7         CALL         L11
  8         STORE        3[LB]
  9         RETURN (0)   1
 10  L11:   PUSH         0
 11         LOADL        50
 12         RETURN (1)   1
 13         LOADL        50
 14         RETURN (1)   1
