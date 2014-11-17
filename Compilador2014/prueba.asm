.model small 
.stack 
.data 
a_1 db 0h
b_1 db 0h
c_1 db 0h
resultado_1 db 0h
.code
MOV ax,1
MOV a_1,ax
MOV ax,2
MOV b_1,ax
MOV ax,3
MOV c_1,ax
MOV ax,a_1
MOV ax,b_1
MOV resultado_1,ax
MOV ax,resultado_1
MOV ah,ax
