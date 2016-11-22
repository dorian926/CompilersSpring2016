  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         1
  5         LOADL        1
  6         LOADL        2
  7         CALL         newobj  
  8         STORE        3[LB]
  9         LOAD         3[LB]
 10         LOADL        1
 11         LOADL        2
 12         LOADL        2
 13         CALL         newobj  
 14         CALL         fieldupd
 15         LOAD         3[LB]
 16         LOADL        1
 17         CALL         fieldref
 18         LOADL        1
 19         LOAD         3[LB]
 20         CALL         fieldupd
 21         LOAD         3[LB]
 22         CALLI        L11
 23         RETURN (0)   1
 24  L11:   PUSH         1
 25         LOADL        10
 26         STORE        3[LB]
 27         LOADA        0[OB]
 28         LOADL        0
 29         LOADL        4
 30         CALL         fieldupd
 31         LOADL        1
 32         LOADL        3
 33         LOADL        4
 34         LOADA        0[OB]
 35         CALLI        L12
 36         CALL         add     
 37         STORE        3[LB]
 38         LOAD         3[LB]
 39         CALL         putintnl
 40         RETURN (0)   0
 41  L12:   PUSH         0
 42         LOADA        0[OB]
 43         LOADL        0
 44         CALL         fieldref
 45         LOAD         -2[LB]
 46         CALL         add     
 47         LOAD         -1[LB]
 48         CALL         add     
 49         RETURN (1)   2
