  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
  5         LOADL        0
  6         LOADL        0
  7         CALL         newobj  
  8         STORE        3[LB]
  9         LOADL        0
 10         STORE        4[LB]
 11         JUMP         L12
 12  L11:   PUSH         0
 13         LOAD         3[LB]
 14         CALLI        L13
 15         POP          1
 16         LOAD         4[LB]
 17         LOADL        1
 18         CALL         add     
 19         STORE        4[LB]
 20         POP          0
 21  L12:   LOAD         4[LB]
 22         LOADL        1025
 23         CALL         lt      
 24         JUMPIF (1)   L11
 25         LOADL        25
 26         CALL         putintnl
 27         RETURN (0)   1
 28  L13:   PUSH         0
 29         LOADL        55
 30         RETURN (1)   0
