  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         3
  5         LOADL        2
  6         STORE        3[LB]
  7         LOADL        2
  8         LOADL        5
  9         CALL         mult    
 10         LOADL        3
 11         CALL         sub     
 12         CALL         newarr  
 13         STORE        4[LB]
 14         LOAD         4[LB]
 15         LOADL        0
 16         LOADL        13
 17         CALL         arrayupd
 18         LOAD         4[LB]
 19         LOAD         3[LB]
 20         LOADL        2
 21         CALL         sub     
 22         CALL         arrayref
 23         LOAD         3[LB]
 24         CALL         gt      
 25         STORE        5[LB]
 26         LOAD         5[LB]
 27         JUMPIF (0)   L11
 28         LOAD         4[LB]
 29         LOADL        0
 30         CALL         arrayref
 31         CALL         putintnl
 32         JUMP         L12
 33  L11:   LOADL        1
 34         CALL         neg     
 35         CALL         putintnl
 36  L12:   RETURN (0)   1
