  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
  5         LOADL        24
  6         STORE        3[LB]
  7         LOADL        0
  8         STORE        4[LB]
  9         JUMP         L12
 10  L11:   PUSH         1
 11         LOAD         4[LB]
 12         STORE        5[LB]
 13         LOAD         4[LB]
 14         LOADL        1
 15         CALL         add     
 16         STORE        4[LB]
 17         POP          1
 18  L12:   LOAD         4[LB]
 19         LOADL        1025
 20         CALL         lt      
 21         JUMPIF (1)   L11
 22         LOAD         3[LB]
 23         CALL         putintnl
 24         RETURN (0)   1
