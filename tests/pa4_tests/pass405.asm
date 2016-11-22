  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
  5         LOADL        1
  6         LOADL        1
  7         CALL         newobj  
  8         STORE        3[LB]
  9         LOADL        5
 10         LOAD         3[LB]
 11         LOADL        0
 12         CALL         fieldref
 13         CALL         add     
 14         STORE        4[LB]
 15         LOAD         4[LB]
 16         CALL         putintnl
 17         RETURN (0)   1
