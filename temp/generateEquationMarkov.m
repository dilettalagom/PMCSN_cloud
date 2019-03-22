function generateEquationMarkov()

    n = 21;
    
    la = 6.00;
    lb = 6.25;
    ma = 0.45;
    mb = 0.27;
    
    a = zeros(1,(n)*(n+1)/2);
    states = string(a);
    %Matrix = zeros(n*(n+1)/2,n*(n+1)/2);
    equations = sym(a);
    count= 1;
    for layers = 1:n
        i = layers - 1;
        j = 0;
        for k = 1:layers
          
            if i == 0
                b1 = 0;
            else
                b1 = 1; 
            end
            if j == 0
                b2 = 0;
            else
                b2 = 1;
            end
            if layers == n 
                a = 0;
            else
                a = 1;
            end
            

            
            str = 'p'+string(i)+'o'+string(j);
            var = sym(str);
            if ( a ~= 0 )
                str1 = 'p'+string(i+1)+'o'+string(j);
                str2 = 'p'+string(i)+'o'+string(j+1);
                var1 = sym(str1);
                var2 = sym(str2);
            else
                var1 = sym('a');
                var2 = sym('a');
            end
            if ( b1 ~= 0 )
                str3 = 'p'+string(i-1)+'o'+string(j);
                var3 = sym(str3);
            else
                var3 = sym('a');      
            end
            if ( b2 ~= 0 )
                str4 = 'p'+string(i)+'o'+string(j-1);
                var4 = sym(str4);
            else
                var4 = sym('a');    
            end
   
            equations(count) = sym (var*(a*la + a*lb + i*ma + j*mb) - a*var1*(i + 1)*ma - a*var2*(j + 1)*mb - b1*var3*la - b2*var4*lb == 0);

            states(count)= str;
            
            
            count=count+1;
            i=i-1;
            j=j+1;
        end
    end
    v = sym(states);        
    
   
    
    %disp(v);
    %disp(equations);
           
    
    %%
  
    
    %%
    x = states(1);
    for i = 2:states.size(2)
        x = sym(x) + sym( states(i) );
    end
    equations(states.size(2)) = x - 1== 0;
    
    %disp(x);
    
    %%
   format long g;
   [A,b] = equationsToMatrix(equations,v);
   S = double(A);
   [V,D] = eig(S);
   
   X = linsolve(A,b);
   Y = double(X);
   %P = [v(:),Y];
   %%
  
   sum(Y(211:231));
end
