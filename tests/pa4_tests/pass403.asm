  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        2
  6         STORE        3[LB]
  7         LOAD         3[LB]
  8         LOADL        2
  9         CALL         eq      
 10         JUMPIF (0)   L11
 11         LOADL        3
 12         STORE        3[LB]
 13         JUMP         L12
 14  L11:   LOADL        1
 15         CALL         neg     
 16         STORE        3[LB]
 17  L12:   LOAD         3[LB]
 18         CALL         putintnl
 19         RETURN (0)   1
