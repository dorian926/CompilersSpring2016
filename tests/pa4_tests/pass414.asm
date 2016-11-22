  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        0
  6         LOADL        0
  7         CALL         newobj  
  8         STORE        3[LB]
  9         LOADL        1
 10         LOADL        7
 11         LOAD         3[LB]
 12         CALLI        L11
 13         CALL         add     
 14         CALL         putintnl
 15         RETURN (0)   1
 16  L11:   PUSH         1
 17         LOADL        1
 18         CALL         neg     
 19         STORE        3[LB]
 20         LOAD         -1[LB]
 21         LOADL        0
 22         CALL         le      
 23         JUMPIF (0)   L12
 24         LOADL        0
 25         STORE        3[LB]
 26         JUMP         L14
 27  L12:   LOAD         -1[LB]
 28         LOADL        1
 29         CALL         eq      
 30         JUMPIF (0)   L13
 31         LOADL        1
 32         STORE        3[LB]
 33         JUMP         L14
 34  L13:   LOAD         -1[LB]
 35         LOADL        1
 36         CALL         sub     
 37         LOADA        0[OB]
 38         CALLI        L11
 39         LOAD         -1[LB]
 40         LOADL        2
 41         CALL         sub     
 42         LOADA        0[OB]
 43         CALLI        L11
 44         CALL         add     
 45         STORE        3[LB]
 46  L14:   LOAD         3[LB]
 47         RETURN (1)   1
