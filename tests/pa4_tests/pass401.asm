  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        1
  6         STORE        3[LB]
  7         LOAD         3[LB]
  8         CALL         putintnl
  9         RETURN (0)   1
