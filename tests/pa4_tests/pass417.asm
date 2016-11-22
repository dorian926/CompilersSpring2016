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
 10         LOADL        0
 11         LOADL        1
 12         CALL         neg     
 13         CALL         fieldupd
 14         LOADL        17
 15         LOAD         3[LB]
 16         CALLI        L11
 17         STORE        3[LB]
 18         LOAD         3[LB]
 19         LOADL        0
 20         CALL         fieldref
 21         CALL         putintnl
 22         RETURN (0)   1
 23  L11:   PUSH         2
 24         LOADL        1
 25         LOADL        1
 26         CALL         newobj  
 27         STORE        3[LB]
 28         LOAD         3[LB]
 29         LOADL        0
 30         LOAD         -1[LB]
 31         CALL         fieldupd
 32         LOAD         3[LB]
 33         CALLI        L12
 34         STORE        4[LB]
 35         LOAD         4[LB]
 36         RETURN (1)   1
 37  L12:   PUSH         0
 38         LOADA        0[OB]
 39         RETURN (1)   0
