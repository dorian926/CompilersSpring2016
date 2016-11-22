  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        1
  6         LOADL        1
  7         CALL         newobj  
  8         STORE        3[LB]
  9         LOAD         3[LB]
 10         CALLI        L11
 11         RETURN (0)   1
 12  L11:   PUSH         1
 13         LOADL        10
 14         STORE        3[LB]
 15         LOAD         3[LB]
 16         CALL         putintnl
 17         RETURN (0)   0
