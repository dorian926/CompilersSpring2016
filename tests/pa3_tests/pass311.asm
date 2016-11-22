  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         3
  5         LOADL        4
  6         STORE        3[LB]
  7         LOAD         3[LB]
  8         CALL         newarr  
  9         STORE        4[LB]
 10         LOADL        0
 11         STORE        5[LB]
 12         LOADL        1
 13         STORE        5[LB]
 14         LOAD         4[LB]
 15         LOADL        0
 16         LOAD         5[LB]
 17         CALL         arrayupd
 18         JUMP         L12
 19  L11:   PUSH         0
 20         LOAD         4[LB]
 21         LOAD         5[LB]
 22         LOAD         4[LB]
 23         LOAD         5[LB]
 24         LOADL        1
 25         CALL         sub     
 26         CALL         arrayref
 27         LOAD         5[LB]
 28         CALL         add     
 29         CALL         arrayupd
 30         LOAD         5[LB]
 31         LOADL        1
 32         CALL         add     
 33         STORE        5[LB]
 34         POP          0
 35  L12:   LOAD         5[LB]
 36         LOAD         3[LB]
 37         CALL         lt      
 38         JUMPIF (1)   L11
 39         RETURN (0)   1
