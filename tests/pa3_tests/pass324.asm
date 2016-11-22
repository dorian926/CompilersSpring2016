  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
  5         LOADL        -1
  6         LOADL        1
  7         CALL         newobj  
  8         STORE        3[LB]
  9         CALL         L11
 10         LOADL        0
 11         CALL         fieldref
 12         CALL         add     
 13         STORE        4[LB]
 14         RETURN (0)   1
 15  L11:   PUSH         0
 16         LOADL        3
 17         RETURN (1)   0
 18         LOADL        3
 19         RETURN (1)   0
