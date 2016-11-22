  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        10
  6         CALL         newarr  
  7         STORE        3[LB]
  8         LOAD         3[LB]
  9         LOADL        1
 10         CALL         neg     
 11         CALL         fieldupd
 12         RETURN (0)   1
