  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        -1
  6         LOADL        1
  7         CALL         newobj  
  8         STORE        3[LB]
  9         LOADL        57
 10         CALL         L11
 11         STORE        3[LB]
 12         RETURN (0)   1
 13  L11:   PUSH         2
 14         LOADL        -1
 15         LOADL        1
 16         CALL         newobj  
 17         STORE        3[LB]
 18         LOADL        0
 19         LOAD         -1[LB]
 20         CALL         fieldupd
 21         CALL         L12
 22         STORE        4[LB]
 23         LOAD         4[LB]
 24         RETURN (1)   1
 25         LOAD         4[LB]
 26         RETURN (1)   1
 27  L12:   PUSH         0
 28         RETURN (1)   0
 29         RETURN (1)   0
