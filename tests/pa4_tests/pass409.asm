  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         4
  5         LOADL        4
  6         STORE        3[LB]
  7         LOAD         3[LB]
  8         CALL         newarr  
  9         STORE        4[LB]
 10         LOADL        1
 11         STORE        5[LB]
 12         LOAD         4[LB]
 13         LOADL        0
 14         LOAD         5[LB]
 15         CALL         arrayupd
 16         JUMP         L12
 17  L11:   PUSH         0
 18         LOAD         4[LB]
 19         LOAD         5[LB]
 20         LOAD         4[LB]
 21         LOAD         5[LB]
 22         LOADL        1
 23         CALL         sub     
 24         CALL         arrayref
 25         LOAD         5[LB]
 26         CALL         add     
 27         CALL         arrayupd
 28         LOAD         5[LB]
 29         LOADL        1
 30         CALL         add     
 31         STORE        5[LB]
 32         POP          0
 33  L12:   LOAD         5[LB]
 34         LOAD         4[LB]
 35         CALL         pred    
 36         LOADI  
 37         CALL         lt      
 38         JUMPIF (1)   L11
 39         LOAD         4[LB]
 40         LOADL        3
 41         CALL         arrayref
 42         LOADL        2
 43         CALL         add     
 44         STORE        6[LB]
 45         LOAD         6[LB]
 46         CALL         putintnl
 47         RETURN (0)   1
