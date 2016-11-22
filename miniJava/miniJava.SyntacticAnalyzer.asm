  0         LOADL        0
  1         LOADL        -1
  2         CALL         L10
  3         HALT   (0)   
  4  L10:   PUSH         4
  5         LOADL        1
  6         STORE        3[LB]
  7         LOADL        2
  8         LOAD         3[LB]
  9         CALL         mult    
 10         LOAD         3[LB]
 11         CALL         add     
 12         LOADL        1
 13         CALL         sub     
 14         STORE        3[LB]
 15         LOADL        3
 16         CALL         putint  
 17         CALL         puteol  
 18         LOAD         3[LB]
 19         LOADL        1
 20         CALL         neg     
 21         CALL         ne      
 22         JUMPIF (0)   L11
 23         LOADL        4
 24         CALL         putint  
 25         CALL         puteol  
 26         JUMP         L12
 27  L11:   LOADL        1
 28         CALL         neg     
 29         CALL         putint  
 30         CALL         puteol  
 31  L12:   LOADL        0
 32         STORE        4[LB]
 33         JUMP         L14
 34  L13:   PUSH         0
 35         LOAD         4[LB]
 36         LOADL        1
 37         CALL         add     
 38         STORE        4[LB]
 39         LOAD         4[LB]
 40         STORE        3[LB]
 41         POP          0
 42  L14:   LOAD         4[LB]
 43         LOADL        5
 44         CALL         lt      
 45         JUMPIF (1)   L13
 46         LOAD         3[LB]
 47         CALL         putint  
 48         CALL         puteol  
 49         LOADL        -1
 50         LOADL        2
 51         CALL         newobj  
 52         STORE        5[LB]
 53         LOAD         5[LB]
 54         CALL         ne      
 55         JUMPIF (0)   L15
 56         LOADL        6
 57         CALL         putint  
 58         CALL         puteol  
 59  L15:   LOADL        7
 60         LOADL        0
 61         CALL         fieldref
 62         CALL         add     
 63         STORE        3[LB]
 64         LOAD         3[LB]
 65         CALL         putint  
 66         CALL         puteol  
 67         LOADL        1
 68         LOADL        -1
 69         LOADL        2
 70         CALL         newobj  
 71         CALL         fieldupd
 72         LOADL        0
 73         LOADL        8
 74         CALL         fieldupd
 75         LOADL        0
 76         CALL         fieldref
 77         CALL         putint  
 78         CALL         puteol  
 79         LOADL        4
 80         CALL         newarr  
 81         STORE        6[LB]
 82         CALL         pred    
 83         LOADI  
 84         STORE        3[LB]
 85         LOADL        2
 86         LOAD         3[LB]
 87         CALL         mult    
 88         LOADL        1
 89         CALL         add     
 90         CALL         putint  
 91         CALL         puteol  
 92         LOAD         6[LB]
 93         LOADL        0
 94         LOADL        0
 95         CALL         arrayupd
 96         LOADL        1
 97         STORE        4[LB]
 98         JUMP         L17
 99  L16:   PUSH         0
100         LOAD         6[LB]
101         LOAD         4[LB]
102         LOAD         6[LB]
103         LOAD         4[LB]
104         LOADL        1
105         CALL         sub     
106         CALL         arrayref
107         LOAD         4[LB]
108         CALL         add     
109         CALL         arrayupd
110         LOAD         4[LB]
111         LOADL        1
112         CALL         add     
113         STORE        4[LB]
114         POP          0
115  L17:   LOAD         4[LB]
116         CALL         pred    
117         LOADI  
118         CALL         lt      
119         JUMPIF (1)   L16
120         LOAD         6[LB]
121         LOADL        3
122         CALL         arrayref
123         LOADL        4
124         CALL         add     
125         STORE        3[LB]
126         LOAD         3[LB]
127         CALL         putint  
128         CALL         puteol  
129         CALL         L18
130         LOADL        999
131         CALL         putint  
132         CALL         puteol  
133  L18:   PUSH         1
134         LOADL        11
135         STORE        3[LB]
136         LOAD         3[LB]
137         CALL         putint  
138         CALL         puteol  
139         LOADL        1
140         CALL         fieldupd
141         LOADA        0[OB]
142         LOADL        0
143         LOADL        12
144         CALL         fieldupd
145         LOADL        0
146         CALL         fieldref
147         STORE        3[LB]
148         LOAD         3[LB]
149         CALL         putint  
150         CALL         puteol  
151         LOADA        0[OB]
152         LOADL        0
153         LOADL        4
154         CALL         fieldupd
155         LOADL        2
156         LOADL        3
157         LOADL        4
158         LOADA        0[OB]
159         CALL         L19
160         CALL         add     
161         STORE        3[LB]
162         LOAD         3[LB]
163         CALL         putint  
164         CALL         puteol  
165         LOADL        8
166         LOADL        3
167         CALL         L21
168         CALL         add     
169         CALL         putint  
170         CALL         puteol  
171         LOADL        0
172         LOADL        4
173         CALL         fieldupd
174         LOADL        0
175         LOADL        5
176         CALL         fieldupd
177         LOADL        2
178         LOADL        1
179         CALL         fieldref
180         CALL         L20
181         CALL         add     
182         CALL         putint  
183         CALL         puteol  
184  L19:   PUSH         0
185         LOADA        0[OB]
186         LOADL        0
187         CALL         fieldref
188         LOAD         -2[LB]
189         CALL         add     
190         LOAD         -1[LB]
191         CALL         add     
192         RETURN (1)   2
193  L20:   PUSH         0
194         LOADL        0
195         CALL         fieldref
196         LOADL        0
197         CALL         fieldref
198         CALL         add     
199         LOADL        0
200         CALL         fieldref
201         CALL         add     
202         RETURN (1)   2
203  L21:   PUSH         1
204         LOADL        1
205         STORE        3[LB]
206         LOAD         -1[LB]
207         LOADL        1
208         CALL         gt      
209         JUMPIF (0)   L22
210         LOAD         -1[LB]
211         LOAD         -1[LB]
212         LOADL        1
213         CALL         sub     
214         LOADA        0[OB]
215         CALL         L21
216         CALL         mult    
217         STORE        3[LB]
218  L22:   LOAD         3[LB]
219         RETURN (1)   1
