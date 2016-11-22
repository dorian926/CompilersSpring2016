  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
  5         LOADL        4
  6         STORE        3[LB]
  7         LOAD         3[LB]
  8         CALL         newarr  
  9         STORE        4[LB]
 10         RETURN (0)   1
