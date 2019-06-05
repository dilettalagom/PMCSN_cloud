function generateEquationMarkovAlg2()

n = 21;

la = 6.00;
lb = 6.25;
ma = 0.45;
mb = 0.27;

limit = 11;

mAc= 0.25;
mBc = 0.22;

tAc = 1/mAc;
tBc = 1/mBc;

dim2 = zeros(1, ( (limit)*(limit+1)/2 )+ limit*(n-limit) );
states = string(dim2);
variable = zeros(length(dim2),2);
equations = sym(dim2);
count= 1;

%Costruzione delle equazioni
for layers = 1:n
    
    i = layers - 1;
    j = 0;
    if ( layers<limit )
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
            variable(count,1) = i;
            variable(count,2) = j;
            str = sym(str);
            if ( a ~= 0 )
                str1 = 'p'+string(i+1)+'o'+string(j);
                str2 = 'p'+string(i)+'o'+string(j+1);
                str1 = sym(str1);
                str2 = sym(str2);
            else
                str1 = sym('init');
                str2 = sym('init');
                
            end
            if ( b1 ~= 0 )
                str3 = 'p'+string(i-1)+'o'+string(j);
                str3 = sym(str3);
            else
                str3 = sym('init');
            end
            if ( b2 ~= 0 )
                str4 = 'p'+string(i)+'o'+string(j-1);
                str4 = sym(str4);
            else
                str4 = sym('init');
            end
            %eq = sym (str* (c *(la + lb) +(~c)*(la) + i*ma + j*mb ) - a*str1*(i + 1)*ma - c*a*str2*(j + 1)*mb - b1*str3*la - c*b2*str4*lb  == 0)
            equations(count) = sym (str* (a *(la + lb) + i*ma + j*mb ) - a*str1*(i + 1)*ma - a*str2*(j + 1)*mb - b1*str3*la - b2*str4*lb  == 0);
            states(count)= str;
            
            
            count=count+1;
            i=i-1;
            j=j+1;
        end
    elseif (layers>limit)
        for k = 1:limit
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
            if (i+j+1) == n
                a = 0;
            else
                a = 1;
            end
            if j == limit-1
                c = 0;
            else
                c = 1;
            end
            str = 'p'+string(i)+'o'+string(j);
            variable(count,1) = i;
            variable(count,2) = j;
            str = sym(str);
            if ( a ~= 0 )
                str1 = 'p'+string(i+1)+'o'+string(j);
                str2 = 'p'+string(i)+'o'+string(j+1);
                str1 = sym(str1);
                str2 = sym(str2);
            else
                str1 = sym('init');
                str2 = sym('init');
                
            end
            if ( b1 ~= 0 )
                str3 = 'p'+string(i-1)+'o'+string(j);
                str3 = sym(str3);
            else
                str3 = sym('init');
            end
            if ( b2 ~= 0 )
                str4 = 'p'+string(i)+'o'+string(j-1);
                str4 = sym(str4);
            else
                str4 = sym('init');
            end
            %eq = sym (str* (c *(la + lb) +(~c)*(la) + i*ma + j*mb ) - a*str1*(i + 1)*ma - c*a*str2*(j + 1)*mb - b1*str3*la - c*b2*str4*lb  == 0)
            equations(count) = sym ( str*(a *la + i*ma + j*mb ) - a*str1*(i + 1)*ma - c*a*str2*(j + 1)*mb - b1*str3*la == 0);
            states(count)= str;
            
            
            count=count+1;
            i=i-1;
            j=j+1;
        end
    else
        for k = 1:limit
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
            if (i+j+1) == n
                a = 0;
            else
                a = 1;
            end
            if j == limit-1
                c = 0;
            else
                c = 1;
            end
            str = 'p'+string(i)+'o'+string(j);
            variable(count,1) = i;
            variable(count,2) = j;
            str = sym(str);
            if ( a ~= 0 )
                str1 = 'p'+string(i+1)+'o'+string(j);
                str2 = 'p'+string(i)+'o'+string(j+1);
                str1 = sym(str1);
                str2 = sym(str2);
            else
                str1 = sym('init');
                str2 = sym('init');
                
            end
            if ( b1 ~= 0 )
                str3 = 'p'+string(i-1)+'o'+string(j);
                str3 = sym(str3);
            else
                str3 = sym('init');
            end
            if ( b2 ~= 0 )
                str4 = 'p'+string(i)+'o'+string(j-1);
                str4 = sym(str4);
            else
                str4 = sym('init');
            end
            %eq = sym (str* (c *(la + lb) +(~c)*(la) + i*ma + j*mb ) - a*str1*(i + 1)*ma - c*a*str2*(j + 1)*mb - b1*str3*la - c*b2*str4*lb  == 0)
            equations(count) = sym ( str*(a *la + i*ma + j*mb ) - a*str1*(i + 1)*ma - c*a*str2*(j + 1)*mb - b1*str3*la - b2*str4*lb == 0);
            states(count)= str;
            
            
            count=count+1;
            i=i-1;
            j=j+1;
        end
    end
end
states = sym(states);



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
%somma delle probabilit√† degli stati dell'ultimo layer
pq =0;
pq_1 =0;
pq_2 =0;
count= (limit*(limit-1) /2)+1;
%names = cell(11*(n-limit),2);
l=1;
for layers = limit:n
    for k = 1:limit
        %i = variable(count+k-1,1);
        %j = variable(count+k-1,2);
        %names{l,1} = 'p'+string(i)+'o'+string(j);
        if layers == n
            %names{l,2} = 'la+lb';
            pq_1 = pq_1 + Y(count+k-1); 
            pq_2 = pq_2 + Y(count+k-1);
            pq = pq + (la+lb)*Y(count+k-1);           
        else
            %names{l,2} = 'lb';
            pq_2 = pq_2 + Y(count+k-1);
            pq = pq + lb*Y(count+k-1);
        end
        l=l+1;
    end
    count=count+k;
end
pq = pq / (la+lb) ;
%fprintf('probabilit‡ di blocco %f\n',pq);
%fprintf('probabilit‡ di blocco pq_1 %f\n',pq_1);
%fprintf('probabilit‡ di blocco pq_2 %f\n',pq_2);

%%
s = 0;
s1 = 0;
s2 = 0;
count= 2;
for layers = 2:n
    if (layers < limit)
        for k = 1:layers
            i = variable(count+k-1,1);
            j = variable(count+k-1,2);
            s = s +(layers)*Y(count+k-1);  % somma( n*p(i,j))
            s1 = s1 + i *Y(count+k-1);
            s2 = s2 + j *Y(count+k-1);
        end
    else
        for k = 1:limit
            i = variable(count+k-1,1);
            j = variable(count+k-1,2);
            s = s +(i+j)*Y(count+k-1);  % somma( n*p(i,j))
            s1 = s1 + i *Y(count+k-1);
            s2 = s2 + j *Y(count+k-1);
        end
    end
    count=count+k;
end

% E[T]_CLOUDLET = sum(n(i,j)*p(i,j)) per ogni i, per ogni j
% E[T]_CLOUDLET_type1 = sum(n1(i,j)*p(i,j) per ogni i, per ogni j
% E[T]_CLOUDLET_type2 = sum(n2(i,j)*p(i,j) per ogni i, per ogni j
clc
p1 = (la*pq_1)/ (pq*(la+lb));
p2 = 1-p1;

disp("pq: "+pq);
disp("pq_1: "+pq_1);
disp("pq_2: "+pq_2);

Rclet = s / (la+lb); % cloudlet
disp("E[T]_CLOUDLET generato analiticamente dalla catena di Markov: " + Rclet);

Rclet_t1 = s1 / la; % cloudlet
disp("E[T1]_CLOUDLET generato analiticamente dalla catena di Markov: " + Rclet_t1);

Rclet_t2 = s2 / lb; % cloudlet
disp("E[T2]_CLOUDLET generato analiticamente dalla catena di Markov: " + Rclet_t2);

Rc = (p1*tAc + p2*tBc) ; % cloud
disp("E[T]_CLOUD generato analiticamente dalla catena di Markov: " + Rc);

Rc_task1 = (pq_1)*tAc; % cloud
disp("E[T1]_CLOUD generato analiticamente dalla catena di Markov: " + Rc_task1);

Rc_task2 = pq_2*tBc; % cloud
disp("E[T2]_CLOUD generato analiticamente dalla catena di Markov: " + Rc_task2);

Rtot = Rclet+Rc; %sistema
disp("E[T]_SISTEMA generato analiticamente dalla catena di Markov: " + Rtot);

Rtot_task1 = Rclet_t1+Rc_task1; %sistema
disp("E[T1]_SISTEMA generato analiticamente dalla catena di Markov: " + Rtot_task1);

Rtot_task2 = Rclet_t2+Rc_task2; %sistema
disp("E[T2]_SISTEMA generato analiticamente dalla catena di Markov: " + Rtot_task2);




end



