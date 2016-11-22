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
 31         LOADA        0[OB]
 32         LOADL        1
 33         CALL         fieldref
 34         LOADL        0
 35         LOADL        5
 36         CALL         fieldupd
 37         LOADL        2
 38         LOADA        0[OB]
 39         LOADA        0[OB]
 40         LOADL        1
 41         CALL         fieldref
 42         LOADA        0[OB]
 43         CALLI        L12
 44         CALL         add     
 45         CALL         putintnl
 46         RETURN (0)   0
 47         PUSH         0
 48         LOADA        0[OB]
 49         LOADL        0
 50         CALL         fieldref
 51         LOAD         -2[LB]
 52         CALL         add     
 53         LOAD         -1[LB]
 54         CALL         add     
 55         RETURN (1)   2
 56  L12:   PUSH         0
 57         LOAD         -2[LB]
 58         LOADL        0
 59         CALL         fieldref
 60         LOAD         -1[LB]
 61         LOADL        0
 62         CALL         fieldref
 63         CALL         add     
 64         LOADA        0[OB]
 65         LOADL        0
 66         CALL         fieldref
 67         CALL         add     
 68         RETURN (1)   2
