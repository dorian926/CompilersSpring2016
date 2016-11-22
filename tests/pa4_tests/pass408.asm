  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         3
  5         LOADL        8
  6         STORE        3[LB]
  7         LOAD         3[LB]
  8         CALL         newarr  
  9         STORE        4[LB]
 10         LOAD         4[LB]
 11         CALL         pred    
 12         LOADI  
 13         STORE        5[LB]
 14         LOAD         5[LB]
 15         CALL         putintnl
 16         RETURN (0)   1
