  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
  5         LOADL        -1
  6         LOADL        1
  7         CALL         newobj  
  8         STORE        3[LB]
  9         LOADL        -1
 10         LOADL        1
 11         CALL         newobj  
 12         STORE        4[LB]
 13         CALL         L12
 14         CALL         L13
 15         CALL         eq      
 16         JUMPIF (0)   L11
 17         RETURN (0)   1
 18  L11:   RETURN (0)   1
 19  L12:   PUSH         0
 20         LOADA        0[OB]
 21         LOADL        0
 22         CALL         fieldupd
 23         RETURN (0)   0
 24  L13:   PUSH         0
 25         LOADA        0[OB]
 26         LOADL        0
 27         CALL         fieldref
 28         RETURN (1)   0
 29         LOADA        0[OB]
 30         LOADL        0
 31         CALL         fieldref
 32         RETURN (1)   0
