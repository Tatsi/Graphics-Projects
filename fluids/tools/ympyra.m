function [] = ympyra(n)
close all

kulma = 2*pi/n;
figure
hold on

fid = fopen('triangle.txt','w');
fprintf(fid,'%i\n',3*n);

fid = fopen('triangle_tex_coord.txt','w');
fprintf(fid2,'%i\n',3*n);

for i = [0:n-1]
    Ax = 0.5*cos(i*kulma);
    Ay = 0.5*sin(i*kulma);
    Bx = 0.5*cos((i+1)*kulma);
    By = 0.5*sin((i+1)*kulma);
    a = [Ax Ay 0];
    b = [Bx By 0];
    c = [0 0 0];
    
    aa = [Ax+0.5 -(Ay-0.5) 0];
    bb = [Bx+0.5 -(By-0.5) 0];
    
    line([Ax Bx],[Ay By])
    line([Ax 0],[Ay 0])
    line([0 Bx],[0 By])
    
    axis square;
    
    fprintf(fid,'%f %f\n',a);
    fprintf(fid,'%f %f\n',b);
    fprintf(fid,'%f %f\n',c);
    
    fprintf(fid2,'%f %f\n',aa);
    fprintf(fid2,'%f %f\n',bb);
    fprintf(fid2,'%f %f\n',c);
end

fclose(fid);
fclose(fid2)