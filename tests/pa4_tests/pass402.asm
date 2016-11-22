  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
  5         LOADL        1
  6         STORE        3[LB]
  7         LOAD         3[LB]
  8         LOADL        2
  9         LOAD         3[LB]
 10         CALL         mult    
 11         CALL         add     
 12         LOADL        1
 13         CALL         sub     
 14         STORE        4[LB]
 15         LOAD         4[LB]
 16         CALL         putintnl
 17         RETURN (0)   1
