function generateEquationMarkov()

    n = 21;
    
    la = 6.00;
    lb = 6.25;
    ma = 0.45;
    mb = 0.27;
    
    dim = zeros(1,(n)*(n+1)/2);
    states = string(dim);
    equations = sym(dim);
    count= 1;
    
    %Costruzione delle equazioni
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
            str = sym(str);
            if ( a ~= 0 )
                str1 = 'p'+string(i+1)+'o'+string(j);
                str2 = 'p'+string(i)+'o'+string(j+1);
                str1 = sym(str1);
                str2 = sym(str2);
        
            else
                str1 = sym('a');
                str2 = sym('a');
            end
            if ( b1 ~= 0 )
                str3 = 'p'+string(i-1)+'o'+string(j);
                str3 = sym(str3);
            else
                str3 = sym('a');      
            end
            if ( b2 ~= 0 )
                str4 = 'p'+string(i)+'o'+string(j-1);
                str4 = sym(str4);
            else
                str4 = sym('a');    
            end
   
            equations(count) = sym (str*(a*la + a*lb + i*ma + j*mb) - a*str1*(i + 1)*ma - a*str2*(j + 1)*mb - b1*str3*la - b2*str4*lb == 0);

            states(count)= str;
            
            
            count=count+1;
            i=i-1;
            j=j+1;
        end
    end
    states = sym(states);        
               
    
    %%
    
    %Sostituzione dell' equazione degli stati che sommano a 1 all'ultima
    %equazione
    
    count=count-1;
    equations(count) = states(1);
    for i = 2:count
        equations(count) = equations(count) + states(i) ;
    end
    equations(count) = equations(count) - 1== 0;
    
    %disp(x);
    
    %%
    
    %Risoluzione delle equazioni
   format long g;
   [A,b] = equationsToMatrix(equations,states);
  
   X = linsolve(A,b);
   Y = double(X);
  
   
   %%
   %somma delle probabilità degli stati dell'ultimo layer
   sum(Y(211:231))
   
   %%
    s = 0;
    count= 2;
    for layers = 2:n
        display("layer: " + layers);
        for k = 1:layers
            s = s + (layers-1)*Y(count+k-1);
            
        end
        count=count+k
    end


 %%
 t = s / 12.25 % cloudlet
 
