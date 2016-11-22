  0         PUSH         1
  1         PUSH         1
  2         LOADL        0
  3         LOADL        -1
  4         CALL         L11
  5         HALT   (0)   
  6  L10:   PUSH         0
  7         LOAD         0[SB]
  8         LOAD         -1[LB]
  9         CALL         add     
 10         STORE        0[SB]
 11         LOADL        2
 12         LOAD         0[SB]
 13         CALL         add     
 14         STORE        1[SB]
 15         RETURN (0)   1
 16  L11:   PUSH         1
 17         LOADL        3
 18         STORE        0[SB]
 19         LOADL        4
 20         LOADL        0
 21         CALLI        L10
 22         LOADL        0
 23         LOAD         0[SB]
 24         LOAD         1[SB]
 25         CALL         add     
 26         LOADL        7
 27         CALL         add     
 28         STORE        3[LB]
 29         LOAD         3[LB]
 30         CALL         putintnl
 31         RETURN (0)   1
