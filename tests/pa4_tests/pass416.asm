  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        1
  6         STORE        3[LB]
  7         PUSH         1
  8         LOADL        5
  9         STORE        4[LB]
 10         LOAD         3[LB]
 11         LOAD         4[LB]
 12         CALL         add     
 13         STORE        3[LB]
 14         POP          1
 15         LOAD         3[LB]
 16         LOADL        1
 17         CALL         add     
 18         STORE        3[LB]
 19         PUSH         1
 20         LOADL        9
 21         STORE        4[LB]
 22         LOAD         3[LB]
 23         LOAD         4[LB]
 24         CALL         add     
 25         STORE        3[LB]
 26         POP          1
 27         LOAD         3[LB]
 28         CALL         putintnl
 29         RETURN (0)   1