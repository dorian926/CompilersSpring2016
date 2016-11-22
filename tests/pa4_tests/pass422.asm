  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         4
  5         LOADL        0
  6         LOADL        1
  7         CALL         newobj  
  8         STORE        3[LB]
  9         LOAD         3[LB]
 10         LOADL        0
 11         LOADL        0
 12         CALL         fieldupd
 13         LOADL        22
 14         STORE        4[LB]
 15         LOADL        0
 16         JUMPIF (0)   L11
 17         LOADL        1
 18         LOAD         3[LB]
 19         CALLI        L16
 20         CALL         and     
 21         JUMP         L12
 22  L11:   LOADL        0
 23  L12:   STORE        5[LB]
 24         LOADL        1
 25         JUMPIF (1)   L13
 26         LOADL        0
 27         LOAD         3[LB]
 28         CALLI        L16
 29         CALL         or      
 30         JUMP         L14
 31  L13:   LOADL        1
 32  L14:   STORE        6[LB]
 33         LOAD         3[LB]
 34         LOADL        0
 35         CALL         fieldref
 36         JUMPIF (0)   L15
 37         LOADL        1
 38         CALL         neg     
 39         STORE        4[LB]
 40  L15:   LOAD         4[LB]
 41         CALL         putintnl
 42         RETURN (0)   1
 43  L16:   PUSH         0
 44         LOADA        0[OB]
 45         LOADL        0
 46         LOADL        1
 47         CALL         fieldupd
 48         LOADL        1
 49         RETURN (1)   0
