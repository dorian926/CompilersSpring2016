  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         2
  5         LOADL        -1
  6         LOADL        0
  7         CALL         newobj  
  8         STORE        3[LB]
  9         CALL         L12
 10         CALL         L11
 11         LOADL        5
 12         CALL         eq      
 13         CALL         and     
 14         STORE        4[LB]
 15         RETURN (0)   1
 16  L11:   PUSH         0
 17         LOADL        5
 18         RETURN (1)   0
 19         LOADL        5
 20         RETURN (1)   0
 21  L12:   PUSH         0
 22         LOADL        1
 23         LOADL        0
 24         CALL         eq      
 25         RETURN (1)   0
 26         LOADL        1
 27         LOADL        0
 28         CALL         eq      
 29         RETURN (1)   0
