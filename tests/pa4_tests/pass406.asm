  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
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
 16         LOADL        0
 17         LOADL        5
 18         CALL         fieldupd
 19         LOAD         3[LB]
 20         LOADL        1
 21         CALL         fieldref
 22         LOADL        0
 23         LOADL        1
 24         CALL         fieldupd
 25         LOAD         3[LB]
 26         LOADL        0
 27         CALL         fieldref
 28         LOAD         3[LB]
 29         LOADL        1
 30         CALL         fieldref
 31         LOADL        0
 32         CALL         fieldref
 33         CALL         add     
 34         STORE        4[LB]
 35         LOAD         4[LB]
 36         CALL         putintnl
 37         RETURN (0)   1