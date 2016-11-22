  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        -1
  6         LOADL        2
  7         CALL         newobj  
  8         STORE        3[LB]
  9         LOADL        0
 10         LOAD         3[LB]
 11         CALL         fieldupd
 12         LOADL        1
 13         LOADL        3
 14         CALL         fieldupd
 15         RETURN (0)   1
