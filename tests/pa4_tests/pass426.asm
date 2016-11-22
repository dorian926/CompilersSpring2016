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
 11         LOADL        1
 12         CALL         neg     
 13         LOAD         3[LB]
 14         LOADL        0
 15         CALL         fieldref
 16         CALLI        L12
 17         RETURN (0)   1
 18  L11:   PUSH         0
 19         LOADA        0[OB]
 20         LOADL        0
 21         LOADA        0[OB]
 22         CALL         fieldupd
 23         RETURN (0)   0
 24  L12:   PUSH         0
 25         LOAD         -1[LB]
 26         LOADL        27
 27         CALL         add     
 28         CALL         putintnl
 29         RETURN (0)   1
