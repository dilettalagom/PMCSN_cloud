function generateEquationMarkov()

n = 21;

la = 6.00;
lb = 6.25;
ma = 0.45;
mb = 0.27;

mAc= 0.25;
mBc = 0.22;

tAc = 1/mAc;
tBc = 1/mBc;

dim = zeros(1,(n)*(n+1)/2);
states = string(dim);
variable = zeros(length(dim),2);
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
        variable(count,1) = i;
        variable(count,2) = j;
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
l= length(Y);
u= l-20;
pq = sum( Y( u:l ) );
pq_2 = pq;
pq_1 = pq;


%%
s = 0;
s1 = 0;
s2 = 0;
count= 2;
for layers = 2:n
    for k = 1:layers
        i = variable(count+k-1,1);
        j = variable(count+k-1,2);
        s = s +(i+j)*Y(count+k-1);  % somma( n*p(i,j))
        s1 = s1 + i *Y(count+k-1);
        s2 = s2 + j *Y(count+k-1);
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

disp("p1 cloud: "+p1);
disp("p2 cloud: "+p2);

Rclet = s / (la+lb); % cloudlet
disp("E[T]_CLOUDLET generato analiticamente dalla catena di Markov: " + Rclet);

Rclet_t1 = s1 / la; % cloudlet
disp("E[T1]_CLOUDLET generato analiticamente dalla catena di Markov: " + Rclet_t1);

Rclet_t2 = s2 / lb; % cloudlet
disp("E[T2]_CLOUDLET generato analiticamente dalla catena di Markov: " + Rclet_t2);

Rc = pq*(p1*tAc + p2*tBc) ; % cloud
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