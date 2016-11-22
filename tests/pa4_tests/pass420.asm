  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         0
  5         LOADL        1
  6         JUMPIF (0)   L11
  7         LOADL        1
  8         LOADL        1
  9         CALL         and     
 10         JUMP         L12
 11  L11:   LOADL        0
 12  L12:   JUMPIF (1)   L13
 13         LOADL        0
 14         LOADL        1
 15         LOADL        0
 16         CALL         eq      
 17         CALL         or      
 18         JUMP         L14
 19  L13:   LOADL        1
 20  L14:   JUMPIF (0)   L15
 21         LOADL        20
 22         CALL         putintnl
 23         JUMP         L16
 24  L15:   LOADL        1
 25         CALL         neg     
 26         CALL         putintnl
 27  L16:   RETURN (0)   1
