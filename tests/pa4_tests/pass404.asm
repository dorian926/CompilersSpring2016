  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
  5         LOADL        1
  6         CALL         neg     
  7         STORE        3[LB]
  8         LOADL        0
  9         STORE        4[LB]
 10         JUMP         L12
 11  L11:   PUSH         0
 12         LOAD         4[LB]
 13         LOADL        1
 14         CALL         add     
 15         STORE        4[LB]
 16         LOAD         4[LB]
 17         STORE        3[LB]
 18         POP          0
 19  L12:   LOAD         4[LB]
 20         LOADL        4
 21         CALL         lt      
 22         JUMPIF (1)   L11
 23         LOAD         3[LB]
 24         CALL         putintnl
 25         RETURN (0)   1
